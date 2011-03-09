/*
 * EvaluatorMain.java
 * 
 * Copyright (c) 2011, Christoph Käding, DFKI. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.dfki.km.text20.lightning.evaluator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.diagnosis.local.Diagnosis;
import de.dfki.km.augmentedtext.services.language.statistics.Statistics;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.worker.training.DataContainer;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class EvaluatorMain extends EvaluationWindow implements ActionListener {

    private static EvaluatorMain evaluatorMain;

    private ArrayList<File> files;

    private PluginManager pluginManager;

    private ArrayList<SaliencyDetector> saliencyDetectors;

    private ArrayList<PluginInformation> information;

    private ArrayList<SaliencyDetector> selectedDetectors;

    private EvaluatorWorker worker;

    private boolean running;

    private boolean finished;
    
    private long currentTimeStamp;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Set global look and feel. 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel.\n");
        }

        EvaluatorMain.getInstance().init();
    }

    /**
     * 
     * @return singleton instance
     */
    public static EvaluatorMain getInstance() {
        if (evaluatorMain == null) evaluatorMain = new EvaluatorMain();
        return evaluatorMain;
    }

    /**
     * 
     */
    private void init() {
        this.files = new ArrayList<File>();
        this.information = new ArrayList<PluginInformation>();

        this.buttonSelect.addActionListener(this);
        this.buttonStart.addActionListener(this);
        this.buttonRemove.addActionListener(this);

        this.buttonStart.setEnabled(false);
        this.buttonStart.setText("Start");

        this.labelDescription.setText("Step 1: Select *.training files.");

        this.running = false;
        this.finished = false;

        this.currentTimeStamp = System.currentTimeMillis();
        this.worker = new EvaluatorWorker(this.currentTimeStamp);
        

        // set logging properties
        final JSPFProperties props = new JSPFProperties();
        props.setProperty(Diagnosis.class, "recording.enabled", "true");
        props.setProperty(Diagnosis.class, "recording.file", "diagnosis.record");
        props.setProperty(Diagnosis.class, "recording.format", "java/serialization");
        props.setProperty(Diagnosis.class, "analysis.stacktraces.enabled", "true");
        props.setProperty(Diagnosis.class, "analysis.stacktraces.depth", "10000");

        // set statistic properties
        props.setProperty(Statistics.class, "application.id", "StatisticsTest");

        // initialize plugin manager
        this.pluginManager = PluginManagerFactory.createPluginManager(props);

        // add plugins at classpath
        try {
            this.pluginManager.addPluginsFrom(new URI("classpath://*"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.saliencyDetectors = new ArrayList<SaliencyDetector>(new PluginManagerUtil(this.pluginManager).getPlugins(SaliencyDetector.class));
        this.selectedDetectors = new ArrayList<SaliencyDetector>();
        this.files = new ArrayList<File>();

        for (int i = 0; i < this.saliencyDetectors.size(); i++) {
            this.saliencyDetectors.get(i).getInformation().setId(i);
            this.information.add(this.saliencyDetectors.get(i).getInformation());
        }

        this.listDetectors.setListData(this.information.toArray());

        setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonSelect) {
            this.buttonSelectActionPerformed();
            return;
        }
        if (event.getSource() == this.buttonStart) {
            this.buttonStartActionPerformed();
            return;
        }
        if (event.getSource() == this.buttonRemove) {
            this.buttonRemoveActionPerformed();
            return;
        }
    }

    private void buttonSelectActionPerformed() {
        JFileChooser chooser = initChooser();
        chooser.showOpenDialog(null);
        this.labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
        this.buttonStart.setEnabled(true);
    }

    private void buttonStartActionPerformed() {
        if (this.finished) this.exit();
        if (!this.running) {
            if (this.listDetectors.getSelectedValues().length == 0) return;
            this.running = true;
            this.labelDescription.setText("Step 3: Wait for the results.");
            this.buttonStart.setText("Stop");
            this.buttonRemove.setEnabled(false);
            this.buttonSelect.setEnabled(false);
            this.listDetectors.setEnabled(false);
            this.listFiles.setEnabled(false);
            this.checkBoxImages.setEnabled(false);
            this.checkBoxSummary.setEnabled(false);
            this.startEvaluation();
        } else {
            this.running = false;
            this.labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
            this.buttonStart.setText("Start");
            this.buttonRemove.setEnabled(true);
            this.buttonSelect.setEnabled(true);
            this.listDetectors.setEnabled(true);
            this.listFiles.setEnabled(true);
            this.checkBoxImages.setEnabled(true);
            this.checkBoxSummary.setEnabled(true);
            this.worker.stop();
        }
    }

    private void buttonRemoveActionPerformed() {
        for (Object selected : this.listFiles.getSelectedValues()) {
            if (selected instanceof File) this.files.remove(selected);
        }
        this.listFiles.removeAll();
        this.listFiles.setListData(this.files.toArray());
    }

    private JFileChooser initChooser() {
        JFileChooser chooser = new JFileChooser() {

            // react on selection
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();
                files.addAll(Arrays.asList(getSelectedFiles()));
                listFiles.setListData(files.toArray());
            }
        };
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileFilter() {
            private String extension = ".training";

            @Override
            public String getDescription() {
                return this.extension;
            }

            @Override
            public boolean accept(File file) {
                if (file == null) return false;

                if (file.isDirectory()) return true;

                return file.getName().toLowerCase().endsWith(this.extension);
            }
        });

        return chooser;
    }

    private ArrayList<DataContainer> readFile(File file) {
        ObjectInputStream inputStream = null;
        Object object;
        ArrayList<DataContainer> container = new ArrayList<DataContainer>();
        try {
            inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            while ((object = inputStream.readObject()) != null) {
                if (object instanceof DataContainer) {
                    container.add((DataContainer) object);
                }
            }
            inputStream.close();
            return container;
        } catch (EOFException eofe) {
            try {
                if (inputStream != null) inputStream.close();
                return container;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startEvaluation() {
        for (Object selected : this.listDetectors.getSelectedValues()) {
            if (selected instanceof PluginInformation) {
                this.selectedDetectors.add(this.saliencyDetectors.get(((PluginInformation) selected).getId()));
            }
        }

        for (File file : this.files) {
            for (DataContainer container : this.readFile(file)) {
                for (SaliencyDetector detector : this.selectedDetectors) {

                    this.worker.start(detector, container, this.checkBoxSummary.isSelected(), this.checkBoxImages.isSelected(), file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator + "data" + File.separator)));

                    if (!this.running) break;
                }
                if (!this.running) break;
            }
            if (!this.running) break;
        }
        this.labelDescription.setText("Evaluation finished. " + this.worker.getBestResult() + " achived the best results.");
        this.selectedDetectors.clear();
        this.finished = true;
        this.buttonStart.setText("Exit");
    }

    // TODO: catch window closing event
    private void exit() {
        this.pluginManager.shutdown();
        dispose();
        System.exit(0);
    }
}

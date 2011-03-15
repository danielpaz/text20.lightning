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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.diagnosis.local.Diagnosis;
import de.dfki.km.augmentedtext.services.language.statistics.Statistics;
import de.dfki.km.text20.lightning.evaluator.gui.EvaluationWindow;
import de.dfki.km.text20.lightning.evaluator.worker.EvaluatorWorker;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.worker.training.StorageContainer;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class EvaluatorMain extends EvaluationWindow implements ActionListener,
        WindowListener {

    /** singleton instance of this main */
    private static EvaluatorMain evaluatorMain;

    /** selected *.training files */
    private ArrayList<File> files;

    /** manager which handles the detector plugins, statitic plugin, logging plugin .... */
    private PluginManager pluginManager;

    /** list of available detectors */
    private ArrayList<SaliencyDetector> saliencyDetectors;

    /** list of plugin information of the available detectors */
    private ArrayList<PluginInformation> information;

    /** list of the from listDetectors selected detectors */
    private ArrayList<SaliencyDetector> selectedDetectors;

    /** evaluation worker which runs the detectors */
    private EvaluatorWorker worker;

    /** indicates if the tool is running */
    private boolean running;

    /** indicates id the tool is finished */
    private boolean finished;

    /** timestamp from the start of this tool */
    private long currentTimeStamp;

    /**
     * main entry point
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Set global look and feel. 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel.\n");
        }

        // create and initialize singleton
        EvaluatorMain.getInstance().init();
    }

    /**
     * creates the singleton instance
     * 
     * @return singleton instance
     */
    public static EvaluatorMain getInstance() {
        if (evaluatorMain == null) evaluatorMain = new EvaluatorMain();
        return evaluatorMain;
    }

    /**
     * Initializes all necessary variables and sets the window visible.
     */
    private void init() {
        // initialize arraylists
        this.files = new ArrayList<File>();
        this.information = new ArrayList<PluginInformation>();
        this.selectedDetectors = new ArrayList<SaliencyDetector>();

        // add action listeners
        this.buttonSelect.addActionListener(this);
        this.buttonStart.addActionListener(this);
        this.buttonRemove.addActionListener(this);
        this.mainFrame.addWindowListener(this);

        // set enable/disable and text to some components
        this.buttonStart.setEnabled(false);
        this.buttonStart.setText("Start");
        this.labelDescription.setText("Step 1: Select *.training files.");
        this.checkBoxImages.setSelected(true);
        this.checkBoxSummary.setSelected(true);
        this.buttonStart.setEnabled(false);
        this.listDetectors.setEnabled(false);
        this.progressBar.setEnabled(false);

        // initialize status
        this.running = false;
        this.finished = false;

        // get timestamp
        this.currentTimeStamp = System.currentTimeMillis();

        // create new worker
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
            this.pluginManager.addPluginsFrom(new File("plugins/").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // initialize list of detectors
        this.saliencyDetectors = new ArrayList<SaliencyDetector>(new PluginManagerUtil(this.pluginManager).getPlugins(SaliencyDetector.class));
        for (int i = 0; i < this.saliencyDetectors.size(); i++) {
            this.saliencyDetectors.get(i).getInformation().setId(i);
            this.information.add(this.saliencyDetectors.get(i).getInformation());
        }

        // initialize listDetectors
        this.listDetectors.setListData(this.information.toArray());
        this.listDetectors.setCellRenderer(this.initRenderer());

        this.mainFrame.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        // check which source occurs the action event and start a handle function
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

    /**
     * fired when the buttonSelect is clicked
     * opens the file chooser
     */
    private void buttonSelectActionPerformed() {
        JFileChooser chooser = initChooser();
        chooser.showOpenDialog(null);
    }

    /**
     * fired when the buttonStart is clicked
     * reacts on the current status of this tool and starts a handle for it
     */
    private void buttonStartActionPerformed() {
        // if the work is finished, exit the tool
        if (this.finished) this.exit();

        // if the tool ist not running ...
        if (!this.running) {
            // ... and if some detectors are selected ...
            if (this.listDetectors.getSelectedValues().length == 0) return;

            // ... set some gui components disable and repaint the gui
            this.running = true;
            this.labelDescription.setText("Step 3: Wait for the results.");
            this.labelDescription.revalidate();
            this.buttonStart.setText("Stop");
            this.buttonStart.revalidate();
            this.buttonRemove.setEnabled(false);
            this.buttonRemove.revalidate();
            this.buttonSelect.setEnabled(false);
            this.buttonSelect.revalidate();
            this.listDetectors.setEnabled(false);
            this.listDetectors.revalidate();
            this.listFiles.setEnabled(false);
            this.listFiles.revalidate();
            this.checkBoxImages.setEnabled(false);
            this.checkBoxImages.revalidate();
            this.checkBoxSummary.setEnabled(false);
            this.checkBoxSummary.revalidate();
            this.mainFrame.repaint();
            
            // FIXME: update gui
            
            // start evaluation
            this.startEvaluation();

            // or if the tool is running ...
        } else {

            // ... reset to startable state
            this.running = false;
            this.labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
            this.buttonStart.setText("Start");
            this.buttonRemove.setEnabled(true);
            this.buttonSelect.setEnabled(true);
            this.listDetectors.setEnabled(true);
            this.listFiles.setEnabled(true);
            this.checkBoxImages.setEnabled(true);
            this.checkBoxSummary.setEnabled(true);
        }
    }

    /**
     * fired when the buttonRemove is clicked
     * removes selected files from array list and listFiles
     */
    private void buttonRemoveActionPerformed() {
        // for every selected file ...
        for (Object selected : this.listFiles.getSelectedValues()) {

            // ... remove it from array list
            if (selected instanceof File) this.files.remove(selected);
        }

        // clear listFiles and add all in files included files
        this.listFiles.removeAll();
        this.listFiles.setListData(this.files.toArray());
    }

    /**
     * initializes filechooser
     * 
     * @return
     */
    private JFileChooser initChooser() {
        JFileChooser chooser = new JFileChooser() {

            // react on selection
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();

                // add selected files to array list ...
                for (File file : getSelectedFiles())
                    files.addAll(getAllTrainingFiles(file));

                // ... and to the listFiles
                listFiles.setListData(files.toArray());

                // enable some gui components and change label text
                labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
                listDetectors.setEnabled(true);
                buttonStart.setEnabled(true);
                progressBar.setEnabled(true);
            }
        };

        // set behavior of this chooser
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {

            // filter string, only files with this extension and directories will be shown
            private String extension = ".training";

            @Override
            public String getDescription() {
                return this.extension;
            }

            // set filter
            @Override
            public boolean accept(File file) {
                if (file == null) return false;

                if (file.isDirectory()) return true;

                return file.getName().toLowerCase().endsWith(this.extension);
            }
        });

        // return the created chooser
        return chooser;
    }

    /**
     * return all *.training-files within a directory
     * 
     * @param file
     * @return arraylist of files
     */
    private ArrayList<File> getAllTrainingFiles(File file) {
        // initialize list
        ArrayList<File> result = new ArrayList<File>();

        // if given file is a file ...
        if (file.isFile()) {

            // ... and ends with '.training' ....
            if (file.getName().endsWith(".training")) {

                // return it
                result.add(file);
                return result;
            }

            // or if given file is a directory ...
        } else if (file.isDirectory()) {

            // ... check all included files
            for (File includedFiles : file.listFiles()) {
                result.addAll(this.getAllTrainingFiles(includedFiles));
            }

            // return results
            return result;
        }

        // if file is either a file nor a directory, return a empty list
        return result;
    }

    /**
     * the whole plugin information is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
     * 
     * @return a changed default renderer
     */
    private DefaultListCellRenderer initRenderer() {
        return new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // set new displayed attribute
                if (value instanceof PluginInformation) {
                    setText(((PluginInformation) value).getDisplayName());
                    setToolTipText(((PluginInformation) value).getToolTip());
                }

                return this;
            }
        };
    }

    /**
     * reads DataContainers from given file
     * 
     * @param file
     * @return readed DataContainers
     */
    private ArrayList<StorageContainer> readFile(File file) {
        // initialize some variables
        ObjectInputStream inputStream = null;
        Object object;
        ArrayList<StorageContainer> container = new ArrayList<StorageContainer>();

        try {

            // read from file till eof
            inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            while ((object = inputStream.readObject()) != null) {
                if (object instanceof StorageContainer) {
                    container.add((StorageContainer) object);
                }
            }

            // close stream and return readed content
            inputStream.close();
            return container;

        } catch (EOFException eofe) {
            try {

                // close stream and return readed content
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

    /**
     * starts evaluation process
     */
    private void startEvaluation() {
        // initialize variables
        String bestResult = "";
        ArrayList<StorageContainer> containers = new ArrayList<StorageContainer>();
        int i = 1;

        // add selected detectors to array list 
        for (Object selected : this.listDetectors.getSelectedValues())
            if (selected instanceof PluginInformation)
                this.selectedDetectors.add(this.saliencyDetectors.get(((PluginInformation) selected).getId()));

        // read container to get thier number
        for (File file : this.files)
            containers.addAll(this.readFile(file));

        // initialize progress bar
        this.progressBar.setMaximum(containers.size() * this.selectedDetectors.size());
        this.progressBar.setStringPainted(true);

        // run through every file ...
        for (File file : this.files) {

            // ... and through every container in it ...
            for (StorageContainer container : this.readFile(file)) {

                // ... and evry detector
                for (SaliencyDetector detector : this.selectedDetectors) {

                    // process evaluation
                    this.worker.evaluate(file.getName(), detector, container, this.checkBoxImages.isSelected(), file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator + "data" + File.separator)));

                    // update progress bar
                    this.progressBar.setValue(i++);
                    this.progressBar.paint(this.progressBar.getGraphics());

                    if (!this.running) break;
                }
                if (!this.running) break;
            }
            if (!this.running) break;
        }

        // show best result
        bestResult = this.worker.getBestResult(this.checkBoxSummary.isSelected(), this.saliencyDetectors);
        this.labelDescription.setText("Evaluation finished. " + bestResult + " achived the best results.");

        // inidicate finish
        this.selectedDetectors.clear();
        this.finished = true;
        this.buttonStart.setText("Exit");
    }

    /**
     * closes tool cleanly
     */
    private void exit() {
        this.pluginManager.shutdown();
        this.mainFrame.dispose();
        System.exit(0);
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent arg0) {
        this.running = false;
        this.pluginManager.shutdown();
        System.exit(0);
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }
}

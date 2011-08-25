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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.gui.DetectorEvaluationGui;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.worker.DataPackage;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.worker.DataXMLParser;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.worker.EvaluationThread;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class DetectorEvaluator extends DetectorEvaluationGui implements ActionListener,
        WindowListener {

    // coverage, height, width, size, sensitivity

    /** */
    private static DetectorEvaluator main;

    /** selected *.xml files */
    private ArrayList<File> files;

    /** the text detector */
    private SaliencyDetector textDetector;

    /** */
    private EvaluationThread evaluationThread;

    /** */
    private long timestamp;

    /** timestamp from the start calculation */
    private long startTimeStamp;

    /** progress of the progress bar */
    private int progress;

    /** progress of the part progress bar */
    private int progressPart;

    /**
     * manager which handles the detector plugins, statitic plugin, logging
     * plugin ....
     */
    private PluginManager pluginManager;

    /** */
    private boolean running;

    /** */
    private boolean finished;

    /** amount of generated fixations */
    private int amount;

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

        // create and initialize singleton
        DetectorEvaluator.getInstance().init();

        System.out.println("Initializing done.\n");
    }

    /**
     * creates the singleton instance
     * 
     * @return singleton instance
     */
    public static DetectorEvaluator getInstance() {
        if (main == null) main = new DetectorEvaluator();
        return main;
    }

    /**
     * Initializes all necessary variables and sets the window visible.
     */
    private void init() {
        // initialize variables
        this.files = new ArrayList<File>();
        this.evaluationThread = new EvaluationThread();
        this.running = false;
        this.finished = false;
        boolean found = false;
        this.progress = 1;
        this.progressPart = 1;
        this.amount = 0;
        this.startTimeStamp = 0;
        this.labelDescription.setText("");
        this.labelPart.setText("");

        // initialize gui
        this.buttonStart.setEnabled(false);
        this.progressBar.setEnabled(false);
        this.progressBarPart.setEnabled(false);
        this.labelDescription.setEnabled(false);
        this.labelPart.setEnabled(false);
        this.checkBoxDrawImages.setSelected(false);
        this.checkBoxBigSteps.setSelected(true);
        this.checkboxBigStepsActionPerformed();

        // add action listener
        this.buttonRemove.addActionListener(this);
        this.buttonSelect.addActionListener(this);
        this.buttonStart.addActionListener(this);
        this.addWindowListener(this);
        this.checkBoxBigSteps.addActionListener(this);

        // set logging properties
        final JSPFProperties props = new JSPFProperties();
        props.setProperty(PluginManager.class, "cache.enabled", "true");
        props.setProperty(PluginManager.class, "cache.mode", "weak");

        // initialize plugin manager
        this.pluginManager = PluginManagerFactory.createPluginManager(props);

        // add plugins at classpath
        try {
            this.pluginManager.addPluginsFrom(new URI("classpath://*"));
            this.pluginManager.addPluginsFrom(new File("plugins/").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // get text detector
        for (SaliencyDetector detector : new PluginManagerUtil(this.pluginManager).getPlugins(SaliencyDetector.class)) {
            if (detector.getInformation().getDisplayName().equals("Text Detector")) {
                this.textDetector = detector;
                found = true;
                break;
            }
        }
        if (!found) {
            System.err.println("Text Detector not found!");
            System.exit(0);
        }

        // set timestamp
        this.timestamp = System.currentTimeMillis();

        // set visible
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonRemove) {
            this.buttonRemoveActionPerformed();
        } else if (event.getSource() == this.buttonSelect) {
            this.buttonSelectActionPerformed();
        } else if (event.getSource() == this.buttonStart) {
            this.buttonStartActionPerformed();
        } else if (event.getSource() == this.checkBoxBigSteps) {
            this.checkboxBigStepsActionPerformed();
        }
    }

    private void buttonStartActionPerformed() {
        if (this.finished) {
            // close tool
            this.exit();

        } else if (this.running) {
            // stop tool
            this.running = false;
            this.buttonStart.setText("Start");
            this.labelDescription.setText("stoped");
            this.enableGui(true);
            this.progressBar.setValue(0);
            this.progressBarPart.setValue(0);
            this.progress = 1;
            this.progressPart = 1;
            this.timestamp = System.currentTimeMillis();

        } else {

            // initialize variables
            int coverageMin = Integer.parseInt(this.spinnerCoverageMin.getValue().toString());
            int coverageMax = Integer.parseInt(this.spinnerCoverageMax.getValue().toString());
            int heightMin = Integer.parseInt(this.spinnerHeightMin.getValue().toString());
            int heightMax = Integer.parseInt(this.spinnerHeightMax.getValue().toString());
            int widthMin = Integer.parseInt(this.spinnerWidthMin.getValue().toString());
            int widthMax = Integer.parseInt(this.spinnerWidthMax.getValue().toString());
            int lineMin = Integer.parseInt(this.spinnerSizeMin.getValue().toString());
            int lineMax = Integer.parseInt(this.spinnerSizeMax.getValue().toString());
            double sensitivityMin = Double.parseDouble(this.spinnerSensitivityMin.getValue().toString());
            double sensitivityMax = Double.parseDouble(this.spinnerSensitivityMax.getValue().toString());
            this.amount = Integer.parseInt(this.spinnerAmount.getValue().toString());
            long size = 1;
            DataXMLParser dataParser = new DataXMLParser();
            ArrayList<File> tmpFile = new ArrayList<File>(this.files);

            // check values
            if (coverageMax < coverageMin) {
                this.labelDescription.setText("ERROR: coverageMax < coverageMin");
                return;
            } else if (heightMax < heightMin) {
                this.labelDescription.setText("ERROR: heightMax < heightMin");
                return;
            } else if (widthMax < widthMin) {
                this.labelDescription.setText("ERROR: widthMax < widthMin");
                return;
            } else if (lineMax < lineMin) {
                this.labelDescription.setText("ERROR: lineMax < lineMin");
                return;
            } else if (sensitivityMax < sensitivityMin) {
                this.labelDescription.setText("ERROR: sensitivityMax < sensitivityMin");
                return;
            }

            // validate files
            for (File file : this.files) {
                if (!dataParser.isValid(file)) tmpFile.remove(file);
            }
            this.files = tmpFile;

            if (this.files.size() < 1) {
                this.labelDescription.setText("ERROR: no valid files");
                return;
            }

            // calculate size
            if (this.checkBoxBigSteps.isSelected()) size = size * ((coverageMax - coverageMin + 5) / 5);
            else
                size = size * (coverageMax - coverageMin + 1);
            if (this.checkBoxBigSteps.isSelected()) size = size * ((heightMax - heightMin + 5) / 5);
            else
                size = size * (heightMax - heightMin + 1);
            if (this.checkBoxBigSteps.isSelected()) size = size * ((widthMax - widthMin + 5) / 5);
            else
                size = size * (widthMax - widthMin + 1);
            size = size * ((lineMax - lineMin + 10) / 10);
            if (this.checkBoxBigSteps.isSelected()) size = size * (((int) ((sensitivityMax - sensitivityMin + 0.5) * 10)) / 5);
            else
                size = size * ((int) ((sensitivityMax - sensitivityMin + 0.1) * 10));
            size = size * this.amount;
            long sizePart = size;
            long tmp = 0;
            for (File file : this.files) {
                tmp = tmp + dataParser.count(file);
            }
            size = size * tmp;

            if (size > Integer.MAX_VALUE) {
                this.labelDescription.setText("ERROR: to many datasets");
                return;
            }

            // set running
            this.running = true;

            // disable spinner
            this.enableGui(false);

            // reset text
            this.buttonStart.setText("Stop");

            // initialize progress bar
            this.progressBar.setMaximum((int) size);
            this.progressBar.setStringPainted(true);
            this.progressBarPart.setStringPainted(true);

            // create package
            DataPackage data = new DataPackage();
            data.setCoverageMax(coverageMax);
            data.setCoverageMin(coverageMin);
            data.setHeightMax(heightMax);
            data.setHeightMin(heightMin);
            data.setWidthMax(widthMax);
            data.setWidthMin(widthMin);
            data.setLineMax(lineMax);
            data.setLineMin(lineMin);
            data.setSensitivityMax(sensitivityMax);
            data.setSensitivityMin(sensitivityMin);
            data.setTimestamp(this.timestamp);
            data.setFiles(this.files);
            data.setDimension(Integer.parseInt(this.spinnerDimension.getValue().toString()));
            data.setAmount(Integer.parseInt(this.spinnerAmount.getValue().toString()));
            data.setDrawImages(this.checkBoxDrawImages.isSelected());
            data.setBigSteps(this.checkBoxBigSteps.isSelected());
            data.setSize((int) size);
            data.setSizePart((int) sizePart);

            // initialize and start evaluationThread
            this.evaluationThread.init(data, this.textDetector);
            Thread thread = new Thread(this.evaluationThread);
            thread.start();
        }
    }

    /**
     * fired when the checkbox is clicked
     * changes the spinner
     */
    private void checkboxBigStepsActionPerformed() {
        // initialize spinner
        if (this.checkBoxBigSteps.isSelected()) {
            this.spinnerSensitivityMax.setModel(new SpinnerNumberModel(10, 0.0, Double.MAX_VALUE, 1));
            this.spinnerSensitivityMin.setModel(new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 1));
            this.spinnerCoverageMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 10));
            this.spinnerCoverageMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
            this.spinnerHeightMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 10));
            this.spinnerHeightMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
            this.spinnerWidthMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 10));
            this.spinnerWidthMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
            this.spinnerSizeMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 10));
            this.spinnerSizeMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
        } else {
            this.spinnerSensitivityMax.setModel(new SpinnerNumberModel(10, 0.0, Double.MAX_VALUE, 0.1));
            this.spinnerSensitivityMin.setModel(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1));
            this.spinnerCoverageMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 1));
            this.spinnerCoverageMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            this.spinnerHeightMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 1));
            this.spinnerHeightMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            this.spinnerWidthMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 1));
            this.spinnerWidthMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            this.spinnerSizeMax.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 10));
            this.spinnerSizeMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
        }
    }

    /**
     * fired when the buttonSelect is clicked opens the file chooser
     */
    private void buttonSelectActionPerformed() {
        JFileChooser chooser = initChooser();
        chooser.showOpenDialog(null);
    }

    /**
     * fired when the buttonRemove is clicked removes selected files from array
     * list and listFiles
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
     * enable / disable gui elements
     */
    private void enableGui(boolean enable) {
        this.labelCoverage.setEnabled(enable);
        this.labelHeight.setEnabled(enable);
        this.labelMax.setEnabled(enable);
        this.labelMin.setEnabled(enable);
        this.labelSensitivity.setEnabled(enable);
        this.labelSize.setEnabled(enable);
        this.labelWidth.setEnabled(enable);
        this.spinnerCoverageMax.setEnabled(enable);
        this.spinnerCoverageMin.setEnabled(enable);
        this.spinnerHeightMax.setEnabled(enable);
        this.spinnerHeightMin.setEnabled(enable);
        this.spinnerSensitivityMax.setEnabled(enable);
        this.spinnerSensitivityMin.setEnabled(enable);
        this.spinnerSizeMax.setEnabled(enable);
        this.spinnerSizeMin.setEnabled(enable);
        this.spinnerWidthMax.setEnabled(enable);
        this.spinnerWidthMin.setEnabled(enable);
        this.labelAmount.setEnabled(enable);
        this.spinnerAmount.setEnabled(enable);
        this.spinnerDimension.setEnabled(enable);
        this.labelDimension.setEnabled(enable);
        this.buttonRemove.setEnabled(enable);
        this.buttonSelect.setEnabled(enable);
        this.listFiles.setEnabled(enable);
        this.labelDrawImages.setEnabled(enable);
        this.checkBoxDrawImages.setEnabled(enable);
        this.labelBigSteps.setEnabled(enable);
        this.checkBoxBigSteps.setEnabled(enable);
    }

    /**
     * closes tool cleanly
     */
    private void exit() {
        this.pluginManager.shutdown();
        this.dispose();
        System.out.println();
        System.out.println("Application closed.");
        System.exit(0);
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
                // TODO: add regex to filter other XML-files
                for (File allFiles : getSelectedFiles())
                    for (File file : getAllXMLFiles(allFiles))
                        // ignore duplicates
                        if (!files.contains(file)) files.add(file);

                // ... and to the listFiles
                listFiles.setListData(files.toArray());

                // enable some gui components
                buttonStart.setEnabled(true);
                progressBar.setEnabled(true);
                labelDescription.setEnabled(true);
                progressBarPart.setEnabled(true);
                labelPart.setEnabled(true);
            }
        };

        // set behavior of this chooser
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {

            // filter string, only files with this extension and directories
            // will be shown
            private String extension = ".xml";

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
     * return all *.xml-files within a directory
     * 
     * @param file
     * @return arraylist of files
     */
    private ArrayList<File> getAllXMLFiles(File file) {
        // initialize list
        ArrayList<File> result = new ArrayList<File>();

        // if given file is a file ...
        if (file.isFile()) {

            // ... and ends with '.xml' ....
            if (file.getName().endsWith(".xml")) {

                // return it
                result.add(file);
                return result;
            }

            // or if given file is a directory ...
        } else if (file.isDirectory()) {

            // ... check all included files
            for (File includedFiles : file.listFiles()) {
                result.addAll(this.getAllXMLFiles(includedFiles));
            }

            // return results
            return result;
        }

        // if file is either a file nor a directory, return a empty list
        return result;
    }

    /**
     * updates current progress of the progress bar
     */
    public void updateProgressBar() {
        // calculating time remaining
        long passedTime = System.currentTimeMillis() - this.startTimeStamp;
        double timeLeftMs = (((double) passedTime / this.progress) * this.progressBar.getMaximum()) - passedTime;
        String timeLeft = "";

        // test for valuable data
        if (timeLeftMs <= 0) timeLeft = "... calculating";
        else {

            // calculate h,min,s
            long timeLeftH = Math.round((timeLeftMs / (1000 * 60 * 60)) - 0.5);
            timeLeftMs = timeLeftMs - (timeLeftH * 1000 * 60 * 60);
            long timeLeftMin = Math.round(timeLeftMs / (1000 * 60) - 0.5);
            timeLeftMs = timeLeftMs - (timeLeftMin * 1000 * 60);
            long timeLeftS = Math.round((timeLeftMs / 1000) - 0.5);

            // build string
            if (timeLeftH > 0) timeLeft = timeLeft + timeLeftH + "h ";
            if (timeLeftMin > 0) timeLeft = timeLeft + timeLeftMin + "min ";
            if (timeLeftS > 0) timeLeft = timeLeft + timeLeftS + "s";
        }

        // indicate time remaining
        this.labelDescription.setText("estimated time remaining: " + timeLeft);

        // update progress bars
        this.progressBar.setValue(this.progress++);
        //        this.progressBar.paint(this.progressBar.getGraphics());
        this.progressBarPart.setValue(this.progressPart++);
        //        this.progressBarPart.paint(this.progressBarPart.getGraphics());
    }

    /**
     * @param overall
     * @param currentFile
     */
    public void setPart(int overall, String currentFile) {
        this.progressPart = 1;
        this.progressBarPart.setMaximum(overall);
        this.labelPart.setText("current file: " + currentFile);
    }

    /**
     * @param startTimeStamp the startTimeStamp to set
     */
    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    /**
     * finishes the evaluation
     */
    public void finish() {
        // inidicate finish
        this.labelDescription.setText("Evaluation finished.");
        this.labelPart.setText("All files done.");
        // because of multithreading sometimes updates will be not done  
        this.progressBar.setValue(this.progressBar.getMaximum());
        this.progressBarPart.setValue(this.progressBarPart.getMaximum());
        this.finished = true;
        this.buttonStart.setText("Exit");
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return this.running;
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent arg0) {
        this.exit();
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent arg0) {
    }
}

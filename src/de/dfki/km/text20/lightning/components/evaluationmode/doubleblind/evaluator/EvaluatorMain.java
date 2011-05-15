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
package de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.diagnosis.local.Diagnosis;
import net.xeoh.plugins.diagnosis.local.DiagnosisChannel;
import de.dfki.km.augmentedtext.services.language.statistics.Statistics;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.gui.EvaluationWindow;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.plugins.CoverageAnalyser;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.worker.DataXMLParser;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.worker.EvaluationThread;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.worker.EvaluatorWorker;
import de.dfki.km.text20.lightning.diagnosis.channels.tracing.LightningTracer;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class EvaluatorMain extends EvaluationWindow implements ActionListener,
        WindowListener {

    /** singleton instance of this main */
    private static EvaluatorMain evaluatorMain;

    /** selected *.xml files */
    private ArrayList<File> files;

    /** manager which handles the detector plugins, statitic plugin, logging plugin .... */
    private PluginManager pluginManager;

    /** list of available detectors */
    private ArrayList<SaliencyDetector> saliencyDetectors;

    /** list of available detectors */
    private ArrayList<CoverageAnalyser> coverageDetectors;

    /** list of plugin information of the available detectors */
    private ArrayList<PluginInformation> saliencyInformation;

    /** list of plugin information of the available detectors */
    private ArrayList<PluginInformation> coverageInformation;

    /** list of the from listDetectors selected detectors */
    private ArrayList<SaliencyDetector> selectedDetectors;

    /** current choosed coverage analyser */
    private CoverageAnalyser currentAnalyser;

    /** evaluation worker which runs the detectors */
    private EvaluatorWorker worker;

    /** indicates if the tool is running */
    private boolean running;

    /** indicates id the tool is finished */
    private boolean finished;

    /** timestamp from the start of this tool */
    private long currentTimeStamp;

    /** logging channel */
    private DiagnosisChannel<String> channel;

    /** thread in which the evaluation runs */
    private EvaluationThread evaluationThread;

    /** progress of the progress bar*/
    private int progress;

    /** timer for window focus */
    private Timer timer;

    /** frame for configuration guis of plugins */
    private JFrame child;

    /** threshold for text coverage */
    private double treshold;

    /** amount of generated fixations */
    private int amount;

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

        System.out.println("Initializing done.\n");
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
        this.saliencyInformation = new ArrayList<PluginInformation>();
        this.coverageInformation = new ArrayList<PluginInformation>();
        this.selectedDetectors = new ArrayList<SaliencyDetector>();
        this.progress = 1;
        this.amount = 0;

        // add action listeners
        this.buttonSelect.addActionListener(this);
        this.buttonStart.addActionListener(this);
        this.buttonRemove.addActionListener(this);
        this.mainFrame.addWindowListener(this);
        this.checkBoxConfiguration.addActionListener(this);
        this.buttonConfiguration.addActionListener(this);
        this.comboBoxTextPlugin.addActionListener(this);
        this.buttonTextConfig.addActionListener(this);
        this.listDetectors.addListSelectionListener(new ListSelectionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (listDetectors.getSelectedValue() instanceof PluginInformation)
                    buttonConfiguration.setEnabled(((PluginInformation) listDetectors.getSelectedValue()).isGuiAvailable() && checkBoxConfiguration.isSelected());
            }
        });

        // set enable/disable and text to some components
        this.buttonStart.setEnabled(false);
        this.buttonStart.setText("Start");
        this.labelDescription.setText("Step 1: Select *_data.xml files.");
        this.checkBoxImages.setSelected(true);
        this.checkBoxSummary.setSelected(true);
        this.buttonStart.setEnabled(false);
        this.listDetectors.setEnabled(false);
        this.progressBar.setEnabled(false);
        this.checkBoxConfiguration.setEnabled(false);
        this.buttonConfiguration.setEnabled(false);

        // initialize status
        this.running = false;
        this.finished = false;

        // get timestamp
        this.currentTimeStamp = System.currentTimeMillis();

        // set logging properties
        final JSPFProperties props = new JSPFProperties();
        props.setProperty(Diagnosis.class, "recording.enabled", "true");
        props.setProperty(Diagnosis.class, "recording.file", "diagnosis.record");
        props.setProperty(Diagnosis.class, "recording.format", "java/serialization");
        props.setProperty(Diagnosis.class, "analysis.stacktraces.enabled", "true");
        props.setProperty(Diagnosis.class, "analysis.stacktraces.depth", "10000");
        props.setProperty(PluginManager.class, "cache.enabled", "true");
        props.setProperty(PluginManager.class, "cache.mode", "weak");

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
            this.saliencyInformation.add(this.saliencyDetectors.get(i).getInformation());
        }

        // initialize coverage analyzer
        this.coverageDetectors = new ArrayList<CoverageAnalyser>(new PluginManagerUtil(this.pluginManager).getPlugins(CoverageAnalyser.class));
        for (int i = 0; i < this.coverageDetectors.size(); i++) {
            this.coverageDetectors.get(i).getInformation().setId(i);
            this.coverageInformation.add(this.coverageDetectors.get(i).getInformation());
        }

        // create new worker and channel
        this.channel = this.pluginManager.getPlugin(Diagnosis.class).channel(LightningTracer.class);
        this.worker = new EvaluatorWorker(this, this.currentTimeStamp, this.channel);

        // initialize coverage combobox
        for (CoverageAnalyser analyzer : this.coverageDetectors)
            this.comboBoxTextPlugin.addItem(analyzer.getInformation());
        this.comboBoxTextPlugin.setRenderer(new DefaultListCellRenderer() {

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
        });

        // initialize coverage configuration button
        this.buttonTextConfig.setEnabled(((PluginInformation) this.comboBoxTextPlugin.getSelectedItem()).isGuiAvailable());

        // initialize analyzer
        this.currentAnalyser = this.coverageDetectors.get(((PluginInformation) this.comboBoxTextPlugin.getSelectedItem()).getId());

        // initialize coverage spinner
        this.treshold = 15;
        this.spinnerThresh.setModel(new SpinnerNumberModel(this.treshold, 0, 100, 0.1));

        // initialize listDetectors
        this.listDetectors.setListData(this.saliencyInformation.toArray());
        this.listDetectors.setCellRenderer(this.initRenderer());

        // initialize evaluation evaluationThread
        this.evaluationThread = new EvaluationThread();

        // log start
        this.channel.status("Session started.");

        // initialize timer and child
        this.child = null;
        this.timer = new Timer(500, new ActionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (child.isShowing()) return;
                timer.stop();
                mainFrame.setEnabled(true);
                mainFrame.requestFocus();
            }
        });

        // set the window visible
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

        if (event.getSource() == this.checkBoxConfiguration) {
            this.checkboxConfigActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonConfiguration) {
            this.buttonConfigActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonTextConfig) {
            this.buttonTextConfigActionPerformed();
            return;
        }

        if (event.getSource() == this.comboBoxTextPlugin) {
            this.comboboxAnalyzerActionPerformed();
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
            this.buttonStart.setText("Stop");
            this.buttonRemove.setEnabled(false);
            this.buttonSelect.setEnabled(false);
            this.listDetectors.setEnabled(false);
            this.listFiles.setEnabled(false);
            this.checkBoxImages.setEnabled(false);
            this.checkBoxSummary.setEnabled(false);
            this.labelDimension.setEnabled(false);
            this.spinnerDimension.setEnabled(false);
            this.checkBoxConfiguration.setEnabled(false);
            this.buttonConfiguration.setEnabled(false);
            this.comboBoxTextPlugin.setEnabled(false);
            this.buttonTextConfig.setEnabled(false);
            this.labelThresh.setEnabled(false);
            this.spinnerThresh.setEnabled(false);

            // start evaluation
            this.startEvaluation();

            // or if the tool is running ...
        } else {

            // ... reset to startable state
            this.evaluationThread.stop();
            this.running = false;
            this.progressBar.setValue(0);
            this.labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
            this.buttonStart.setText("Start");
            this.buttonRemove.setEnabled(true);
            this.buttonSelect.setEnabled(true);
            this.listDetectors.setEnabled(true);
            this.listFiles.setEnabled(true);
            this.checkBoxImages.setEnabled(true);
            this.checkBoxSummary.setEnabled(true);
            this.labelDimension.setEnabled(true);
            this.spinnerDimension.setEnabled(true);
            this.checkBoxConfiguration.setEnabled(true);
            this.buttonConfiguration.setEnabled(true);
            this.comboBoxTextPlugin.setEnabled(true);
            this.buttonTextConfig.setEnabled(true);
            this.labelThresh.setEnabled(true);
            this.spinnerThresh.setEnabled(true);
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
     * fired when the checkboxConfig is clicked
     * sets the gui in config mode
     */
    private void checkboxConfigActionPerformed() {
        this.listDetectors.clearSelection();

        // set selection mode
        if (this.checkBoxConfiguration.isSelected()) {
            this.listDetectors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.labelDescription.setText("ConfigMode: Select any detector and click the 'Configuration'-button.");
        } else {
            this.listDetectors.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            this.labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
        }

        // enable/disable elements
        this.buttonRemove.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.buttonSelect.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.listFiles.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.checkBoxImages.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.checkBoxSummary.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.labelDimension.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.spinnerDimension.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.comboBoxTextPlugin.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.buttonTextConfig.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.labelThresh.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.spinnerThresh.setEnabled(!this.checkBoxConfiguration.isSelected());
        this.buttonConfiguration.setEnabled(!this.checkBoxConfiguration.isSelected());
    }

    /**
     * fired when the buttonConfig is clicked
     * opens the gui for selected detectors
     */
    private void buttonConfigActionPerformed() {
        if (this.listDetectors.getSelectedValue() instanceof PluginInformation)
            this.child = this.saliencyDetectors.get(((PluginInformation) this.listDetectors.getSelectedValue()).getId()).getGui();
        this.child.setVisible(true);
        this.mainFrame.setEnabled(false);
        this.timer.start();
    }

    /**
     * fired when the buttonConfig is clicked
     * opens the gui for coverage analyzer
     */
    private void buttonTextConfigActionPerformed() {
        if (this.comboBoxTextPlugin.getSelectedItem() instanceof PluginInformation)
            this.child = this.coverageDetectors.get(((PluginInformation) this.comboBoxTextPlugin.getSelectedItem()).getId()).getGui();
        this.child.setVisible(true);
        this.mainFrame.setEnabled(false);
        this.timer.start();
    }

    /**
     * fired when the selection of the analyzer combobox is changed
     * enables/disables config button and changes currentAnalyser
     */
    private void comboboxAnalyzerActionPerformed() {
        // initialize coverage configuration button
        this.buttonTextConfig.setEnabled(((PluginInformation) this.comboBoxTextPlugin.getSelectedItem()).isGuiAvailable());

        // initialize analyzer
        this.currentAnalyser = this.coverageDetectors.get(((PluginInformation) this.comboBoxTextPlugin.getSelectedItem()).getId());
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

                // enable some gui components and change label text
                labelDescription.setText("Step 2: Select the detectors you want to use and press 'Start'.");
                listDetectors.setEnabled(true);
                buttonStart.setEnabled(true);
                progressBar.setEnabled(true);
                checkBoxConfiguration.setEnabled(true);
            }
        };

        // set behavior of this chooser
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {

            // filter string, only files with this extension and directories will be shown
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
     * the whole plugin saliencyInformation is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
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
     * starts evaluation process
     */
    private void startEvaluation() {
        // initialize variables
        DataXMLParser dataParser = new DataXMLParser();
        ArrayList<File> tmp = new ArrayList<File>(this.files);
        int size = 0;

        // set threshold and amount
        this.treshold = Double.parseDouble(this.spinnerThresh.getValue().toString());
        this.amount = Integer.parseInt(this.spinnerAmount.getValue().toString());

        // start coverage analyzer
        this.currentAnalyser.start();

        // add selected detectors to array list 
        for (Object selected : this.listDetectors.getSelectedValues())
            if (selected instanceof PluginInformation)
                this.selectedDetectors.add(this.saliencyDetectors.get(((PluginInformation) selected).getId()));

        // kill pupil files
        for (File file : this.files) {
            if (file.getAbsolutePath().endsWith("_pupils.xml")) tmp.remove(file);
        }
        this.files = new ArrayList<File>(tmp);

        // validate files
        for (File file : this.files) {
            if (!dataParser.isValid(file)) tmp.remove(file);
        }
        this.files = tmp;

        // calculate size
        for (File file : this.files)
            size = size + dataParser.count(file);
        size = size * this.selectedDetectors.size() * this.amount;

        // initialize progress bar
        this.progressBar.setMaximum(size);
        this.progressBar.setStringPainted(true);

        // initialize and start evaluationThread
        this.evaluationThread.init(this);
        Thread thread = new Thread(this.evaluationThread);
        thread.start();
    }

    /**
     * finishes the evaluation
     */
    public void finish() {
        // show best result
        String bestResult = this.worker.getBestResult(this.saliencyDetectors);
        this.labelDescription.setText("Evaluation finished. " + bestResult + " achived the best results.");
        System.out.println("Evaluation finished. Best results for " + bestResult + ".\n");

        // inidicate finish
        this.selectedDetectors.clear();
        this.finished = true;
        this.buttonStart.setText("Exit");
    }

    /**
     * updates current progress of the progress bar
     */
    public void updateProgressBar() {
        this.progressBar.setValue(this.progress++);
        this.progressBar.paint(this.progressBar.getGraphics());
    }

    /**
     * closes tool cleanly
     */
    private void exit() {
        this.currentAnalyser.stop();
        this.pluginManager.shutdown();
        this.evaluationThread.stop();
        this.mainFrame.dispose();
        System.out.println("Application closed.");
        System.exit(0);
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
        if (this.running) this.currentAnalyser.stop();
        this.running = false;
        this.pluginManager.shutdown();
        this.evaluationThread.stop();
        System.out.println("Application closed.");
        System.exit(0);
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

    /**
     * @return the files
     */
    public ArrayList<File> getFiles() {
        return this.files;
    }

    /**
     * @return the selectedDetectors
     */
    public ArrayList<SaliencyDetector> getSelectedDetectors() {
        return this.selectedDetectors;
    }

    /**
     * @return the worker
     */
    public EvaluatorWorker getWorker() {
        return this.worker;
    }

    /**    
     * @return true if they should be written
     */
    public boolean writeImages() {
        return this.checkBoxImages.isSelected();
    }

    /**    
     * @return true if it should be written
     */
    public boolean writeLog() {
        return this.checkBoxSummary.isSelected();
    }

    /**      
     * @return the dimension
     */
    public int getDimension() {
        return Integer.parseInt(this.spinnerDimension.getValue().toString());
    }

    /**
     * @return current choosed analyser
     */
    public CoverageAnalyser getCoverageAnalyser() {
        return this.currentAnalyser;
    }

    /**
     * @return current threshold
     */
    public double getCoverageThreshold() {
        return this.treshold;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return this.amount;
    }
}

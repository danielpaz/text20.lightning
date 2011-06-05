/*
 * LiveConfigGuiImpl.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.LiveMode;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.WordXMLParser;
import de.dfki.km.text20.lightning.plugins.PluginInformation;

/**
 * @author Christoph Käding
 * 
 */
@SuppressWarnings("serial")
public class LiveConfigGuiImpl extends LiveConfigGui implements ActionListener,
        WindowListener {

    /** */
    private LiveMode liveMode;

    /** */
    private ArrayList<File> files;

    /** */
    private String currentFile;

    /** */
    private ArrayList<String> currentWords;

    /** */
    private boolean autoSelect;

    /** frame for configuration guis of plugins */
    private JFrame child;

    /** timer for window focus */
    private Timer timer;

    /** id of algorithm one */
    private int one;

    /** id of algorithm two */
    private int two;

    /**
     * creates instance and initializes some stuff
     * 
     * @param liveMode
     */
    @SuppressWarnings("boxing")
    public LiveConfigGuiImpl(LiveMode liveMode) {
        // initialize variables
        this.liveMode = liveMode;
        this.files = new ArrayList<File>();
        this.currentWords = new ArrayList<String>();
        this.child = null;
        this.currentFile = "";

        // handle to few detectors
        if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size() < 2) {
            this.buttonCancel.addActionListener(this);
            return;
        }

        // preselect values
        for (int i = 0; i < MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size(); i++)
            if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation().getDisplayName().equals("Text Detector"))
                this.one = i;

        for (int i = 0; i < MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size(); i++)
            if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation().getDisplayName().equals("Dummy Filter"))
                this.two = i;

        // initialize comboboxes
        this.comboBoxOne.setRenderer(this.detectorRenderer());
        this.comboBoxTwo.setRenderer(this.detectorRenderer());
        this.comboBoxActionPerformed();
        this.buttonTwoConfig.setEnabled(((PluginInformation) this.comboBoxTwo.getSelectedItem()).isGuiAvailable());
        this.buttonOneConfig.setEnabled(((PluginInformation) this.comboBoxOne.getSelectedItem()).isGuiAvailable());

        // add action listener
        this.buttonCancel.addActionListener(this);
        this.buttonOK.addActionListener(this);
        this.buttonRemove.addActionListener(this);
        this.buttonSelect.addActionListener(this);
        this.comboBoxOne.addActionListener(this);
        this.comboBoxTwo.addActionListener(this);
        this.buttonOneConfig.addActionListener(this);
        this.buttonTwoConfig.addActionListener(this);
        this.addWindowListener(this);

        // initialize timer
        this.timer = new Timer(500, new ActionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (child.isShowing()) return;
                timer.stop();
                setEnabled(true);
                requestFocus();
            }
        });

        // preselect steps
        this.spinnerSteps.setValue(25);
        
        // show gui
        this.setVisible(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (this.autoSelect) {
            return;
        } else if (event.getSource() == this.buttonCancel) {
            this.buttonCancelActionPerformed();
        } else if (event.getSource() == this.buttonOK) {
            this.buttonOKActionPerformed();
        } else if (event.getSource() == this.buttonRemove) {
            this.buttonRemoveActionPerformed();
        } else if (event.getSource() == this.buttonSelect) {
            this.buttonSelectActionPerformed();
        } else if (event.getSource() == this.comboBoxOne) {
            this.one = ((PluginInformation) this.comboBoxOne.getSelectedItem()).getId();
            this.buttonOneConfig.setEnabled(((PluginInformation) this.comboBoxOne.getSelectedItem()).isGuiAvailable());
            this.comboBoxActionPerformed();
        } else if (event.getSource() == this.buttonOneConfig) {
            this.buttonOneConfigActionPerformed();
        } else if (event.getSource() == this.comboBoxTwo) {
            this.two = ((PluginInformation) this.comboBoxTwo.getSelectedItem()).getId();
            this.buttonTwoConfig.setEnabled(((PluginInformation) this.comboBoxTwo.getSelectedItem()).isGuiAvailable());
            this.comboBoxActionPerformed();
        } else if (event.getSource() == this.buttonTwoConfig) {
            this.buttonTwoConfigActionPerformed();
        }
    }

    /**
     * fired when the buttonOK is clicked starts mode
     */
    private void buttonOKActionPerformed() {
        // initialize variables
        WordXMLParser parser = new WordXMLParser();
        int size = 0;

        // run through all files
        for (File file : this.files) {

            // validate file
            if (parser.isValid(file)) {

                // read file
                parser.readFile(this, file);

                // check if variables are updated by parser
                if (!this.currentFile.equals("")) {

                    // add data
                    this.liveMode.addToData(this.currentFile, this.currentWords);

                    // reset data
                    this.currentFile = "";
                    this.currentWords.clear();
                    size++;
                }
            }
        }

        // test if some valid data is added
        if (size == 0) return;

        // set other stuff
        this.liveMode.setSteps(Integer.parseInt(this.spinnerSteps.getValue().toString()));
        this.liveMode.setOne(this.one);
        this.liveMode.setTwo(this.two);

        // close config
        this.dispose();

        // start quickness evaluation
        this.liveMode.startTraining();
    }

    private void comboBoxActionPerformed() {
        this.autoSelect = true;

        // clean up
        this.comboBoxOne.removeAllItems();
        this.comboBoxTwo.removeAllItems();

        // combobox one
        for (int i = 0; i < MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size(); i++) {
            if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation().getId() != this.two)
                this.comboBoxOne.addItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation());
        }
        this.comboBoxOne.setSelectedItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(this.one).getInformation());

        // combobox two
        for (int i = 0; i < MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size(); i++) {
            if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation().getId() != this.one)
                this.comboBoxTwo.addItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation());
        }
        this.comboBoxTwo.setSelectedItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(this.two).getInformation());

        this.autoSelect = false;
    }

    /**
     * Fired if the Config button one is clicked. Shows the configdialog.
     */
    private void buttonOneConfigActionPerformed() {
        this.child = MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(((PluginInformation) this.comboBoxOne.getSelectedItem()).getId()).getGui();
        this.child.setVisible(true);
        this.setEnabled(false);
        this.timer.start();
    }

    /**
     * Fired if the Config button two is clicked. Shows the configdialog.
     */
    private void buttonTwoConfigActionPerformed() {
        this.child = MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(((PluginInformation) this.comboBoxTwo.getSelectedItem()).getId()).getGui();
        this.child.setVisible(true);
        this.setEnabled(false);
        this.timer.start();
    }

    /**
     * fired when the buttonCancel is clicked closes gui
     */
    private void buttonCancelActionPerformed() {
        this.dispose();
        MainClass.getInstance().toggleMode();
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
     * @param currentFile
     *            the currentFile to set
     */
    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    /**
     * @param currentWords
     *            the currentWords to set
     */
    public void setCurrentWords(ArrayList<String> currentWords) {
        this.currentWords = currentWords;
    }

    /**
     * the whole plugin information is added to the combobox, so here the
     * displayname is changed from .toString() to .getDisplayName()
     * 
     * @return a changed default renderer
     */
    private DefaultListCellRenderer detectorRenderer() {
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

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent e) {
        MainClass.getInstance().toggleMode();
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent e) {
    }
}

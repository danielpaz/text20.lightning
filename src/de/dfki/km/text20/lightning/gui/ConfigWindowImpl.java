/*
 * ConfigWindowImpl.java
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
package de.dfki.km.text20.lightning.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.hotkey.Hotkey;
import de.dfki.km.text20.lightning.hotkey.HotkeyContainer;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.mousewarp.MouseWarper;

/**
 * This is the configuration window which is shown after a click on the 'configuration' button of the tray menu. 
 * Here are all the things shown, which are important and should be changeable.
 * 
 * @author Christoph Käding
 *
 */
@SuppressWarnings({ "serial", "boxing" })
public class ConfigWindowImpl extends ConfigWindow implements ActionListener,
        WindowListener {

    /**
     * manageHotkeyComboBox() changes the items and selection of the actionHotkey and the statusHotkey comboboxes.
     * These two boxes have their own actionlisteners which call manageHotkeyComboBox(). So an endless ring of calls is created when anything is changed.
     * This boolean shows if the action event is fired by an automatic or manual selection of these comboboxes.
     */
    private boolean autoSelect;

    /** The method manager is needed to display and change the available plugins. */
    private InternalPluginManager internalPluginManager;

    /** stores configuration of this tool */
    private Properties properties;

    /** renderer to show displaynames in comboboxes */
    private DefaultListCellRenderer renderer;

    /** instance of the mainclass */
    private MainClass main;

    /** file chooser for output path */
    private JFileChooser chooser;

    /** frame for configuration guis of plugins */
    private JFrame child;

    /** */
    private WindowListener childWindowListener;

    /**
     * builds the window with all its components and shows it
     */
    public ConfigWindowImpl() {
        // initialize variables
        this.main = MainClass.getInstance();
        this.internalPluginManager = this.main.getInternalPluginManager();
        this.properties = this.main.getProperties();

        // take values of global properties and preselect them
        this.spinnerDimension.setValue(this.properties.getDimension());
        this.buttonSubmit.setEnabled(!this.main.isSubmitted());
        this.checkBoxRecalibration.setSelected(this.properties.isRecalibration());

        // initialize renderer of comboboxes
        this.renderer = initRenderer();

        // manage comboboxes
        this.manageHotkeyComboBox();
        this.manageComboBoxSaliencyDetector();

        // manage warp config
        this.manageWarpConfig();

        // add action listener
        this.buttonOK.addActionListener(this);
        this.buttonCancel.addActionListener(this);
        this.buttonDefault.addActionListener(this);
        this.buttonSubmit.addActionListener(this);
        this.comboBoxActionHotkey.addActionListener(this);
        this.comboBoxStatusHotkey.addActionListener(this);
        this.checkBoxUseWarp.addActionListener(this);
        this.checkBoxEvaluation.addActionListener(this);
        this.buttonDetectorConfig.addActionListener(this);
        this.buttonWarpConfig.addActionListener(this);
        this.comboBoxWarpMethod.addActionListener(this);
        this.comboBoxDetector.addActionListener(this);
        this.mainFrame.addWindowListener(this);
        this.buttonClearRecalibration.addActionListener(this);
        this.buttonSelect.addActionListener(this);

        // initialize checkbox
        this.checkBoxEvaluation.setSelected(!this.main.isNormalMode());
        this.checkBockEvaluationActionPerformed();
        this.checkBoxSound.setSelected(this.properties.isSoundActivated());

        // initializes tooltips
        this.manageToolTips();

        // initialize current evaluation settings
        this.textFieldOutputPath.setText(new File(this.main.getEvaluationSettings()[1]).getAbsolutePath());
        for (String option : this.main.getEvaluationOptions())
            this.comboBoxMode.addItem(option);
        this.comboBoxMode.setSelectedItem(this.main.getEvaluationSettings()[0]);

        // initialize file chooser
        this.chooser = new JFileChooser() {
            @SuppressWarnings("unqualified-field-access")
            public void approveSelection() {
                if (getSelectedFile().isFile()) return;
                super.approveSelection();
                textFieldOutputPath.setText(this.getSelectedFile().getAbsolutePath());
            }
        };
        this.chooser.setMultiSelectionEnabled(false);
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // initialize child
        this.child = null;
        this.childWindowListener = initChildWindowListener();

        // show the gui
        this.mainFrame.repaint();
        this.mainFrame.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    /**
     * catches the action events from gui and starts the associated action
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        // check if the event is fired by a automatic selection
        if (this.autoSelect) return;

        // check which source occurs the action event and start a handle function
        if (event.getSource() == this.buttonOK) {
            this.buttonOKActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonCancel) {
            this.buttonCancelActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonDefault) {
            this.buttonDefaultActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonSubmit) {
            this.buttonSubmitActionPerformed();
            return;
        }

        if (event.getSource() == this.comboBoxActionHotkey) {
            this.comboBoxActionHotkeyActionPerformed();
            return;
        }

        if (event.getSource() == this.comboBoxStatusHotkey) {
            this.comboBoxStatusHotkeyActionPerformed();
            return;
        }

        if (event.getSource() == this.checkBoxUseWarp) {
            this.checkBoxUseWarpActionPerformed();
            return;
        }

        if (event.getSource() == this.checkBoxEvaluation) {
            this.checkBockEvaluationActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonWarpConfig) {
            this.buttonWarpConfigActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonDetectorConfig) {
            this.buttonDetectorConfigActionPerformed();
            return;
        }

        if (event.getSource() == this.comboBoxDetector) {
            this.comboBoxDetectorActionPerformed();
            return;
        }

        if (event.getSource() == this.comboBoxWarpMethod) {
            this.comboBoxWarpMethodActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonClearRecalibration) {
            this.buttonClearActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonSelect) {
            this.buttonSelectActionPerformed();
            return;
        }
    }

    /**
     * Fired if the OK button is clicked. All changes were applied.
     */
    protected void buttonOKActionPerformed() {
        // initialize temporary variables
        String[] settings = { this.comboBoxMode.getSelectedItem().toString(), this.textFieldOutputPath.getText() };

        // change variables in properties and in method manager
        this.properties.setDimension(Integer.parseInt(this.spinnerDimension.getValue().toString()));
        this.properties.setUseWarp(this.checkBoxUseWarp.isSelected());
        this.properties.setSoundActivated(this.checkBoxSound.isSelected());
        this.properties.setRecalibration(this.checkBoxRecalibration.isSelected());
        Hotkey.getInstance().setHotkey(1, ((HotkeyContainer) this.comboBoxActionHotkey.getSelectedItem()), true);
        Hotkey.getInstance().setHotkey(2, ((HotkeyContainer) this.comboBoxStatusHotkey.getSelectedItem()), true);

        // set new plugins
        this.internalPluginManager.setCurrentSaliencyDetector(((PluginInformation) this.comboBoxDetector.getSelectedItem()).getId());
        this.internalPluginManager.setCurrentMouseWarper(((PluginInformation) this.comboBoxWarpMethod.getSelectedItem()).getId());

        // set evaluation settings
        this.main.setEvaluationSettings(settings);

        // refresh warper and plugins
        this.main.refreshWarper();

        // set mode
        if (this.main.isNormalMode() && this.checkBoxEvaluation.isSelected())
            this.main.toggleMode();
        if (!this.main.isNormalMode() && !this.checkBoxEvaluation.isSelected())
            this.main.toggleMode();

        // update statistics
        this.main.addToStatistic("settings changed");
        this.main.setupStatistics();

        // reset evaluator
        if (this.checkBoxEvaluation.isSelected()) this.main.resetEvaluator();

        // close the gui
        this.mainFrame.dispose();
    }

    /**
     * Fired if the Cancel button is clicked. Closes the window.
     */
    private void buttonSubmitActionPerformed() {
        this.buttonSubmit.setEnabled(false);
        this.main.setSubmitted(true);
        this.main.publishStatistics();
    }

    /**
     * Fired if the Cancel button is clicked. Closes the window.
     */
    private void buttonCancelActionPerformed() {
        // close the window
        this.mainFrame.dispose();

        // reset temporary keys
        Hotkey.getInstance().resetTmpKeys();
    }

    /**
     * Fired if the clear calibration button is clicked.
     */
    private void buttonClearActionPerformed() {
        // clear calibration
        this.main.getRecalibrator().clearRecalibration();

        // deactivate button
        this.buttonClearRecalibration.setEnabled(false);
    }

    /**
     * manages the visibility of the configbutton
     */
    private void comboBoxDetectorActionPerformed() {
        //        this.buttonDetectorConfig.setText(((PluginInformation) this.comboBoxDetector.getSelectedItem()).getDisplayName() + " Configuration");
        this.buttonDetectorConfig.setEnabled(((PluginInformation) this.comboBoxDetector.getSelectedItem()).isGuiAvailable());
    }

    /**
     * manages the visibility of the configbutton
     */
    private void comboBoxWarpMethodActionPerformed() {
        //        this.buttonWarpConfig.setText(((PluginInformation) this.comboBoxWarpMethod.getSelectedItem()).getDisplayName() + " Configuration");
        this.buttonWarpConfig.setEnabled(((PluginInformation) this.comboBoxWarpMethod.getSelectedItem()).isGuiAvailable());
    }

    /**
     * Fired if the Detector Config button is clicked. Shows the configdialog.
     */
    private void buttonDetectorConfigActionPerformed() {
        this.child = this.internalPluginManager.getSaliencyDetectors().get(((PluginInformation) this.comboBoxDetector.getSelectedItem()).getId()).getGui();
        this.child.addWindowListener(this.childWindowListener);
        this.child.setVisible(true);
        this.mainFrame.setEnabled(false);
    }

    /**
     * Fired if the Warper Config button is clicked. Shows the configdialog.
     */
    private void buttonWarpConfigActionPerformed() {
        this.child = this.internalPluginManager.getMouseWarpers().get(((PluginInformation) this.comboBoxWarpMethod.getSelectedItem()).getId()).getGui();
        this.child.addWindowListener(this.childWindowListener);
        this.child.setVisible(true);
        this.mainFrame.setEnabled(false);
    }

    /**
     * restores default values
     */
    private void buttonDefaultActionPerformed() {
        // restore default values 
        this.properties.restoreDefault();

        // take values of global properties and preselect them
        this.spinnerDimension.setValue(this.properties.getDimension());
        this.checkBoxSound.setSelected(this.properties.isSoundActivated());

        // make hotkey notifying the change
        Hotkey.getInstance().resetTmpKeys();
        Hotkey.getInstance().getCurrentHotkey(1, true);
        Hotkey.getInstance().getCurrentHotkey(2, true);

        // manage comboboxes
        this.manageHotkeyComboBox();
        this.manageComboBoxSaliencyDetector();

        // manage warp config
        this.manageWarpConfig();
    }

    /**
     * Fired if something changed at the actionhotkey combobox. Sets the choosed hotkey as actionhotkey.
     */
    private void comboBoxActionHotkeyActionPerformed() {
        Hotkey.getInstance().setHotkey(1, ((HotkeyContainer) this.comboBoxActionHotkey.getSelectedItem()), false);
        this.manageHotkeyComboBox();
    }

    /**
     * fired if the checkbock evaluation is selected
     */
    private void checkBockEvaluationActionPerformed() {
        // change enable status of some components
        this.checkBoxUseWarp.setEnabled(!this.checkBoxEvaluation.isSelected());
        this.labelDetector.setEnabled(!this.checkBoxEvaluation.isSelected());
        this.comboBoxDetector.setEnabled(!this.checkBoxEvaluation.isSelected());
        if (!this.checkBoxEvaluation.isSelected() && this.internalPluginManager.getCurrentSaliencyDetector().getInformation().isGuiAvailable()) this.buttonDetectorConfig.setEnabled(true);
        else
            this.buttonDetectorConfig.setEnabled(false);
        this.labelOutputPath.setEnabled(this.checkBoxEvaluation.isSelected());
        this.buttonSelect.setEnabled(this.checkBoxEvaluation.isSelected());
        this.labelMode.setEnabled(this.checkBoxEvaluation.isSelected());
        this.comboBoxMode.setEnabled(this.checkBoxEvaluation.isSelected());
        this.textFieldOutputPath.setEnabled(this.checkBoxEvaluation.isSelected());
        this.labelEnableMouseWarp.setEnabled(!this.checkBoxEvaluation.isSelected());
        this.checkBoxUseWarpActionPerformed();
        if (!this.checkBoxEvaluation.isSelected() && this.checkBoxUseWarp.isSelected()) this.enableWarpConfig(true);
        else
            this.enableWarpConfig(false);
        this.buttonSubmit.setEnabled(!this.checkBoxEvaluation.isSelected());
        if (!this.checkBoxEvaluation.isSelected()) {
            this.buttonOK.setText("OK");
        } else {
            this.buttonOK.setText("Start");
        }

    }

    /**
     * Fired if the Select button is clicked. Shows the file chooser.
     */
    private void buttonSelectActionPerformed() {
        this.chooser.showOpenDialog(this.mainFrame);
    }

    /**
     * Fired if something changed at the statushotkey combobox. Sets the choosed hotkey as statushotkey.
     */
    private void comboBoxStatusHotkeyActionPerformed() {
        Hotkey.getInstance().setHotkey(2, ((HotkeyContainer) this.comboBoxStatusHotkey.getSelectedItem()), false);
        this.manageHotkeyComboBox();
    }

    /**
     * enables or disables config
     */
    private void checkBoxUseWarpActionPerformed() {
        this.enableWarpConfig(this.checkBoxUseWarp.isSelected());
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
     * manages the hotkeys which are available for the different functions to avoid a doubled selection of one hotkey to more than one function
     */
    private void manageHotkeyComboBox() {
        this.autoSelect = true;

        // clean comboboxes
        this.comboBoxActionHotkey.removeAllItems();
        this.comboBoxStatusHotkey.removeAllItems();

        // adds all hotkeys to the specific comboboxes which are not selected by the other function
        for (int i = 0; i < Hotkey.getInstance().getHotkeys().size(); i++) {
            if (!Hotkey.getInstance().getHotkeys().get(i).toString().equals(Hotkey.getInstance().getCurrentHotkey(2, false).toString()))
                this.comboBoxActionHotkey.addItem(Hotkey.getInstance().getHotkeys().get(i));
        }
        for (int i = 0; i < Hotkey.getInstance().getHotkeys().size(); i++) {
            if (!Hotkey.getInstance().getHotkeys().get(i).toString().equals(Hotkey.getInstance().getCurrentHotkey(1, false).toString()))
                this.comboBoxStatusHotkey.addItem(Hotkey.getInstance().getHotkeys().get(i));
        }

        // preselect property values
        for (int i = 0; i < this.comboBoxActionHotkey.getItemCount(); i++) {
            if (Hotkey.getInstance().getCurrentHotkey(1, false).toString().equals(this.comboBoxActionHotkey.getItemAt(i).toString())) {
                this.comboBoxActionHotkey.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < this.comboBoxStatusHotkey.getItemCount(); i++) {
            if (Hotkey.getInstance().getCurrentHotkey(2, false).toString().equals(this.comboBoxStatusHotkey.getItemAt(i).toString())) {
                this.comboBoxStatusHotkey.setSelectedIndex(i);
            }
        }

        this.autoSelect = false;
    }

    /**
     * Takes the list of available plugins and shows them in the combobox.
     */
    private void manageComboBoxSaliencyDetector() {
        // remove content
        this.autoSelect = true;
        this.comboBoxDetector.removeAllItems();
        this.autoSelect = false;

        // add all saliency detectors to the combobox
        for (int i = 0; i < this.internalPluginManager.getSaliencyDetectors().size(); i++) {
            this.comboBoxDetector.addItem(this.internalPluginManager.getSaliencyDetectors().get(i).getInformation());
        }

        // preselect the current one
        if (this.internalPluginManager.getCurrentSaliencyDetector() != null)
            this.comboBoxDetector.setSelectedItem(this.internalPluginManager.getCurrentSaliencyDetector().getInformation());

        // set renderer
        this.comboBoxDetector.setRenderer(this.renderer);

        // initialize button
        //        this.buttonDetectorConfig.setText(this.internalPluginManager.getCurrentSaliencyDetector().getInformation().getDisplayName() + " Configuration");
        this.buttonDetectorConfig.setEnabled(this.internalPluginManager.getCurrentSaliencyDetector().getInformation().isGuiAvailable());
    }

    /**
     * manages values of the spinners and comboBox which are used to configure the mouse warping
     */
    private void manageWarpConfig() {
        // preselect values
        this.checkBoxUseWarp.setSelected(this.properties.isUseWarp());

        // remove values
        this.autoSelect = true;
        this.comboBoxWarpMethod.removeAllItems();
        this.autoSelect = false;

        // manage combobox
        for (MouseWarper warper : this.internalPluginManager.getMouseWarpers())
            this.comboBoxWarpMethod.addItem(warper.getInformation());

        // preselect current mouse warper
        if (this.internalPluginManager.getCurrentMouseWarper() != null)
            this.comboBoxWarpMethod.setSelectedItem(this.internalPluginManager.getCurrentMouseWarper().getInformation());

        // set renderer
        this.comboBoxWarpMethod.setRenderer(this.renderer);

        // set button text
        //        this.buttonWarpConfig.setText(this.internalPluginManager.getCurrentMouseWarper().getInformation().getDisplayName() + " Configuration");

        // set enabled
        this.checkBoxUseWarpActionPerformed();
    }

    /**
     * if mouse warp is used the configuration is enabled, otherwise not
     */
    private void enableWarpConfig(boolean enable) {
        // change enable status of some components
        this.comboBoxWarpMethod.setEnabled(enable);
        this.labelWarpMethod.setEnabled(enable);
        if (enable && this.internalPluginManager.getCurrentMouseWarper().getInformation().isGuiAvailable()) this.buttonWarpConfig.setEnabled(true);
        else
            this.buttonWarpConfig.setEnabled(false);
    }

    /**
     * adds tool tips to the components 
     */
    private void manageToolTips() {
        // create tool tip texts
        String labelStatusHotkeyTT = "<HTML><body>Enables/Disables the hotkeys and the mousewarp.</body></HTML>";
        String labelDimensionTT = "<HTML><body>Radius around the fixation point which is checked<br>for something interesting to click on.</body></HTML>";
        String labelActionHotkeyTT = "<HTML><body>Hotkey to click where you are looking at.</body></HTML>";
        String labelDetectorTT = "<HTML><body>Method to check the radius around the fixation point.</body></HTML>";
        String labelEnableMouseWarpTT = "<HTML><body>Enables/Disables the mouse warp permanently.</body></HTML>";
        String labelWarpMethodTT = "<HTML><body>Method which is used to warp the mouse.</body></HTML>";
        String labelEvaluationTT = "<HTML><body>This activates the evaluation mode.<br>This mode is used to collect data<br>which can be evaluated later.</body></HTML>";
        String labelSoundTT = "<HTML><body>Enables/Disables the sound notifications.</body></HTML>";
        String buttonSubmitTT = "<HTML><body>Submits collected statistic data<br>to the statistic-server.</body></HTML>";
        String labelRecalibrationTT = "<HTML><body>Every cursor warp and evaluation<br>step recalibrates the Trackingserver<br>if this checkboxs is selected.<br>This feature will only work with<br>Trackingserver 1.4 or higher.</HTML></body>";
        String buttonClearRecalibrationTT = "<HTML><body>This button clears all the recalibrations<br>and sets the tracking device back<br>to defult calibration.</HTML></body>";
        String buttonDefaultTT = "<HTML><body>Restores default values of some variables.</body></HTML>";

        // set tool tips
        this.labelStatusHotkey.setToolTipText(labelStatusHotkeyTT);
        this.labelDimension.setToolTipText(labelDimensionTT);
        this.labelActionHotkey.setToolTipText(labelActionHotkeyTT);
        this.labelDetector.setToolTipText(labelDetectorTT);
        this.labelEnableMouseWarp.setToolTipText(labelEnableMouseWarpTT);
        this.labelWarpMethod.setToolTipText(labelWarpMethodTT);
        this.labelEvaluation.setToolTipText(labelEvaluationTT);
        this.labelSound.setToolTipText(labelSoundTT);
        this.buttonSubmit.setToolTipText(buttonSubmitTT);
        this.labelRecalibration.setToolTipText(labelRecalibrationTT);
        this.buttonClearRecalibration.setToolTipText(buttonClearRecalibrationTT);
        this.buttonDefault.setToolTipText(buttonDefaultTT);

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
        Hotkey.getInstance().resetTmpKeys();
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

    /**
     * @return window listener for child window
     */
    private WindowListener initChildWindowListener() {
        return new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setEnabled(true);

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        mainFrame.requestFocus();
                        child.removeWindowListener(childWindowListener);
                        child = null;
                    }
                });
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        };
    }
}

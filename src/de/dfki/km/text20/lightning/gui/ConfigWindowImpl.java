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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.hotkey.Hotkey;
import de.dfki.km.text20.lightning.hotkey.HotkeyContainer;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;

/**
 * This is the configuration window which is shown after a click on the 'configuration' button of the tray menu. 
 * Here are all the things shown, which are important and should be changeable.
 * 
 * @author Christoph Käding
 *
 */
@SuppressWarnings({ "serial", "boxing" })
public class ConfigWindowImpl extends ConfigWindow implements ActionListener {

    /**
     * manageHotkeyComboBox() changes the items and selection of the actionHotkey and the statusHotkey comboboxes.
     * These two boxes have thier own actionlisteners which call manageHotkeyComboBox(). So an endless ring of calls is created when anything is changed.
     * This boolean shows if the action event is fired by an automatic or manual selection of these comboboxes.
     */
    private boolean autoSelect;

    /** The method manager is needed to display and change the available plugins. */
    private InternalPluginManager internalPluginManager;

    /** stores configuration of this tool */
    private Properties properties;

    /** renderer to show displaynames in comboboxes */
    private DefaultListCellRenderer renderer;

    /**
     * builds the window with all its components and shows it
     * 
     * @param manager
     * necessary to show and switch the plugins.
     */
    public ConfigWindowImpl() {
        this.internalPluginManager = MainClass.getInstance().getInternalPluginManager();
        this.properties = MainClass.getInstance().getProperties();

        // take values of global properties and preselect them
        this.spinnerDimension.setValue(this.properties.getDimension());

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
        this.checkBoxTraining.addActionListener(this);

        // initialize checkbox
        this.checkBoxTraining.setSelected(!MainClass.getInstance().isNormalMode());

        // initializes tooltips
        this.manageToolTips();

        this.textFieldName.setText(MainClass.getInstance().getCurrentUser());

        // show the gui
        setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    /**
     * catches the action events from gui and starts the associated action
     */
    @Override
    public void actionPerformed(ActionEvent event) {
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
            // TODO: add method
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

        if (event.getSource() == this.checkBoxTraining) {
            this.checkBockTrainingActionPerformed();
            return;
        }
    }

    /**
     * Fired if the OK button is clicked. All changes were applied.
     */
    protected void buttonOKActionPerformed() {
        // initialize variables with gui values, used to initialize warper
        int angle = Integer.parseInt(this.spinnerAngle.getValue().toString());
        int distance = Integer.parseInt(this.spinnerDistance.getValue().toString());
        long duration = Long.parseLong(this.spinnerDuration.getValue().toString());
        int home = Integer.parseInt(this.spinnerHomeRadius.getValue().toString());
        int set = Integer.parseInt(this.spinnerSetRadius.getValue().toString());

        // change variables in the properties and in the method manager
        this.properties.setDimension(Integer.parseInt(this.spinnerDimension.getValue().toString()));
        this.properties.setAngleThreshold(angle);
        this.properties.setDistanceThreshold(distance);
        this.properties.setDurationThreshold(duration);
        this.properties.setHomeRadius(home);
        this.properties.setSetRadius(set);
        this.properties.setUseWarp(this.checkBoxUseWarp.isSelected());

        // set new plugins
        this.internalPluginManager.setCurrentSaliencyDetector(((PluginInformation) this.comboBoxSearchMethod.getSelectedItem()).getId());
        this.internalPluginManager.setCurrentMouseWarper(((PluginInformation) this.comboBoxWarpMethod.getSelectedItem()).getId());
        this.internalPluginManager.getCurrentMouseWarper().initValues(angle, distance, duration, home, set);

        // set user name
        MainClass.getInstance().setCurrentUser(this.textFieldName.getText());

        // set mode
        if (MainClass.getInstance().isNormalMode() && this.checkBoxTraining.isSelected())
            MainClass.getInstance().toggleMode();
        if (!MainClass.getInstance().isNormalMode() && !this.checkBoxTraining.isSelected())
            MainClass.getInstance().toggleMode();
        MainClass.getInstance().resetTrainer(this.textFieldName.getText());

        // close the gui
        this.dispose();
    }

    /**
     * Fired if the Cancel button is clicked. Closes the window.
     */
    private void buttonCancelActionPerformed() {

        this.dispose();
    }

    /**
     * restores default values
     */
    private void buttonDefaultActionPerformed() {
        // restore default values 
        this.properties.restoreDefault();

        // take values of global properties and preselect them
        this.spinnerDimension.setValue(this.properties.getDimension());

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
        if (!this.autoSelect) {
            Hotkey.getInstance().setHotkey(1, ((HotkeyContainer) this.comboBoxActionHotkey.getSelectedItem()));
            this.manageHotkeyComboBox();
        }
    }

    /**
     * fired if the checkbock training is selected
     */
    private void checkBockTrainingActionPerformed() {
        this.checkBoxUseWarp.setEnabled(!this.checkBoxTraining.isSelected());
        this.enableWarpConfig(!this.checkBoxTraining.isSelected());
    }

    /**
     * Fired if something changed at the statushotkey combobox. Sets the choosed hotkey as statushotkey.
     */
    // TODO: only apply when OK is clicked
    private void comboBoxStatusHotkeyActionPerformed() {
        if (!this.autoSelect) {
            Hotkey.getInstance().setHotkey(2, ((HotkeyContainer) this.comboBoxStatusHotkey.getSelectedItem()));
            this.manageHotkeyComboBox();
        }
    }

    /**
     * enables or disables config
     */
    private void checkBoxUseWarpActionPerformed() {
        this.enableWarpConfig(this.checkBoxUseWarp.isSelected());
    }

    /**
     * the whole plugin is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
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
            if (!Hotkey.getInstance().getHotkeys().get(i).toString().equals(Hotkey.getInstance().getCurrentHotkey(2).toString()))
                this.comboBoxActionHotkey.addItem(Hotkey.getInstance().getHotkeys().get(i));
        }
        for (int i = 0; i < Hotkey.getInstance().getHotkeys().size(); i++) {
            if (!Hotkey.getInstance().getHotkeys().get(i).toString().equals(Hotkey.getInstance().getCurrentHotkey(1).toString()))
                this.comboBoxStatusHotkey.addItem(Hotkey.getInstance().getHotkeys().get(i));
        }

        // preselect property values
        for (int i = 0; i < this.comboBoxActionHotkey.getItemCount(); i++) {
            if (Hotkey.getInstance().getCurrentHotkey(1).toString().equals(this.comboBoxActionHotkey.getItemAt(i).toString())) {
                this.comboBoxActionHotkey.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < this.comboBoxStatusHotkey.getItemCount(); i++) {
            if (Hotkey.getInstance().getCurrentHotkey(2).toString().equals(this.comboBoxStatusHotkey.getItemAt(i).toString())) {
                this.comboBoxStatusHotkey.setSelectedIndex(i);
            }
        }

        this.autoSelect = false;
    }

    /**
     * Takes the list of available plugins and shows them in the combobox.
     */
    private void manageComboBoxSaliencyDetector() {
        // add all saliency detectors to the combobox
        for (int i = 0; i < this.internalPluginManager.getSaliencyDetectors().size(); i++) {
            this.comboBoxSearchMethod.addItem(this.internalPluginManager.getSaliencyDetectors().get(i).getInformation());
        }

        // preselect the current one
        if (this.internalPluginManager.getCurrentSaliencyDetector() != null)
            this.comboBoxSearchMethod.setSelectedItem(this.internalPluginManager.getCurrentSaliencyDetector().getInformation());

        // set renderer
        this.comboBoxSearchMethod.setRenderer(this.renderer);
    }

    /**
     * manages values of the spinners and comboBox which are used to configure the mouse warping
     */
    private void manageWarpConfig() {
        // preselect values
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerDistance.setValue(this.properties.getDistanceThreshold());
        this.spinnerDuration.setValue(this.properties.getDurationThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());
        this.spinnerSetRadius.setValue(this.properties.getSetRadius());
        this.checkBoxUseWarp.setSelected(this.properties.isUseWarp());

        // manage combobox
        for (MouseWarper warper : this.internalPluginManager.getMouseWarpers())
            this.comboBoxWarpMethod.addItem(warper.getInformation());

        // preselect current mouse warper
        if (this.internalPluginManager.getCurrentMouseWarper() != null)
            this.comboBoxWarpMethod.setSelectedItem(this.internalPluginManager.getCurrentMouseWarper().getInformation());

        // set renderer
        this.comboBoxWarpMethod.setRenderer(this.renderer);

        // set enabled
        this.enableWarpConfig(this.checkBoxUseWarp.isSelected());
    }

    /**
     * if mouse warp is used the configuration is enabled, otherwise not
     */
    private void enableWarpConfig(boolean enable) {
        // set enabled
        this.spinnerAngle.setEnabled(enable);
        this.spinnerDistance.setEnabled(enable);
        this.spinnerDuration.setEnabled(enable);
        this.spinnerHomeRadius.setEnabled(enable);
        this.spinnerSetRadius.setEnabled(enable);
        this.comboBoxWarpMethod.setEnabled(enable);
    }

    /**
     * adds tool tips to the components
     */
    private void manageToolTips() {
        // create tool tip texts
        String labelStatusHotkeyTT = "<HTML><body>Enables/Disables the hotkeys and the mousewarp.</body></HTML>";
        String labelDimensionTT = "<HTML><body>Radius around the fixation point which is checked<br>for something interesting to click on.</body></HTML>";
        String labelActionHotkeyTT = "<HTML><body>Hotkey to click where you are looking at.</body></HTML>";
        String labelSearchMethodTT = "<HTML><body>Method to check the radius around the fixation point.</body></HTML>";
        String labelEnableMouseWarpTT = "<HTML><body>Enables/Disables the mouse warp permanently.</body></HTML>";
        String labelWarpMethodTT = "<HTML><body>Method which is used to warp the mouse.</body></HTML>";
        String labelAngleThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must do this in an angle within this<br>threshold to activate the mouse warp.<br>The lower this value the more exact you must<br>move your mouse.</body></HTML>";
        String labelDistanceThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must move minimal this way in pixels to<br>activate the mouse warp. The higher this value<br>the more pixels you have to pass.<br>This means you have to move faster.</body></HTML>";
        String labelDurationThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must move minimal this time in ms to<br>activate the mouse warp. The higher this value<br>the more exact the calculation can be done and<br>the mouse only warps when you really want it.</body></HTML>";
        String labelHomeRadiusTT = "<HTML><body>If you move your mouse to your fixation point<br>and your cursor is within this radius, your<br>mousecursor will not be warped.</body></HTML>";
        String labelSetRadiusTT = "<HTML><body>If you move your mouse to your fixation point<br>and your mouse warp is activated, it will be set<br>in the choosed distance from the fixation point<br>to allow you to stop the movement by<br>yourself.</body></HTML>";

        // set tool tips
        this.labelStatusHotkey.setToolTipText(labelStatusHotkeyTT);
        this.labelDimension.setToolTipText(labelDimensionTT);
        this.labelActionHotkey.setToolTipText(labelActionHotkeyTT);
        this.labelSearchMethod.setToolTipText(labelSearchMethodTT);
        this.labelEnableMouseWarp.setToolTipText(labelEnableMouseWarpTT);
        this.labelWarpMethod.setToolTipText(labelWarpMethodTT);
        this.labelAngleThreshold.setToolTipText(labelAngleThresholdTT);
        this.labelDistanceThreshold.setToolTipText(labelDistanceThresholdTT);
        this.labelDurationThreshold.setToolTipText(labelDurationThresholdTT);
        this.labelHomeRadius.setToolTipText(labelHomeRadiusTT);
        this.labelSetRadius.setToolTipText(labelSetRadiusTT);
    }
}

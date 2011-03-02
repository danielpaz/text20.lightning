/*
 * Created by JFormDesigner on Wed Feb 16 12:14:36 CET 2011
 */
/*
 * ConfigWindow.java
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
 *
 */
package de.dfki.km.text20.lightning.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.hotkey.Hotkey;
import de.dfki.km.text20.lightning.hotkey.HotkeyContainer;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.training.Trainer;

/**
 * This is the configuration window which is shown after a click on the 'configuration' button of the tray menu. 
 * Here are all the things shown, which are important and should be changeable.
 * 
 * @author Christoph Käding
 */
@SuppressWarnings({ "boxing", "serial" })
public class ConfigWindow extends JFrame {

    /**
     * builds the window with all its components and shows it
     * 
     * @param manager
     * necessary to show and switch the plugins.
     */
    public ConfigWindow() {
        this.internalPluginManager = MainClass.getInstance().getInternalPluginManager();

        // create file chooser for outputpath
        this.chooser = new JFileChooser() {

            // react on selection
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();
                MainClass.getInstance().getProperties().setOutputPath(this.getSelectedFile().getAbsolutePath());
                textFieldOutputPath.setText(MainClass.getInstance().getProperties().getOutputPath());
            }
        };
        this.chooser.setMultiSelectionEnabled(false);
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // build basic components
        initComponents();

        // take values of global properties and preselect them
        this.checkBoxShowPictures.setSelected(MainClass.getInstance().getProperties().isShowImages());
        this.spinnerDimension.setValue(MainClass.getInstance().getProperties().getDimension());
        this.textFieldOutputPath.setText(MainClass.getInstance().getProperties().getOutputPath());

        // initialize renderer of comboboxes
        this.renderer = initRenderer();

        // manage comboboxes
        this.manageTrainerComboBox();
        this.manageHotkeyComboBox();
        this.manageComboBoxSaliencyDetector();

        // manage warp config
        this.manageWarpConfig();

        // show the gui
        setVisible(true);
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
     * manages content of trainer combobox
     */
    private void manageTrainerComboBox() {
        // add alls trainer to combobox
        for (Trainer trainer : this.internalPluginManager.getTrainer())
            this.comboBoxLearnMethod.addItem(trainer);

        // preselect current trainer
        if (this.internalPluginManager.getCurrentTrainer() != null)
            this.comboBoxLearnMethod.setSelectedItem(this.internalPluginManager.getCurrentTrainer());

        // set renderer
        this.comboBoxLearnMethod.setRenderer(this.renderer);
    }

    /**
     * Takes the list of available plugins and shows them in the combobox.
     */
    private void manageComboBoxSaliencyDetector() {
        // add all saliency detectors to the combobox
        for (int i = 0; i < this.internalPluginManager.getSaliencyDetectors().size(); i++) {
            this.comboBoxSearchMethod.addItem(this.internalPluginManager.getSaliencyDetectors().get(i));
        }

        // preselect the current one
        if (this.internalPluginManager.getCurrentSaliencyDetector() != null)
            this.comboBoxSearchMethod.setSelectedItem(this.internalPluginManager.getCurrentSaliencyDetector());

        // set renderer
        this.comboBoxSearchMethod.setRenderer(this.renderer);
    }

    /**
     * manages values of the spinners and comboBox which are used to configure the mouse warping
     */
    private void manageWarpConfig() {
        // preselect values
        this.spinnerAngle.setValue(MainClass.getInstance().getProperties().getAngleThreshold());
        this.spinnerDistance.setValue(MainClass.getInstance().getProperties().getDistanceThreshold());
        this.spinnerDuration.setValue(MainClass.getInstance().getProperties().getDurationThreshold());
        this.spinnerHomeRadius.setValue(MainClass.getInstance().getProperties().getHomeRadius());
        this.spinnerSetRadius.setValue(MainClass.getInstance().getProperties().getSetRadius());
        this.checkBoxUseWarp.setSelected(MainClass.getInstance().getProperties().isUseWarp());

        // manage combobox
        for (MouseWarper warper : this.internalPluginManager.getMouseWarpers())
            this.comboBoxWarpMethod.addItem(warper);

        // preselect current mouse warper
        if (this.internalPluginManager.getCurrentMouseWarper() != null)
            this.comboBoxWarpMethod.setSelectedItem(this.internalPluginManager.getCurrentMouseWarper());

        // set renderer
        this.comboBoxWarpMethod.setRenderer(this.renderer);

        // set enabled
        this.enableWarpConfig();
    }

    /**
     * the whole plugin is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
     * 
     * @return a changed default renderer
     */
    private DefaultListCellRenderer initRenderer() {
        // TODO: change that only a kind of identifier is added to the combobox
        return new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // set new displayed attribute
                if (value instanceof MouseWarper) {
                    setText(((MouseWarper) value).getInformation().getDisplayName());
                    setToolTipText(((MouseWarper) value).getInformation().getToolTip());
                }
                
                if (value instanceof Trainer) {
                    setText(((Trainer) value).getInformation().getDisplayName());
                    setToolTipText(((Trainer) value).getInformation().getToolTip());
                }
                
                if (value instanceof SaliencyDetector) {
                    setText(((SaliencyDetector) value).getInformation().getDisplayName());
                    setToolTipText(((SaliencyDetector) value).getInformation().getToolTip());
                }

                return this;
            }
        };
    }

    /**
     * if mouse warp is used the configuration is enabled, otherwise not
     */
    private void enableWarpConfig() {
        // set enabled
        this.spinnerAngle.setEnabled(this.checkBoxUseWarp.isSelected());
        this.spinnerDistance.setEnabled(this.checkBoxUseWarp.isSelected());
        this.spinnerDuration.setEnabled(this.checkBoxUseWarp.isSelected());
        this.spinnerHomeRadius.setEnabled(this.checkBoxUseWarp.isSelected());
        this.spinnerSetRadius.setEnabled(this.checkBoxUseWarp.isSelected());
        this.comboBoxWarpMethod.setEnabled(this.checkBoxUseWarp.isSelected());
    }

    /**
     * Fired if the OK button is clicked. All changes were applied.
     * 
     * @param e
     */
    private void buttonOKActionPerformed(ActionEvent e) {
        // initialize variables with gui values
        int angle = Integer.parseInt(this.spinnerAngle.getValue().toString());
        int distance = Integer.parseInt(this.spinnerDistance.getValue().toString());
        long duration = Long.parseLong(this.spinnerDuration.getValue().toString());
        int home = Integer.parseInt(this.spinnerHomeRadius.getValue().toString());
        int set = Integer.parseInt(this.spinnerSetRadius.getValue().toString());

        // change variables in the properties and in the method manager
        MainClass.getInstance().getProperties().setShowImages(this.checkBoxShowPictures.isSelected());
        MainClass.getInstance().getProperties().setDimension(Integer.parseInt(this.spinnerDimension.getValue().toString()));
        MainClass.getInstance().getProperties().setOutputPath(this.textFieldOutputPath.getText());
        MainClass.getInstance().getProperties().setAngleThreshold(angle);
        MainClass.getInstance().getProperties().setDistanceThreshold(distance);
        MainClass.getInstance().getProperties().setDurationThreshold(duration);
        MainClass.getInstance().getProperties().setHomeRadius(home);
        MainClass.getInstance().getProperties().setSetRadius(set);
        MainClass.getInstance().getProperties().setUseWarp(this.checkBoxUseWarp.isSelected());

        // TODO: do these also in properties, see MethodManager class
        this.internalPluginManager.setCurrentSaliencyDetector((SaliencyDetector) this.comboBoxSearchMethod.getSelectedItem());
        this.internalPluginManager.setCurrentMouseWarper((MouseWarper) this.comboBoxWarpMethod.getSelectedItem());
        this.internalPluginManager.setCurrentTrainer((Trainer) this.comboBoxLearnMethod.getSelectedItem());

        // TODO: maybe do this on a other place
        this.internalPluginManager.getCurrentMouseWarper().initValues(angle, distance, duration, home, set);

        // close the gui
        this.dispose();
    }

    /**
     * Fired if the Cancel button is clicked. Closes the window.
     * 
     * @param e
     */
    private void buttonCancelActionPerformed(ActionEvent e) {

        this.dispose();
    }

    /**
     * Fired if the Select button is clicked. Opens the filechooser.
     * 
     * @param e
     */
    private void buttonSelectActionPerformed(ActionEvent e) {

        this.chooser.showOpenDialog(this);
    }

    /**
     * Fired if something changed at the actionhotkey combobox. Sets the choosed hotkey as actionhotkey.
     * 
     * @param e
     */
    void comboBoxActionHotkeyActionPerformed(ActionEvent e) {
        if (!this.autoSelect) {
            Hotkey.getInstance().setHotkey(1, ((HotkeyContainer) this.comboBoxActionHotkey.getSelectedItem()));
            this.manageHotkeyComboBox();
        }
    }

    /**
     * Fired if something changed at the statushotkey combobox. Sets the choosed hotkey as statushotkey.
     * 
     * @param e
     */
    // TODO: only apply when OK is clicked
    private void comboBoxStatusHotkeyActionPerformed(ActionEvent e) {
        if (!this.autoSelect) {
            Hotkey.getInstance().setHotkey(2, ((HotkeyContainer) this.comboBoxStatusHotkey.getSelectedItem()));
            this.manageHotkeyComboBox();
        }
    }

    /**
     * see TODO
     * 
     * @param e
     */
    private void comboBoxSearchMethodActionPerformed(ActionEvent e) {
        // TODO: delete this function
    }

    /**
     * see TODO
     * 
     * @param e
     */
    private void comboBoxLearnMethodActionPerformed(ActionEvent e) {
        // TODO: delete this function
    }

    /**
     * enables or disables config
     * 
     * @param e
     */
    private void checkBoxUseWarpActionPerformed(ActionEvent e) {
        this.enableWarpConfig();
    }

    /**
     * restores default values
     * 
     * @param e
     */
    private void buttonDefaultActionPerformed(ActionEvent e) {
        // restore default values 
        MainClass.getInstance().getProperties().restoreDefault();

        // take values of global properties and preselect them
        this.checkBoxShowPictures.setSelected(MainClass.getInstance().getProperties().isShowImages());
        this.spinnerDimension.setValue(MainClass.getInstance().getProperties().getDimension());
        this.textFieldOutputPath.setText(MainClass.getInstance().getProperties().getOutputPath());

        // manage comboboxes
        this.manageTrainerComboBox();
        this.manageHotkeyComboBox();
        this.manageComboBoxSaliencyDetector();

        // manage warp config
        this.manageWarpConfig();
    }

    /**
     * TODO: upload statistics
     * 
     * @param e
     */
    private void buttonSubmitActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    /**
     * auto generated code, initializes gui components
     */
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        checkBoxShowPictures = new JCheckBox();
        separator4 = new JSeparator();
        label3 = new JLabel();
        buttonSelect = new JButton();
        textFieldOutputPath = new JTextField();
        separator5 = new JSeparator();
        label15 = new JLabel();
        checkBoxUseWarp = new JCheckBox();
        label8 = new JLabel();
        comboBoxWarpMethod = new JComboBox();
        label9 = new JLabel();
        spinnerAngle = new JSpinner();
        separator1 = new JSeparator();
        label10 = new JLabel();
        spinnerDistance = new JSpinner();
        label5 = new JLabel();
        comboBoxStatusHotkey = new JComboBox();
        label11 = new JLabel();
        spinnerDuration = new JSpinner();
        separator3 = new JSeparator();
        label12 = new JLabel();
        spinnerHomeRadius = new JSpinner();
        label2 = new JLabel();
        spinnerDimension = new JSpinner();
        label13 = new JLabel();
        spinnerSetRadius = new JSpinner();
        label4 = new JLabel();
        comboBoxActionHotkey = new JComboBox();
        label6 = new JLabel();
        comboBoxSearchMethod = new JComboBox();
        buttonDefault = new JButton();
        buttonSubmit = new JButton();
        label7 = new JLabel();
        comboBoxLearnMethod = new JComboBox();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        buttonBar = new JPanel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Project Lightning (Desktop)");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.X_AXIS));

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "4*(30dlu, $lcgap), 3dlu, 4*($lcgap, 30dlu), 5*($lcgap, default)",
                    "3*(default, $lgap), [7dlu,default], 20*($lgap, default)"));

                //---- label1 ----
                label1.setText("Show Images");
                contentPanel.add(label1, cc.xywh(1, 1, 3, 1));
                contentPanel.add(checkBoxShowPictures, cc.xywh(5, 1, 3, 1));

                //---- separator4 ----
                separator4.setOrientation(SwingConstants.VERTICAL);
                contentPanel.add(separator4, cc.xy(9, 1));

                //---- label3 ----
                label3.setText("Output Directory");
                contentPanel.add(label3, cc.xywh(1, 3, 3, 1));

                //---- buttonSelect ----
                buttonSelect.setText("Select");
                buttonSelect.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonSelectActionPerformed(e);
                    }
                });
                contentPanel.add(buttonSelect, cc.xywh(5, 3, 3, 1));
                contentPanel.add(textFieldOutputPath, cc.xywh(1, 5, 7, 1));

                //---- separator5 ----
                separator5.setOrientation(SwingConstants.VERTICAL);
                contentPanel.add(separator5, cc.xywh(9, 1, 1, 25));

                //---- label15 ----
                label15.setText("Enable Mouse Warp");
                label15.setToolTipText("Enables/Disables the mouse warp permanently.");
                contentPanel.add(label15, cc.xywh(11, 1, 3, 1));

                //---- checkBoxUseWarp ----
                checkBoxUseWarp.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkBoxUseWarpActionPerformed(e);
                    }
                });
                contentPanel.add(checkBoxUseWarp, cc.xy(15, 1));

                //---- label8 ----
                label8.setText("Warp Method");
                label8.setToolTipText("Method which is used to warp the mouse.");
                contentPanel.add(label8, cc.xywh(11, 3, 3, 1));
                contentPanel.add(comboBoxWarpMethod, cc.xywh(15, 3, 3, 1));

                //---- label9 ----
                label9.setText("Angle Threshold");
                label9.setToolTipText("If you move your mouse to your fixation point, you must do this in an angle within this threshold to activate the mouse warp. The lower this value the more exact you must move your mouse.");
                contentPanel.add(label9, cc.xywh(11, 5, 3, 1));

                //---- spinnerAngle ----
                spinnerAngle.setModel(new SpinnerNumberModel(10, 0, 180, 1));
                contentPanel.add(spinnerAngle, cc.xywh(15, 5, 3, 1));
                contentPanel.add(separator1, cc.xywh(1, 7, 7, 1));

                //---- label10 ----
                label10.setText("Distance Threshold");
                label10.setToolTipText("If you move your mouse to your fixation point, you must move minimal this way in pixels to activate the mouse warp. The higher this value the more pixels you have to pass. This means you have to move faster.");
                contentPanel.add(label10, cc.xywh(11, 7, 3, 1));

                //---- spinnerDistance ----
                spinnerDistance.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerDistance, cc.xywh(15, 7, 3, 1));

                //---- label5 ----
                label5.setText("Status Hotkey");
                label5.setToolTipText("Enables/Disables the hotkeys and the mousewarp.");
                contentPanel.add(label5, cc.xywh(1, 9, 3, 1));

                //---- comboBoxStatusHotkey ----
                comboBoxStatusHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxStatusHotkeyActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxStatusHotkey, cc.xywh(5, 9, 3, 1));

                //---- label11 ----
                label11.setText("Duration Threshold");
                label11.setToolTipText("If you move your mouse to your fixation point, you must move minimal this time in ms to activate the mouse warp. The higher this value the more exact the calculation can be done and the mouse only warps when you really want it.");
                contentPanel.add(label11, cc.xywh(11, 9, 3, 1));

                //---- spinnerDuration ----
                spinnerDuration.setModel(new SpinnerNumberModel(300, 300, 2147483647, 100));
                contentPanel.add(spinnerDuration, cc.xywh(15, 9, 3, 1));
                contentPanel.add(separator3, cc.xywh(1, 11, 7, 1));

                //---- label12 ----
                label12.setText("Home Radius");
                label12.setToolTipText("If you move your mouse to your fixation point and your cursor is within this radius, your mousecursor will not be warped.");
                contentPanel.add(label12, cc.xywh(11, 11, 3, 1));

                //---- spinnerHomeRadius ----
                spinnerHomeRadius.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerHomeRadius, cc.xywh(15, 11, 3, 1));

                //---- label2 ----
                label2.setText("Dimension");
                label2.setToolTipText("Radius around the fixation point which is checked for something interesting to click on.");
                contentPanel.add(label2, cc.xywh(1, 13, 3, 1));

                //---- spinnerDimension ----
                spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                contentPanel.add(spinnerDimension, cc.xywh(5, 13, 3, 1));

                //---- label13 ----
                label13.setText("Set Radius");
                label13.setToolTipText("If you move your mouse to your fixation point and your mouse warp is activated, it will be set in the choosed distance from the fixation point to allow you to stop the movement by yourself.");
                contentPanel.add(label13, cc.xywh(11, 13, 3, 1));

                //---- spinnerSetRadius ----
                spinnerSetRadius.setModel(new SpinnerNumberModel(20, 0, 2147483647, 1));
                contentPanel.add(spinnerSetRadius, cc.xywh(15, 13, 3, 1));

                //---- label4 ----
                label4.setText("Action Hotkey");
                label4.setToolTipText("Hotkey to click where you are looking at.");
                contentPanel.add(label4, cc.xywh(1, 15, 3, 1));

                //---- comboBoxActionHotkey ----
                comboBoxActionHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxActionHotkeyActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxActionHotkey, cc.xywh(5, 15, 3, 1));

                //---- label6 ----
                label6.setText("Search Method");
                label6.setToolTipText("Method to check the radius around the fixation point.");
                contentPanel.add(label6, cc.xywh(1, 17, 3, 1));

                //---- comboBoxSearchMethod ----
                comboBoxSearchMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxSearchMethodActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxSearchMethod, cc.xywh(5, 17, 3, 1));

                //---- buttonDefault ----
                buttonDefault.setText("Default");
                buttonDefault.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonDefaultActionPerformed(e);
                    }
                });
                contentPanel.add(buttonDefault, cc.xywh(11, 17, 3, 1));

                //---- buttonSubmit ----
                buttonSubmit.setText("Submit");
                buttonSubmit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonSubmitActionPerformed(e);
                    }
                });
                contentPanel.add(buttonSubmit, cc.xywh(15, 17, 3, 1));

                //---- label7 ----
                label7.setText("Learn Method");
                label7.setToolTipText("Method which is used in the trainings mode.");
                contentPanel.add(label7, cc.xywh(1, 19, 3, 1));

                //---- comboBoxLearnMethod ----
                comboBoxLearnMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxLearnMethodActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxLearnMethod, cc.xywh(5, 19, 3, 1));

                //---- buttonOK ----
                buttonOK.setText("OK");
                buttonOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonOKActionPerformed(e);
                    }
                });
                contentPanel.add(buttonOK, cc.xywh(11, 19, 3, 1));

                //---- buttonCancel ----
                buttonCancel.setText("Cancel");
                buttonCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonCancelActionPerformed(e);
                    }
                });
                contentPanel.add(buttonCancel, cc.xywh(15, 19, 3, 1));

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    buttonBar.setLayout(new CardLayout());
                }
                contentPanel.add(buttonBar, cc.xy(13, 31));
            }
            dialogPane.add(contentPanel);
        }
        contentPane.add(dialogPane);
        setSize(435, 305);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JCheckBox checkBoxShowPictures;
    private JSeparator separator4;
    private JLabel label3;
    private JButton buttonSelect;
    private JTextField textFieldOutputPath;
    private JSeparator separator5;
    private JLabel label15;
    private JCheckBox checkBoxUseWarp;
    private JLabel label8;
    private JComboBox comboBoxWarpMethod;
    private JLabel label9;
    private JSpinner spinnerAngle;
    private JSeparator separator1;
    private JLabel label10;
    private JSpinner spinnerDistance;
    private JLabel label5;
    private JComboBox comboBoxStatusHotkey;
    private JLabel label11;
    private JSpinner spinnerDuration;
    private JSeparator separator3;
    private JLabel label12;
    private JSpinner spinnerHomeRadius;
    private JLabel label2;
    private JSpinner spinnerDimension;
    private JLabel label13;
    private JSpinner spinnerSetRadius;
    private JLabel label4;
    private JComboBox comboBoxActionHotkey;
    private JLabel label6;
    private JComboBox comboBoxSearchMethod;
    private JButton buttonDefault;
    private JButton buttonSubmit;
    private JLabel label7;
    private JComboBox comboBoxLearnMethod;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttonBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    /** filechooser for outputpath */
    private JFileChooser chooser;

    /**
     * manageHotkeyComboBox() changes the items and selection of the actionHotkey and the statusHotkey comboboxes.
     * These two boxes have thier own actionlisteners which call manageHotkeyComboBox(). So an endless ring of calls is created when anything is changed.
     * This boolean shows if the action event is fired by an automatic or manual selection of these comboboxes.
     */
    private boolean autoSelect;

    /** The method manager is needed to display and change the available plugins. */
    private InternalPluginManager internalPluginManager;

    /** renderer to show displaynames in comboboxes */
    private DefaultListCellRenderer renderer;
}

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
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.tools.Hotkey;
import de.dfki.km.text20.lightning.tools.HotkeyContainer;

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

        // manage comboboxes
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
     * Takes the list of available plugins and shows them in the combobox.
     */
    private void manageComboBoxSaliencyDetector() {
        this.autoSelect = true;

        // add all saliency detectors to the combobox
        for (int i = 0; i < this.internalPluginManager.getSaliencyDetectors().size(); i++) {
            this.comboBoxSearchMethod.addItem(this.internalPluginManager.getSaliencyDetectors().get(i));
        }

        // preselect the current one
        if (this.internalPluginManager.getCurrentSaliencyDetector() != null)
            this.comboBoxSearchMethod.setSelectedItem(this.internalPluginManager.getCurrentSaliencyDetector());

        // the whole plugin is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
        // TODO: change that only a kind of identifier is added to the combobox
        this.comboBoxSearchMethod.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SaliencyDetector) {
                    setText(((SaliencyDetector) value).getInformation().displayName);
                }
                return this;
            }
        });

        this.autoSelect = false;
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

        // the whole plugin is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
        // TODO: change that only a kind of identifier is added to the combobox
        this.comboBoxWarpMethod.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MouseWarper) {
                    setText(((MouseWarper) value).getInformation().displayName);
                }
                return this;
            }
        });

        // set enabled
        this.enableWarpConfig();
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
                contentPanel.setLayout(new FormLayout("4*(30dlu, $lcgap), 3dlu, 4*($lcgap, 30dlu), 5*($lcgap, default)", "3*(default, $lgap), [7dlu,default], 20*($lgap, default)"));

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
                contentPanel.add(label8, cc.xywh(11, 3, 3, 1));
                contentPanel.add(comboBoxWarpMethod, cc.xywh(15, 3, 3, 1));

                //---- label9 ----
                label9.setText("Angle Threshold");
                contentPanel.add(label9, cc.xywh(11, 5, 3, 1));

                //---- spinnerAngle ----
                spinnerAngle.setModel(new SpinnerNumberModel(10, 0, 180, 1));
                contentPanel.add(spinnerAngle, cc.xywh(15, 5, 3, 1));
                contentPanel.add(separator1, cc.xywh(1, 7, 7, 1));

                //---- label10 ----
                label10.setText("Distance Threshold");
                contentPanel.add(label10, cc.xywh(11, 7, 3, 1));
                contentPanel.add(spinnerDistance, cc.xywh(15, 7, 3, 1));

                //---- label5 ----
                label5.setText("Status Hotkey");
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
                contentPanel.add(label11, cc.xywh(11, 9, 3, 1));
                contentPanel.add(spinnerDuration, cc.xywh(15, 9, 3, 1));
                contentPanel.add(separator3, cc.xywh(1, 11, 7, 1));

                //---- label12 ----
                label12.setText("Home Radius");
                contentPanel.add(label12, cc.xywh(11, 11, 3, 1));
                contentPanel.add(spinnerHomeRadius, cc.xywh(15, 11, 3, 1));

                //---- label2 ----
                label2.setText("Dimension");
                contentPanel.add(label2, cc.xywh(1, 13, 3, 1));

                //---- spinnerDimension ----
                spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                contentPanel.add(spinnerDimension, cc.xywh(5, 13, 3, 1));

                //---- label13 ----
                label13.setText("Set Radius");
                label13.setToolTipText("sdfgsd,g");
                contentPanel.add(label13, cc.xywh(11, 13, 3, 1));

                //---- spinnerSetRadius ----
                spinnerSetRadius.setModel(new SpinnerNumberModel(20, null, null, 1));
                contentPanel.add(spinnerSetRadius, cc.xywh(15, 13, 3, 1));

                //---- label4 ----
                label4.setText("Action Hotkey");
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
                contentPanel.add(label6, cc.xywh(1, 17, 3, 1));

                //---- comboBoxSearchMethod ----
                comboBoxSearchMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxSearchMethodActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxSearchMethod, cc.xywh(5, 17, 3, 1));

                //---- label7 ----
                label7.setText("Learn Method");
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
}

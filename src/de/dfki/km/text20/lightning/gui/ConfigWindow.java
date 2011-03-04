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
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("all")
public class ConfigWindow extends JFrame {

    public ConfigWindow() {
        initComponents();
    }

    private void buttonSelectActionPerformed(ActionEvent e) {
    }

    private void checkBoxUseWarpActionPerformed(ActionEvent e) {
    }

    private void comboBoxStatusHotkeyActionPerformed(ActionEvent e) {
    }

    private void comboBoxActionHotkeyActionPerformed(ActionEvent e) {
    }

    private void comboBoxSearchMethodActionPerformed(ActionEvent e) {
    }

    private void buttonDefaultActionPerformed(ActionEvent e) {
    }

    private void buttonSubmitActionPerformed(ActionEvent e) {
    }

    private void comboBoxLearnMethodActionPerformed(ActionEvent e) {
    }

    private void buttonOKActionPerformed(ActionEvent e) {
    }

    private void buttonCancelActionPerformed(ActionEvent e) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        labelShowImages = new JLabel();
        checkBoxShowPictures = new JCheckBox();
        separator4 = new JSeparator();
        labelOutputDirectory = new JLabel();
        buttonSelect = new JButton();
        textFieldOutputPath = new JTextField();
        separator5 = new JSeparator();
        labelEnableMouseWarp = new JLabel();
        checkBoxUseWarp = new JCheckBox();
        labelWarpMethod = new JLabel();
        comboBoxWarpMethod = new JComboBox();
        labelAngleThreshold = new JLabel();
        spinnerAngle = new JSpinner();
        separator1 = new JSeparator();
        labelDistanceThreshold = new JLabel();
        spinnerDistance = new JSpinner();
        labelStatusHotkey = new JLabel();
        comboBoxStatusHotkey = new JComboBox();
        labelDurationThreshold = new JLabel();
        spinnerDuration = new JSpinner();
        separator3 = new JSeparator();
        labelHomeRadius = new JLabel();
        spinnerHomeRadius = new JSpinner();
        labelDimension = new JLabel();
        spinnerDimension = new JSpinner();
        labelSetRadius = new JLabel();
        spinnerSetRadius = new JSpinner();
        labelActionHotkey = new JLabel();
        comboBoxActionHotkey = new JComboBox();
        labelSearchMethod = new JLabel();
        comboBoxSearchMethod = new JComboBox();
        buttonDefault = new JButton();
        buttonSubmit = new JButton();
        labelLearnMethod = new JLabel();
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

                //---- labelShowImages ----
                labelShowImages.setText("Show Images");
                contentPanel.add(labelShowImages, cc.xywh(1, 1, 3, 1));
                contentPanel.add(checkBoxShowPictures, cc.xywh(5, 1, 3, 1));

                //---- separator4 ----
                separator4.setOrientation(SwingConstants.VERTICAL);
                contentPanel.add(separator4, cc.xy(9, 1));

                //---- labelOutputDirectory ----
                labelOutputDirectory.setText("Output Directory");
                contentPanel.add(labelOutputDirectory, cc.xywh(1, 3, 3, 1));

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

                //---- labelEnableMouseWarp ----
                labelEnableMouseWarp.setText("Enable Mouse Warp");
                contentPanel.add(labelEnableMouseWarp, cc.xywh(11, 1, 3, 1));

                //---- checkBoxUseWarp ----
                checkBoxUseWarp.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkBoxUseWarpActionPerformed(e);
                    }
                });
                contentPanel.add(checkBoxUseWarp, cc.xywh(15, 1, 3, 1));

                //---- labelWarpMethod ----
                labelWarpMethod.setText("Warp Method");
                contentPanel.add(labelWarpMethod, cc.xywh(11, 3, 3, 1));
                contentPanel.add(comboBoxWarpMethod, cc.xywh(15, 3, 3, 1));

                //---- labelAngleThreshold ----
                labelAngleThreshold.setText("Angle Threshold");
                contentPanel.add(labelAngleThreshold, cc.xywh(11, 5, 3, 1));

                //---- spinnerAngle ----
                spinnerAngle.setModel(new SpinnerNumberModel(10, 0, 180, 1));
                contentPanel.add(spinnerAngle, cc.xywh(15, 5, 3, 1));
                contentPanel.add(separator1, cc.xywh(1, 7, 7, 1));

                //---- labelDistanceThreshold ----
                labelDistanceThreshold.setText("Distance Threshold");
                contentPanel.add(labelDistanceThreshold, cc.xywh(11, 7, 3, 1));

                //---- spinnerDistance ----
                spinnerDistance.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerDistance, cc.xywh(15, 7, 3, 1));

                //---- labelStatusHotkey ----
                labelStatusHotkey.setText("Status Hotkey");
                contentPanel.add(labelStatusHotkey, cc.xywh(1, 9, 3, 1));

                //---- comboBoxStatusHotkey ----
                comboBoxStatusHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxStatusHotkeyActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxStatusHotkey, cc.xywh(5, 9, 3, 1));

                //---- labelDurationThreshold ----
                labelDurationThreshold.setText("Duration Threshold");
                contentPanel.add(labelDurationThreshold, cc.xywh(11, 9, 3, 1));

                //---- spinnerDuration ----
                spinnerDuration.setModel(new SpinnerNumberModel(300, 300, 2147483647, 100));
                contentPanel.add(spinnerDuration, cc.xywh(15, 9, 3, 1));
                contentPanel.add(separator3, cc.xywh(1, 11, 7, 1));

                //---- labelHomeRadius ----
                labelHomeRadius.setText("Home Radius");
                contentPanel.add(labelHomeRadius, cc.xywh(11, 11, 3, 1));

                //---- spinnerHomeRadius ----
                spinnerHomeRadius.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
                contentPanel.add(spinnerHomeRadius, cc.xywh(15, 11, 3, 1));

                //---- labelDimension ----
                labelDimension.setText("Dimension");
                labelDimension.setIcon(null);
                contentPanel.add(labelDimension, cc.xywh(1, 13, 3, 1));

                //---- spinnerDimension ----
                spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                contentPanel.add(spinnerDimension, cc.xywh(5, 13, 3, 1));

                //---- labelSetRadius ----
                labelSetRadius.setText("Set Radius");
                contentPanel.add(labelSetRadius, cc.xywh(11, 13, 3, 1));

                //---- spinnerSetRadius ----
                spinnerSetRadius.setModel(new SpinnerNumberModel(20, 0, 2147483647, 1));
                contentPanel.add(spinnerSetRadius, cc.xywh(15, 13, 3, 1));

                //---- labelActionHotkey ----
                labelActionHotkey.setText("Action Hotkey");
                labelActionHotkey.setIcon(null);
                contentPanel.add(labelActionHotkey, cc.xywh(1, 15, 3, 1));

                //---- comboBoxActionHotkey ----
                comboBoxActionHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxActionHotkeyActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxActionHotkey, cc.xywh(5, 15, 3, 1));

                //---- labelSearchMethod ----
                labelSearchMethod.setText("Search Method");
                contentPanel.add(labelSearchMethod, cc.xywh(1, 17, 3, 1));

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

                //---- labelLearnMethod ----
                labelLearnMethod.setText("Learn Method");
                contentPanel.add(labelLearnMethod, cc.xywh(1, 19, 3, 1));

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
    protected JLabel labelShowImages;
    protected JCheckBox checkBoxShowPictures;
    private JSeparator separator4;
    protected JLabel labelOutputDirectory;
    protected JButton buttonSelect;
    protected JTextField textFieldOutputPath;
    private JSeparator separator5;
    protected JLabel labelEnableMouseWarp;
    protected JCheckBox checkBoxUseWarp;
    protected JLabel labelWarpMethod;
    protected JComboBox comboBoxWarpMethod;
    protected JLabel labelAngleThreshold;
    protected JSpinner spinnerAngle;
    private JSeparator separator1;
    protected JLabel labelDistanceThreshold;
    protected JSpinner spinnerDistance;
    protected JLabel labelStatusHotkey;
    protected JComboBox comboBoxStatusHotkey;
    protected JLabel labelDurationThreshold;
    protected JSpinner spinnerDuration;
    private JSeparator separator3;
    protected JLabel labelHomeRadius;
    protected JSpinner spinnerHomeRadius;
    protected JLabel labelDimension;
    protected JSpinner spinnerDimension;
    protected JLabel labelSetRadius;
    protected JSpinner spinnerSetRadius;
    protected JLabel labelActionHotkey;
    protected JComboBox comboBoxActionHotkey;
    protected JLabel labelSearchMethod;
    protected JComboBox comboBoxSearchMethod;
    protected JButton buttonDefault;
    protected JButton buttonSubmit;
    protected JLabel labelLearnMethod;
    protected JComboBox comboBoxLearnMethod;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    private JPanel buttonBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

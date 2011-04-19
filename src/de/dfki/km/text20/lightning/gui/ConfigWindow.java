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
import javax.swing.border.*;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("all")
public class ConfigWindow {

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

    private void checkBoxEvaluationActionPerformed(ActionEvent e) {
    }

    private void checkBoxTrainingActionPerformed(ActionEvent e) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        mainFrame = new JFrame();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        labelActionHotkey = new JLabel();
        comboBoxActionHotkey = new JComboBox();
        labelStatusHotkey = new JLabel();
        comboBoxStatusHotkey = new JComboBox();
        labelSound = new JLabel();
        checkBoxSound = new JCheckBox();
        labelDimension = new JLabel();
        spinnerDimension = new JSpinner();
        labelRecalibration = new JLabel();
        checkBoxRecalibration = new JCheckBox();
        buttonClearRecalibration = new JButton();
        panel2 = new JPanel();
        labelEnableMouseWarp = new JLabel();
        checkBoxUseWarp = new JCheckBox();
        labelWarpMethod = new JLabel();
        comboBoxWarpMethod = new JComboBox();
        buttonWarpConfig = new JButton();
        panel3 = new JPanel();
        labelEvaluation = new JLabel();
        checkBoxEvaluation = new JCheckBox();
        labelName = new JLabel();
        textFieldName = new JTextField();
        labelScreenBright = new JLabel();
        comboBoxScreenBright = new JComboBox();
        labelSettingBright = new JLabel();
        comboBoxSettingBright = new JComboBox();
        labelOutputPath = new JLabel();
        buttonSelect = new JButton();
        textFieldOutputPath = new JTextField();
        panel4 = new JPanel();
        labelDetector = new JLabel();
        comboBoxDetector = new JComboBox();
        buttonDetectorConfig = new JButton();
        buttonSubmit = new JButton();
        buttonDefault = new JButton();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== mainFrame ========
        {
            mainFrame.setTitle("Project Lightning (Desktop)");
            mainFrame.setResizable(false);
            Container mainFrameContentPane = mainFrame.getContentPane();
            mainFrameContentPane.setLayout(new GridLayout());

            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.DIALOG_BORDER);
                dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.X_AXIS));

                //======== contentPanel ========
                {
                    contentPanel.setLayout(new FormLayout(
                        "3*(20dlu:grow, $lcgap), 20dlu:grow",
                        "top:default, $lgap, top:48dlu, $lgap, default:grow, $lgap, default"));
                    ((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{1, 3}, {5, 7}});

                    //======== panel1 ========
                    {
                        panel1.setBorder(new TitledBorder("General Options"));
                        panel1.setLayout(new FormLayout(
                            "20dlu:grow, $lcgap, 20dlu:grow",
                            "5*(default, $lgap), default"));

                        //---- labelActionHotkey ----
                        labelActionHotkey.setText("Cursorwarp Key");
                        labelActionHotkey.setIcon(null);
                        panel1.add(labelActionHotkey, cc.xywh(1, 1, 2, 1));

                        //---- comboBoxActionHotkey ----
                        comboBoxActionHotkey.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxActionHotkeyActionPerformed(e);
                            }
                        });
                        panel1.add(comboBoxActionHotkey, cc.xy(3, 1));

                        //---- labelStatusHotkey ----
                        labelStatusHotkey.setText("Key to Enable & Disable");
                        panel1.add(labelStatusHotkey, cc.xywh(1, 3, 2, 1));

                        //---- comboBoxStatusHotkey ----
                        comboBoxStatusHotkey.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxStatusHotkeyActionPerformed(e);
                            }
                        });
                        panel1.add(comboBoxStatusHotkey, cc.xy(3, 3));

                        //---- labelSound ----
                        labelSound.setText("Play Sounds");
                        panel1.add(labelSound, cc.xy(1, 5));

                        //---- checkBoxSound ----
                        checkBoxSound.setSelectedIcon(null);
                        panel1.add(checkBoxSound, cc.xy(3, 5));

                        //---- labelDimension ----
                        labelDimension.setText("Pixels to Consider");
                        labelDimension.setIcon(null);
                        panel1.add(labelDimension, cc.xywh(1, 7, 2, 1));

                        //---- spinnerDimension ----
                        spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                        panel1.add(spinnerDimension, cc.xy(3, 7));

                        //---- labelRecalibration ----
                        labelRecalibration.setText("Use Recalibration");
                        panel1.add(labelRecalibration, cc.xywh(1, 9, 1, 3));
                        panel1.add(checkBoxRecalibration, cc.xy(3, 9));

                        //---- buttonClearRecalibration ----
                        buttonClearRecalibration.setText("Clear Recalibration");
                        panel1.add(buttonClearRecalibration, cc.xy(3, 11));
                    }
                    contentPanel.add(panel1, cc.xywh(1, 1, 3, 3, CellConstraints.FILL, CellConstraints.TOP));

                    //======== panel2 ========
                    {
                        panel2.setBorder(new TitledBorder("Mouse Warping"));
                        panel2.setLayout(new FormLayout(
                            "80dlu:grow, $lcgap, 80dlu:grow",
                            "2*(default, $lgap), default"));

                        //---- labelEnableMouseWarp ----
                        labelEnableMouseWarp.setText("Enable Mouse Warp");
                        panel2.add(labelEnableMouseWarp, cc.xywh(1, 1, 2, 1));

                        //---- checkBoxUseWarp ----
                        checkBoxUseWarp.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                checkBoxUseWarpActionPerformed(e);
                            }
                        });
                        panel2.add(checkBoxUseWarp, cc.xy(3, 1));

                        //---- labelWarpMethod ----
                        labelWarpMethod.setText("Warp Method");
                        panel2.add(labelWarpMethod, cc.xywh(1, 3, 2, 3));
                        panel2.add(comboBoxWarpMethod, cc.xy(3, 3));

                        //---- buttonWarpConfig ----
                        buttonWarpConfig.setText("Configuration");
                        panel2.add(buttonWarpConfig, cc.xy(3, 5));
                    }
                    contentPanel.add(panel2, cc.xywh(5, 1, 3, 1, CellConstraints.FILL, CellConstraints.TOP));

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("Evaluation "));
                        panel3.setLayout(new FormLayout(
                            "80dlu:grow, $lcgap, 80dlu:grow",
                            "5*(default, $lgap), default"));

                        //---- labelEvaluation ----
                        labelEvaluation.setText("Evaluation Mode");
                        panel3.add(labelEvaluation, cc.xywh(1, 1, 2, 1));

                        //---- checkBoxEvaluation ----
                        checkBoxEvaluation.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                checkBoxTrainingActionPerformed(e);
                            }
                        });
                        panel3.add(checkBoxEvaluation, cc.xy(3, 1));

                        //---- labelName ----
                        labelName.setText("Username");
                        panel3.add(labelName, cc.xywh(1, 3, 2, 1));
                        panel3.add(textFieldName, cc.xy(3, 3));

                        //---- labelScreenBright ----
                        labelScreenBright.setText("Screen Brightness");
                        panel3.add(labelScreenBright, cc.xywh(1, 5, 2, 1));
                        panel3.add(comboBoxScreenBright, cc.xy(3, 5));

                        //---- labelSettingBright ----
                        labelSettingBright.setText("Setting Brightness");
                        panel3.add(labelSettingBright, cc.xywh(1, 7, 2, 1));
                        panel3.add(comboBoxSettingBright, cc.xy(3, 7));

                        //---- labelOutputPath ----
                        labelOutputPath.setText("Output Path");
                        panel3.add(labelOutputPath, cc.xywh(1, 9, 1, 3));

                        //---- buttonSelect ----
                        buttonSelect.setText("Select");
                        panel3.add(buttonSelect, cc.xy(3, 9));
                        panel3.add(textFieldOutputPath, cc.xy(3, 11));
                    }
                    contentPanel.add(panel3, cc.xywh(5, 3, 3, 3, CellConstraints.FILL, CellConstraints.FILL));

                    //======== panel4 ========
                    {
                        panel4.setBorder(new TitledBorder("Cursor Warping"));
                        panel4.setLayout(new FormLayout(
                            "20dlu:grow, $lcgap, 20dlu:grow",
                            "default, $lgap, default"));

                        //---- labelDetector ----
                        labelDetector.setText("Text Detector / Warping");
                        panel4.add(labelDetector, cc.xywh(1, 1, 2, 3));

                        //---- comboBoxDetector ----
                        comboBoxDetector.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxSearchMethodActionPerformed(e);
                            }
                        });
                        panel4.add(comboBoxDetector, cc.xy(3, 1));

                        //---- buttonDetectorConfig ----
                        buttonDetectorConfig.setText("Configuration");
                        panel4.add(buttonDetectorConfig, cc.xy(3, 3));
                    }
                    contentPanel.add(panel4, cc.xywh(1, 5, 3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

                    //---- buttonSubmit ----
                    buttonSubmit.setText("Submit");
                    buttonSubmit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonSubmitActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonSubmit, cc.xy(1, 7));

                    //---- buttonDefault ----
                    buttonDefault.setText("Default");
                    buttonDefault.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonDefaultActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonDefault, cc.xy(3, 7));

                    //---- buttonOK ----
                    buttonOK.setText("OK");
                    buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonOKActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonOK, cc.xywh(5, 7, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

                    //---- buttonCancel ----
                    buttonCancel.setText("Cancel");
                    buttonCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonCancelActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonCancel, cc.xy(7, 7));
                }
                dialogPane.add(contentPanel);
            }
            mainFrameContentPane.add(dialogPane);
            mainFrame.setSize(570, 370);
            mainFrame.setLocationRelativeTo(mainFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JFrame mainFrame;
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    protected JLabel labelActionHotkey;
    protected JComboBox comboBoxActionHotkey;
    protected JLabel labelStatusHotkey;
    protected JComboBox comboBoxStatusHotkey;
    protected JLabel labelSound;
    protected JCheckBox checkBoxSound;
    protected JLabel labelDimension;
    protected JSpinner spinnerDimension;
    protected JLabel labelRecalibration;
    protected JCheckBox checkBoxRecalibration;
    protected JButton buttonClearRecalibration;
    private JPanel panel2;
    protected JLabel labelEnableMouseWarp;
    protected JCheckBox checkBoxUseWarp;
    protected JLabel labelWarpMethod;
    protected JComboBox comboBoxWarpMethod;
    protected JButton buttonWarpConfig;
    private JPanel panel3;
    protected JLabel labelEvaluation;
    protected JCheckBox checkBoxEvaluation;
    protected JLabel labelName;
    protected JTextField textFieldName;
    protected JLabel labelScreenBright;
    protected JComboBox comboBoxScreenBright;
    protected JLabel labelSettingBright;
    protected JComboBox comboBoxSettingBright;
    protected JLabel labelOutputPath;
    protected JButton buttonSelect;
    protected JTextField textFieldOutputPath;
    private JPanel panel4;
    protected JLabel labelDetector;
    protected JComboBox comboBoxDetector;
    protected JButton buttonDetectorConfig;
    protected JButton buttonSubmit;
    protected JButton buttonDefault;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

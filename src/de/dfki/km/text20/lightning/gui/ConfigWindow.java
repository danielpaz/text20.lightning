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
        this.mainFrame = new JFrame();
        this.dialogPane = new JPanel();
        this.contentPanel = new JPanel();
        this.panel1 = new JPanel();
        this.labelActionHotkey = new JLabel();
        this.comboBoxActionHotkey = new JComboBox();
        this.labelStatusHotkey = new JLabel();
        this.comboBoxStatusHotkey = new JComboBox();
        this.labelSound = new JLabel();
        this.checkBoxSound = new JCheckBox();
        this.labelDimension = new JLabel();
        this.spinnerDimension = new JSpinner();
        this.labelDetector = new JLabel();
        this.comboBoxDetector = new JComboBox();
        this.buttonDetectorConfig = new JButton();
        this.separator4 = new JSeparator();
        this.panel2 = new JPanel();
        this.labelEnableMouseWarp = new JLabel();
        this.checkBoxUseWarp = new JCheckBox();
        this.labelWarpMethod = new JLabel();
        this.comboBoxWarpMethod = new JComboBox();
        this.buttonWarpConfig = new JButton();
        this.panel3 = new JPanel();
        this.labelEvaluation = new JLabel();
        this.checkBoxEvaluation = new JCheckBox();
        this.labelName = new JLabel();
        this.textFieldName = new JTextField();
        this.labelScreenBright = new JLabel();
        this.textFieldScreenBright = new JTextField();
        this.labelSettingBright = new JLabel();
        this.textFieldSettingBright = new JTextField();
        this.buttonSubmit = new JButton();
        this.buttonDefault = new JButton();
        this.buttonOK = new JButton();
        this.buttonCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== mainFrame ========
        {
            this.mainFrame.setTitle("Project Lightning (Desktop)");
            this.mainFrame.setResizable(false);
            Container mainFrameContentPane = this.mainFrame.getContentPane();
            mainFrameContentPane.setLayout(new GridLayout());

            //======== dialogPane ========
            {
                this.dialogPane.setBorder(Borders.DIALOG_BORDER);
                this.dialogPane.setLayout(new BoxLayout(this.dialogPane, BoxLayout.X_AXIS));

                //======== contentPanel ========
                {
                    this.contentPanel.setLayout(new FormLayout(
                        "2*(pref:grow, $lcgap), 3dlu, $lcgap, 30dlu:grow, $lcgap, default:grow",
                        "top:default, $lgap, top:70dlu, 3*($lgap, top:default), 2*($lgap, default), $lgap, [7dlu,default], 2*($lgap, default), $lgap, 15dlu, 3*($lgap, default)"));
                    ((FormLayout)this.contentPanel.getLayout()).setColumnGroups(new int[][] {{1, 3}, {7, 9}});

                    //======== panel1 ========
                    {
                        this.panel1.setBorder(new TitledBorder("Cursor Warping"));
                        this.panel1.setLayout(new FormLayout(
                            "default:grow, $lcgap, 50dlu:grow",
                            "5*(default, $lgap), default"));

                        //---- labelActionHotkey ----
                        this.labelActionHotkey.setText("Cursorwarp Key");
                        this.labelActionHotkey.setIcon(null);
                        this.panel1.add(this.labelActionHotkey, cc.xywh(1, 1, 2, 1));

                        //---- comboBoxActionHotkey ----
                        this.comboBoxActionHotkey.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxActionHotkeyActionPerformed(e);
                            }
                        });
                        this.panel1.add(this.comboBoxActionHotkey, cc.xy(3, 1));

                        //---- labelStatusHotkey ----
                        this.labelStatusHotkey.setText("Key to Enable & Disable");
                        this.panel1.add(this.labelStatusHotkey, cc.xywh(1, 3, 2, 1));

                        //---- comboBoxStatusHotkey ----
                        this.comboBoxStatusHotkey.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxStatusHotkeyActionPerformed(e);
                            }
                        });
                        this.panel1.add(this.comboBoxStatusHotkey, cc.xy(3, 3));

                        //---- labelSound ----
                        this.labelSound.setText("Play Sound w. Successful");
                        this.panel1.add(this.labelSound, cc.xy(1, 5));

                        //---- checkBoxSound ----
                        this.checkBoxSound.setSelectedIcon(null);
                        this.panel1.add(this.checkBoxSound, cc.xy(3, 5));

                        //---- labelDimension ----
                        this.labelDimension.setText("Pixels to Consider");
                        this.labelDimension.setIcon(null);
                        this.panel1.add(this.labelDimension, cc.xywh(1, 7, 2, 1));

                        //---- spinnerDimension ----
                        this.spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                        this.panel1.add(this.spinnerDimension, cc.xy(3, 7));

                        //---- labelDetector ----
                        this.labelDetector.setText("Text Detector / Warping");
                        this.panel1.add(this.labelDetector, cc.xywh(1, 9, 2, 1));

                        //---- comboBoxDetector ----
                        this.comboBoxDetector.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxSearchMethodActionPerformed(e);
                            }
                        });
                        this.panel1.add(this.comboBoxDetector, cc.xy(3, 9));

                        //---- buttonDetectorConfig ----
                        this.buttonDetectorConfig.setText("text");
                        this.panel1.add(this.buttonDetectorConfig, cc.xywh(1, 11, 3, 1));
                    }
                    this.contentPanel.add(this.panel1, cc.xywh(1, 1, 3, 3));

                    //---- separator4 ----
                    this.separator4.setOrientation(SwingConstants.VERTICAL);
                    this.contentPanel.add(this.separator4, cc.xywh(5, 1, 1, 7));

                    //======== panel2 ========
                    {
                        this.panel2.setBorder(new TitledBorder("Mouse Warping"));
                        this.panel2.setLayout(new FormLayout(
                            "default:grow, $lcgap, 50dlu",
                            "2*(default, $lgap), default"));

                        //---- labelEnableMouseWarp ----
                        this.labelEnableMouseWarp.setText("Enable Mouse Warp");
                        this.panel2.add(this.labelEnableMouseWarp, cc.xywh(1, 1, 2, 1));

                        //---- checkBoxUseWarp ----
                        this.checkBoxUseWarp.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                checkBoxUseWarpActionPerformed(e);
                            }
                        });
                        this.panel2.add(this.checkBoxUseWarp, cc.xy(3, 1));

                        //---- labelWarpMethod ----
                        this.labelWarpMethod.setText("Warp Method");
                        this.panel2.add(this.labelWarpMethod, cc.xywh(1, 3, 2, 1));
                        this.panel2.add(this.comboBoxWarpMethod, cc.xy(3, 3));

                        //---- buttonWarpConfig ----
                        this.buttonWarpConfig.setText("text");
                        this.panel2.add(this.buttonWarpConfig, cc.xywh(1, 5, 3, 1));
                    }
                    this.contentPanel.add(this.panel2, cc.xywh(7, 1, 3, 1));

                    //======== panel3 ========
                    {
                        this.panel3.setBorder(new TitledBorder("Evaluation "));
                        this.panel3.setLayout(new FormLayout(
                            "default:grow, $lcgap, 50dlu",
                            "4*(default, $lgap), default"));

                        //---- labelEvaluation ----
                        this.labelEvaluation.setText("Evaluation Mode");
                        this.panel3.add(this.labelEvaluation, cc.xywh(1, 1, 2, 1));

                        //---- checkBoxEvaluation ----
                        this.checkBoxEvaluation.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                checkBoxTrainingActionPerformed(e);
                            }
                        });
                        this.panel3.add(this.checkBoxEvaluation, cc.xy(3, 1));

                        //---- labelName ----
                        this.labelName.setText("Username");
                        this.panel3.add(this.labelName, cc.xywh(1, 3, 2, 1));
                        this.panel3.add(this.textFieldName, cc.xy(3, 3));

                        //---- labelScreenBright ----
                        this.labelScreenBright.setText("Screen Brightness");
                        this.panel3.add(this.labelScreenBright, cc.xywh(1, 5, 2, 1));
                        this.panel3.add(this.textFieldScreenBright, cc.xy(3, 5));

                        //---- labelSettingBright ----
                        this.labelSettingBright.setText("Setting Brightness");
                        this.panel3.add(this.labelSettingBright, cc.xywh(1, 7, 2, 1));
                        this.panel3.add(this.textFieldSettingBright, cc.xy(3, 7));

                        //---- buttonSubmit ----
                        this.buttonSubmit.setText("Submit");
                        this.buttonSubmit.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                buttonSubmitActionPerformed(e);
                            }
                        });
                        this.panel3.add(this.buttonSubmit, cc.xywh(1, 9, 3, 1));
                    }
                    this.contentPanel.add(this.panel3, cc.xywh(7, 3, 3, 7));

                    //---- buttonDefault ----
                    this.buttonDefault.setText("Default");
                    this.buttonDefault.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonDefaultActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonDefault, cc.xywh(1, 5, 2, 1));

                    //---- buttonOK ----
                    this.buttonOK.setText("OK");
                    this.buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonOKActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonOK, cc.xywh(1, 7, 2, 1));

                    //---- buttonCancel ----
                    this.buttonCancel.setText("Cancel");
                    this.buttonCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonCancelActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonCancel, cc.xy(3, 7));
                }
                this.dialogPane.add(this.contentPanel);
            }
            mainFrameContentPane.add(this.dialogPane);
            this.mainFrame.setSize(670, 610);
            this.mainFrame.setLocationRelativeTo(this.mainFrame.getOwner());
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
    protected JLabel labelDetector;
    protected JComboBox comboBoxDetector;
    protected JButton buttonDetectorConfig;
    private JSeparator separator4;
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
    protected JTextField textFieldScreenBright;
    protected JLabel labelSettingBright;
    protected JTextField textFieldSettingBright;
    protected JButton buttonSubmit;
    protected JButton buttonDefault;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

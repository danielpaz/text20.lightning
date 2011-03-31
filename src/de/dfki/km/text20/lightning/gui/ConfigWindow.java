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
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        mainFrame = new JFrame();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        labelActionHotkey = new JLabel();
        comboBoxActionHotkey = new JComboBox();
        separator4 = new JSeparator();
        separator5 = new JSeparator();
        labelEnableMouseWarp = new JLabel();
        checkBoxUseWarp = new JCheckBox();
        labelStatusHotkey = new JLabel();
        comboBoxStatusHotkey = new JComboBox();
        labelWarpMethod = new JLabel();
        comboBoxWarpMethod = new JComboBox();
        labelDimension = new JLabel();
        spinnerDimension = new JSpinner();
        buttonWarpConfig = new JButton();
        labelSound = new JLabel();
        checkBoxSound = new JCheckBox();
        separator3 = new JSeparator();
        separator1 = new JSeparator();
        labelDetector = new JLabel();
        comboBoxDetector = new JComboBox();
        labelEvaluation = new JLabel();
        checkBoxEvaluation = new JCheckBox();
        buttonDetectorConfig = new JButton();
        labelName = new JLabel();
        textFieldName = new JTextField();
        separator6 = new JSeparator();
        labelScreenBright = new JLabel();
        textFieldScreenBright = new JTextField();
        buttonDefault = new JButton();
        buttonSubmit = new JButton();
        labelSettingBright = new JLabel();
        textFieldSettingBright = new JTextField();
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
                        "4*(30dlu, $lcgap), 3dlu, 4*($lcgap, 30dlu)",
                        "3*(default, $lgap), [7dlu,default], 2*($lgap, default), $lgap, 15dlu, 2*($lgap, default)"));

                    //---- labelActionHotkey ----
                    labelActionHotkey.setText("Action Hotkey");
                    labelActionHotkey.setIcon(null);
                    contentPanel.add(labelActionHotkey, cc.xywh(1, 1, 3, 1));

                    //---- comboBoxActionHotkey ----
                    comboBoxActionHotkey.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            comboBoxActionHotkeyActionPerformed(e);
                        }
                    });
                    contentPanel.add(comboBoxActionHotkey, cc.xywh(5, 1, 3, 1));

                    //---- separator4 ----
                    separator4.setOrientation(SwingConstants.VERTICAL);
                    contentPanel.add(separator4, cc.xy(9, 1));

                    //---- separator5 ----
                    separator5.setOrientation(SwingConstants.VERTICAL);
                    contentPanel.add(separator5, cc.xywh(9, 1, 1, 17));

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

                    //---- labelStatusHotkey ----
                    labelStatusHotkey.setText("Status Hotkey");
                    contentPanel.add(labelStatusHotkey, cc.xywh(1, 3, 3, 1));

                    //---- comboBoxStatusHotkey ----
                    comboBoxStatusHotkey.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            comboBoxStatusHotkeyActionPerformed(e);
                        }
                    });
                    contentPanel.add(comboBoxStatusHotkey, cc.xywh(5, 3, 3, 1));

                    //---- labelWarpMethod ----
                    labelWarpMethod.setText("Warp Method");
                    contentPanel.add(labelWarpMethod, cc.xywh(11, 3, 3, 1));
                    contentPanel.add(comboBoxWarpMethod, cc.xywh(15, 3, 3, 1));

                    //---- labelDimension ----
                    labelDimension.setText("Dimension");
                    labelDimension.setIcon(null);
                    contentPanel.add(labelDimension, cc.xywh(1, 5, 3, 1));

                    //---- spinnerDimension ----
                    spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                    contentPanel.add(spinnerDimension, cc.xywh(5, 5, 3, 1));

                    //---- buttonWarpConfig ----
                    buttonWarpConfig.setText("text");
                    contentPanel.add(buttonWarpConfig, cc.xywh(11, 5, 7, 1));

                    //---- labelSound ----
                    labelSound.setText("Sound");
                    contentPanel.add(labelSound, cc.xy(1, 7));

                    //---- checkBoxSound ----
                    checkBoxSound.setSelectedIcon(null);
                    contentPanel.add(checkBoxSound, cc.xywh(5, 7, 3, 1));
                    contentPanel.add(separator3, cc.xywh(11, 7, 7, 1));
                    contentPanel.add(separator1, cc.xywh(1, 9, 7, 1));

                    //---- labelDetector ----
                    labelDetector.setText("Detector");
                    contentPanel.add(labelDetector, cc.xywh(11, 9, 3, 1));

                    //---- comboBoxDetector ----
                    comboBoxDetector.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            comboBoxSearchMethodActionPerformed(e);
                        }
                    });
                    contentPanel.add(comboBoxDetector, cc.xywh(15, 9, 3, 1));

                    //---- labelEvaluation ----
                    labelEvaluation.setText("Evaluation Mode");
                    contentPanel.add(labelEvaluation, cc.xywh(1, 11, 3, 1));

                    //---- checkBoxEvaluation ----
                    checkBoxEvaluation.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            checkBoxTrainingActionPerformed(e);
                        }
                    });
                    contentPanel.add(checkBoxEvaluation, cc.xywh(5, 11, 3, 1));

                    //---- buttonDetectorConfig ----
                    buttonDetectorConfig.setText("text");
                    contentPanel.add(buttonDetectorConfig, cc.xywh(11, 11, 7, 1));

                    //---- labelName ----
                    labelName.setText("Unsername");
                    contentPanel.add(labelName, cc.xywh(1, 13, 3, 1));
                    contentPanel.add(textFieldName, cc.xywh(5, 13, 3, 1));
                    contentPanel.add(separator6, cc.xywh(11, 13, 7, 1));

                    //---- labelScreenBright ----
                    labelScreenBright.setText("Screen Brightness");
                    contentPanel.add(labelScreenBright, cc.xywh(1, 15, 3, 1));
                    contentPanel.add(textFieldScreenBright, cc.xywh(5, 15, 3, 1));

                    //---- buttonDefault ----
                    buttonDefault.setText("Default");
                    buttonDefault.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonDefaultActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonDefault, cc.xywh(11, 15, 3, 1));

                    //---- buttonSubmit ----
                    buttonSubmit.setText("Submit");
                    buttonSubmit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonSubmitActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonSubmit, cc.xywh(15, 15, 3, 1));

                    //---- labelSettingBright ----
                    labelSettingBright.setText("Setting Brightness");
                    contentPanel.add(labelSettingBright, cc.xywh(1, 17, 3, 1));
                    contentPanel.add(textFieldSettingBright, cc.xywh(5, 17, 3, 1));

                    //---- buttonOK ----
                    buttonOK.setText("OK");
                    buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonOKActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonOK, cc.xywh(11, 17, 3, 1));

                    //---- buttonCancel ----
                    buttonCancel.setText("Cancel");
                    buttonCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonCancelActionPerformed(e);
                        }
                    });
                    contentPanel.add(buttonCancel, cc.xywh(15, 17, 3, 1));
                }
                dialogPane.add(contentPanel);
            }
            mainFrameContentPane.add(dialogPane);
            mainFrame.setSize(430, 290);
            mainFrame.setLocationRelativeTo(mainFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JFrame mainFrame;
    private JPanel dialogPane;
    private JPanel contentPanel;
    protected JLabel labelActionHotkey;
    protected JComboBox comboBoxActionHotkey;
    private JSeparator separator4;
    private JSeparator separator5;
    protected JLabel labelEnableMouseWarp;
    protected JCheckBox checkBoxUseWarp;
    protected JLabel labelStatusHotkey;
    protected JComboBox comboBoxStatusHotkey;
    protected JLabel labelWarpMethod;
    protected JComboBox comboBoxWarpMethod;
    protected JLabel labelDimension;
    protected JSpinner spinnerDimension;
    protected JButton buttonWarpConfig;
    protected JLabel labelSound;
    protected JCheckBox checkBoxSound;
    private JSeparator separator3;
    private JSeparator separator1;
    protected JLabel labelDetector;
    protected JComboBox comboBoxDetector;
    private JLabel labelEvaluation;
    protected JCheckBox checkBoxEvaluation;
    protected JButton buttonDetectorConfig;
    protected JLabel labelName;
    protected JTextField textFieldName;
    private JSeparator separator6;
    protected JLabel labelScreenBright;
    protected JTextField textFieldScreenBright;
    protected JButton buttonDefault;
    protected JButton buttonSubmit;
    protected JLabel labelSettingBright;
    protected JTextField textFieldSettingBright;
    protected JButton buttonOK;
    protected JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

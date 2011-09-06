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

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

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
        this.labelRecalibration = new JLabel();
        this.checkBoxRecalibration = new JCheckBox();
        this.buttonClearRecalibration = new JButton();
        this.panel2 = new JPanel();
        this.labelEnableMouseWarp = new JLabel();
        this.checkBoxUseWarp = new JCheckBox();
        this.labelWarpMethod = new JLabel();
        this.comboBoxWarpMethod = new JComboBox();
        this.buttonWarpConfig = new JButton();
        this.panel4 = new JPanel();
        this.labelDetector = new JLabel();
        this.comboBoxDetector = new JComboBox();
        this.buttonDetectorConfig = new JButton();
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
                        "3*(20dlu:grow, $lcgap), 20dlu:grow",
                        "top:default, $lgap, top:48dlu:grow, $lgap, default"));
                    ((FormLayout)this.contentPanel.getLayout()).setColumnGroups(new int[][] {{1, 3}, {5, 7}});

                    //======== panel1 ========
                    {
                        this.panel1.setBorder(new TitledBorder("General Options"));
                        this.panel1.setLayout(new FormLayout(
                            "20dlu:grow, $lcgap, 20dlu:grow",
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
                        this.labelSound.setText("Play Sounds");
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

                        //---- labelRecalibration ----
                        this.labelRecalibration.setText("Use Recalibration");
                        this.panel1.add(this.labelRecalibration, cc.xywh(1, 9, 1, 3));
                        this.panel1.add(this.checkBoxRecalibration, cc.xy(3, 9));

                        //---- buttonClearRecalibration ----
                        this.buttonClearRecalibration.setText("Clear Recalibration");
                        this.panel1.add(this.buttonClearRecalibration, cc.xy(3, 11));
                    }
                    this.contentPanel.add(this.panel1, cc.xywh(1, 1, 3, 3, CellConstraints.FILL, CellConstraints.FILL));

                    //======== panel2 ========
                    {
                        this.panel2.setBorder(new TitledBorder("Mouse Warping"));
                        this.panel2.setLayout(new FormLayout(
                            "80dlu:grow, $lcgap, 80dlu:grow",
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
                        this.panel2.add(this.labelWarpMethod, cc.xywh(1, 3, 2, 3));
                        this.panel2.add(this.comboBoxWarpMethod, cc.xy(3, 3));

                        //---- buttonWarpConfig ----
                        this.buttonWarpConfig.setText("Configuration");
                        this.panel2.add(this.buttonWarpConfig, cc.xy(3, 5));
                    }
                    this.contentPanel.add(this.panel2, cc.xywh(5, 1, 3, 1, CellConstraints.FILL, CellConstraints.TOP));

                    //======== panel4 ========
                    {
                        this.panel4.setBorder(new TitledBorder("Cursor Warping"));
                        this.panel4.setLayout(new FormLayout(
                            "20dlu:grow, $lcgap, 20dlu:grow",
                            "default, $lgap, default"));

                        //---- labelDetector ----
                        this.labelDetector.setText("Text Detector / Warping");
                        this.panel4.add(this.labelDetector, cc.xywh(1, 1, 2, 3));

                        //---- comboBoxDetector ----
                        this.comboBoxDetector.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                comboBoxSearchMethodActionPerformed(e);
                            }
                        });
                        this.panel4.add(this.comboBoxDetector, cc.xy(3, 1));

                        //---- buttonDetectorConfig ----
                        this.buttonDetectorConfig.setText("Configuration");
                        this.panel4.add(this.buttonDetectorConfig, cc.xy(3, 3));
                    }
                    this.contentPanel.add(this.panel4, cc.xywh(5, 3, 3, 1, CellConstraints.FILL, CellConstraints.FILL));

                    //---- buttonSubmit ----
                    this.buttonSubmit.setText("Submit");
                    this.buttonSubmit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonSubmitActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonSubmit, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

                    //---- buttonDefault ----
                    this.buttonDefault.setText("Default");
                    this.buttonDefault.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonDefaultActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonDefault, cc.xy(3, 5, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

                    //---- buttonOK ----
                    this.buttonOK.setText("OK");
                    this.buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonOKActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonOK, cc.xy(5, 5, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

                    //---- buttonCancel ----
                    this.buttonCancel.setText("Cancel");
                    this.buttonCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            buttonCancelActionPerformed(e);
                        }
                    });
                    this.contentPanel.add(this.buttonCancel, cc.xy(7, 5, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
                }
                this.dialogPane.add(this.contentPanel);
            }
            mainFrameContentPane.add(this.dialogPane);
            this.mainFrame.setSize(730, 335);
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
    protected JLabel labelRecalibration;
    protected JCheckBox checkBoxRecalibration;
    protected JButton buttonClearRecalibration;
    private JPanel panel2;
    protected JLabel labelEnableMouseWarp;
    protected JCheckBox checkBoxUseWarp;
    protected JLabel labelWarpMethod;
    protected JComboBox comboBoxWarpMethod;
    protected JButton buttonWarpConfig;
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

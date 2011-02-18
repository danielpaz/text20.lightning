/*
 * Created by JFormDesigner on Wed Feb 16 12:14:36 CET 2011
 */

package de.dfki.km.click2sight.gui;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.dfki.km.click2sight.MainClass;
import de.dfki.km.click2sight.plugins.MethodManager;
import de.dfki.km.click2sight.plugins.PositionFinder;
import de.dfki.km.click2sight.tools.Hotkey;
import de.dfki.km.click2sight.tools.HotkeyContainer;

/**
 * @author Christoph Käding
 */

@SuppressWarnings("all")
public class ConfigWindow extends JFrame {
    public ConfigWindow(MethodManager manager) {
        this.methodManager = manager;
        
        this.chooser = new JFileChooser() {
            public void approveSelection() {
                if (getSelectedFile().isFile()) {
                    return;
                } else {
                    super.approveSelection();
                    MainClass.setOutputPath(this.getSelectedFile().getAbsolutePath());
                    textFieldOutputPath.setText(MainClass.getOutputPath());
                }
            }
        };
        this.chooser.setMultiSelectionEnabled(false);
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            this.chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }

        initComponents();
        this.checkBoxShowPictures.setSelected(MainClass.showImages());
        this.spinnerDimension.setValue(MainClass.getDimension());
        this.textFieldOutputPath.setText(MainClass.getOutputPath());
        this.manageHotkeyComboBox();
        this.managePositionFinderComboBox();
        setVisible(true);
    }

    private void manageHotkeyComboBox() {
        this.autoSelect = true;
        this.comboBoxActionHotkey.removeAllItems();
        this.comboBoxStatusHotkey.removeAllItems();
        for (int i = 0; i < Hotkey.getHotkeys().size(); i++) {
            if (!Hotkey.getHotkeys().get(i).equals(Hotkey.getCurrentHotkey(2)))
                this.comboBoxActionHotkey.addItem(Hotkey.getHotkeys().get(i));
        }
        for (int i = 0; i < Hotkey.getHotkeys().size(); i++) {
            if (!Hotkey.getHotkeys().get(i).equals(Hotkey.getCurrentHotkey(1)))
                this.comboBoxStatusHotkey.addItem(Hotkey.getHotkeys().get(i));
        }
        this.comboBoxActionHotkey.setSelectedItem(Hotkey.getCurrentHotkey(1));
        this.comboBoxStatusHotkey.setSelectedItem(Hotkey.getCurrentHotkey(2));
        this.autoSelect = false;
    }

    private void managePositionFinderComboBox() {
        this.autoSelect = true;
        for (int i = 0; i < methodManager.getPositionFinder().size(); i++) {
            this.comboBoxSearchMethod.addItem(methodManager.getPositionFinder().get(i));
        }
        if (methodManager.getCurrentPositionFinder() != null)
            this.comboBoxSearchMethod.setSelectedItem(methodManager.getCurrentPositionFinder());
        this.autoSelect = false;
    }

    private void buttonOKActionPerformed(ActionEvent e) {
        MainClass.setShowImages(this.checkBoxShowPictures.isSelected());
        MainClass.setDimension(Integer.parseInt(this.spinnerDimension.getValue().toString()));
        MainClass.setOutputPath(this.textFieldOutputPath.getText());
        methodManager.setCurrentPositionFinder((PositionFinder)comboBoxSearchMethod.getSelectedItem());
        this.dispose();
    }

    private void buttonCancelActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void buttonSelectActionPerformed(ActionEvent e) {
        this.chooser.showOpenDialog(this);
    }

    private void comboBoxActionHotkeyActionPerformed(ActionEvent e) {
        if (!this.autoSelect) {
            Hotkey.setHotkey(1, ((HotkeyContainer)comboBoxActionHotkey.getSelectedItem()));
            this.manageHotkeyComboBox();
        }
    }

    private void comboBoxStatusHotkeyActionPerformed(ActionEvent e) {
        if (!this.autoSelect) {
            Hotkey.setHotkey(2, ((HotkeyContainer)comboBoxStatusHotkey.getSelectedItem()));
            this.manageHotkeyComboBox();
        }
    }

    private void comboBoxSearchMethodActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void comboBoxLearnMethodActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Christoph Käding
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        checkBoxShowPictures = new JCheckBox();
        label2 = new JLabel();
        spinnerDimension = new JSpinner();
        label3 = new JLabel();
        buttonSelect = new JButton();
        textFieldOutputPath = new JTextField();
        label4 = new JLabel();
        comboBoxActionHotkey = new JComboBox();
        label5 = new JLabel();
        comboBoxStatusHotkey = new JComboBox();
        label6 = new JLabel();
        comboBoxSearchMethod = new JComboBox();
        label7 = new JLabel();
        comboBoxLearnMethod = new JComboBox();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        buttonBar = new JPanel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Click2Sight");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);

            // JFormDesigner evaluation mark
            dialogPane.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0), "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), java.awt.Color.red), dialogPane.getBorder()));
            dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName()))
                        throw new RuntimeException();
                }
            });

            dialogPane.setLayout(new GridLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout("4*(30dlu, $lcgap), 8*(default, $lcgap), default", "19*(default, $lgap), default"));

                //---- label1 ----
                label1.setText("show images");
                contentPanel.add(label1, cc.xywh(1, 1, 3, 1));
                contentPanel.add(checkBoxShowPictures, cc.xywh(5, 1, 3, 1));

                //---- label2 ----
                label2.setText("dimesnion");
                contentPanel.add(label2, cc.xywh(1, 3, 3, 1));

                //---- spinnerDimension ----
                spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                contentPanel.add(spinnerDimension, cc.xywh(5, 3, 3, 1));

                //---- label3 ----
                label3.setText("output directory");
                contentPanel.add(label3, cc.xywh(1, 5, 3, 1));

                //---- buttonSelect ----
                buttonSelect.setText("Select");
                buttonSelect.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonSelectActionPerformed(e);
                    }
                });
                contentPanel.add(buttonSelect, cc.xywh(5, 5, 3, 1));
                contentPanel.add(textFieldOutputPath, cc.xywh(1, 7, 7, 1));

                //---- label4 ----
                label4.setText("action hotkey");
                contentPanel.add(label4, cc.xywh(1, 9, 3, 1));

                //---- comboBoxActionHotkey ----
                comboBoxActionHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxActionHotkeyActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxActionHotkey, cc.xywh(5, 9, 3, 1));

                //---- label5 ----
                label5.setText("status hotkey");
                contentPanel.add(label5, cc.xywh(1, 11, 3, 1));

                //---- comboBoxStatusHotkey ----
                comboBoxStatusHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxStatusHotkeyActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxStatusHotkey, cc.xywh(5, 11, 3, 1));

                //---- label6 ----
                label6.setText("search method");
                contentPanel.add(label6, cc.xywh(1, 13, 3, 1));

                //---- comboBoxSearchMethod ----
                comboBoxSearchMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxSearchMethodActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxSearchMethod, cc.xywh(5, 13, 3, 1));

                //---- label7 ----
                label7.setText("learn method");
                contentPanel.add(label7, cc.xywh(1, 15, 3, 1));

                //---- comboBoxLearnMethod ----
                comboBoxLearnMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxLearnMethodActionPerformed(e);
                    }
                });
                contentPanel.add(comboBoxLearnMethod, cc.xywh(5, 15, 3, 1));

                //---- buttonOK ----
                buttonOK.setText("OK");
                buttonOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonOKActionPerformed(e);
                    }
                });
                contentPanel.add(buttonOK, cc.xywh(1, 17, 3, 1));

                //---- buttonCancel ----
                buttonCancel.setText("Cancel");
                buttonCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonCancelActionPerformed(e);
                    }
                });
                contentPanel.add(buttonCancel, cc.xywh(5, 17, 3, 1));

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    buttonBar.setLayout(new CardLayout());
                }
                contentPanel.add(buttonBar, cc.xy(11, 23));
            }
            dialogPane.add(contentPanel);
        }
        contentPane.add(dialogPane);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Christoph Käding
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JCheckBox checkBoxShowPictures;
    private JLabel label2;
    private JSpinner spinnerDimension;
    private JLabel label3;
    private JButton buttonSelect;
    private JTextField textFieldOutputPath;
    private JLabel label4;
    private JComboBox comboBoxActionHotkey;
    private JLabel label5;
    private JComboBox comboBoxStatusHotkey;
    private JLabel label6;
    private JComboBox comboBoxSearchMethod;
    private JLabel label7;
    private JComboBox comboBoxLearnMethod;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttonBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private JFileChooser chooser;
    private boolean autoSelect;
    private MethodManager methodManager;
}

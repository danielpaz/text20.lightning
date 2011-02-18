/*
 * Created by JFormDesigner on Wed Feb 16 12:14:36 CET 2011
 */

package de.dfki.km.text20.lightning.gui;

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

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.plugins.MethodManager;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.tools.Hotkey;
import de.dfki.km.text20.lightning.tools.HotkeyContainer;

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
        methodManager.setCurrentPositionFinder((SaliencyDetector)comboBoxSearchMethod.getSelectedItem());
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
        // Generated using JFormDesigner non-commercial license
        this.dialogPane = new JPanel();
        this.contentPanel = new JPanel();
        this.label1 = new JLabel();
        this.checkBoxShowPictures = new JCheckBox();
        this.label2 = new JLabel();
        this.spinnerDimension = new JSpinner();
        this.label3 = new JLabel();
        this.buttonSelect = new JButton();
        this.textFieldOutputPath = new JTextField();
        this.label4 = new JLabel();
        this.comboBoxActionHotkey = new JComboBox();
        this.label5 = new JLabel();
        this.comboBoxStatusHotkey = new JComboBox();
        this.label6 = new JLabel();
        this.comboBoxSearchMethod = new JComboBox();
        this.label7 = new JLabel();
        this.comboBoxLearnMethod = new JComboBox();
        this.buttonOK = new JButton();
        this.buttonCancel = new JButton();
        this.buttonBar = new JPanel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Click2Sight");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout());

        //======== dialogPane ========
        {
            this.dialogPane.setBorder(Borders.DIALOG_BORDER);
            this.dialogPane.setLayout(new GridLayout());

            //======== contentPanel ========
            {
                this.contentPanel.setLayout(new FormLayout(
                    "4*(30dlu, $lcgap), 8*(default, $lcgap), default",
                    "19*(default, $lgap), default"));

                //---- label1 ----
                this.label1.setText("show images");
                this.contentPanel.add(this.label1, cc.xywh(1, 1, 3, 1));
                this.contentPanel.add(this.checkBoxShowPictures, cc.xywh(5, 1, 3, 1));

                //---- label2 ----
                this.label2.setText("dimesnion");
                this.contentPanel.add(this.label2, cc.xywh(1, 3, 3, 1));

                //---- spinnerDimension ----
                this.spinnerDimension.setModel(new SpinnerNumberModel(0, 0, 999, 1));
                this.contentPanel.add(this.spinnerDimension, cc.xywh(5, 3, 3, 1));

                //---- label3 ----
                this.label3.setText("output directory");
                this.contentPanel.add(this.label3, cc.xywh(1, 5, 3, 1));

                //---- buttonSelect ----
                this.buttonSelect.setText("Select");
                this.buttonSelect.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonSelectActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.buttonSelect, cc.xywh(5, 5, 3, 1));
                this.contentPanel.add(this.textFieldOutputPath, cc.xywh(1, 7, 7, 1));

                //---- label4 ----
                this.label4.setText("action hotkey");
                this.contentPanel.add(this.label4, cc.xywh(1, 9, 3, 1));

                //---- comboBoxActionHotkey ----
                this.comboBoxActionHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxActionHotkeyActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.comboBoxActionHotkey, cc.xywh(5, 9, 3, 1));

                //---- label5 ----
                this.label5.setText("status hotkey");
                this.contentPanel.add(this.label5, cc.xywh(1, 11, 3, 1));

                //---- comboBoxStatusHotkey ----
                this.comboBoxStatusHotkey.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxStatusHotkeyActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.comboBoxStatusHotkey, cc.xywh(5, 11, 3, 1));

                //---- label6 ----
                this.label6.setText("search method");
                this.contentPanel.add(this.label6, cc.xywh(1, 13, 3, 1));

                //---- comboBoxSearchMethod ----
                this.comboBoxSearchMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxSearchMethodActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.comboBoxSearchMethod, cc.xywh(5, 13, 3, 1));

                //---- label7 ----
                this.label7.setText("learn method");
                this.contentPanel.add(this.label7, cc.xywh(1, 15, 3, 1));

                //---- comboBoxLearnMethod ----
                this.comboBoxLearnMethod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxLearnMethodActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.comboBoxLearnMethod, cc.xywh(5, 15, 3, 1));

                //---- buttonOK ----
                this.buttonOK.setText("OK");
                this.buttonOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonOKActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.buttonOK, cc.xywh(1, 17, 3, 1));

                //---- buttonCancel ----
                this.buttonCancel.setText("Cancel");
                this.buttonCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonCancelActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.buttonCancel, cc.xywh(5, 17, 3, 1));

                //======== buttonBar ========
                {
                    this.buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    this.buttonBar.setLayout(new CardLayout());
                }
                this.contentPanel.add(this.buttonBar, cc.xy(11, 23));
            }
            this.dialogPane.add(this.contentPanel);
        }
        contentPane.add(this.dialogPane);
        setSize(295, 345);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
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

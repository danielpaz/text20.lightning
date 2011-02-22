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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
 * This is the configuration window which is shown after a click on the 'configuration' button of the tray menu. 
 * Here are all the things shown, which are important an should be changeable.
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
    public ConfigWindow(MethodManager manager) {
        this.methodManager = manager;

        // create file chooser for outputpath
        this.chooser = new JFileChooser() {

            // react on selection
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();
                MainClass.getProperties().setOutputPath(this.getSelectedFile().getAbsolutePath());
                textFieldOutputPath.setText(MainClass.getProperties().getOutputPath());
            }
        };
        this.chooser.setMultiSelectionEnabled(false);
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // build basic components
        initComponents();
        
        // take values of global properties and preselect them
        this.checkBoxShowPictures.setSelected(MainClass.getProperties().isShowImages());
        this.spinnerDimension.setValue(MainClass.getProperties().getDimension());
        this.textFieldOutputPath.setText(MainClass.getProperties().getOutputPath());
        
        // manage comboboxes
        this.manageHotkeyComboBox();
        this.manageComboBoxSaliencyDetector();
        
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
            if (!Hotkey.getInstance().getHotkeys().get(i).equals(Hotkey.getInstance().getCurrentHotkey(2)))
                this.comboBoxActionHotkey.addItem(Hotkey.getInstance().getHotkeys().get(i));
        }
        for (int i = 0; i < Hotkey.getInstance().getHotkeys().size(); i++) {
            if (!Hotkey.getInstance().getHotkeys().get(i).equals(Hotkey.getInstance().getCurrentHotkey(1)))
                this.comboBoxStatusHotkey.addItem(Hotkey.getInstance().getHotkeys().get(i));
        }
        
        // preselect property values
        this.comboBoxActionHotkey.setSelectedItem(Hotkey.getInstance().getCurrentHotkey(1));
        this.comboBoxStatusHotkey.setSelectedItem(Hotkey.getInstance().getCurrentHotkey(2));
        
        this.autoSelect = false;
    }

    /**
     * Takes the list of available plugins and shows them in the combobox.
     */
    private void manageComboBoxSaliencyDetector() {
        this.autoSelect = true;
        
        // add all saliency detectors to the combobox
        for (int i = 0; i < this.methodManager.getSaliencyDetectors().size(); i++) {
            this.comboBoxSearchMethod.addItem(this.methodManager.getSaliencyDetectors().get(i));
        }
        
        // preselect the current one
        if (this.methodManager.getCurrentSaliencyDetector() != null)
            this.comboBoxSearchMethod.setSelectedItem(this.methodManager.getCurrentSaliencyDetector());

        // the whole plugin is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
        // TODO: change that only a kind of identifier is added to the combobox
        this.comboBoxSearchMethod.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SaliencyDetector) {
                    setText(((SaliencyDetector) value).getDisplayName());
                }
                return this;
            }
        });
        
        this.autoSelect = false;
    }

    /**
     * Fired if the OK button is clicked. All changes were applied.
     * 
     * @param e
     */
    private void buttonOKActionPerformed(ActionEvent e) {
        
        // change variables in the properties and in the method manager
        MainClass.getProperties().setShowImages(this.checkBoxShowPictures.isSelected());
        MainClass.getProperties().setDimension(Integer.parseInt(this.spinnerDimension.getValue().toString()));
        MainClass.getProperties().setOutputPath(this.textFieldOutputPath.getText());
        // TODO: do these also in properties, see MethodManager class
        this.methodManager.setCurrentSaliencyDetector((SaliencyDetector) this.comboBoxSearchMethod.getSelectedItem());
        
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
     * auto generated code, initializes gui components
     */
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
                this.contentPanel.setLayout(new FormLayout("4*(30dlu, $lcgap), 8*(default, $lcgap), default", "19*(default, $lgap), default"));

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
                    @SuppressWarnings("synthetic-access")
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
                    @SuppressWarnings("synthetic-access")
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
                    @SuppressWarnings("synthetic-access")
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
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        comboBoxLearnMethodActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.comboBoxLearnMethod, cc.xywh(5, 15, 3, 1));

                //---- buttonOK ----
                this.buttonOK.setText("OK");
                this.buttonOK.addActionListener(new ActionListener() {
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonOKActionPerformed(e);
                    }
                });
                this.contentPanel.add(this.buttonOK, cc.xywh(1, 17, 3, 1));

                //---- buttonCancel ----
                this.buttonCancel.setText("Cancel");
                this.buttonCancel.addActionListener(new ActionListener() {
                    @SuppressWarnings("synthetic-access")
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

    /** filechooser for outputpath */
    private JFileChooser chooser;
    
    /**
     * manageHotkeyComboBox() changes the items and selection of the actionHotkey and the statusHotkey comboboxes.
     * These two boxes have thier own actionlisteners which call manageHotkeyComboBox(). So an endless ring of calls is created when anything is changed.
     * This boolean shows if the action event is fired by an automatic or manual selection of these comboboxes.
     */
    private boolean autoSelect;
    
    /** The method manager is needed to display and change the available plugins. */
    private MethodManager methodManager;
}

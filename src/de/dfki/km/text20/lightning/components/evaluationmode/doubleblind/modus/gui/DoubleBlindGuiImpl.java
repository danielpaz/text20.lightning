/*
 * DoubleBlindGuiImpl.java
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
 */
package de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.modus.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.modus.DoubleBlindMode;
import de.dfki.km.text20.lightning.plugins.PluginInformation;

/**
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class DoubleBlindGuiImpl extends DoubleBlindGui implements ActionListener {

    /** */
    private DoubleBlindMode blindMode;

    /** */
    private boolean autoSelect;

    /** */
    private int one;

    /** */
    private int two;

    /** frame for configuration guis of plugins */
    private JFrame child;

    /** timer for window focus */
    private Timer timer;

    /**
     * creates instance, initializes variables and opens gui
     * 
     * @param blindMode
     */
    public DoubleBlindGuiImpl(DoubleBlindMode blindMode) {

        // initialize variables
        this.blindMode = blindMode;
        this.autoSelect = false;
        this.one = 0;
        this.two = 1;
        this.child = null;

        // handle to few detectors
        if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size() < 2) {
            this.buttonCancel.addActionListener(this);
            return;
        }

        // initialize comboboxes
        this.comboBoxOne.setRenderer(this.detectorRenderer());
        this.comboBoxTwo.setRenderer(this.detectorRenderer());
        this.comboboxActionPerformed();
        this.buttonTwoConfig.setEnabled(((PluginInformation) this.comboBoxTwo.getSelectedItem()).isGuiAvailable());
        this.buttonOneConfig.setEnabled(((PluginInformation) this.comboBoxOne.getSelectedItem()).isGuiAvailable());

        // add action listener
        this.buttonStart.addActionListener(this);
        this.buttonCancel.addActionListener(this);
        this.comboBoxOne.addActionListener(this);
        this.comboBoxTwo.addActionListener(this);
        this.buttonOneConfig.addActionListener(this);
        this.buttonTwoConfig.addActionListener(this);

        // initialize timer
        this.timer = new Timer(500, new ActionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (child.isShowing()) return;
                timer.stop();
                setEnabled(true);
                requestFocus();
            }
        });

        // show gui
        this.setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (this.autoSelect) {
            return;
        } else if (event.getSource() == this.buttonStart) {
            this.buttonStartActionPerformed();
        } else if (event.getSource() == this.buttonCancel) {
            this.buttonCancelActionPerformed();
        } else if (event.getSource() == this.comboBoxOne) {
            this.one = ((PluginInformation) this.comboBoxOne.getSelectedItem()).getId();
            this.buttonOneConfig.setEnabled(((PluginInformation) this.comboBoxOne.getSelectedItem()).isGuiAvailable());
            this.comboboxActionPerformed();
        } else if (event.getSource() == this.comboBoxTwo) {
            this.two = ((PluginInformation) this.comboBoxTwo.getSelectedItem()).getId();
            this.buttonTwoConfig.setEnabled(((PluginInformation) this.comboBoxTwo.getSelectedItem()).isGuiAvailable());
            this.comboboxActionPerformed();
        } else if (event.getSource() == this.buttonOneConfig){
            this.buttonOneConfigActionPerformed();
        }else if (event.getSource() == this.buttonTwoConfig){
            this.buttonTwoConfigActionPerformed();
        }
    }

    /**
     * Fired if the Config button one is clicked. Shows the configdialog.
     */
    private void buttonOneConfigActionPerformed() {
        this.child = MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(((PluginInformation) this.comboBoxOne.getSelectedItem()).getId()).getGui();
        this.child.setVisible(true);
        this.setEnabled(false);
        this.timer.start();
    }

    /**
     * Fired if the Config button two is clicked. Shows the configdialog.
     */
    private void buttonTwoConfigActionPerformed() {
        this.child = MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(((PluginInformation) this.comboBoxTwo.getSelectedItem()).getId()).getGui();
        this.child.setVisible(true);
        this.setEnabled(false);
        this.timer.start();
    }
    
    private void buttonStartActionPerformed() {
        // set values
        this.blindMode.setOne(((PluginInformation) this.comboBoxOne.getSelectedItem()).getId());
        this.blindMode.setTwo(((PluginInformation) this.comboBoxTwo.getSelectedItem()).getId());
        this.blindMode.setTime(Integer.parseInt(this.spinnerTime.getValue().toString()));
        
        // close gui
        this.dispose();

        // start mode
        this.blindMode.start();
    }

    private void buttonCancelActionPerformed() {
        this.dispose();
    }

    private void comboboxActionPerformed() {
        this.autoSelect = true;

        // clean up
        this.comboBoxOne.removeAllItems();
        this.comboBoxTwo.removeAllItems();

        // combobox one
        for (int i = 0; i < MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size(); i++) {
            if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation().getId() != this.two)
                this.comboBoxOne.addItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation());
        }
        this.comboBoxOne.setSelectedItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(this.one).getInformation());

        // combobox two
        for (int i = 0; i < MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().size(); i++) {
            if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation().getId() != this.one)
                this.comboBoxTwo.addItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(i).getInformation());
        }
        this.comboBoxTwo.setSelectedItem(MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(this.two).getInformation());

        this.autoSelect = false;
    }

    /**
     * the whole plugin information is added to the combobox, so here the displayname is changed from .toString() to .getDisplayName()
     * 
     * @return a changed default renderer
     */
    private DefaultListCellRenderer detectorRenderer() {
        return new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // set new displayed attribute
                if (value instanceof PluginInformation) {
                    setText(((PluginInformation) value).getDisplayName());
                    setToolTipText(((PluginInformation) value).getToolTip());
                }

                return this;
            }
        };
    }
}

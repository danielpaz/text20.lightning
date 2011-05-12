/*
 * ContentPanelConfigImpl.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * first content panel
 * contains configurations
 * 
 * @author Christoph Käding
 */
@SuppressWarnings("serial")
public class ContentPanelConfigImpl extends ContentPanelConfig implements ActionListener {

    /** path to coordinates file */
    private String pathCoordinates;

    /** path to text file */
    private String pathText;

    /** current chooser */
    private JFileChooser chooser;

    /**
     * indicates which button was clicked
     * 
     * true = buttonSelectText
     * false = buttonSelectCoordinates
     */
    private boolean whichButton;

    /** 
     * creates new instance and initializes variables
     */
    public ContentPanelConfigImpl() {
        // initialize chooser
        this.chooser = new JFileChooser() {
            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            public void approveSelection() {
                super.approveSelection();
                if (whichButton) {
                    textFieldPathText.setText(this.getSelectedFile().getAbsolutePath());
                    pathText = this.getSelectedFile().getAbsolutePath();
                } else {
                    textFieldPathCoordinates.setText(this.getSelectedFile().getAbsolutePath());
                    pathCoordinates = this.getSelectedFile().getAbsolutePath();
                }
            }
        };

        // set behavior of this chooser
        this.chooser.setMultiSelectionEnabled(false);
        this.chooser.setAcceptAllFileFilterUsed(false);
        this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.chooser.setFileFilter(new FileFilter() {

            // filter string, only files with this extension and directories will be shown
            private String extension = ".xml";

            @Override
            public String getDescription() {
                return this.extension;
            }

            // set filter
            @Override
            public boolean accept(File file) {
                if (file == null) return false;

                if (file.isDirectory()) return true;

                return file.getName().toLowerCase().endsWith(this.extension);
            }
        });

        // initialize checkboxes
        this.checkBoxDetector.setSelected(true);
        this.checkBoxPrecision.setSelected(true);
        this.checkBoxWarper.setSelected(true);
        
        // initialize Strings
        this.pathCoordinates = "";
        this.pathText = "";

        // add action listeners
        this.checkBoxDetector.addActionListener(this);
        this.checkBoxPrecision.addActionListener(this);
        this.checkBoxWarper.addActionListener(this);
        this.buttonSelectCoordinates.addActionListener(this);
        this.buttonSelectText.addActionListener(this);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonSelectCoordinates) {
            this.buttonSelectCoordinatesActionPerformed();
        } else if (event.getSource() == this.buttonSelectText) {
            this.buttonSelectTextActionPerformed();
        } else if (event.getSource() == this.checkBoxDetector) {
            this.checkboxEvaluateDetectorActionPerformed();
        } else if (event.getSource() == this.checkBoxPrecision) {
            this.checkboxEvaluatePrecisionActionPerformed();
        } else if (event.getSource() == this.checkBoxWarper) {
            this.checkboxEvaluateWarperActionPerformed();
        }
    }

    /** 
     * fired if the buttonSelectCoordinates is clicked
     * opens chooser
     */
    private void buttonSelectCoordinatesActionPerformed() {
        this.whichButton = false;
        this.chooser.showOpenDialog(this);
    }

    /** 
     * fired if the buttonSelectText is clicked
     * opens chooser
     */
    private void buttonSelectTextActionPerformed() {
        this.whichButton = true;
        this.chooser.showOpenDialog(this);
    }

    /**
     * fired if the checkbox detector was clicked
     * disables/enables gui elements
     */
    private void checkboxEvaluateDetectorActionPerformed() {
        this.labelText.setEnabled(this.checkBoxDetector.isSelected() || this.checkBoxWarper.isSelected());
        this.buttonSelectText.setEnabled(this.checkBoxDetector.isSelected() || this.checkBoxWarper.isSelected());
        this.textFieldPathText.setEnabled(this.checkBoxDetector.isSelected() || this.checkBoxWarper.isSelected());
    }

    /**
     * fired if the checkbox warper was clicked
     * disables/enables gui elements
     */
    private void checkboxEvaluateWarperActionPerformed() {
        this.labelText.setEnabled(this.checkBoxDetector.isSelected() || this.checkBoxWarper.isSelected());
        this.buttonSelectText.setEnabled(this.checkBoxDetector.isSelected() || this.checkBoxWarper.isSelected());
        this.textFieldPathText.setEnabled(this.checkBoxDetector.isSelected() || this.checkBoxWarper.isSelected());
    }

    /**
     * fired if the checkbox precision was clicked
     * disables/enables gui elements
     */
    private void checkboxEvaluatePrecisionActionPerformed() {
        this.labelCoordinates.setEnabled(this.checkBoxPrecision.isSelected());
        this.buttonSelectCoordinates.setEnabled(this.checkBoxPrecision.isSelected());
        this.textFieldPathCoordinates.setEnabled(this.checkBoxPrecision.isSelected());
    }

    /**
     * @return the evaluateWarp
     */
    public boolean isEvaluateWarp() {
        return this.checkBoxWarper.isSelected();
    }

    /**
     * @return the evaluateDetector
     */
    public boolean isEvaluateDetector() {
        return this.checkBoxDetector.isSelected();
    }

    /**
     * @return the evaluatePrecision
     */
    public boolean isEvaluatePrecision() {
        return this.checkBoxPrecision.isSelected();
    }

    /**
     * @return the pathCoordinates
     */
    public String getPathCoordinates() {
        return this.pathCoordinates;
    }

    /**
     * @return the pathText
     */
    public String getPathText() {
        return this.pathText;
    }
}

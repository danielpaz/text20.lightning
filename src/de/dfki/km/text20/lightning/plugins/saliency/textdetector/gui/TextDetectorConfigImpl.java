/*
 * CoverageDetectorConfigImpl.java
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
package de.dfki.km.text20.lightning.plugins.saliency.textdetector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpinnerNumberModel;

import de.dfki.km.text20.lightning.plugins.saliency.textdetector.TextDetectorProperties;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class TextDetectorConfigImpl extends TextDetectorConfig implements ActionListener {

    private TextDetectorProperties properties;

    /**
     * 
     */
    public TextDetectorConfigImpl() {
        // initialize variables
        this.properties = TextDetectorProperties.getInstance();
        
        // add listener
        this.buttonCancel.addActionListener(this);
        this.buttonDefault.addActionListener(this);
        this.buttonOK.addActionListener(this);

        // preselect values
        this.initializeValues();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonCancel) this.buttonCancelActionPerformed();
        else if (event.getSource() == this.buttonDefault) this.buttonDefaultActionPerformed();
        else if (event.getSource() == this.buttonOK) this.buttonOkActionPerformed();
    }

    private void buttonOkActionPerformed() {
        // set values
        this.properties.setSensitivity(Double.parseDouble(this.spinnerSensitivity.getValue().toString()));
        this.properties.setLetterHeight(Integer.parseInt(this.spinnerLetterHeight.getValue().toString()));
        this.properties.setLineSize(Integer.parseInt(this.spinnerLineSize.getValue().toString()));
        this.properties.setDebug(this.checkBoxDebug.isSelected());
        this.properties.setThreshold(Double.parseDouble(this.spinnerThreshold.getValue().toString()));

        // dispose
        this.dispose();
    }

    private void buttonCancelActionPerformed() {
        this.dispose();
    }

    private void buttonDefaultActionPerformed() {
        // restore defaults
        this.properties.restoreDefault();

        // select values
        this.initializeValues();
    }

    @SuppressWarnings("boxing")
    private void initializeValues() {
        this.spinnerSensitivity.setModel(new SpinnerNumberModel(this.properties.getSenitivity(), 0.1, Double.MAX_VALUE, 0.1));
        this.spinnerLetterHeight.setValue(this.properties.getLetterHeight());
        this.spinnerLineSize.setValue(this.properties.getLineSize());
        this.checkBoxDebug.setSelected(this.properties.isDebug());
        this.spinnerThreshold.setModel(new SpinnerNumberModel(this.properties.getThreshold(), 0, 100, 0.1));
    }
}

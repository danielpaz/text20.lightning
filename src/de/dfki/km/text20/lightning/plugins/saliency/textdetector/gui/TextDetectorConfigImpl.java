/*
 * TextDetectorConfigImpl.java
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
        this.checkBoxMerge.addActionListener(this);
        this.checkBoxDelete.addActionListener(this);

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
        else if (event.getSource() == this.checkBoxMerge)
            this.checkBockUseMergeActionPerformed();
        else if (event.getSource() == this.checkBoxDelete)
            this.checkBockUseDeleteActionPerformed();
    }

    private void buttonOkActionPerformed() {
        // set values
        this.properties.setDestinyFact(Double.parseDouble(this.spinnerDestinyFactor.getValue().toString()));
        this.properties.setDist1(Integer.parseInt(this.spinnerDist1.getValue().toString()));
        this.properties.setDist2(Integer.parseInt(this.spinnerDist2.getValue().toString()));
        this.properties.setDistFact(Double.parseDouble(this.spinnerDistFac.getValue().toString()));
        this.properties.setLetterHeight(Integer.parseInt(this.spinnerLetterHeight.getValue().toString()));
        this.properties.setLineSize(Integer.parseInt(this.spinnerLineSize.getValue().toString()));
        this.properties.setMass(Integer.parseInt(this.spinnerMass.getValue().toString()));
        this.properties.setStemSize(Integer.parseInt(this.spinnerStemSize.getValue().toString()));
        this.properties.setDebug(this.checkBoxDebug.isSelected());
        this.properties.setUseMerge(this.checkBoxMerge.isSelected());
        this.properties.setUseDelete(this.checkBoxDelete.isSelected());
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

    private void checkBockUseMergeActionPerformed() {
        this.labelDelete.setEnabled(this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(this.checkBoxMerge.isSelected());
        this.labelDestinyFactor.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDestinyFactor.setEnabled(this.checkBoxMerge.isSelected());
        this.labelDist1.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDist1.setEnabled(this.checkBoxMerge.isSelected());
        this.labelDist2.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDist2.setEnabled(this.checkBoxMerge.isSelected());
        this.labelDistFac.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDistFac.setEnabled(this.checkBoxMerge.isSelected());
        this.labelMass.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerMass.setEnabled(this.checkBoxMerge.isSelected());
        this.labelStemSize.setEnabled((this.checkBoxDelete.isSelected() && this.checkBoxMerge.isSelected()));
        this.spinnerStemSize.setEnabled((this.checkBoxDelete.isSelected() && this.checkBoxMerge.isSelected()));
        this.labelDelete.setEnabled(this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(this.checkBoxMerge.isSelected());
    }

    private void checkBockUseDeleteActionPerformed() {
        this.labelStemSize.setEnabled((this.checkBoxDelete.isSelected() && this.checkBoxMerge.isSelected()));
        this.spinnerStemSize.setEnabled((this.checkBoxDelete.isSelected() && this.checkBoxMerge.isSelected()));
    }

    @SuppressWarnings("boxing")
    private void initializeValues() {
        this.checkBoxMerge.setSelected(this.properties.isUseMerge());
        this.checkBoxDelete.setSelected(this.properties.isUseDelete());
        this.spinnerDestinyFactor.setModel(new SpinnerNumberModel(this.properties.getDistFact(), 0.1, Double.MAX_VALUE, 0.1));
        this.labelDestinyFactor.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDestinyFactor.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDist1.setValue(this.properties.getDist1());
        this.labelDist1.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDist1.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDist2.setValue(this.properties.getDist2());
        this.labelDist2.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDist2.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDistFac.setModel(new SpinnerNumberModel(this.properties.getDistFact(), 0.1, Double.MAX_VALUE, 0.1));
        this.labelDistFac.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerDistFac.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerLetterHeight.setValue(this.properties.getLetterHeight());
        this.spinnerLineSize.setValue(this.properties.getLineSize());
        this.spinnerMass.setValue(this.properties.getMass());
        this.labelMass.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerMass.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerStemSize.setValue(this.properties.getStemSize());
        this.labelStemSize.setEnabled((this.checkBoxDelete.isSelected() && this.checkBoxMerge.isSelected()));
        this.spinnerStemSize.setEnabled((this.checkBoxDelete.isSelected() && this.checkBoxMerge.isSelected()));
        this.checkBoxDebug.setSelected(this.properties.isDebug());
        this.labelDelete.setEnabled(this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(this.checkBoxMerge.isSelected());
        this.spinnerThreshold.setModel(new SpinnerNumberModel(this.properties.getThreshold(), 0, 100, 0.1));
    }
}

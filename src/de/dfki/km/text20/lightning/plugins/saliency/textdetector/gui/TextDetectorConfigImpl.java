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
public class TextDetectorConfigImpl extends TextDetectorConfig implements ActionListener {

    private TextDetectorProperties properties;

    /**
     * 
     */
    @SuppressWarnings("boxing")
    public TextDetectorConfigImpl() {
        // initialize variables
        this.properties = TextDetectorProperties.getInstance();

        // add listener
        this.buttonCancel.addActionListener(this);
        this.buttonDefault.addActionListener(this);
        this.buttonOK.addActionListener(this);

        // preselect values
        this.spinnerDestinyFactor.setModel(new SpinnerNumberModel(this.properties.getDistFact(), 0.1, Double.MAX_VALUE, 0.1));
        this.spinnerDist1.setValue(this.properties.getDist1());
        this.spinnerDist2.setValue(this.properties.getDist2());
        this.spinnerDistFac.setModel(new SpinnerNumberModel(this.properties.getDistFact(), 0.1, Double.MAX_VALUE, 0.1));
        this.spinnerLetterHeight.setValue(this.properties.getLetterHeight());
        this.spinnerLineSize.setValue(this.properties.getLineSize());
        this.spinnerMass.setValue(this.properties.getMass());
        this.spinnerStemSize.setValue(this.properties.getStemSize());
        this.checkBoxDebug.setSelected(this.properties.isDebug());
        this.checkBoxMerge.setSelected(this.properties.isUseMerge());
        this.checkBoxDelete.setSelected(this.properties.isUseDelete());
        this.labelDelete.setEnabled(!this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(!this.checkBoxMerge.isSelected());

        // set visible
        this.MainFrame.setVisible(true);
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

        // dispose
        this.MainFrame.dispose();
    }

    private void buttonCancelActionPerformed() {
        this.MainFrame.dispose();
    }

    @SuppressWarnings("boxing")
    private void buttonDefaultActionPerformed() {
        // restore defaults
        this.properties.restoreDefault();

        // select values
        this.spinnerDestinyFactor.setValue(this.properties.getDestinyFact());
        this.spinnerDist1.setValue(this.properties.getDist1());
        this.spinnerDist2.setValue(this.properties.getDist2());
        this.spinnerDistFac.setValue(this.properties.getDistFact());
        this.spinnerLetterHeight.setValue(this.properties.getLetterHeight());
        this.spinnerLineSize.setValue(this.properties.getLineSize());
        this.spinnerMass.setValue(this.properties.getMass());
        this.spinnerStemSize.setValue(this.properties.getStemSize());
        this.checkBoxDebug.setSelected(this.properties.isDebug());
        this.labelDelete.setEnabled(!this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(!this.checkBoxMerge.isSelected());
        this.checkBoxMerge.setSelected(this.properties.isUseMerge());
        this.checkBoxDelete.setSelected(this.properties.isUseDelete());
        this.labelDelete.setEnabled(!this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(!this.checkBoxMerge.isSelected());
    }

    private void checkBockUseMergeActionPerformed() {
        this.labelDelete.setEnabled(!this.checkBoxMerge.isSelected());
        this.checkBoxDelete.setEnabled(!this.checkBoxMerge.isSelected());
    }
}

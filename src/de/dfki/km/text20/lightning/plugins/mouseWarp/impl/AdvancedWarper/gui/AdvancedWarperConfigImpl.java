/*
 * AdvancedWarperConfigImpl.java
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
package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.AdvancedWarper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.dfki.km.text20.lightning.plugins.mouseWarp.impl.improvedSimpleWarper.ImprovedWarperProperties;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings({ "serial", "boxing" })
public class AdvancedWarperConfigImpl extends AdvancedWarperConfig implements
        ActionListener {

    /** current used properties */
    private ImprovedWarperProperties properties;

    /**
     * creates new AdvancedWarperConfigImpl-object and initializes variables
     */
    public AdvancedWarperConfigImpl() {
        // initialize properties and preselect spinners
        this.properties = ImprovedWarperProperties.getInstance();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerDistance.setValue(this.properties.getDistanceThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());

        // add listener
        this.buttonCancel.addActionListener(this);
        this.buttonDefault.addActionListener(this);
        this.buttonOK.addActionListener(this);

        // add tooltips
        this.manageToolTips();

        // shows the gui
        setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        // check the source of the event and starts an associated handle
        if (event.getSource() == this.buttonDefault) {
            this.buttonDefaultActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonCancel) {
            this.buttonCancelActionPerformed();
            return;
        }

        if (event.getSource() == this.buttonOK) {
            this.buttonOKActionPerformed();
            return;
        }
    }

    /**
     * Fired when Default-button is clicked.
     * Restores default values from properties.
     */
    private void buttonDefaultActionPerformed() {
        this.properties.restoreDefault();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerDistance.setValue(this.properties.getDistanceThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());
    }

    /**
     * Fired when OK-button is clicked.
     * Sets selected values in properties.
     */
    private void buttonOKActionPerformed() {
        this.properties.setAngleThreshold(Integer.parseInt(this.spinnerAngle.getValue().toString()));
        this.properties.setDistanceThreshold(Integer.parseInt(this.spinnerDistance.getValue().toString()));
        this.properties.setHomeRadius(Integer.parseInt(this.spinnerHomeRadius.getValue().toString()));
        dispose();
    }

    /**
     * Fired when Cancel-button is clicked.
     * Closes the gui.
     */
    private void buttonCancelActionPerformed() {
        dispose();
    }

    private void manageToolTips() {
        // set tooltip text
        String labelAngleThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must do this in an angle within this<br>threshold to activate the mouse warp.<br>The lower this value the more exact you must<br>move your mouse.</body></HTML>";
        String labelDistanceThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must move minimal this way in pixels to<br>activate the mouse warp. The higher this value<br>the more pixels you have to pass.<br>This means you have to move faster.</body></HTML>";
        String labelHomeRadiusTT = "<HTML><body>If you move your mouse to your fixation point<br>and your cursor is within this radius, your<br>mousecursor will not be warped.</body></HTML>";

        // add tooltip
        this.labelAngleThreshold.setToolTipText(labelAngleThresholdTT);
        this.labelDistanceThreshold.setToolTipText(labelDistanceThresholdTT);
        this.labelHomeRadius.setToolTipText(labelHomeRadiusTT);
    }
}

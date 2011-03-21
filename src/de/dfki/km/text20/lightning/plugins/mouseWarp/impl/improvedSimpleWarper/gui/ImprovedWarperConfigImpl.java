/*
 * ImprovedWarperConfigImpl.java
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
package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.improvedSimpleWarper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.dfki.km.text20.lightning.plugins.mouseWarp.impl.improvedSimpleWarper.ImprovedWarperProperties;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings({ "serial", "boxing" })
public class ImprovedWarperConfigImpl extends ImprovedWarperConfig implements
        ActionListener {

    /** current used properties */
    private ImprovedWarperProperties properties;

    /**
     * creates new ImprovedWarperConfigImpl-object and initializes variables
     */
    public ImprovedWarperConfigImpl() {
        // initialize properties and preselect spinners
        this.properties = ImprovedWarperProperties.getInstance();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerDistance.setValue(this.properties.getDistanceThreshold());
        this.spinnerDuration.setValue(this.properties.getDurationThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());
        this.spinnerSetRadius.setValue(this.properties.getSetRadius());

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
        this.spinnerDuration.setValue(this.properties.getDurationThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());
        this.spinnerSetRadius.setValue(this.properties.getSetRadius());
    }

    /**
     * Fired when OK-button is clicked.
     * Sets selected values in properties.
     */
    private void buttonOKActionPerformed() {
        this.properties.setAngleThreshold(Integer.parseInt(this.spinnerAngle.getValue().toString()));
        this.properties.setDistanceThreshold(Integer.parseInt(this.spinnerDistance.getValue().toString()));
        this.properties.setDurationThreshold(Integer.parseInt(this.spinnerDuration.getValue().toString()));
        this.properties.setHomeRadius(Integer.parseInt(this.spinnerHomeRadius.getValue().toString()));
        this.properties.setSetRadius(Integer.parseInt(this.spinnerSetRadius.getValue().toString()));
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
        String labelDurationThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must move minimal this time in ms to<br>activate the mouse warp. The higher this value<br>the more exact the calculation can be done and<br>the mouse only warps when you really want it.</body></HTML>";
        String labelHomeRadiusTT = "<HTML><body>If you move your mouse to your fixation point<br>and your cursor is within this radius, your<br>mousecursor will not be warped.</body></HTML>";
        String labelSetRadiusTT = "<HTML><body>If you move your mouse to your fixation point<br>and your mouse warp is activated, it will be set<br>in the choosed distance from the fixation point<br>to allow you to stop the movement by<br>yourself.</body></HTML>";

        // add tooltip
        this.labelAngleThreshold.setToolTipText(labelAngleThresholdTT);
        this.labelDistanceThreshold.setToolTipText(labelDistanceThresholdTT);
        this.labelDurationThreshold.setToolTipText(labelDurationThresholdTT);
        this.labelHomeRadius.setToolTipText(labelHomeRadiusTT);
        this.labelSetRadius.setToolTipText(labelSetRadiusTT);
    }
}

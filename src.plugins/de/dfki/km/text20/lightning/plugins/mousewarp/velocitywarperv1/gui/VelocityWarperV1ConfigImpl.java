/*
 * VelocityWarperV1ConfigImpl.java
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
package de.dfki.km.text20.lightning.plugins.mousewarp.velocitywarperv1.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpinnerNumberModel;

import de.dfki.km.text20.lightning.plugins.mousewarp.velocitywarperv1.VelocityWarperV1Properties;

/**
 * @author Christoph Käding
 * 
 */
@SuppressWarnings({ "serial", "boxing" })
public class VelocityWarperV1ConfigImpl extends VelocityWarperV1Config implements
        ActionListener {

    /** current used properties */
    private VelocityWarperV1Properties properties;

    /**
     * creates new DistanceWarperConfigImpl-object and initializes variables
     */
    public VelocityWarperV1ConfigImpl() {
        // initialize properties and preselect spinners
        this.properties = VelocityWarperV1Properties.getInstance();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerSpeed.setModel(new SpinnerNumberModel(this.properties.getSpeed(), 0.1, 2.147483647E9, 0.1));
        this.spinnerReactionTime.setValue(this.properties.getReactionTime());

        // add listener
        this.buttonCancel.addActionListener(this);
        this.buttonDefault.addActionListener(this);
        this.buttonOK.addActionListener(this);

        // add tooltips
        this.manageToolTips();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
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
     * Fired when Default-button is clicked. Restores default values from
     * properties.
     */
    private void buttonDefaultActionPerformed() {
        this.properties.restoreDefault();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerSpeed.setValue(this.properties.getSpeed());
        this.spinnerReactionTime.setValue(this.properties.getReactionTime());
    }

    /**
     * Fired when OK-button is clicked. Sets selected values in properties.
     */
    private void buttonOKActionPerformed() {
        this.properties.setAngleThreshold(Integer.parseInt(this.spinnerAngle.getValue().toString()));
        this.properties.setSpeed(Double.parseDouble(this.spinnerSpeed.getValue().toString()));
        this.properties.setReactionTime(Integer.parseInt(this.spinnerReactionTime.getValue().toString()));
        dispose();
    }

    /**
     * Fired when Cancel-button is clicked. Closes the gui.
     */
    private void buttonCancelActionPerformed() {
        dispose();
    }

    private void manageToolTips() {
        // set tooltip text
        String labelAngleThresholdTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must do this in an angle within this<br>threshold to activate the mouse warp.<br>The lower this value the more exact you must<br>move your mouse.</body></HTML>";
        String labelSpeedTT = "<HTML><body>If you move your mouse to your fixation point,<br>you must move minimal with this speed in pixels per ms<br>to activate the mouse warp. The higher this value<br>the fatser you have to move.</body></HTML>";
        String labelReactionTimeTT = "<HTML><body>This is your reaction time you would be need to<br>react on the mouse warp. This is used to calculate<br>the point where the cursor will be placed.</body></HTML>";

        // add tooltip
        this.labelAngleThreshold.setToolTipText(labelAngleThresholdTT);
        this.labelSpeed.setToolTipText(labelSpeedTT);
        this.labelReactionTime.setToolTipText(labelReactionTimeTT);
    }
}

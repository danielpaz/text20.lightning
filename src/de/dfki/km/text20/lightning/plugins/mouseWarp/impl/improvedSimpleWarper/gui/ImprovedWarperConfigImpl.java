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

import de.dfki.km.text20.lightning.plugins.mouseWarp.impl.improvedSimpleWarper.resource.ImprovedWarperProperties;

/**
 * @author Christoph Käding
 *
 */
@SuppressWarnings({"serial","boxing"})
public class ImprovedWarperConfigImpl extends ImprovedWarperConfig implements ActionListener {

    /** current used properties */
    private ImprovedWarperProperties properties;
    
    /**
     * creates new ImprovedWarperConfigImpl-object and initializes variables
     */
    public ImprovedWarperConfigImpl() {
        this.properties = ImprovedWarperProperties.getInstance();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerDistance.setValue(this.properties.getDistanceThreshold());
        this.spinnerDuration.setValue(this.properties.getDurationThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());
        this.spinnerSetRadius.setValue(this.properties.getSetRadius());
        
        this.buttonCancel.addActionListener(this);
        this.buttonDefault.addActionListener(this);
        this.buttonOK.addActionListener(this);
        
        setVisible(true);
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.buttonDefault) {
            this.buttonDefaultActionPerformed();
            return;
        }
        
        if (event.getSource()  == this.buttonCancel){
            this.buttonCancelActionPerformed();
            return;
        }
        
        if(event.getSource() == this.buttonOK) {
            this.buttonOKActionPerformed();
            return;
        }
    }

    private void buttonDefaultActionPerformed(){
        this.properties.restoreDefault();
        this.spinnerAngle.setValue(this.properties.getAngleThreshold());
        this.spinnerDistance.setValue(this.properties.getDistanceThreshold());
        this.spinnerDuration.setValue(this.properties.getDurationThreshold());
        this.spinnerHomeRadius.setValue(this.properties.getHomeRadius());
        this.spinnerSetRadius.setValue(this.properties.getSetRadius());
    }
    
    private void buttonOKActionPerformed() {
        this.properties.setAngleThreshold(Integer.parseInt(this.spinnerAngle.getValue().toString()));
        this.properties.setDistanceThreshold(Integer.parseInt(this.spinnerDistance.getValue().toString()));
        this.properties.setDurationThreshold(Integer.parseInt(this.spinnerDuration.getValue().toString()));
        this.properties.setHomeRadius(Integer.parseInt(this.spinnerHomeRadius.getValue().toString()));
        this.properties.setSetRadius(Integer.parseInt(this.spinnerSetRadius.getValue().toString()));
        dispose();
    }
    
    private void buttonCancelActionPerformed() {
        dispose();
    }
}

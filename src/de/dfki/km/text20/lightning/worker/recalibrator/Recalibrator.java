/*
 * Recalibrator.java
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
package de.dfki.km.text20.lightning.worker.recalibrator;

import java.awt.Point;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDevice;
import de.dfki.km.text20.trackingserver.eyes.remote.TrackingCommand;
import de.dfki.km.text20.trackingserver.eyes.remote.options.sendcommand.OptionRecalibrationPattern;

/**
 * @author Christoph Käding
 *
 */
public class Recalibrator {

    /** */
    private Properties properties;
    
    /** */
    private OptionRecalibrationPattern pattern;
    
    /** current EyeTrackingDevice */
    private EyeTrackingDevice tarcker;
    
    /**
     * initializes all variables
     * 
     * @param device 
     */
    public void init(EyeTrackingDevice device) {
        this.properties = MainClass.getInstance().getProperties();
        this.pattern = null;
        this.tarcker = device;
    }
    
    /*
     *  NOTE:
     *  
     *  see:
     *  de.dfki.km.text20.trackingserver.eyes.remote.impl.TrackingServerRegitry.sendCommand();
     *  de.dfki.km.text20.trackingserver.eyes.remote.impl.ReferenceBasedDisplacementFilter.updateReferencePoint();
     *  de.dfki.km.text20.trackingserver.eyes.remote.impl.ReferenceBasedDisplacementFilter.clearReferencePoints();
     *  
     *  All reference points will be deleted in sendCommand(), so it is needed to call updateReferencePoint() 
     *  and clearReferencePoints() directly?
     *  
     *  BUT where to get a instance of the current ReferenceBasedDisplacementFilter?
     *  
     *  changes in:
     *  de.dfki.km.text20.trackingserver.eyes.remote.TrackingCommand;
     *      added new constant
     *  de.dfki.km.text20.trackingserver.eyes.remote.impl.TrackingServerRegistryImpl.sendCommand();
     *      added case for UPDATE_CALIBRATION
     */
    /**
     * 
     * @param fixation
     * @param offset
     */
    public void updateCalibration(Point fixation, Point offset) {
        if(this.properties == null) return;
        if(!this.properties.isRecalibration()) return;
        
        this.pattern = new OptionRecalibrationPattern();
        this.pattern.addPoint(fixation, offset.x, offset.y, System.currentTimeMillis());
       
        this.tarcker.sendLowLevelCommand(TrackingCommand.UPDATE_CALIBRATION, this.pattern);
    }
    
    /**
     * clears current online recalibration
     */
    public void clearRecalibration() {
        this.tarcker.sendLowLevelCommand(TrackingCommand.DROP_RECALIBRATION);
    }
}

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

/**
 * @author Christoph Käding
 *
 */
public class Recalibrator {

    /** */
    private Properties properties;
    
    /**
     * 
     */
    public Recalibrator() {
        this.properties = MainClass.getInstance().getProperties();
    }
    
    /**
     * 
     * @param fixation
     * @param offset
     */
    public void updateCalibration(Point fixation, Point offset) {
        if(!this.properties.isRecalibration()) return;
    }
}

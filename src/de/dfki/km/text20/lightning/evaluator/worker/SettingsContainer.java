/*
 * SettingsContainer.java
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
package de.dfki.km.text20.lightning.evaluator.worker;

import java.util.HashMap;
import java.util.Map;

/**
 * Container which stores the experiment settings of each XML-file.
 * 
 * @author Christoph Käding
 *
 */
public class SettingsContainer {

    /** included dimension */
    private int dimension;

    /** included screen brightness */
    private int screenBright;

    /** included setting brightness */
    private int settingBright;
    
    /** indicates how often the mouseposition was outside of the dimension */
    private int outOfDim;
    
    /** indicates how often was the screenrect was outside of the screenshot */
    private int outOfRect;
    
    /** indicates if recalibration was used */
    private boolean recalibration;

    /**
     * Creates a new container and initializes its variables.
     * 
     * @param dimension
     * @param recalibration 
     * @param screenBright
     * @param settingBright
     */
    public SettingsContainer(int dimension, boolean recalibration, int screenBright, int settingBright) {
        this.dimension = dimension;
        this.screenBright = screenBright;
        this.settingBright = settingBright;
        this.recalibration = recalibration;
        this.outOfDim = 0;
        this.outOfRect = 0;
    }

    /**
     * stored dimension
     * 
     * @return the dimension
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * stored brightness of the screen
     * 
     * @return the screenBright
     */
    public int getScreenBright() {
        return this.screenBright;
    }

    /**
     * stored brightness of the setting
     * 
     * @return the settingBright
     */
    public int getSettingBright() {
        return this.settingBright;
    }

    /**
     * increase MouseOutOfDimension-counter
     */
    public void addOutOfDim() {
        this.outOfDim++;
    }
    
    /**
     * returns current MouseOutOfDimension-counter
     * 
     * @return outOfDim
     */
    public int getOutOfDim() {
        return this.outOfDim;
    }

    /**
     * increase MouseOutOfDimension-counter
     */
    public void addOutOfRaster() {
        this.outOfRect++;
    }
    
    /**
     * returns current ScreenrectOutOfDimension-counter
     * 
     * @return outOfRect
     */
    public int getOutOfRaster() {
        return this.outOfRect;
    }
    /**
     * @return the recalibration
     */
    public boolean isRecalibration() {
        return this.recalibration;
    }
}

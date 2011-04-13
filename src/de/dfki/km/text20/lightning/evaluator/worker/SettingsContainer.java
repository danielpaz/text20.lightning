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
    private String screenBright;

    /** included setting brightness */
    private String settingBright;
    
    /** indicates how often was the mouseposition outside of the dimension */
    private int count;
    
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
    public SettingsContainer(int dimension, boolean recalibration, String screenBright, String settingBright) {
        this.dimension = dimension;
        this.screenBright = screenBright;
        this.settingBright = settingBright;
        this.recalibration = recalibration;
        this.count = 0;
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
    public String getScreenBright() {
        return this.screenBright;
    }

    /**
     * stored brightness of the setting
     * 
     * @return the settingBright
     */
    public String getSettingBright() {
        return this.settingBright;
    }

    /**
     * increase MouseOutOfDimension-counter
     */
    public void addOutOfDim() {
        this.count++;
    }
    
    /**
     * returns current MouseOutOfDimension-counter
     * 
     * @return count
     */
    public int getOutOfDim() {
        return this.count;
    }

    /**
     * @return the recalibration
     */
    public boolean isRecalibration() {
        return this.recalibration;
    }
}

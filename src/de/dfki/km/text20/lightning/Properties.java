/*
 * Properties.java
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
package de.dfki.km.text20.lightning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import de.dfki.km.text20.lightning.tools.HotkeyContainer;

/**
 * stores configurations, write them to a file on exit and tries to load them on startup 
 * 
 * @author Christoph Käding
 *
 */
@SuppressWarnings("serial")
public class Properties implements Serializable {

    /** shows if screenshots etc are written to the outputpath */
    @Attribute
    private boolean showImages;

    /** dimension for the screenshots */
    @Attribute
    private int dimension;

    /** global output path */
    @Attribute
    private String outputPath;

    /** action hotkey */
    @Element
    private HotkeyContainer actionHotkey;

    /** status hotkey */
    @Element
    private HotkeyContainer statusHotkey;

    @Attribute
    /** angle threshold for mousewarping */
    private int angleThreshold;
    
    @Attribute
    /** distance threshold for mousewarping */
    private int distanceThreshold;
    
    @Attribute
    /** duration threshold for mousewarping */
    private long durationThreshold;
    
    @Attribute
    /** home radius for mousewarping */
    private int homeRadius;
    
    @Attribute
    /** set radius for mousewarping */
    private int setRadius;
    
    /** indicates if mousewarping ins enabled */
    private boolean useWarp;

    /** file where porperties are stored */
    private File propertiesFile;

    /** from properties file readed object */
    private Object object;
    
    /**
     * creates properties, tries to load property file
     */
    public Properties() {

        // creates properties file
        this.propertiesFile = new File("properties");

        // status is used to indicate if the properties object could be readed probably 
        boolean status = false;

        if (this.propertiesFile.exists()) {
            try {

                // read object from file
                ObjectInputStream inputStream = new ObjectInputStream((new FileInputStream(this.propertiesFile)));
                this.object = inputStream.readObject();

                if (this.object instanceof Properties) {

                    // store readed configurations
                    this.showImages = ((Properties) this.object).isShowImages();
                    this.dimension = ((Properties) this.object).getDimension();
                    this.outputPath = ((Properties) this.object).getOutputPath();
                    this.actionHotkey = ((Properties) this.object).getActionHotkey();
                    this.statusHotkey = ((Properties) this.object).getStatusHotkey();
                    this.angleThreshold = ((Properties) this.object).getAngleThreshold();
                    this.distanceThreshold = ((Properties) this.object).getDistanceThreshold();
                    this.durationThreshold = ((Properties) this.object).getDurationThreshold();
                    this.homeRadius = ((Properties) this.object).getHomeRadius();
                    this.setRadius = ((Properties) this.object).getSetRadius();
                    this.useWarp = ((Properties) this.object).isUseWarp();

                    // reading successful
                    status = true;
                    System.out.println("Properties file was found.");
                    System.out.println("showImages: " + this.showImages + ", dimension: " + this.dimension + ", actionHotkey: " + this.actionHotkey + ", statusHotkey: " + this.statusHotkey);
                    System.out.println("useWarp: " + this.useWarp + ", angle: " + this.angleThreshold + ", distance: " + this.distanceThreshold + ", duration: " + this.durationThreshold + ", homeRadius: " + this.homeRadius + ", setRadius: " + this.setRadius);
                }

                // cleanup
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if reading was not successful or properties file was not found
        if (!status) {

            // set default values
            this.showImages = false;
            this.dimension = 100;
            this.outputPath = "./output";
            this.actionHotkey = null;
            this.statusHotkey = null;
            this.angleThreshold = 10;
            this.distanceThreshold = 200;
            this.durationThreshold = 1000;
            this.homeRadius = 200;
            this.setRadius = 20;
            this.useWarp = true;
            System.out.println("Properties file was not found.");
        }
    }

    /**
     * indicates if images should be shown
     * 
     * @return true if images should be written to files
     */
    public boolean isShowImages() {
        return this.showImages;
    }

    /**
     * set the boolean if images should be written to files
     * 
     * @param showImages
     */
    public void setShowImages(boolean showImages) {
        this.showImages = showImages;
    }

    /**
     * return dimension of screenshots
     * 
     * @return dimension
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * set dimension of screenshots
     * 
     * @param dimension
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * global output path
     * 
     * @return outputPath
     */
    public String getOutputPath() {
        return this.outputPath;
    }

    /**
     * sets the outputpath to choosed directory
     * 
     * @param choosedOutputPath 
     */
    public void setOutputPath(String choosedOutputPath) {
        try {
            File outputDir = new File(choosedOutputPath);

            if (outputDir.isDirectory()) {

                // if the choosed path is a directory, its path is the new outputpath
                this.outputPath = choosedOutputPath;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * current action hotkey container
     * 
     * @return actionHotkey
     */
    public HotkeyContainer getActionHotkey() {
        return this.actionHotkey;
    }

    /**
     * set current hotkey container
     * 
     * @param actionHotkey
     */
    public void setActionHotkey(HotkeyContainer actionHotkey) {
        this.actionHotkey = actionHotkey;
    }

    /**
     * current status hotkey container
     * 
     * @return statusHotkey
     */
    public HotkeyContainer getStatusHotkey() {
        return this.statusHotkey;
    }

    /**
     * set current status hotkey container
     * 
     * @param statusHotkey
     */
    public void setStatusHotkey(HotkeyContainer statusHotkey) {
        this.statusHotkey = statusHotkey;
    }

    
    
    /**
     * @return the angleThreshold
     */
    public int getAngleThreshold() {
        return this.angleThreshold;
    }

    /**
     * @param angleThreshold the angleThreshold to set
     */
    public void setAngleThreshold(int angleThreshold) {
        this.angleThreshold = angleThreshold;
    }

    /**
     * @return the distanceThreshold
     */
    public int getDistanceThreshold() {
        return this.distanceThreshold;
    }

    /**
     * @param distanceThreshold the distanceThreshold to set
     */
    public void setDistanceThreshold(int distanceThreshold) {
        this.distanceThreshold = distanceThreshold;
    }

    /**
     * @return the durationThreshold
     */
    public long getDurationThreshold() {
        return this.durationThreshold;
    }

    /**
     * @param durationThreshold the durationThreshold to set
     */
    public void setDurationThreshold(long durationThreshold) {
        this.durationThreshold = durationThreshold;
    }

    /**
     * @return the homeRadius
     */
    public int getHomeRadius() {
        return this.homeRadius;
    }

    /**
     * @param homeRadius the homeRadius to set
     */
    public void setHomeRadius(int homeRadius) {
        this.homeRadius = homeRadius;
    }

    
    
    /**
     * @return the setRadius
     */
    public int getSetRadius() {
        return this.setRadius;
    }

    /**
     * @param setRadius the setRadius to set
     */
    public void setSetRadius(int setRadius) {
        this.setRadius = setRadius;
    }

    
    
    /**
     * @return the useWarp
     */
    public boolean isUseWarp() {
        return this.useWarp;
    }

    /**
     * @param useWarp the useWarp to set
     */
    public void setUseWarp(boolean useWarp) {
        this.useWarp = useWarp;
    }

    /**
     * write properties to propertiesFile
     */
    public void writeProperties() {
        try {

            // write object
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(this.propertiesFile));
            outputStream.writeObject(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

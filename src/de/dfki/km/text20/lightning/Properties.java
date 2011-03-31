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

import de.dfki.km.text20.lightning.hotkey.HotkeyContainer;

/**
 * stores configurations, write them to a file on exit and tries to load them on startup 
 * 
 * @author Christoph Käding
 *
 */
public class Properties implements Serializable {

    /** generated serial id */
    private static final long serialVersionUID = 6929704088177718826L;

    /** dimension for the screenshots */
    @Attribute
    private int dimension;

    /** action hotkey */
    @Element
    private HotkeyContainer actionHotkey;

    /** status hotkey */
    @Element
    private HotkeyContainer statusHotkey;

    @Attribute
    /** name of currently used warper */
    private String warperName;

    @Attribute
    /** name of currently used detector */
    private String detectorName;

    @Attribute
    /** indicates if mousewarping ins enabled */
    private boolean useWarp;

    /** file where porperties are stored */
    private File propertiesFile;

    /** from properties file readed object */
    private Object object;
    
    /** indicates if the sound is activated */
    private boolean soundActivated; 

    /**
     * creates properties, tries to load property file
     */
    public Properties() {

        // creates properties file
        this.propertiesFile = new File("properties.prop");

        // status is used to indicate if the properties object could be readed probably 
        boolean status = false;

        if (this.propertiesFile.exists()) {
            try {

                // read object from file
                ObjectInputStream inputStream = new ObjectInputStream((new FileInputStream(this.propertiesFile)));
                this.object = inputStream.readObject();

                if (this.object instanceof Properties) {

                    // store readed configurations
                    this.dimension = ((Properties) this.object).getDimension();
                    this.actionHotkey = ((Properties) this.object).getActionHotkey();
                    this.statusHotkey = ((Properties) this.object).getStatusHotkey();
                    this.useWarp = ((Properties) this.object).isUseWarp();
                    this.warperName = ((Properties) this.object).getWarperName();
                    this.detectorName = ((Properties) this.object).getDetectorName();
                    this.soundActivated = ((Properties) this.object).isSoundActivated();

                    // reading successful
                    status = true;
                    System.out.println("Properties file was found.");
                    System.out.println("dimension: " + this.dimension + ", actionHotkey: " + this.actionHotkey + ", statusHotkey: " + this.statusHotkey);
                    System.out.println("use warp: " + this.useWarp + ", sound activated: " + this.soundActivated);
                    System.out.println("Detector: " + this.detectorName + ", Warper: " + this.warperName);
                }

                // cleanup
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if reading was not successful or properties file was not found
        if (!status) {
            this.restoreDefault();
            System.out.println("Properties file was not found.");
        }
    }

    /**
     * restores default values
     */
    public void restoreDefault() {
        // set default values
        this.dimension = 200;
        this.actionHotkey = null;
        this.statusHotkey = null;
        this.useWarp = true;
        this.warperName = "";
        this.detectorName = "";
        this.soundActivated = true;
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
     * @return the soundActivated
     */
    public boolean isSoundActivated() {
        return this.soundActivated;
    }

    /**
     * @param soundActivated the soundActivated to set
     */
    public void setSoundActivated(boolean soundActivated) {
        this.soundActivated = soundActivated;
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
     * @return the currentWarperName
     */
    public String getWarperName() {
        return this.warperName;
    }

    /**
     * @param currentWarperName the currentWarperName to set
     */
    public void setWarperName(String currentWarperName) {
        this.warperName = currentWarperName;
    }

    /**
     * @return the currentDetectorName
     */
    public String getDetectorName() {
        return this.detectorName;
    }

    /**
     * @param currentDetectorName the currentDetectorName to set
     */
    public void setDetectorName(String currentDetectorName) {
        this.detectorName = currentDetectorName;
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

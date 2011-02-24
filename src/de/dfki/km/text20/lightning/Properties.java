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

    /** file where porperties are stored */
    private File propertiesFile;

    /** from properties file readed object */
    private Object object;

    /**
     * creates properties, tries to load property file
     */
    public Properties() {

        // TODO: change path
        //        String propertiesPath = "C:" + File.separator + "Users" + File.separator + "nesti" + File.separator + "Desktop" + File.separator + "Click2Sight" + File.separator + "properties";
        String propertiesPath = "." + File.separator + "properties";

        // creates properties file
        this.propertiesFile = new File(propertiesPath);

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

                    // reading successful
                    status = true;
                    System.out.println("Properties file was found.");
                    System.out.println("showImages: " + this.showImages + ", dimension: " + this.dimension + ", actionHotkey: " + this.actionHotkey + ", statusHotkey: " + this.statusHotkey);
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
            // TODO: change path
            //            this.outputPath = "C:" + File.separator + "Users" + File.separator + "nesti" + File.separator + "Desktop" + File.separator + "Click2SightOutput";
            this.outputPath = "." + File.separator + "output";
            this.actionHotkey = null;
            this.statusHotkey = null;
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

/*
 * ImprovedWarperProperties.java
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
package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.improvedSimpleWarper.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.simpleframework.xml.Attribute;

/**
 * stores configurations, write them to a file on exit and tries to load them on startup 
 * 
 * @author Christoph Käding
 *
 */
public class ImprovedWarperProperties implements Serializable {

    /** generated serial id */
    private static final long serialVersionUID = 8852912029566928635L;

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

    /** file where porperties are stored */
    private File propertiesFile;

    /** from properties file readed object */
    private Object object;
    
    /** singleton instance of this properties */
    private static ImprovedWarperProperties properties;

    /**
     * creates properties, tries to load property file
     */
    private ImprovedWarperProperties() {

        // creates properties file
        this.propertiesFile = new File("./plugins/improvedWarperProperties.prop");

        // status is used to indicate if the properties object could be readed probably 
        boolean status = false;

        if (this.propertiesFile.exists()) {
            try {

                // read object from file
                ObjectInputStream inputStream = new ObjectInputStream((new FileInputStream(this.propertiesFile)));
                this.object = inputStream.readObject();

                if (this.object instanceof ImprovedWarperProperties) {

                    // store readed configurations
                    this.angleThreshold = ((ImprovedWarperProperties) this.object).getAngleThreshold();
                    this.distanceThreshold = ((ImprovedWarperProperties) this.object).getDistanceThreshold();
                    this.durationThreshold = ((ImprovedWarperProperties) this.object).getDurationThreshold();
                    this.homeRadius = ((ImprovedWarperProperties) this.object).getHomeRadius();
                    this.setRadius = ((ImprovedWarperProperties) this.object).getSetRadius();

                    // reading successful
                    status = true;
                    System.out.println("WarperProperties file was found.");
                    System.out.println("angle: " + this.angleThreshold + ", distance: " + this.distanceThreshold + ", duration: " + this.durationThreshold + ", homeRadius: " + this.homeRadius + ", setRadius: " + this.setRadius);
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
            System.out.println("ImprovedWarperProperties file was not found.");
        }
    }

    /**
     * restores default values
     */
    public void restoreDefault() {
        // set default values
        this.angleThreshold = 10;
        this.distanceThreshold = 200;
        this.durationThreshold = 200;
        this.homeRadius = 200;
        this.setRadius = 20;
    }

    /**
     * creates and returns the singleton instance
     * 
     * @return properties
     */
    public static ImprovedWarperProperties getInstance() {
        if(properties == null) {
            properties = new ImprovedWarperProperties();
        }
        return properties;
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
     * write properties to propertiesFile
     */
    public void writeProperties() {
        try {

            // write object
            new File("plugins").mkdirs();
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(this.propertiesFile));
            outputStream.writeObject(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

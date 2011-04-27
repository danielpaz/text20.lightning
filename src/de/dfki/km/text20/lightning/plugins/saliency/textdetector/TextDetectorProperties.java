/*
 * TextDetectorProperties.java
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
package de.dfki.km.text20.lightning.plugins.saliency.textdetector;

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
public class TextDetectorProperties implements Serializable {

    /** generated serial id */
    private static final long serialVersionUID = -7009680382311094022L;

    @Attribute
    private int letterHeight;

    @Attribute
    private int stemSize;

    @Attribute
    private int lineSize;

    @Attribute
    private boolean debug;

    @Attribute
    private boolean useMerge;

    @Attribute
    private boolean useDelete;
    
    @Attribute
    private double destinyFact;

    @Attribute
    private int mass;

    @Attribute
    private int dist1;

    @Attribute
    private double distFact;

    @Attribute
    private int dist2;

    /** file where porperties are stored */
    private File propertiesFile;

    /** from properties file readed object */
    private Object object;

    /** singleton instance of this properties */
    private static TextDetectorProperties properties;

    /**
     * creates properties, tries to load property file
     */
    private TextDetectorProperties() {

        // creates properties file
        this.propertiesFile = new File("./plugins/TextDetector/properties.prop");

        // status is used to indicate if the properties object could be readed probably 
        boolean status = false;

        if (this.propertiesFile.exists()) {
            try {

                // read object from file
                ObjectInputStream inputStream = new ObjectInputStream((new FileInputStream(this.propertiesFile)));
                this.object = inputStream.readObject();

                if (this.object instanceof TextDetectorProperties) {

                    // store readed configurations
                    this.letterHeight = ((TextDetectorProperties) this.object).getLetterHeight();
                    this.stemSize = ((TextDetectorProperties) this.object).getStemSize();
                    this.lineSize = ((TextDetectorProperties) this.object).getLineSize();
                    this.debug = ((TextDetectorProperties) this.object).isDebug();
                    this.useMerge = ((TextDetectorProperties) this.object).isUseMerge();
                    this.useDelete = ((TextDetectorProperties) this.object).isUseDelete();
                    this.destinyFact = ((TextDetectorProperties) this.object).getDestinyFact();
                    this.mass = ((TextDetectorProperties) this.object).getMass();
                    this.dist1 = ((TextDetectorProperties) this.object).getDist1();
                    this.distFact = ((TextDetectorProperties) this.object).getDistFact();
                    this.dist2 = ((TextDetectorProperties) this.object).getDist2();

                    // reading successful
                    status = true;
                    System.out.println("\r\nTextDetector properties file was found.");
                    System.out.println("letter height: " + this.letterHeight + ", stem size: " + this.stemSize + "%, line size: " + this.lineSize + ", debug: " + this.debug+ ", merge: " + this.useMerge+ ", delete: " + this.useDelete);
                    System.out.println("destiny factor: " + this.destinyFact + ", mass: " + this.mass + ", distance 1: " + this.dist1 + ", distance factor: " + this.distFact + ", distance 2: " + this.dist2);
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
            System.out.println("\r\nTextDetector properties file was not found.\r\n");
        }
    }

    /**
     * restores default values
     */
    public void restoreDefault() {
        // set default values
        this.letterHeight = 7;
        this.stemSize = 0;
        this.lineSize = 100;
        this.debug = false;
        this.useMerge = false;
        this.useDelete = false;
        this.destinyFact = 0.5;
        this.mass = 15;
        this.dist1 = 4;
        this.distFact = 1;
        this.dist2 = 20;
    }

    /**
     * @return the letterHeight
     */
    public int getLetterHeight() {
        return this.letterHeight;
    }

    /**
     * @param letterHeight the letterHeight to set
     */
    public void setLetterHeight(int letterHeight) {
        this.letterHeight = letterHeight;
    }

    /**
     * @return the stemSize
     */
    public int getStemSize() {
        return this.stemSize;
    }

    /**
     * @param stemSize the stemSize to set
     */
    public void setStemSize(int stemSize) {
        this.stemSize = stemSize;
    }

    /**
     * @return the lineSize
     */
    public int getLineSize() {
        return this.lineSize;
    }

    /**
     * @param lineSize the lineSize to set
     */
    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the useMerge
     */
    public boolean isUseMerge() {
        return this.useMerge;
    }

    /**
     * @param useMerge the useMerge to set
     */
    public void setUseMerge(boolean useMerge) {
        this.useMerge = useMerge;
    }

    /**
     * @return the useDelete
     */
    public boolean isUseDelete() {
        return this.useDelete;
    }

    /**
     * @param useDelete the useDelete to set
     */
    public void setUseDelete(boolean useDelete) {
        this.useDelete = useDelete;
    }

    /**
     * @return the destinyFact
     */
    public double getDestinyFact() {
        return this.destinyFact;
    }

    /**
     * @param destinyFact the destinyFact to set
     */
    public void setDestinyFact(double destinyFact) {
        this.destinyFact = destinyFact;
    }

    /**
     * @return the mass
     */
    public int getMass() {
        return this.mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(int mass) {
        this.mass = mass;
    }

    /**
     * @return the dist1
     */
    public int getDist1() {
        return this.dist1;
    }

    /**
     * @param dist1 the dist1 to set
     */
    public void setDist1(int dist1) {
        this.dist1 = dist1;
    }

    /**
     * @return the distFact
     */
    public double getDistFact() {
        return this.distFact;
    }

    /**
     * @param distFact the distFact to set
     */
    public void setDistFact(double distFact) {
        this.distFact = distFact;
    }

    /**
     * @return the dist2
     */
    public int getDist2() {
        return this.dist2;
    }

    /**
     * @param dist2 the dist2 to set
     */
    public void setDist2(int dist2) {
        this.dist2 = dist2;
    }

    /**
     * creates and returns the singleton instance
     * 
     * @return properties
     */
    public static TextDetectorProperties getInstance() {
        if (properties == null) {
            properties = new TextDetectorProperties();
        }
        return properties;
    }

    /**
     * write properties to propertiesFile
     */
    public void writeProperties() {
        try {

            // write object
            new File("plugins/TextDetector").mkdirs();
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(this.propertiesFile));
            outputStream.writeObject(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

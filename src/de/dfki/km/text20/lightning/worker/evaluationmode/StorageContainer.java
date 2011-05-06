/*
 * StorageContainer.java
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
package de.dfki.km.text20.lightning.worker.evaluationmode;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

/**
 * Container which stores collected evaluation data.
 * 
 * @author Christoph Käding
 *
 */
public class StorageContainer implements Serializable {

    /** generated serial id */
    private static final long serialVersionUID = 1135235125761821153L;

    /** fixation point */
    @Element
    private Point fixation;

    /** related position */
    @Element
    private Point relatedPoint;

    /** timestamp of the evaluation step */
    @Attribute
    private long timestamp;
    
    /** text coverage of current screenshot */
    private double textCoverage;
    
    /** current list of usable brigthness settings */
    private static Map<Integer, String> brightness;

    /**
     * Creates new container an initializes its values.
     * 
     * @param timestamp
     * @param fixation 
     * @param relatedPoint
     * @param pubils 
     */
    public StorageContainer(long timestamp, Point fixation, Point relatedPoint) {
        this.timestamp = timestamp;
        this.relatedPoint = relatedPoint;
        this.fixation = fixation;
    }

    /**
     * @return the relatedPoint
     */
    public Point getRelatedPoint() {
        return this.relatedPoint;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * @return the fixation point
     */
    public Point getFixation() {
        return this.fixation;
    }
    
    /**
     * @return the textCoverage
     */
    public double getTextCoverage() {
        return this.textCoverage;
    }

    /**
     * @param textCoverage the textCoverage to set
     */
    public void setTextCoverage(double textCoverage) {
        this.textCoverage = textCoverage;
    }

    /**
     * @return available brightness options
     */
    @SuppressWarnings("boxing")
    public static Map<Integer, String> getScreenBrightnessOptions() {
        brightness = new HashMap<Integer, String>();
        brightness.put(0, "not choosen");
        brightness.put(1, "dark");
        brightness.put(2, "medium-dark");
        brightness.put(3, "medium-light");
        brightness.put(4, "light");
        return brightness;
    }
    /**
     * @return available brightness options
     */
    @SuppressWarnings("boxing")
    public static Map<Integer, String> getSettingBrightnessOptions() {
        brightness = new HashMap<Integer, String>();
        brightness.put(0, "not choosen");
        brightness.put(1, "dark");
        brightness.put(2, "medium-dark");
        brightness.put(3, "medium-light");
        brightness.put(4, "light");
        return brightness;
    }
}

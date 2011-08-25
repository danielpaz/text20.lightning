/*
 * DataPackage.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.worker;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Christoph Käding
 */
public class DataPackage {

    /** */
    private int coverageMin;

    /** */
    private int coverageMax;

    /** */
    private int heightMin;

    /** */
    private int heightMax;

    /** */
    private int widthMin;

    /** */
    private int widthMax;

    /** */
    private int lineMin;

    /** */
    private int lineMax;

    /** */
    private double sensitivityMin;

    /** */
    private double sensitivityMax;
    
    /** */
    private long timestamp;

    /** */
    private ArrayList<File> files;
    
    /** */ 
    private int amount;
    
    /** */
    private int dimension;
    
    /** */
    private boolean drawImages;
    
    /** */
    private boolean bigSteps;
    
    /** */
    private int size; 
    
    /** */
    private int sizePart;

    /**
     * @return the coverageMin
     */
    public int getCoverageMin() {
        return this.coverageMin;
    }

    /**
     * @param coverageMin the coverageMin to set
     */
    public void setCoverageMin(int coverageMin) {
        this.coverageMin = coverageMin;
    }

    /**
     * @return the coverageMax
     */
    public int getCoverageMax() {
        return this.coverageMax;
    }

    /**
     * @param coverageMax the coverageMax to set
     */
    public void setCoverageMax(int coverageMax) {
        this.coverageMax = coverageMax;
    }

    /**
     * @return the heightMin
     */
    public int getHeightMin() {
        return this.heightMin;
    }

    /**
     * @param heightMin the heightMin to set
     */
    public void setHeightMin(int heightMin) {
        this.heightMin = heightMin;
    }

    /**
     * @return the heightMax
     */
    public int getHeightMax() {
        return this.heightMax;
    }

    /**
     * @param heightMax the heightMax to set
     */
    public void setHeightMax(int heightMax) {
        this.heightMax = heightMax;
    }

    /**
     * @return the widthMin
     */
    public int getWidthMin() {
        return this.widthMin;
    }

    /**
     * @param widthMin the widthMin to set
     */
    public void setWidthMin(int widthMin) {
        this.widthMin = widthMin;
    }

    /**
     * @return the widthMax
     */
    public int getWidthMax() {
        return this.widthMax;
    }

    /**
     * @param widthMax the widthMax to set
     */
    public void setWidthMax(int widthMax) {
        this.widthMax = widthMax;
    }

    /**
     * @return the lineMin
     */
    public int getLineMin() {
        return this.lineMin;
    }

    /**
     * @param lineMin the lineMin to set
     */
    public void setLineMin(int lineMin) {
        this.lineMin = lineMin;
    }

    /**
     * @return the lineMax
     */
    public int getLineMax() {
        return this.lineMax;
    }

    /**
     * @param lineMax the lineMax to set
     */
    public void setLineMax(int lineMax) {
        this.lineMax = lineMax;
    }

    /**
     * @return the sensitivityMin
     */
    public double getSensitivityMin() {
        return this.sensitivityMin;
    }

    /**
     * @param sensitivityMin the sensitivityMin to set
     */
    public void setSensitivityMin(double sensitivityMin) {
        this.sensitivityMin = sensitivityMin;
    }

    /**
     * @return the sensitivityMax
     */
    public double getSensitivityMax() {
        return this.sensitivityMax;
    }

    /**
     * @param sensitivityMax the sensitivityMax to set
     */
    public void setSensitivityMax(double sensitivityMax) {
        this.sensitivityMax = sensitivityMax;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the files
     */
    public ArrayList<File> getFiles() {
        return this.files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * @return the drawImages
     */
    public boolean isDrawImages() {
        return this.drawImages;
    }

    /**
     * @param drawImages the drawImages to set
     */
    public void setDrawImages(boolean drawImages) {
        this.drawImages = drawImages;
    }

    /**
     * @return the bigSteps
     */
    public boolean isBigSteps() {
        return this.bigSteps;
    }

    /**
     * @param bigSteps the bigSteps to set
     */
    public void setBigSteps(boolean bigSteps) {
        this.bigSteps = bigSteps;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the sizePart
     */
    public int getSizePart() {
        return this.sizePart;
    }

    /**
     * @param sizePart the sizePart to set
     */
    public void setSizePart(int sizePart) {
        this.sizePart = sizePart;
    }
}

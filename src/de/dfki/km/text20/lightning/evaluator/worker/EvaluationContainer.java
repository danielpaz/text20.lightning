/*
 * EvaluationContainer.java
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import de.dfki.km.text20.lightning.worker.evaluationmode.StorageContainer;

/**
 * Container which stores the calculated resultsOverAll for each method.
 * 
 * @author Christoph Käding
 *
 */
public class EvaluationContainer {

    /** map with stored results as value and method id as key */
    private Map<Integer, Double> resultsOverAll;

    /** map with stored results as value and method id as key */
    private Map<Integer, Double> resultsHigher;

    /** map with stored results as value and method id as key */
    private Map<Integer, Double> resultsLower;

    /** id wich is counted to get the number of 'adds' */
    private int keyId;

    /** number of 'adds' */
    private int sizeOverAll;

    /** number of 'adds' */
    private int sizeHigher;

    /** number of 'adds' */
    private int sizeLower;

    /** temp for calculations */
    private double temp;

    /** list of ids */
    private ArrayList<Integer> ids;

    /** absolute name of the logfile */
    private String log;

    /** timestamp of the start of the evaluation */
    private long timeStamp;

    /** username */
    private String name;

    /** indicates if the pupilsize should be devided or not */
    private boolean firstGetOverAll;

    /** indicates if the pupilsize should be devided or not */
    private boolean firstGetHigher;

    /** indicates if the pupilsize should be devided or not */
    private boolean firstGetLower;

    /** threshold for text coverage */
    private double threshold;

    /** list of distances between mouse and fixation */
    private ArrayList<StorageContainer> container;

    /** 
     * size of pupils
     * 0 = left
     * 1 = right
     */
    private float[] pupilsizeOverAll;

    /** 
     * size of pupils
     * 0 = left
     * 1 = right
     */
    private float[] pupilsizeHigher;

    /** 
     * size of pupils
     * 0 = left
     * 1 = right
     */
    private float[] pupilsizeLower;

    /**
     * Creates a new container and initializes all necessary variables.
     * Also adds the first id with its value.
     * 
     * @param id 
     *      of the method
     * @param distance
     *      distance between mouse position and calculated result 
     * @param container 
     *      current storage container
     * @param threshold 
     *      current used text coverage threshold
     * @param log
     *      absolute path of the logfile
     * @param name
     *      name of the current evaluated user
     * @param timeStamp
     *      start of the evaluation session
     */
    public EvaluationContainer(int id, double distance, StorageContainer container,
                               double threshold, String log, String name, long timeStamp) {
        // initialize variables
        this.resultsOverAll = new Hashtable<Integer, Double>();
        this.resultsHigher = new Hashtable<Integer, Double>();
        this.resultsLower = new Hashtable<Integer, Double>();
        this.ids = new ArrayList<Integer>();
        this.container = new ArrayList<StorageContainer>();
        this.keyId = id;
        this.sizeOverAll = 0;
        this.sizeHigher = 0;
        this.sizeLower = 0;
        this.log = log;
        this.name = name;
        this.timeStamp = timeStamp;
        this.pupilsizeOverAll = new float[2];
        this.pupilsizeHigher = new float[2];
        this.pupilsizeLower = new float[2];
        this.firstGetOverAll = true;
        this.firstGetHigher = true;
        this.firstGetLower = true;
        this.threshold = threshold;

        // add first value
        this.add(id, distance, container);
    }

    /**
     * Adds given distance by name to the storing map. 
     * If the key already is in the map, the value will be added to it.
     * 
     * @param id 
     *      of the method
     * @param distance
     *      distance between mouse position and calculated result 
     * @param storageContainer
     *      current storage container 
     */
    @SuppressWarnings("boxing")
    public void add(int id, double distance, StorageContainer storageContainer) {
        // if the given id is the key id ... 
        if (this.keyId == id) {
            // ... increase sizeO ...
            this.sizeOverAll++;

            // ... and add pupilsize
            this.pupilsizeOverAll[0] = storageContainer.getPupils()[0] + this.pupilsizeOverAll[0];
            this.pupilsizeOverAll[1] = storageContainer.getPupils()[1] + this.pupilsizeOverAll[1];

            // add container
            this.container.add(storageContainer);

            // store by coverage
            if (storageContainer.getTextCoverage() > this.threshold) {
                // increase size ...
                this.sizeHigher++;

                // ... and add pupilsize
                this.pupilsizeHigher[0] = storageContainer.getPupils()[0] + this.pupilsizeHigher[0];
                this.pupilsizeHigher[1] = storageContainer.getPupils()[1] + this.pupilsizeHigher[1];

            } else {

                // increase size ...
                this.sizeLower++;

                // ... and add pupilsize
                this.pupilsizeLower[0] = storageContainer.getPupils()[0] + this.pupilsizeLower[0];
                this.pupilsizeLower[1] = storageContainer.getPupils()[1] + this.pupilsizeLower[1];
            }
        }

        // if the map contains already the given id, store value in temp
        if (this.resultsOverAll.containsKey(id)) this.temp = this.resultsOverAll.get(id);

        // put temp + given value by given id to the map
        this.resultsOverAll.put(id, this.temp + distance);

        // add id to the list of ids if it is not already there
        if (!this.ids.contains(id)) this.ids.add(id);

        // reset temp
        this.temp = 0;

        if (storageContainer.getTextCoverage() > this.threshold) {

            // if the map contains already the given id, store value in temp
            if (this.resultsHigher.containsKey(id))
                this.temp = this.resultsHigher.get(id);

            // put temp + given value by given id to the map
            this.resultsHigher.put(id, this.temp + distance);

        } else {

            // if the map contains already the given id, store value in temp
            if (this.resultsLower.containsKey(id)) this.temp = this.resultsLower.get(id);

            // put temp + given value by given id to the map
            this.resultsLower.put(id, this.temp + distance);
        }

        // reset temp
        this.temp = 0;
    }

    /**
     * Returns averaged distance for the given id.
     * 
     * @param id
     * @return averaged distance
     */
    @SuppressWarnings("boxing")
    public double getAveragedDistanceOverAll(int id) {
        if (this.resultsOverAll.containsKey(id))
            return (this.resultsOverAll.get(id) / this.sizeOverAll);
        return 0;
    }

    /**
     * Returns averaged distance for the given id.
     * 
     * @param id
     * @return averaged distance
     */
    @SuppressWarnings("boxing")
    public double getAveragedDistanceHigher(int id) {
        if (this.resultsHigher.containsKey(id))
            return (this.resultsHigher.get(id) / this.sizeHigher);
        return 0;
    }

    /**
     * Returns averaged distance for the given id.
     * 
     * @param id
     * @return averaged distance
     */
    @SuppressWarnings("boxing")
    public double getAveragedDistanceLower(int id) {
        if (this.resultsLower.containsKey(id))
            return (this.resultsLower.get(id) / this.sizeLower);
        return 0;
    }

    /**
     * averaged pupilsize from all entries
     * 
     * @return pupils
     */
    public float[] getAveragedPupilsOverAll() {
        if (this.firstGetOverAll) {
            this.pupilsizeOverAll[0] = this.pupilsizeOverAll[0] / this.sizeOverAll;
            this.pupilsizeOverAll[1] = this.pupilsizeOverAll[1] / this.sizeOverAll;
            this.firstGetOverAll = false;
        }
        return this.pupilsizeOverAll;
    }

    /**
     * averaged pupilsize from all entries with a higher coverage than the threshold
     * 
     * @return pupils
     */
    public float[] getAveragedPupilsHigher() {
        if (this.firstGetHigher) {
            this.pupilsizeHigher[0] = this.pupilsizeHigher[0] / this.sizeHigher;
            this.pupilsizeHigher[1] = this.pupilsizeHigher[1] / this.sizeHigher;
            this.firstGetHigher = false;
        }
        return this.pupilsizeHigher;
    }

    /**
     * averaged pupilsize from all entries with a lower coverage than the threshold
     * 
     * @return pupils
     */
    public float[] getAveragedPupilsLower() {
        if (this.firstGetLower) {
            this.pupilsizeLower[0] = this.pupilsizeLower[0] / this.sizeLower;
            this.pupilsizeLower[1] = this.pupilsizeLower[1] / this.sizeLower;
            this.firstGetLower = false;
        }
        return this.pupilsizeLower;
    }

    /**
     * returns a list of recognized ids
     * 
     * @return ids
     */
    public ArrayList<Integer> getIds() {
        return this.ids;
    }

    /**
     * returns the absolute file name of the logfile
     * 
     * @return absolute file name
     */
    public String getLogPath() {
        return this.log;
    }

    /**
     * returns the name of the current evaluated user
     * 
     * @return user name
     */
    public String getName() {
        return this.name;
    }

    /**
     * returns the timestamp of the start of this evaluation session
     * 
     * @return timestamp
     */
    public long getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * @return the number of datasets
     */
    public int getSizeOverAll() {
        return this.sizeOverAll;
    }

    /**
     * @return the number of datasets
     */
    public int getSizeHigher() {
        return this.sizeHigher;
    }

    /**
     * @return the number of datasets
     */
    public int getSizeLower() {
        return this.sizeLower;
    }

    /**
     * @return the deviation
     */
    public ArrayList<StorageContainer> getContainer() {
        return this.container;
    }
}

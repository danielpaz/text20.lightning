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
package de.dfki.km.text20.lightning.components.evaluator.worker;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

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

    /** path where logfiles should be placed */
    private ArrayList<String> log;

    /** threshold for text coverage */
    private double threshold;

    /**
     * Creates a new container and initializes all necessary variables.
     * Also adds the first id with its value.
     * 
     * @param id 
     *      of the method
     * @param distance
     *      distance between mouse position and calculated result 
     * @param value 
     *      current text coverage value
     * @param threshold 
     *      current used text coverage threshold
     * @param path
     *      path where logfiles should be placed
     */
    public EvaluationContainer(int id, double distance, double value, double threshold,
                               String path) {
        // initialize variables
        this.resultsOverAll = new Hashtable<Integer, Double>();
        this.resultsHigher = new Hashtable<Integer, Double>();
        this.resultsLower = new Hashtable<Integer, Double>();
        this.log = new ArrayList<String>();
        this.ids = new ArrayList<Integer>();
        this.keyId = id;
        this.sizeOverAll = 0;
        this.sizeHigher = 0;
        this.sizeLower = 0;
        this.threshold = threshold;

        // add first value
        this.add(id, distance, value, path);
    }

    /**
     * Adds given distance by name to the storing map. 
     * If the key already is in the map, the value will be added to it.
     * 
     * @param id 
     *      of the method
     * @param distance
     *      distance between mouse position and calculated result 
     * @param value 
     *      current text coverage value
     * @param path 
     *      path where logfile should be placed
     */
    @SuppressWarnings("boxing")
    public void add(int id, double distance, double value, String path) {
        // if the given id is the key id ... 
        if (this.keyId == id) {
            // ... increase sizeO ...
            this.sizeOverAll++;

            // store by coverage
            if (value > this.threshold) {

                // increase size ...
                this.sizeHigher++;

            } else {

                // increase size ...
                this.sizeLower++;
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

        if (value > this.threshold) {

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

        // add path
        if (!this.log.contains(path)) this.log.add(path);
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
     * returns a list of recognized ids
     * 
     * @return ids
     */
    public ArrayList<Integer> getIds() {
        return this.ids;
    }

    /**
     * returns all logpaths
     * 
     * @return absolute file name
     */
    public ArrayList<String> getLogPath() {
        return this.log;
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
}

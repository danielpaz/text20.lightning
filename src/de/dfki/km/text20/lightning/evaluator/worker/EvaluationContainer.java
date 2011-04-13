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

/**
 * Container which stores the calculated results for each method.
 * 
 * @author Christoph Käding
 *
 */
public class EvaluationContainer {

    /** map with stored results as value and method id as key */
    private Map<Integer, Double> results;

    /** id wich is counted to get the number of 'adds' */
    private int keyId;

    /** number of 'adds' */
    private int size;

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
    private boolean firstGet;

    /** list of distances between mouse and fixation */
    private ArrayList<Double> deviation;

    /** 
     * size of pupils
     * 0 = left
     * 1 = right
     */
    private float[] pupilsize;

    /**
     * Creates a new container and initializes all necessary variables.
     * Also adds the first id with its value.
     * 
     * @param id 
     *      of the method
     * @param distanceMR
     *      distance between mouse position and calculated result 
     * @param distanceMF 
     *      distance between mouse position and fixation 
     * @param pupils 
     *      size of the pupils
     * @param log
     *      absolute path of the logfile
     * @param name
     *      name of the current evaluated user
     * @param timeStamp
     *      start of the evaluation session
     */
    public EvaluationContainer(int id, double distanceMR, double distanceMF,
                               float[] pupils, String log, String name, long timeStamp) {
        // initialize variables
        this.results = new Hashtable<Integer, Double>();
        this.ids = new ArrayList<Integer>();
        this.deviation = new ArrayList<Double>();
        this.keyId = id;
        this.size = 0;
        this.log = log;
        this.name = name;
        this.timeStamp = timeStamp;
        this.pupilsize = new float[2];
        this.firstGet = true;

        // add first value
        this.add(id, distanceMR, distanceMF, pupils);
    }

    /**
     * Adds given distance by name to the storing map. 
     * If the key already is in the map, the value will be added to it.
     * 
     * @param id 
     *      of the method
     * @param distanceMR
     *      distance between mouse position and calculated result 
     * @param distanceMF 
     *      distance between mouse position and fixation
     * @param pupils 
     *      size of the pupils
     */
    @SuppressWarnings("boxing")
    public void add(int id, double distanceMR, double distanceMF, float[] pupils) {
        // if the given id is the key id ... 
        if (this.keyId == id) {
            // ... increase size ...
            this.size++;

            // ... and add pupilsize
            this.pupilsize[0] = pupils[0] + this.pupilsize[0];
            this.pupilsize[1] = pupils[1] + this.pupilsize[1];

            // add deviation
            this.deviation.add(distanceMF);
        }

        // if the map contains already the given id, store value in temp
        if (this.results.containsKey(id)) this.temp = this.results.get(id);

        // put temp + given value by given id to the map
        this.results.put(id, this.temp + distanceMR);

        // add id to the list of ids if it is not already there
        if (!this.ids.contains(id)) this.ids.add(id);

        //        System.out.println("id: " + id + " distance: " + distance + " pupils: left - " + this.pupilsize[0] + " - right -" + this.pupilsize[1] + " size: " + this.size);

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
    public double getAveragedDistance(int id) {
        if (this.results.containsKey(id)) return (this.results.get(id) / this.size);
        return 0;
    }

    /**
     * averaged pupilsize from all entries
     * 
     * @return pupils
     */
    public float[] getAveragedPupils() {
        if (this.firstGet) {
            this.pupilsize[0] = this.pupilsize[0] / this.size;
            this.pupilsize[1] = this.pupilsize[1] / this.size;
            this.firstGet = false;
        }
        return this.pupilsize;
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
    public int getSize() {
        return this.size;
    }

    /**
     * @return the deviation
     */
    public ArrayList<Double> getDeviation() {
        return this.deviation;
    }
}

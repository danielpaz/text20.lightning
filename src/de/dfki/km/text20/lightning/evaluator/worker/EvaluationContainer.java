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
    
    /** size of pupils
     *  0 = left
     *  1 = right
     */
    private float[] pupilsize;

    /**
     * Creates a new container and initializes all necessary variables.
     * Also adds the first id with its value.
     * 
     * @param id 
     *      of the method
     * @param distance
     *      distance between mouse position and calculated result 
     * @param pupils 
     *      size of the pupils
     * @param log
     *      absolute path of the logfile
     * @param name
     *      name of the current evaluated user
     * @param timeStamp
     *      start of the evaluation session
     */
    public EvaluationContainer(int id, double distance, float[] pupils, String log, String name,
                               long timeStamp) {
        // initialize variables
        this.results = new Hashtable<Integer, Double>();
        this.ids = new ArrayList<Integer>();
        this.keyId = id;
        this.size = 0;
        this.log = log;
        this.name = name;
        this.timeStamp = timeStamp;
        this.pupilsize = new float[2];

        // add first value
        this.add(id, distance, pupils);
    }

    /**
     * Adds given distance by name to the storing map. 
     * If the key already is in the map, the value will be added to it.
     * 
     * @param id 
     *      of the method
     * @param distance
     *      distance between mouse position and calculated result 
     * @param pupils 
     *      size of the pupils
     */
    @SuppressWarnings("boxing")
    public void add(int id, double distance, float[] pupils) {
        // if the given id is the key id, increase size
        if (this.keyId == id) this.size++;

        // if the map contains already the given id, store value in temp
        if (this.results.containsKey(id)) this.temp = this.results.get(id);

        // put temp + given value by given id to the map
        this.results.put(id, this.temp + distance);

        // add id to the list of ids if it is not already there
        if (!this.ids.contains(id)) this.ids.add(id);

        // add pupilsize
        this.pupilsize[0] = pupils[0] + this.pupilsize[0];
        this.pupilsize[1] = pupils[1] + this.pupilsize[1];
        
        //        System.out.println("id: " + id + " value: " + value + " size: " + this.size + " keyId: " + this.keyId);

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
        this.pupilsize[0] = this.pupilsize[0]/ this.size;
        this.pupilsize[1] = this.pupilsize[1]/ this.size;
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
}

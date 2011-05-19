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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.evaluator.worker;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<Integer, ArrayList<Double>> resultsOverAll;

    /** map with stored results as value and method id as key */
    private Map<Integer, ArrayList<Double>> resultsHigher;

    /** map with stored results as value and method id as key */
    private Map<Integer, ArrayList<Double>> resultsLower;

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
        this.resultsOverAll = new Hashtable<Integer, ArrayList<Double>>();
        this.resultsHigher = new Hashtable<Integer, ArrayList<Double>>();
        this.resultsLower = new Hashtable<Integer, ArrayList<Double>>();
        this.log = new ArrayList<String>();
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

        // store distance to overall results
        if (this.resultsOverAll.containsKey(id)) {
            this.resultsOverAll.get(id).add(distance);
        } else {
            ArrayList<Double> tmp = new ArrayList<Double>();
            tmp.add(distance);
            this.resultsOverAll.put(id, tmp);
        }

        // store distance in threshold specific map
        if (value > this.threshold) {

            if (this.resultsHigher.containsKey(id)) {
                this.resultsHigher.get(id).add(distance);
            } else {
                ArrayList<Double> tmp = new ArrayList<Double>();
                tmp.add(distance);
                this.resultsHigher.put(id, tmp);
            }

        } else {

            if (this.resultsLower.containsKey(id)) {
                this.resultsLower.get(id).add(distance);
            } else {
                ArrayList<Double> tmp = new ArrayList<Double>();
                tmp.add(distance);
                this.resultsLower.put(id, tmp);
            }
        }

        // add path
        if (!this.log.contains(path)) this.log.add(path);
    }

    /**
     * Returns averaged distance for the given id and type.
     * 
     * @param id
     * @param type 
     *      
     * @return averaged distance
     */
    public double getAveragedDistance(int id, EvaluationResultType type) {
        // initialize variables
        ArrayList<Double> values = this.getValues(id, type);
        double result = 0;

        // check if datas are available
        if (values.size() == 0) return -1;

        // summarize values
        for (double tmp : values) {
            result = result + tmp;
        }

        // return average
        return result / values.size();
    }

    /**
     * Returns unit variance for the given id and type.
     * 
     * @param id
     * @param type 
     *      
     * @return unit variance
     * 
     * TODO: calculate averaged distance only once
     */
    @SuppressWarnings("boxing")
    public double getUnitVariance(int id, EvaluationResultType type) {
        // initialize variables
        ArrayList<Double> values = this.getValues(id, type);
        HashMap<Double, Double> probability = new HashMap<Double, Double>();
        double tmp;

        // check if datas are available
        if (values.size() == 0) return -1;

        // calculate probability of each value
        for (double value : values) {
            if (probability.containsKey(value)) {
                tmp = probability.get(value);
                tmp = tmp + 1.0 / values.size();
                probability.put(value, tmp);
            } else {
                probability.put(value, 1.0 / values.size());
            }
        }

        // reset tmp
        tmp = 0;

        // calculate unity variance
        for (double value : probability.keySet()) {
            tmp = tmp + (Math.pow(value - this.getAveragedDistance(id, type), 2) * probability.get(value));
        }

        // return unit variance
        return tmp;
    }

    /**
     * Returns standard deviation for the given id and type.
     * 
     * @param id
     * @param type 
     *      
     * @return standard deviation
     * 
     * TODO: calculate unity variance only once
     */
    public double getStandardDeviation(int id, EvaluationResultType type) {
        // initialize variable
        double unityVariance = this.getUnitVariance(id, type);

        // check if unity variance is available
        if (unityVariance == -1) return -1;

        // return standard derivation
        return Math.sqrt(unityVariance);
    }

    /**
     * @param id
     * @param type 
     *      
     * @return minimal value
     */
    public double getMinValue(int id, EvaluationResultType type) {
        // initialize variable
        ArrayList<Double> values = this.getValues(id, type);
        double minValue = Double.MAX_VALUE;

        // check if unity variance is available
        if (values.size() == 0) return -1;

        // set min value
        for (double tmp : values)
            if (tmp < minValue) minValue = tmp;

        // return standard derivation
        return minValue;
    }

    /**
     * @param id
     * @param type 
     *      
     * @return maximal value
     */
    public double getMaxValue(int id, EvaluationResultType type) {
        // initialize variable
        ArrayList<Double> values = this.getValues(id, type);
        double maxValue = Double.MIN_VALUE;

        // check if unity variance is available
        if (values.size() == 0) return -1;

        // set min value
        for (double tmp : values)
            if (tmp > maxValue) maxValue = tmp;

        // return standard derivation
        return maxValue;
    }

    /**
     * Returns median for the given id and type.
     * 
     * @param id
     * @param type 
     *      
     * @return averaged distance
     * 
     * TODO: sort array only once
     */
    @SuppressWarnings("boxing")
    public double getMedian(int id, EvaluationResultType type) {
        // initialize variables
        double median = 0;
        double temp;
        boolean doMore = true;
        ArrayList<Double> values = new ArrayList<Double>(this.getValues(id, type));

        // check if data are available
        if (values.size() == 0) return -1;

        // kind of bubble sort
        while (doMore) {
            doMore = false;
            for (int i = 0; i < values.size() - 1; i++) {
                if (values.get(i) > values.get(i + 1)) {
                    temp = values.get(i);
                    values.set(i, values.get(i + 1));
                    values.set(i + 1, temp);
                    doMore = true;
                }
            }
        }

        // set median
        if ((values.size() % 2) == 1) {

            // if uneven number, set middle value as median
            median = values.get(((values.size() - 1) / 2));
        } else {

            // if even number, calculate average of the to middle values
            median = (values.get(values.size() / 2) + values.get((values.size() / 2) - 1)) / 2;
        }

        // return average
        return median;
    }

    /**
     * returns a list of recognized ids
     * 
     * @param type 
     * 
     * @return ids
     */
    public ArrayList<Integer> getIds(EvaluationResultType type) {
        // create variable
        ArrayList<Integer> ids;

        // initialize variable
        switch (type) {

        case OVERALL:
            ids = new ArrayList<Integer>(this.resultsOverAll.keySet());
            break;

        case HIGHER_THAN_THRESHOLD:
            ids = new ArrayList<Integer>(this.resultsHigher.keySet());
            break;

        case LOWER_THEN_THRESHOLD:
            ids = new ArrayList<Integer>(this.resultsLower.keySet());
            break;

        default:
            ids = new ArrayList<Integer>();
            break;
        }

        // return list
        return ids;
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
     * @param type
     *      
     * @return the number of datasets
     */
    public int getSize(EvaluationResultType type) {

        // return size by type
        switch (type) {

        case OVERALL:
            if (this.getIds(type).size() == 0) return -1;
            return this.resultsOverAll.get(this.getIds(type).get(0)).size();

        case HIGHER_THAN_THRESHOLD:
            if (this.getIds(type).size() == 0) return -1;
            return this.resultsHigher.get(this.getIds(type).get(0)).size();

        case LOWER_THEN_THRESHOLD:
            if (this.getIds(type).size() == 0) return -1;
            return this.resultsLower.get(this.getIds(type).get(0)).size();

        default:
            return -1;
        }
    }

    /**
     * @param id
     * @param type
     * 
     * @return list of distances by id and type
     */
    @SuppressWarnings("boxing")
    public ArrayList<Double> getValues(int id, EvaluationResultType type) {

        // return values by type and id
        switch (type) {

        case OVERALL:
            if (!this.resultsOverAll.containsKey(id)) return new ArrayList<Double>();
            return this.resultsOverAll.get(id);

        case HIGHER_THAN_THRESHOLD:
            if (!this.resultsHigher.containsKey(id)) return new ArrayList<Double>();
            return this.resultsHigher.get(id);

        case LOWER_THEN_THRESHOLD:
            if (!this.resultsLower.containsKey(id)) return new ArrayList<Double>();
            return this.resultsLower.get(id);

        default:
            return new ArrayList<Double>();
        }
    }
}

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
package de.dfki.km.text20.lightning.evaluator;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Christoph Käding
 *
 */
public class EvaluationContainer {

    private Map<Integer, Double> results;

    private int keyKey;

    private int size;

    private double temp;

    public EvaluationContainer() {
        this.results = new Hashtable<Integer, Double>();
        this.keyKey = -1;
        this.size = 0;
    }

    public void add(int key, double value) {
        if (this.keyKey == -1) this.keyKey = key;
        if (this.keyKey == key) this.size++;
        if (this.results.containsKey(key)) this.temp = this.results.get(key);
        this.results.put(key, this.temp + value);
    }

    public double getAverage(int key) {
        if (this.results.containsKey(key)) return this.results.get(key) / this.size;
        else
            return 0;
    }
}

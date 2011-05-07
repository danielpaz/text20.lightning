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
package de.dfki.km.text20.lightning.worker.evaluator.worker;

/**
 * This container stores setting-specific distances between fixation and mouse. 
 * It is possible that different SettingContainer could include the same setting,
 * so an common container is needed.
 * 
 * @author Christoph Käding
 *
 */
public class DerivationContainer {

    /** number of adds */
    private int counter;

    /** summarized distance */
    private double derivation;

    /**
     * creates a new object and initializes variables
     */
    public DerivationContainer() {
        this.counter = 0;
        this.derivation = 0;
    }

    /**
     * adds distance and pupils to storage
     * 
     * @param distance
     */
    public void addDistance(double distance) {
        this.derivation = this.derivation + distance;
        this.counter++;
    }

    /**
     * @return averaged derivation
     */
    public double getAveragedDerivation() {
        if (this.counter > 0) return (this.derivation / this.counter);
        return 0;
    }
}

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

/**
 * This container stores setting-specific distances between fixation and mouse. 
 * It is possible that different SettingContainer could include the same setting,
 * so an common container is needed.
 * 
 * @author Christoph Käding
 *
 */
public class DerivationContainer {

    private int counter;

    private double derivation;

    private float[] pupilsize;

    private boolean firstGet;

    public DerivationContainer() {
        this.counter = 0;
        this.derivation = 0;
        this.pupilsize = new float[2];
        this.pupilsize[0] = 0;
        this.pupilsize[1] = 0;
        this.firstGet = true;
    }

    public void addDistance(double distance, float pupils[]) {
        this.derivation = this.derivation + distance;
        this.pupilsize[0] = this.pupilsize[0] + pupils[0];
        this.pupilsize[1] = this.pupilsize[1] + pupils[1];
        this.counter++;
    }

    public double getAveragedDerivation() {
        if (this.counter > 0) return (this.derivation / this.counter);
        return 0;
    }

    /**
     * averaged pupilsize from all entries
     * 
     * @return pupils
     */
    public float[] getAveragedPupils() {
        if ((this.firstGet) && (this.counter > 0)) {
            this.pupilsize[0] = this.pupilsize[0] / this.counter;
            this.pupilsize[1] = this.pupilsize[1] / this.counter;
            this.firstGet = false;
        }
        return this.pupilsize;
    }
}

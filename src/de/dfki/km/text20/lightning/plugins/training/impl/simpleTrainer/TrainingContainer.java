/*
 * TrainingContainer.java
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
package de.dfki.km.text20.lightning.plugins.training.impl.simpleTrainer;

import java.awt.Point;
import java.util.Map;

/**
 * @author Christoph Käding
 *
 */
public class TrainingContainer {

    private Map<String, Point> calculations;

    private Point mouseOffset;

    /**
     * 
     * @param calculations
     * @param fixation
     * @param mouseOffset
     */
    public TrainingContainer(Map<String, Point> calculations, Point mouseOffset) {
        this.calculations = calculations;
        this.mouseOffset = mouseOffset;
    }

    /**
     * @return the calculations
     */
    public Map<String, Point> getCalculations() {
        return this.calculations;
    }

    /**
     * @return the mouseOffset
     */
    public Point getMouseOffset() {
        return this.mouseOffset;
    }

}

/*
 * SimpleWarper.java
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
package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.simpleWarper;

import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.TreeMap;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;

/**
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class SimpleWarper implements MouseWarper {

    private int angleThres;

    private int distanceThres;

    private long durationThres;

    private int homeR;
    
    private int setR;

    private long currentTimeStamp;

    private TreeMap<Long, Point> mousePositions;

    private Point fixation;

    private Robot robot;

    public SimpleWarper() {
        this.angleThres = 0;
        this.distanceThres = 0;
        this.durationThres = 0;
        this.homeR = 0;
        this.mousePositions = new TreeMap<Long, Point>();
        this.fixation = new Point(0, 0);

        try {
            this.robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#initValues(int, int, long)
     */
    @Override
    public void initValues(int angleThreshold, int distanceThreshold,
                           long durationThreshold, int homeRadius, int setRadius) {
        this.angleThres = angleThreshold;
        this.distanceThres = distanceThreshold;
        this.durationThres = durationThreshold;
        this.homeR = homeRadius;
        this.setR = setRadius;

    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#setFixationPoint(java.awt.Point)
     */
    @Override
    public void setFixationPoint(Point fixation) {
        this.fixation = fixation;
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#addMousePosition(java.awt.Point)
     */
    @SuppressWarnings("boxing")
    @Override
    public void addMousePosition(Point position) {
        /*      this.mousePositions.add(new WarpContainer(position));
         *
         *        if (this.mousePositions.size() > 10) {
         *            this.mousePositions.remove(0);
         *        }
         *
         *        if (this.fixation == null) return;
         *
         *        for (int i = 0; i < this.mousePositions.size(); i++) {
         *            if ((this.mousePositions.get(this.mousePositions.size() - 1).getTimeStamp() - this.mousePositions.get(i).getTimeStamp()) < this.durationThres) {
         *                if (calculateAngle(this.mousePositions.get(0).getPosition(), this.mousePositions.get(i).getPosition()) <= this.angleThres) {
         *                    if (this.mousePositions.get(0).getPosition().distance(this.mousePositions.get(i).getPosition()) >= this.distanceThres) {
         *                        this.robot.mouseMove(this.fixation.x, this.fixation.y);
         *                    }
         *                }
         *            }
         *        }
         */
        this.currentTimeStamp = System.currentTimeMillis();

        this.mousePositions.put(this.currentTimeStamp, position);

        if (this.fixation == null) return;

        if (this.mousePositions.size() > 100)
            this.mousePositions.remove(this.mousePositions.firstEntry().getKey());

        if (this.mousePositions.lastEntry().getValue().distance(this.fixation) < this.homeR)
            return;

        System.out.println("radius");

        long key = this.currentTimeStamp - this.durationThres;

        if (!this.mousePositions.containsKey(key)) return;

        System.out.println("key");

        if (this.mousePositions.lastEntry().getValue().distance(this.mousePositions.get(key)) < this.distanceThres)
            return;

        System.out.println("distance");

        if (calculateAngle(this.mousePositions.get(key), this.mousePositions.lastEntry().getValue()) > this.angleThres)
            return;

        System.out.println("angle");

        if (this.fixation.distance(this.mousePositions.get(key)) < this.fixation.distance(this.mousePositions.lastEntry().getValue()))
            return;
        
        System.out.println("direction");
        
        calculateSetPoint();
        
        this.robot.mouseMove(this.fixation.x, this.fixation.y);
        
        this.fixation = null;
        
        System.out.println("warp");
    }

    private double calculateAngle(Point start, Point stop) {

        double a = start.distance(stop);
        double b = stop.distance(this.fixation);
        double c = this.fixation.distance(start);

        return Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c));
    }
    
    private void calculateSetPoint() {
        
        int dy = this.fixation.y - this.mousePositions.lastEntry().getValue().y;
        int dx = (int)Math.sqrt(Math.pow(dy, 2) + Math.pow(this.setR, 2));
        
        this.fixation.translate(dx, dy);
    }
}

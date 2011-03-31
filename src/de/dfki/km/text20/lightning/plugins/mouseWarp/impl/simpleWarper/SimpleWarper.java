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
import java.util.TreeMap;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;

/**
 * Simple version of mouse warper which checks angle between mouse-move-vector and start of movement to fixation,
 * distance from start to endpoint of movement, duration of movement and how close is the cursor to the fixation.
 * If all premises are fulfilled the cursor is moved to the fixation point 
 * (with the distance of setR to the startpoint of movement).
 * 
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class SimpleWarper implements MouseWarper {

    /** threshold for the angle */
    private int angleThres;

    /** threshold for the distance */
    private int distanceThres;

    /** threshold for the duration */
    private long durationThres;

    /** radius around the fixation within the mouse cursor won't be moved */
    private int homeR;

    /** 
     * Distance from the fixation to the point where the cursor will be set.
     * This is used to allow the user to move the mouse by himself to the target.
     */
    private int setR;

    /** time stamp */
    private long timeStamp;

    /** a list of all mouseposition within the durationThreshold */
    private TreeMap<Long, Point> mousePositions;

    /** last fixation */
    private Point fixation;

    /** necessary to move the mouse */
    private Robot robot;

    /** information object */
    private PluginInformation information;

    /** angle between mouse vector and start-fixation-vector */
    private double angle;

    /**
     * creates a new DistanceWarper and initializes some variables
     */
    public SimpleWarper() {
        this.angleThres = 10;
        this.distanceThres = 200;
        this.durationThres = 200;
        this.homeR = 200;
        this.mousePositions = new TreeMap<Long, Point>();
        this.fixation = new Point(0, 0);
        this.information = new PluginInformation("Simple Warper", "Simple Warper", false);
        this.angle = 0;

        try {
            this.robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void addMousePosition(Point position, int interval) {
        double distanceStartFix;
        double distanceStopFix;

        // timestamp
        this.timeStamp = System.currentTimeMillis();

        // add to map
        this.mousePositions.put(this.timeStamp, position);

        // check if fixation is placed and if there are enough positions stored, 20 is the rate of mouse updates
        if ((this.fixation == null) && (this.mousePositions.size() < this.durationThres * interval))
            return;

        // cut the array to the needed size, 20 is the rate of mouse updates
        if (this.mousePositions.size() * interval > this.durationThres)
            this.mousePositions.remove(this.mousePositions.firstEntry().getKey());

        // store distance
        distanceStartFix = this.mousePositions.firstEntry().getValue().distance(this.fixation);
        distanceStopFix = this.mousePositions.lastEntry().getValue().distance(this.fixation);

        // check if the cursor is already in home radius
        if (distanceStopFix < this.homeR) return;

        // check the direction of movement
        if (distanceStartFix <= distanceStopFix) return;

        // check the distance which the mouse has traveled within the time that is represented by the tree map
        // TODO: change to mouse acceleration
        if (this.mousePositions.lastEntry().getValue().distance(this.mousePositions.firstEntry().getValue()) < this.distanceThres)
            return;

        // checks the angle between mouse movement and vector between start of the mouse movement and fixation point
        this.angle = calculateAngle(this.mousePositions.firstEntry().getValue(), this.mousePositions.lastEntry().getValue());
        if (this.angle > this.angleThres) return;

        // moves fixation point a given distance to the mouse
        calculateSetPoint();

        // places mouse cursor at the fixation point
        this.robot.mouseMove(this.fixation.x, this.fixation.y);

        // resets variables
        this.fixation = null;
        this.mousePositions.clear();
    }

    /**
     * Calculates the angle between mouse-move-vector and start of movement to fixation.
     * This calculation is treated like it is placed in a cartesian coordinate system.
     * 
     * @param start point
     * @param stop point
     * @return the angle
     */
    private double calculateAngle(Point start, Point stop) {
        // angle in radian measure between x-axis and moving vector of the mouse
        double mouseTraceAngle = Math.atan2(start.y - stop.y, start.x - stop.x);

        // angle in radian measure between the x-axis and the vector from start point of the current mouse vector and the fixation point 
        double gazeVectorAngle = Math.atan2(start.y - this.fixation.y, start.x - this.fixation.x);

        // angle between this two ones in degrees
        double angleTmp = (gazeVectorAngle - mouseTraceAngle) * 180 / Math.PI;

        return Math.abs(angleTmp);
    }

    /**
     * Moves the fixation point in direction to the mouse vector. The distance is given by setR.
     * The coordinate system is the screen coordinate system.
     */
    private void calculateSetPoint() {
        // angle in radian measure between this x-axis and the vector from end point of the current mouse vector an the fixation point
        double phi = Math.atan2(this.mousePositions.lastEntry().getValue().y - this.fixation.y, this.mousePositions.lastEntry().getValue().x - this.fixation.y);

        // calculate x and y by their polar coordinates
        int x = (int) (this.setR * Math.cos(phi));
        int y = (int) (this.setR * Math.sin(phi));

        // move fixation point 
        this.fixation.translate(x, y);
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#getGui()
     */
    @Override
    public void showGui() {
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#shutDown()
     */
    @Override
    public void stop() {
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#start()
     */
    @Override
    public void start() {
    }
}

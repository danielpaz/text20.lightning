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
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.mouseWarp.WarpPluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyPluginInformation;

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

        // timestamp
        this.currentTimeStamp = System.currentTimeMillis();

        // add to map
        this.mousePositions.put(this.currentTimeStamp, position);

        // check if fixation is placed
        if (this.fixation == null) return;

        // cut the array to the needed size, 100 is the rate of mouse updates
        if (this.mousePositions.size() * 100 > this.durationThres)
            this.mousePositions.remove(this.mousePositions.firstEntry().getKey());

        // check if the cursor is already in home radius
        if (this.mousePositions.lastEntry().getValue().distance(this.fixation) < this.homeR)
            return;

        // check the distance which the mouse has traveled within the time that is represented by the tree map
        // TODO: change to mouse acceleration
        if (this.mousePositions.lastEntry().getValue().distance(this.mousePositions.firstEntry().getValue()) < this.distanceThres)
            return;

        // checks the angle between mouse movement and vector between start of the mouse movement and fixation point
        if (calculateAngle(this.mousePositions.firstEntry().getValue(), this.mousePositions.lastEntry().getValue()) > this.angleThres)
            return;

        // moves fixation point a given distance to the mouse
        calculateSetPoint();

        // places mouse cursor at the fixation point
        this.robot.mouseMove(this.fixation.x, this.fixation.y);

        // resets variables
        this.fixation = null;
        this.mousePositions.clear();
    }

    /**
     * cartesian
     * 
     * @param start
     * @param stop
     * @return
     */
    private double calculateAngle(Point start, Point stop) {
        // angle in radian measure between x-axis and moving vector of the mouse
        double mouseTraceAngle = Math.atan2(start.y - stop.y, start.x - stop.x);
        
        // angle in radian measure between the x-axis and the vector from start point of the current mouse vector and the fixation point 
        double gazeVectorAngle = Math.atan2(start.y - this.fixation.y, start.x - this.fixation.x);
        
        // angle between this two ones in degrees
        double angle = (gazeVectorAngle - mouseTraceAngle) * 180 / Math.PI;

        return Math.abs(angle);
    }

    /**
     * screen
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
    public WarpPluginInformation getInformation() {
        final WarpPluginInformation information = new WarpPluginInformation();
        information.displayName = "Simple Warper";
        
        return information;
    }
}

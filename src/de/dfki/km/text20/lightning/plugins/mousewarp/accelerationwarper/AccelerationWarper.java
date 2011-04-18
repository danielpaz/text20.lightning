/*
 * AccelerationWarper.java
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
package de.dfki.km.text20.lightning.plugins.mousewarp.accelerationwarper;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.mousewarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.mousewarp.accelerationwarper.gui.AccelerationWarperConfigImpl;

/**
 * Simple version of mouse warper which checks angle between
 * mouse-move-vector and start of movement to fixation, distance from start to
 * endpoint of movement, duration of movement and how close is the cursor to the
 * fixation. If all premises are fulfilled the cursor is moved to the fixation
 * point (with the distance of setR to the startpoint of movement).
 * 
 * @author Christoph Käding
 * 
 */
@PluginImplementation
public class AccelerationWarper implements MouseWarper {

    /** threshold for the angle */
    private int angleThres;

    /** threshold for the speed */
    private double accThres;

    /** needed time to react on the mouse warp */
    private int reactionTime;

    /** a list of all stored mouseposition */
    private ArrayList<Point> mousePositions;

    /** last fixation */
    private Point fixation;

    /** necessary to move the mouse */
    private Robot robot;

    /** information object */
    private PluginInformation information;

    /** angle between mouse vector and start-fixation-vector */
    private double angle;

    /** stored properties for this plugin */
    private AccelerationWarperProperties propertie;

    /** indicates if any calculations are already running */
    private boolean isProcessing;

    /** distance from sthe start to the stop of the mousevector */
    private double distanceStopFix;

    /** speed of the mouse */
    private double velocityStartMid;

    /** speed of the mouse */
    private double velocityMidEnd;

    /** radius in which the mousecursor will be placed around the fixation */
    private int setR;

    /** point where the mousecursor will be placed */
    private Point setPoint;

    /** current screen resolution */
    private int xMax;

    /** current screen resolution */
    private int yMax;

    private double acceleration;

    private boolean accelerated;

    /**
     * creates a new DistanceWarper and initializes some variables
     */
    public AccelerationWarper() {
        // initialize variables
        this.angleThres = 0;
        this.accThres = 0;
        this.reactionTime = 0;
        this.mousePositions = new ArrayList<Point>();
        this.fixation = null;
        this.information = new PluginInformation("Acceleration Warper", "..soon", true);
        this.angle = 0;
        this.propertie = null;
        this.isProcessing = false;
        this.distanceStopFix = 0;
        this.velocityStartMid = 0;
        this.velocityMidEnd = 0;
        this.setR = 0;
        this.setPoint = new Point();
        this.xMax = 0;
        this.yMax = 0;
        this.acceleration = 0;
        this.accelerated = false;

        try {
            this.robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#initValues(int,
     * int, long)
     */
    @Override
    public void start() {
        // load variables from properties
        this.propertie = AccelerationWarperProperties.getInstance();
        this.angleThres = this.propertie.getAngleThreshold();
        this.accThres = this.propertie.getAcceleration();
        this.reactionTime = this.propertie.getReactionTime();
        this.xMax = Toolkit.getDefaultToolkit().getScreenSize().width;
        this.yMax = Toolkit.getDefaultToolkit().getScreenSize().height;

        // refresh map
        this.refreshMouseMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#setFixationPoint
     * (java.awt.Point)
     */
    @Override
    public void setFixationPoint(Point fixation) {
        if (this.isProcessing) return;
        this.fixation = fixation;
        if (this.fixation.x < 0) this.fixation.x = 0;
        else if (this.fixation.x > this.xMax) this.fixation.x = this.xMax;
        if (this.fixation.y < 0) this.fixation.y = 0;
        else if (this.fixation.y > this.yMax) this.fixation.y = this.yMax;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#addMousePosition
     * (java.awt.Point)
     */
    @Override
    public void addMousePosition(Point position, int interval, boolean isFixationValid) {
        this.isProcessing = true;

        // TODO: debugging
        long tmp = System.currentTimeMillis();

        // add to map
        this.mousePositions.add(position);

        // remove first
        this.mousePositions.remove(0);

        // check if fixation is placed
        if ((this.fixation == null || !isFixationValid)) {
            this.isProcessing = false;
            this.accelerated = false;
            return;
        }

        // calculate velocity and setR
        this.velocityStartMid = this.mousePositions.get(0).distance(this.mousePositions.get(1)) / interval;
        this.velocityMidEnd = this.mousePositions.get(1).distance(this.mousePositions.get(2)) / interval;
        this.setR = (int) (this.velocityMidEnd * this.reactionTime);

        // check velocity, this means the movement slows down
        if ((this.velocityMidEnd == 0) || (this.velocityStartMid == 0)) {
            this.isProcessing = false;
            this.accelerated = false;
            return;
        }

        // calculate acceleration
        this.acceleration = (this.velocityMidEnd - this.velocityStartMid) / interval;

        // TODO: debugging
        //        if (this.acceleration > 0) {
        //            System.out.println(this.velocityStartMid + " | " + this.velocityMidEnd + " | " + this.acceleration);
        //        }

        // check the acceleration
        if ((this.acceleration < this.accThres) && !this.accelerated) {
            this.isProcessing = false;
            return;
        }
        if ((this.acceleration < this.accThres) && this.accelerated) {
            this.calculate(tmp);
            return;
        }

        // set accelerated true because threshold was exceeded
        this.accelerated = true;
    }

    private void calculate(long tmp) {

        // store distance
        this.distanceStopFix = this.mousePositions.get(2).distance(this.fixation);

        // check if the cursor is already in home radius
        if (this.distanceStopFix < this.setR) {
            this.isProcessing = false;
            this.accelerated = false;
            return;
        }

        // checks the angle between mouse movement and vector between end of the mouse movement and fixation point
        this.angle = calculateAngle(this.mousePositions.get(1), this.mousePositions.get(2));
        if ((this.angle > this.angleThres)) {
            this.isProcessing = false;

            // no this.accelerated = false; because a change in direction could be lead to an correct movement,
            // but if the boolean is reseted here, it is not recognized
            return;
        }

        // calculate setpoint
        this.setPoint = this.calculateSetPoint(this.setR);

        // places mouse cursor at the fixation point
        this.robot.mouseMove(this.setPoint.x, this.setPoint.y);

        // indicate warp
        System.out.println("Warp - Mouse move to (" + this.setPoint.x + "," + this.setPoint.y + ") over a distance of " + (int) this.setPoint.distance(this.mousePositions.get(2)) + " Pixels and a offset of " + this.setR + " Pixels. Method: Acceleration Warper");

        // TODO: debugging
        //        this.drawPicture();

        // resets variables
        this.fixation = null;
        this.refreshMouseMap();
        this.accelerated = false;
        this.isProcessing = false;

        // TODO: debugging
        System.out.println(System.currentTimeMillis() - tmp);
    }

    /**
     * Calculates the angle between mouse-move-vector and start of movement
     * to fixation. This calculation is treated like it is placed in a cartesian
     * coordinate system.
     * 
     * @param start
     *            point
     * @param stop
     *            point
     * @return the angle
     */
    private double calculateAngle(Point start, Point stop) {
        // angle in radian measure between x-axis and moving vector of the mouse
        double mouseTraceAngle = Math.atan2(start.y - stop.y, start.x - stop.x);

        // angle in radian measure between the x-axis and the vector from start
        // point of the current mouse vector and the fixation point
        double gazeVectorAngle = Math.atan2(start.y - this.fixation.y, start.x - this.fixation.x);

        // angle between this two ones in degrees
        double angleTmp = (gazeVectorAngle - mouseTraceAngle) * 180 / Math.PI;

        return Math.abs(angleTmp);
    }

    /**
     * Calculates a set point between fixation point and the mouse vector. The
     * distance is given by setR. The coordinate system is the cartesian
     * coordinate system.
     */
    private Point calculateSetPoint(int radius) {
        // angle in radian measure between this x-axis and the vector from end
        // of mouse-vector to fixation
        double phi = Math.atan2(this.mousePositions.get(2).y - this.fixation.y, this.mousePositions.get(2).x - this.fixation.x);

        // calculate x and y by their polar coordinates
        int x = (int) (radius * Math.cos(phi));
        int y = (int) (radius * Math.sin(phi));

        // return set point
        return new Point(this.fixation.x + x, this.fixation.y + y);
    }

    /*
     * (non-Javadoc) y
     * 
     * @see
     * de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#getInformation
     * ()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

    /**
     * clears the mouse vector and fills it with dummydata
     */
    private void refreshMouseMap() {
        Point currentMousePos = MouseInfo.getPointerInfo().getLocation();
        this.mousePositions.clear();
        this.mousePositions.add(currentMousePos);
        this.mousePositions.add(currentMousePos);
        this.mousePositions.add(currentMousePos);
    }

    /**
     * writes current movement with recognized target to a file this is for
     * debugging
     */
    @SuppressWarnings("unused")
    private void drawPicture() {
        // initialize variables
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage screenShot = null;

        // create screenshot
        Rectangle screenShotRect = new Rectangle(0, 0, dimension.width, dimension.height);
        try {
            screenShot = new Robot().createScreenCapture(screenShotRect);
        } catch (AWTException e1) {
            e1.printStackTrace();
            return;
        }
        File file = new File("tmp/warp_V3_" + System.currentTimeMillis() + ".png");

        try {
            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            // visualize fixation point
            graphic.setColor(new Color(255, 0, 0, 255));
            graphic.drawOval(this.fixation.x - 5, this.fixation.y - 5, 10, 10);
            graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + this.fixation.x, 12 + this.fixation.y);
            graphic.drawChars(("" + this.angle).toCharArray(), 0, ("" + this.angle).toCharArray().length, 12 + this.fixation.x, 24 + this.fixation.y);
            graphic.setColor(new Color(255, 0, 0, 32));
            graphic.fillOval(this.fixation.x - 5, this.fixation.y - 5, 10, 10);

            // visualize mouse vector
            graphic.setColor(new Color(0, 0, 255, 255));
            graphic.drawOval((int) this.mousePositions.get(0).getX() - 5, (int) this.mousePositions.get(0).getY() - 5, 10, 10);
            graphic.drawChars(("0").toCharArray(), 0, ("0").toCharArray().length, (int) this.mousePositions.get(0).getX() + 12, (int) this.mousePositions.get(0).getY() + 12);
            graphic.setColor(new Color(0, 0, 255, 32));
            graphic.fillOval((int) this.mousePositions.get(0).getX() - 5, (int) this.mousePositions.get(0).getY() - 5, 10, 10);
            graphic.drawLine((int) this.mousePositions.get(0).getX(), (int) this.mousePositions.get(0).getY(), (int) this.mousePositions.get(1).getX(), (int) this.mousePositions.get(1).getY());
            graphic.setColor(new Color(0, 0, 255, 255));
            graphic.drawOval((int) this.mousePositions.get(1).getX() - 5, (int) this.mousePositions.get(1).getY() - 5, 10, 10);
            graphic.drawChars(("1").toCharArray(), 0, ("1").toCharArray().length, (int) this.mousePositions.get(1).getX() + 12, (int) this.mousePositions.get(1).getY() + 12);
            graphic.setColor(new Color(0, 0, 255, 32));
            graphic.fillOval((int) this.mousePositions.get(1).getX() - 5, (int) this.mousePositions.get(1).getY() - 5, 10, 10);
            graphic.drawLine((int) this.mousePositions.get(1).getX(), (int) this.mousePositions.get(1).getY(), (int) this.mousePositions.get(2).getX(), (int) this.mousePositions.get(2).getY());
            graphic.setColor(new Color(0, 0, 255, 255));
            graphic.drawOval((int) this.mousePositions.get(2).getX() - 5, (int) this.mousePositions.get(2).getY() - 5, 10, 10);
            graphic.drawChars(("2").toCharArray(), 0, ("2").toCharArray().length, (int) this.mousePositions.get(2).getX() + 12, (int) this.mousePositions.get(2).getY() + 12);
            graphic.setColor(new Color(0, 0, 255, 32));
            graphic.fillOval((int) this.mousePositions.get(2).getX() - 5, (int) this.mousePositions.get(2).getY() - 5, 10, 10);

            // calculate and visualize setpoint
            graphic.setColor(new Color(0, 255, 0, 255));
            graphic.drawOval(this.setPoint.x - 5, this.setPoint.y - 5, 10, 10);
            graphic.drawChars(("set point").toCharArray(), 0, 9, 12 + this.setPoint.x, 12 + this.setPoint.y);
            graphic.setColor(new Color(0, 255, 0, 32));
            graphic.fillOval(this.setPoint.x - 5, this.setPoint.y - 5, 10, 10);

            // write the image
            file.mkdirs();
            ImageIO.write(screenShot, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#getGui()
     */
    @SuppressWarnings("unused")
    @Override
    public void showGui() {
        // create new gui to show it
        new AccelerationWarperConfigImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#shutDown()
     */
    @Override
    public void stop() {
        // write current poperties in a file
        this.propertie.writeProperties();
    }
}

/*
 * VelocityWarperV1.java
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
package de.dfki.km.text20.lightning.plugins.mousewarp.velocitywarperv1;

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
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.mousewarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.mousewarp.velocitywarperv1.gui.VelocityWarperV1ConfigImpl;

/**
 * Simple version of mouse warper which checks angleFirst between
 * mouse-move-vector and start of movement to fixation, distance from start to
 * endpoint of movement, duration of movement and how close is the cursor to the
 * fixation. If all premises are fulfilled the cursor is moved to the fixation
 * point (with the distance of setR to the startpoint of movement).
 * 
 * @author Christoph Käding
 * 
 */
@PluginImplementation
public class VelocityWarperV1 implements MouseWarper {

    /** threshold for the angleFirst */
    private int angleThres;

    /** threshold for the speed */
    private double speedThres;

    /** needed time to react on the mouse warp */
    private int reactionTime;

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
    private double angleFirst;

    /** angle between mouse vector and start-fixation-vector */
    private double angleSecLast;

    /** key of the second last entry of the hashmap */
    private long secondLastKey;

    /** stored properties for this plugin */
    private VelocityWarperV1Properties propertie;

    /** indicates if any calculations are already running */
    private boolean isProcessing;

    /** distance from sthe start to the stop of the mousevector */
    private double distanceStopFix;

    /** speed of the mouse */
    private double velocity;

    /** radius in which the mousecursor will be placed around the fixation */
    private int setR;

    /** point where the mousecursor will be placed */
    private Point setPoint;

    /** current screen resolution */
    private int xMax;

    /** current screen resolution */
    private int yMax;

    /**
     * creates a new DistanceWarper and initializes some variables
     */
    public VelocityWarperV1() {
        // initialize variables
        this.angleThres = 0;
        this.speedThres = 0;
        this.reactionTime = 0;
        this.mousePositions = new TreeMap<Long, Point>();
        this.fixation = null;
        this.information = new PluginInformation("Velocity Warper V1", "Uses mouse velocityStartMid to calculate warpjump. BETA", true);
        this.angleFirst = 0;
        this.angleSecLast = 0;
        this.propertie = null;
        this.isProcessing = false;
        this.distanceStopFix = 0;
        this.velocity = 0;
        this.setR = 0;
        this.setPoint = new Point();
        this.xMax = 0;
        this.yMax = 0;

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
        this.propertie = VelocityWarperV1Properties.getInstance();
        this.angleThres = this.propertie.getAngleThreshold();
        this.speedThres = this.propertie.getSpeed();
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
    @SuppressWarnings("boxing")
    @Override
    public void addMousePosition(Point position, int interval, boolean isFixationValid) {
        this.isProcessing = true;

        // TODO: debugging
        //		long tmp = System.currentTimeMillis();

        // key of the second last
        this.secondLastKey = this.timeStamp;

        // timestamp
        this.timeStamp = System.currentTimeMillis();

        // add to map
        this.mousePositions.put(this.timeStamp, position);

        // remove first
        this.mousePositions.remove(this.mousePositions.firstKey());

        // check if fixation is placed
        if ((this.fixation == null || !isFixationValid)) {
            this.isProcessing = false;
            return;
        }

        // calculate setRadius and speed
        this.velocity = this.mousePositions.lastEntry().getValue().distance(this.mousePositions.firstEntry().getValue()) / ((this.mousePositions.size() - 1) * interval);
        this.setR = (int) (this.velocity * this.reactionTime);

        // TODO: debugging
        // if (speed > 0)
        // System.out.println(speed + " * " + this.reactionTime + " = " + setR);

        // check the speed
        if (this.velocity < this.speedThres) {
            this.isProcessing = false;
            return;
        }

        // store distance
        this.distanceStopFix = this.mousePositions.lastEntry().getValue().distance(this.fixation);

        // check if the cursor is already in home radius
        if (this.distanceStopFix < this.setR) {
            this.isProcessing = false;
            return;
        }

        // checks the angleFirst between mouse movement and vector between start
        // of the mouse movement and fixation point
        this.angleFirst = calculateAngle(this.mousePositions.firstEntry().getValue(), this.mousePositions.lastEntry().getValue());
        // System.out.println("angleFirst: " + this.angleFirst);
        if ((this.angleFirst > this.angleThres)) {
            this.isProcessing = false;
            return;
        }
        this.angleSecLast = calculateAngle(this.mousePositions.get(this.secondLastKey), this.mousePositions.lastEntry().getValue());
        // System.out.println("angleSecLast: " + this.angleSecLast);
        if ((this.angleSecLast > this.angleThres)) {
            this.isProcessing = false;
            return;
        }

        // calculate setpoint
        this.setPoint = this.calculateSetPoint(this.setR);

        // places mouse cursor at the fixation point
        this.robot.mouseMove(this.setPoint.x, this.setPoint.y);

        // indicate warp
        System.out.println("Warp - Mouse move to (" + this.setPoint.x + "," + this.setPoint.y + ") over a distance of " + (int) this.setPoint.distance(this.mousePositions.lastEntry().getValue()) + " Pixels. Method: Velocity Warper");

        // TODO: debugging
        // this.drawPicture(setPoint);

        // resets variables
        this.fixation = null;
        this.refreshMouseMap();
        this.isProcessing = false;

        // TODO: debugginh
        //		System.out.println(System.currentTimeMillis() - tmp);
    }

    /**
     * Calculates the angleFirst between mouse-move-vector and start of movement
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

        // angleFirst between this two ones in degrees
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
        double phi = Math.atan2(this.mousePositions.lastEntry().getValue().y - this.fixation.y, this.mousePositions.lastEntry().getValue().x - this.fixation.x);

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
    @SuppressWarnings("boxing")
    private void refreshMouseMap() {
        long iMax = 10;
        Point currentMousePos = MouseInfo.getPointerInfo().getLocation();
        this.mousePositions.clear();
        this.timeStamp = iMax - 1;
        for (long i = 0; i < iMax; i++) {
            this.mousePositions.put(i, new Point(currentMousePos));
        }
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
        ArrayList<Long> keys = new ArrayList<Long>(this.mousePositions.keySet());

        // create screenshot
        Rectangle screenShotRect = new Rectangle(0, 0, dimension.width, dimension.height);
        try {
            screenShot = new Robot().createScreenCapture(screenShotRect);
        } catch (AWTException e1) {
            e1.printStackTrace();
            return;
        }
        File file = new File("tmp/warp_" + System.currentTimeMillis() + ".png");

        try {
            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            // visualize fixation point
            graphic.setColor(new Color(255, 0, 0, 255));
            graphic.drawOval(this.fixation.x - 5, this.fixation.y - 5, 10, 10);
            graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + this.fixation.x, 12 + this.fixation.y);
            graphic.drawChars(("" + this.angleFirst).toCharArray(), 0, ("" + this.angleFirst).toCharArray().length, 12 + this.fixation.x, 24 + this.fixation.y);
            graphic.drawChars(("" + this.angleSecLast).toCharArray(), 0, ("" + this.angleSecLast).toCharArray().length, 12 + this.fixation.x, 36 + this.fixation.y);
            graphic.setColor(new Color(255, 0, 0, 32));
            graphic.fillOval(this.fixation.x - 5, this.fixation.y - 5, 10, 10);

            // visualize mouse vector
            for (int i = 0; i < keys.size() - 1; i++) {
                graphic.setColor(new Color(0, 0, 255, 255));
                graphic.drawOval((int) this.mousePositions.get(keys.get(i)).getX() - 5, (int) this.mousePositions.get(keys.get(i)).getY() - 5, 10, 10);
                graphic.drawChars(("" + i).toCharArray(), 0, ("" + i).toCharArray().length, (int) this.mousePositions.get(keys.get(i)).getX() + 12, (int) this.mousePositions.get(keys.get(i)).getY() + 12);
                graphic.setColor(new Color(0, 0, 255, 32));
                graphic.fillOval((int) this.mousePositions.get(keys.get(i)).getX() - 5, (int) this.mousePositions.get(keys.get(i)).getY() - 5, 10, 10);
                graphic.drawLine((int) this.mousePositions.get(keys.get(i)).getX(), (int) this.mousePositions.get(keys.get(i)).getY(), (int) this.mousePositions.get(keys.get(i + 1)).getX(), (int) this.mousePositions.get(keys.get(i + 1)).getY());
            }
            graphic.setColor(new Color(0, 0, 255, 255));
            graphic.drawOval((int) this.mousePositions.lastEntry().getValue().getX() - 5, (int) this.mousePositions.lastEntry().getValue().getY() - 5, 10, 10);
            graphic.drawChars(("" + (this.mousePositions.size() - 1)).toCharArray(), 0, ("" + (this.mousePositions.size() - 1)).toCharArray().length, (int) this.mousePositions.lastEntry().getValue().getX() + 12, (int) this.mousePositions.lastEntry().getValue().getY() + 12);
            graphic.setColor(new Color(0, 0, 255, 32));
            graphic.fillOval((int) this.mousePositions.lastEntry().getValue().getX() - 5, (int) this.mousePositions.lastEntry().getValue().getY() - 5, 10, 10);

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
    @Override
    public JFrame getGui() {
        // create new gui to show it
        return new VelocityWarperV1ConfigImpl();
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

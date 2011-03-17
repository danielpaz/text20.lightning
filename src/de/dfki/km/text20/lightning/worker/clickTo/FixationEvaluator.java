/*
 * FixationEvaluator.java
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
 *
 */
package de.dfki.km.text20.lightning.worker.clickTo;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;

/**
 * Takes a screenshot with in properties stored dimensions around the gaze point 
 * and gives it to the current saliency detector plugin. The offset which is provided by the plugin 
 * is used to click on the calculated target.
 * 
 * @author Christoph Käding
 *
 */
public class FixationEvaluator {

    /** Point of fixation which is provided by the eyetracker */
    private Point fixation;

    /** previous mouse position */
    private Point location;

    /** calculated offset from fixation point to processed target */
    private Point offset;

    /** robot for mouse movement and clicking, creating screenshots*/
    private Robot robot;

    /** current time */
    private long timestamp;

    /** */
    private BufferedImage screenShot;

    /** global used properties */
    private Properties properties;

    /** internal used plugin manager */
    private InternalPluginManager manager;

    /**
     * creates the FixationEvaluator
     * 
     * @param manager
     */
    public FixationEvaluator() {

        // initialize variables
        this.fixation = new Point();
        this.location = new Point();
        this.offset = new Point();
        this.properties = MainClass.getInstance().getProperties();
        this.manager = MainClass.getInstance().getInternalPluginManager();

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            MainClass.getInstance().exit();
        }
    }

    /**
     * sets the fixation point which is provided by the eyetracker
     * @param fixation 
     */
    public void setFixationPoint(Point fixation) {
        this.fixation = fixation;
    }

    /**
     * makes a screenshot with choosed dimension around the fixation point an starts the processing of it. 
     * At the end a mouseclick were taken to the calculated point.
     */
    public void evaluateLocation() {
        // check if a valid fixation is placed
        if(this.fixation == null) return;
        
        // store current mouse position to reset is later 
        this.location = MouseInfo.getPointerInfo().getLocation();

        // create screenshot
        Rectangle screenShotRect = new Rectangle(this.fixation.x - this.properties.getDimension() / 2, this.fixation.y - this.properties.getDimension() / 2, this.properties.getDimension(), this.properties.getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);
        
        // create timestamp
        this.timestamp = System.currentTimeMillis();

        // use plugin to calculate offset
        if (this.manager.getCurrentSaliencyDetector() != null)
            this.offset = this.manager.getCurrentSaliencyDetector().analyse(this.screenShot);

        // update the logfile
        String logString = "Normal - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Offset: (" + this.offset.x + "," + this.offset.y + "), Dimension: " + this.properties.getDimension() + ", Method: " + this.manager.getCurrentSaliencyDetector().getInformation().getDisplayName();
        System.out.println(logString);
        MainClass.getInstance().getChannel().status(logString);

        // click to calculated target and reset mouseposition
        this.robot.mouseMove(this.fixation.x + this.offset.x, this.fixation.y + this.offset.y);
        this.robot.mousePress(InputEvent.BUTTON1_MASK);
        this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
        this.robot.mouseMove(this.location.x, this.location.y);
        
        // reset fixation point
        this.fixation = null;
        
        // for debugging
        this.drawPicture();
    }

    /**
     * writes current screenshot with recognized target to a file
     * this is for debugging
     */
    private void drawPicture() {
        // initialize variables
        int dimension = this.screenShot.getHeight();
        File file = new File("tmp/click_" + System.currentTimeMillis() + ".png");

        try {
            // create screenshot graphic
            Graphics2D graphic = this.screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            // visualize fixation point 
            graphic.setColor(new Color(255, 255, 0, 255));
            graphic.drawOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);
            graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + dimension / 2, 12 + dimension / 2);
            graphic.setColor(new Color(255, 255, 0, 32));
            graphic.fillOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);

            // visualize calculations
            graphic.setColor(new Color(0, 0, 255, 255));
            graphic.drawOval(this.offset.x - 5 + dimension / 2, this.offset.y - 5 + dimension / 2, 10, 10);
            graphic.drawChars(this.manager.getCurrentSaliencyDetector().getInformation().getDisplayName().toCharArray(), 0, this.manager.getCurrentSaliencyDetector().getInformation().getDisplayName().toCharArray().length, this.offset.x + 12 + dimension / 2, this.offset.y + 12 + dimension / 2);
            graphic.setColor(new Color(0, 0, 255, 32));
            graphic.fillOval(this.offset.x - 5 + dimension / 2, this.offset.y - 5 + dimension / 2, 10, 10);

            // write the image
            file.mkdirs();
            ImageIO.write(this.screenShot, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
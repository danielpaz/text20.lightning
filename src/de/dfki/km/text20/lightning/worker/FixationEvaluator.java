/*
 * FixationEvaluator.java
 *
 * Copyright (c) 2011, Christoph K�ding, DFKI. All rights reserved.
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
package de.dfki.km.text20.lightning.worker;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.color.ColorSpace;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.plugins.MethodManager;
import de.dfki.km.text20.lightning.tools.Tools;

/**
 * Takes a screenshot with in properties stored dimensions around the gaze point 
 * and gives it to the current saliency detector plugin. The offset which is provided by the plugin 
 * is used to click on the calculated target.
 * 
 * @author Christoph K�ding
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
    
    /** converter to color the screenshot in grayscale */
    private ColorConvertOp colorConverterGray;
    
    /** converter to recolor the screenshot in rgb */
    private ColorConvertOp colorConverterRGB;
    
    /** current time */
    private long timestamp;
    
    /** */
    private BufferedImage screenShot;
        
    /** necessary to get the current saliency detector plugin*/
    private MethodManager methodManager;

    /**
     * creates the FixationEvaluator
     * 
     * @param manager
     */
    public FixationEvaluator(MethodManager manager) {
        
        // initialize variables
        this.methodManager = manager;
        this.colorConverterGray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        this.colorConverterRGB = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
        this.fixation = new Point();
        this.location = new Point();
        this.offset = new Point();

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            MainClass.exit();
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
        
        // store current mouse position to reset is later 
        this.location = MouseInfo.getPointerInfo().getLocation();

        // create screenshot
        Rectangle screenShotRect = new Rectangle(this.fixation.x - MainClass.getProperties().getDimension() / 2, this.fixation.y - MainClass.getProperties().getDimension() / 2, MainClass.getProperties().getDimension(), MainClass.getProperties().getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);
        this.screenShot = this.colorConverterGray.filter(this.screenShot, null);

        // create timestamp
        this.timestamp = System.currentTimeMillis();

        // use plugin to calculate offset
        this.offset = this.methodManager.getCurrentSaliencyDetector().analyse(this.screenShot);

        if (MainClass.getProperties().isShowImages()) {
            // draw images of the current evaluation to the outputpath
            this.drawImages();
        }

        // update the logfile
        String logString = "Normal - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Offset: (" + this.offset.x + "," + this.offset.y + "), Dimension: " + MainClass.getProperties().getDimension() + System.getProperty("line.separator");
        Tools.updateLogFile(logString);
        
        System.out.println("click to: (" + (this.fixation.x + this.offset.x) + "," + (this.fixation.y + this.offset.y) + ")");

        // click to calculated target and reset mouseposition
        this.robot.mouseMove(this.fixation.x + this.offset.x, this.fixation.y + this.offset.y);
        this.robot.mousePress(InputEvent.BUTTON1_MASK);
        this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
        this.robot.mouseMove(this.location.x, this.location.y);
    }

    /**
     * draws the images of the screenshot and the derivated screenshot to the outputdirectory
     */
    private void drawImages() {
        // recolor the screenshot
        this.screenShot = this.colorConverterRGB.filter(this.screenShot, null);
        Graphics2D graphic = this.screenShot.createGraphics();
        
        // visualize provided target
        graphic.setColor(new Color(255, 255, 0, 255));
        graphic.drawOval(MainClass.getProperties().getDimension() / 2 - 5, MainClass.getProperties().getDimension() / 2 - 5, 10, 10);
        graphic.drawChars(("catched position").toCharArray(), 0, 16, 3, 10);
        graphic.setColor(new Color(255, 255, 0, 64));
        graphic.fillOval(MainClass.getProperties().getDimension() / 2 - 5, MainClass.getProperties().getDimension() / 2 - 5, 10, 10);
        
        // visualize calculated target
        graphic.setColor(new Color(255, 0, 0, 255));
        graphic.drawOval(MainClass.getProperties().getDimension() / 2 + this.offset.x - 5, MainClass.getProperties().getDimension() / 2 + this.offset.y - 5, 10, 10);
        graphic.drawChars(("calculated position").toCharArray(), 0, 19, 3, 25);
        graphic.setColor(new Color(255, 0, 0, 64));
        graphic.fillOval(MainClass.getProperties().getDimension() / 2 + this.offset.x - 5, MainClass.getProperties().getDimension() / 2 + this.offset.y - 5, 10, 10);
        
        // write the image
        Tools.writeImage(this.screenShot, this.timestamp + "_screenshot.png");
    }
}
/*
 * PrecisionTrainer.java
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
package de.dfki.km.text20.lightning.worker.training;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.MainClass;

/**
 * The precision trainer is used in trainings mode. Here the collected data is handeled.
 * 
 * @author Christoph Käding
 *
 */
//TODO: do sth with the trainingsdata
//TODO: use plugin for trainings method
public class PrecisionTrainer {

    /** last fixation point */
    private Point fixation;

    /** associated mouse position which shows the real target*/
    private Point mousePosition;

    /** offset between fixation point and mouse position */
    private Point offset;

    /** robot for creating screenshots */
    private Robot robot;

    /** actual time */
    private long timestamp;

    /** screenshot of the target area*/
    private BufferedImage screenShot;

    /**
     * creates the precision trainer
     * 
     * @param manager
     */
    public PrecisionTrainer() {

        // initialize variables
        this.fixation = new Point();
        this.offset = new Point();

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            MainClass.getInstance().exit();
        }
    }

    /**
     * stores fixation and creates timestamp
     * 
     * @param fixation
     */
    public void setFixationPoint(Point fixation) {
        this.fixation = fixation;
        this.timestamp = System.currentTimeMillis();
        this.doSth();
    }

    /**
     * sets the mouseposition to associate it with the stored fixation
     * 
     * @param mousePosition
     */
    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
        Rectangle screenShotRect = new Rectangle(this.fixation.x - MainClass.getInstance().getProperties().getDimension() / 2, this.fixation.y - MainClass.getInstance().getProperties().getDimension() / 2, MainClass.getInstance().getProperties().getDimension(), MainClass.getInstance().getProperties().getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);

        // calculate offset
        this.offset.setLocation(this.mousePosition.x - this.fixation.x, this.mousePosition.y - this.fixation.y);
        
        // use current trainer plugin to recognize step
        if (MainClass.getInstance().getInternalPluginManager().getCurrentTrainer() != null)
            MainClass.getInstance().getInternalPluginManager().getCurrentTrainer().setStep(this.screenShot, this.offset);

        if (MainClass.getInstance().getProperties().isShowImages()) {
            // draw both points to a image
            this.drawPicture();
        }
        
        // update logfile
        String logString = new String("Training - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Mouseposition: (" + this.mousePosition.x + "," + this.mousePosition.y + "), Dimension: " + MainClass.getInstance().getProperties().getDimension());
        System.out.println(logString);
        MainClass.getInstance().getChannel().status(logString);
    }

    /**
     * draw image to global output path
     */
    private void drawPicture() {

        // create screenshot
        Graphics2D graphic = this.screenShot.createGraphics();

        // visualize fixation point 
        graphic.setColor(new Color(255, 255, 0, 255));
        graphic.drawOval(MainClass.getInstance().getProperties().getDimension() / 2 - 5, MainClass.getInstance().getProperties().getDimension() / 2 - 5, 10, 10);
        graphic.drawChars(("fixation point").toCharArray(), 0, 14, 3, 10);
        graphic.setColor(new Color(255, 255, 0, 32));
        graphic.fillOval(MainClass.getInstance().getProperties().getDimension() / 2 - 5, MainClass.getInstance().getProperties().getDimension() / 2 - 5, 10, 10);

        // visualize mouse position
        graphic.setColor(new Color(255, 0, 0, 255));
        graphic.drawOval(this.offset.x + MainClass.getInstance().getProperties().getDimension() / 2, this.offset.y + MainClass.getInstance().getProperties().getDimension() / 2, 10, 10);
        graphic.drawChars(("mouse position").toCharArray(), 0, 14, 3, 25);
        graphic.setColor(new Color(255, 0, 0, 32));
        graphic.fillOval(this.offset.x + MainClass.getInstance().getProperties().getDimension() / 2, this.offset.y + MainClass.getInstance().getProperties().getDimension() / 2, 10, 10);

        // write the image
        try {
            File outputfile = new File(MainClass.getInstance().getProperties().getOutputPath() + File.separator + this.timestamp + "_training.png");
            outputfile.mkdirs();
            ImageIO.write(this.screenShot, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSth() {
        System.out.println("called");
        if (AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.TRANSLUCENT))
            return;
        System.out.println("translucent");
        if (AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.PERPIXEL_TRANSLUCENT))
            return;
        System.out.println("perpixel");
        if (AWTUtilitiesWrapper.isTranslucencyCapable(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()))
            return;
        System.out.println("graphicsconfig");
        
    }
}

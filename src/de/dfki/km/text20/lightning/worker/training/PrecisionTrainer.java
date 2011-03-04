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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * The precision trainer is used in trainings mode. Here the collected data is handeled.
 * 
 * @author Christoph Käding
 *
 */
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

    /** stores result of every saliency detector */
    private Map<String, Point> calculations;

    /**
     * creates the precision trainer
     * 
     * @param manager
     */
    public PrecisionTrainer() {

        // initialize variables
        this.fixation = new Point();
        this.offset = new Point();
        this.calculations = new Hashtable<String, Point>();

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

        // run through all detectors
        for (SaliencyDetector detector : MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors())
            this.calculations.put(detector.getInformation().getDisplayName(), detector.analyse(this.screenShot));

        // use current trainer plugin to recognize step
        if ((MainClass.getInstance().getInternalPluginManager().getCurrentTrainer() != null) && (MainClass.getInstance().getInternalPluginManager().getCurrentSaliencyDetector() != null))
            MainClass.getInstance().getInternalPluginManager().getCurrentTrainer().setStep(this.calculations, this.fixation, this.offset);

        // draw all points to a image
        if (MainClass.getInstance().getProperties().isShowImages()) this.drawPicture();

        // update logfile
        String logString = new String("Training - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Mouseposition: (" + this.mousePosition.x + "," + this.mousePosition.y + "), Dimension: " + MainClass.getInstance().getProperties().getDimension() + ", Method: " + MainClass.getInstance().getInternalPluginManager().getCurrentTrainer().getInformation().getDisplayName());
        System.out.println(logString);
        MainClass.getInstance().getChannel().status(logString);
    }

    /**
     * draw image to global output path
     */
    private void drawPicture() {
        // initialize variables
        int color = 0;
        Point point = new Point();

        // create screenshot graphic
        Graphics2D graphic = this.screenShot.createGraphics();
        graphic.setFont(graphic.getFont().deriveFont(5));

        // visualize fixation point 
        graphic.setColor(new Color(255, 255, 0, 255));
        graphic.drawOval(MainClass.getInstance().getProperties().getDimension() / 2 - 5, MainClass.getInstance().getProperties().getDimension() / 2 - 5, 10, 10);
        graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + MainClass.getInstance().getProperties().getDimension() / 2, 12 + MainClass.getInstance().getProperties().getDimension() / 2);
        graphic.setColor(new Color(255, 255, 0, 32));
        graphic.fillOval(MainClass.getInstance().getProperties().getDimension() / 2 - 5, MainClass.getInstance().getProperties().getDimension() / 2 - 5, 10, 10);

        // visualize mouse position
        graphic.setColor(new Color(255, 0, 0, 255));
        graphic.drawOval(this.offset.x + MainClass.getInstance().getProperties().getDimension() / 2, this.offset.y + MainClass.getInstance().getProperties().getDimension() / 2, 10, 10);
        graphic.drawChars(("mouse position").toCharArray(), 0, 14, this.offset.x + 12 + MainClass.getInstance().getProperties().getDimension() / 2, this.offset.y + 12 + MainClass.getInstance().getProperties().getDimension() / 2);
        graphic.setColor(new Color(255, 0, 0, 32));
        graphic.fillOval(this.offset.x + MainClass.getInstance().getProperties().getDimension() / 2, this.offset.y + MainClass.getInstance().getProperties().getDimension() / 2, 10, 10);

        // visualize calculations
        for (SaliencyDetector detector : MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors()) {
            point = this.calculations.get(detector.getInformation().getDisplayName());
            color = (50 + color) % 256;
            graphic.setColor(new Color(0, 255 - color, color, 255));
            graphic.drawOval(point.x + MainClass.getInstance().getProperties().getDimension() / 2 - 5, point.y + MainClass.getInstance().getProperties().getDimension() / 2 - 5, 10, 10);
            graphic.drawChars(detector.getInformation().getDisplayName().toCharArray(), 0, detector.getInformation().getDisplayName().toCharArray().length, point.x + 12 + MainClass.getInstance().getProperties().getDimension() / 2, point.y + 12 + MainClass.getInstance().getProperties().getDimension() / 2);
            graphic.setColor(new Color(0, 255 - color, color, 32));
            graphic.fillOval(point.x + MainClass.getInstance().getProperties().getDimension() / 2 - 5, point.y + MainClass.getInstance().getProperties().getDimension() / 2 - 5, 10, 10);
        }

        // write the image
        try {
            File outputfile = new File(MainClass.getInstance().getProperties().getOutputPath() + File.separator + this.timestamp + "_training.png");
            outputfile.mkdirs();
            ImageIO.write(this.screenShot, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: rename and show shaped window
    private void doSth() {

    }
}

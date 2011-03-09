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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.MainClass;

/**
 * The precision trainer is used in trainings mode. Here the collected data is handled.
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

    private ArrayList<DataContainer> allData;

    /**
     * creates the precision trainer
     * 
     * @param manager
     */
    public PrecisionTrainer() {

        // initialize variables
        this.fixation = new Point();
        this.offset = new Point();
        this.allData = new ArrayList<DataContainer>();

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
    }

    /**
     * sets the mouseposition to associate it with the stored fixation
     * 
     * @param mousePosition
     */
    @SuppressWarnings("boxing")
    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
        Rectangle screenShotRect = new Rectangle(this.fixation.x - MainClass.getInstance().getProperties().getDimension() / 2, this.fixation.y - MainClass.getInstance().getProperties().getDimension() / 2, MainClass.getInstance().getProperties().getDimension(), MainClass.getInstance().getProperties().getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);

        // calculate offset
        this.offset.setLocation(this.mousePosition.x - this.fixation.x + MainClass.getInstance().getProperties().getDimension() /2, this.mousePosition.y - this.fixation.y + MainClass.getInstance().getProperties().getDimension() /2);

        // collect data
        this.allData.add(new DataContainer(MainClass.getInstance().getCurrentUser(), new Long(this.timestamp), new Point(this.offset)));

        // write image
        try {
            File outputfile = new File("./training/data/" + MainClass.getInstance().getCurrentUser() + "/" + MainClass.getInstance().getCurrentUser() + "_" + this.timestamp + ".png");
            outputfile.mkdirs();
            ImageIO.write(this.screenShot, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // update logfile
        String logString = new String("Training - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Mouseposition: (" + this.mousePosition.x + "," + this.mousePosition.y + "), Dimension: " + MainClass.getInstance().getProperties().getDimension());
        System.out.println(logString);
        MainClass.getInstance().getChannel().status(logString);
    }

    /**
     * called when training ends
     * writes training data into a file
     */
    public void leaveTraining() {
        // only write file if there is some data
        if (this.allData.size() == 0) return;

        // create file
        File logfile = new File("./training/data/" + MainClass.getInstance().getCurrentUser() + "/" + MainClass.getInstance().getCurrentUser() + "_" + System.currentTimeMillis() + ".training");

        // try to write file
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(logfile));
            for (DataContainer temp : this.allData) {
                outputStream.writeObject(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // reset data
        this.allData.clear();
    }
}

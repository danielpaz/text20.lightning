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
import de.dfki.km.text20.lightning.Properties;

/**
 * The precision trainer is used in trainings mode. Here the collected data is handled.
 * 
 * @author Christoph Käding
 *
 */
public class PrecisionTrainer {

    /** last fixation point */
    private Point fixationTmp;
    
    /** stored fixation point */
    private Point fixation;

    /** associated mouse position which shows the real target*/
    private Point mousePosition;

    /** offset between fixation point and mouse position */
    private Point mousePoint;

    /** robot for creating screenshots */
    private Robot robot;

    /** actual time */
    private long timestamp;

    /** screenshot of the target area*/
    private BufferedImage screenShot;

    /** a list of all catched data */
    private ArrayList<StorageContainer> allData;    

    /** global used properties */
    private Properties properties;
    
    /** name of current registered user */
    private String user;

    /**
     * creates the precision trainer
     * 
     * @param manager
     */
    public PrecisionTrainer() {

        // initialize variables
        this.fixation = new Point();
        this.fixationTmp = new Point();
        this.mousePoint = new Point();
        this.allData = new ArrayList<StorageContainer>();
        this.properties = MainClass.getInstance().getProperties();

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
        this.fixationTmp = fixation;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * stores last fixation so that it can be used to create a trainingsstep
     */
    public void storeFixation() {
        this.fixation = new Point(this.fixationTmp);
    }
    
    /**
     * sets the mouseposition to associate it with the stored fixation
     * 
     * @param mousePosition
     */
    @SuppressWarnings("boxing")
    public void setMousePosition(Point mousePosition) {
        this.user = MainClass.getInstance().getCurrentUser();
        this.mousePosition = mousePosition;
        Rectangle screenShotRect = new Rectangle(this.fixation.x - this.properties.getDimension() / 2, this.fixation.y - this.properties.getDimension() / 2, this.properties.getDimension(), this.properties.getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);

        // calculate offset
        this.mousePoint.setLocation(this.mousePosition.x - this.fixation.x + this.properties.getDimension() /2, this.mousePosition.y - this.fixation.y + this.properties.getDimension() /2);
       
        // collect data
        this.allData.add(new StorageContainer(this.user, new Long(this.timestamp), new Point(this.mousePoint)));

        // write image
        try {
            File outputfile = new File("./training/data/" + this.user + "/" + this.user + "_" + this.timestamp + ".png");
            outputfile.mkdirs();
            ImageIO.write(this.screenShot, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // update logfile
        String logString = new String("Training - Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Mouseposition: (" + this.mousePosition.x + "," + this.mousePosition.y + "), Dimension: " + this.properties.getDimension());
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
        File logfile = new File("./training/data/" + this.user + "/" + this.user + "_" + System.currentTimeMillis() + ".training");

        // try to write file
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(logfile));
            for (StorageContainer temp : this.allData) {
                outputStream.writeObject(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // reset data
        this.allData.clear();
    }
}

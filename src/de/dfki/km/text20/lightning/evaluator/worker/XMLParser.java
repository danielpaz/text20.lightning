/*
 * XMLParser.java
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
package de.dfki.km.text20.lightning.evaluator.worker;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import de.dfki.km.text20.lightning.worker.evaluationmode.StorageContainer;

/**
 * the XML-files which stores the evaluation datas are processed by this class
 * 
 * @author Christoph Käding
 *
 */
public class XMLParser {

    /** readed data */
    private ArrayList<StorageContainer> data;

    /** indicates if the readed comment should be the identifier */
    private boolean firstComment;

    /** indicates if the next entry should be the timestamp */
    private boolean timestamp;

    /** indicates if the next entry should be the x coordinate of the mouseposition */
    private boolean mouseX;

    /** indicates if the next entry should be the y coordinate of the mouseposition */
    private boolean mouseY;

    /** indicates if the next entry should be the dimension of the screenshots */
    private boolean dimension;

    /** in data stored dimension */
    private int dimensionTmp;

    /** temporary stored timestamp */
    private long timastampTmp;

    /** temporary stored mouseX coordinate */
    private int mouseXTmp;

    /** temporary stored mouseY coordinate */
    private int mouseYTmp;

    /** name of the actual file */
    private String fileName;

    /** stores used dimension */
    private int usedDimension;

    /** indicates if the next entry should be the x coordinate of the fixation */
    private boolean fixX;

    /** indicates if the next entry should be the y coordinate of the fixation */
    private boolean fixY;

    /** temporary stored fixX coordinate */
    private int fixXTmp;

    /** temporary stored fixY coordinate */
    private int fixYTmp;

    /** indicates if the next entry should be the size of the left pupil */
    private boolean left;

    /** indicates if the next entry should be the size of the right pupil */
    private boolean right;

    /** 
     * size of pupils
     * 0 = left
     * 1 = right
     */
    private float[] pupils;

    /** indicates if the next entry should be the screen brightness */
    private boolean screen;

    /** temporary stored screen brightness */
    private String screenTmp;

    /** indicates if the next entry should be the setting brightness */
    private boolean setting;

    /** temporary stored setting brightness */
    private String settingTmp;

    /** included setting will be stored in this container */
    private SettingsContainer settingsContainer;

    /**
     * tries to read the included StorageContainer of the given XML-file
     * 
     * @param file
     * @param choosedDimension 
     * @param worker 
     * 
     * @return the readed container
     */
    public ArrayList<StorageContainer> readFile(File file, int choosedDimension,
                                                EvaluatorWorker worker) {
        // initialize variables
        this.data = new ArrayList<StorageContainer>();
        this.firstComment = true;
        this.timestamp = false;
        this.fileName = file.getName();
        this.mouseX = false;
        this.mouseY = false;
        this.dimension = false;
        this.timastampTmp = 0;
        this.mouseXTmp = 0;
        this.mouseYTmp = 0;
        this.dimensionTmp = 0;
        this.usedDimension = choosedDimension;
        this.fixX = false;
        this.fixY = false;
        this.fixXTmp = 0;
        this.fixYTmp = 0;
        this.left = false;
        this.right = false;
        this.pupils = new float[2];
        this.screen = false;
        this.setting = false;
        this.screenTmp = "";
        this.settingTmp = "";
        FileInputStream inputStream = null;
        XMLStreamReader reader = null;

        try {
            inputStream = new FileInputStream(file);
            reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            // if there are still some data ...
            while (reader.hasNext()) {

                // ... read from file
                switch (reader.next()) {

                // if a starttag is found
                case XMLStreamConstants.START_ELEMENT:
                    if (!handleStartElement(reader.getName().toString().trim()))
                        return new ArrayList<StorageContainer>();
                    break;

                // if some characters are found    
                case XMLStreamConstants.CHARACTERS:
                    if (!handleCharacters(reader.getText().trim()))
                        return new ArrayList<StorageContainer>();
                    break;

                // if a comment is found
                case XMLStreamConstants.COMMENT:
                    if (!handleComment(reader.getText().trim()))
                        return new ArrayList<StorageContainer>();
                    break;

                // all other things
                default:
                    break;
                }
            }

            // update settings map at worker
            worker.addToSettingMap(this.fileName, this.settingsContainer);
            
            // close all
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (inputStream != null) inputStream.close();
                if (reader != null) reader.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            return new ArrayList<StorageContainer>();
        }

        // return readed data
        return this.data;
    }

    /**
     * called if some characters are readed
     * 
     * @param value
     * @return true if successful
     */
    private boolean handleCharacters(String value) {
        // if there are a empty char return, this can be happen because there are some whitespace killed by .trim()
        if (value.equals("")) return true;

        // if the readed characters should be the size of the right pupil ...
        if (this.right) {

            // ... store it and ...
            this.pupils[1] = Float.parseFloat(value);

            // ... add a new container to the data
            this.data.add(new StorageContainer(this.timastampTmp, new Point(this.fixXTmp, this.fixYTmp), new Point(this.mouseXTmp, this.mouseYTmp), this.pupils));

            // reset variables
            this.timestamp = false;
            this.mouseX = false;
            this.mouseY = false;
            this.fixX = false;
            this.fixY = false;
            this.left = false;
            this.right = false;
            this.pupils = new float[2];
            this.mouseXTmp = 0;
            this.mouseYTmp = 0;
            this.timastampTmp = 0;
            this.fixXTmp = 0;
            this.fixYTmp = 0;

            // return success
            return true;
        }

        // if the readed characters should be the size of the left pupil ...
        if (this.left) {

            // ... store it
            this.pupils[0] = Float.parseFloat(value);

            // return success
            return true;
        }

        // if the readed characters should be the mouseY coordinate ...
        if (this.mouseY) {

            // ... store it
            this.mouseYTmp = Integer.parseInt(value);

            // indicate warning
            if ((this.mouseYTmp - this.fixYTmp + this.dimensionTmp / 2) > this.usedDimension) {
                System.out.println("WARNING: y-coordinate " + value + " is out of dimension! File: " + this.fileName);
                this.settingsContainer.addOutOfDim();
            }

            // return success
            return true;
        }

        // if the readed characters should be the mouseX coordinate ...
        if (this.mouseX) {

            // ... store it
            this.mouseXTmp = Integer.parseInt(value);

            // indicate warning
            if ((this.mouseXTmp - this.fixXTmp + this.dimensionTmp / 2) > this.usedDimension) {
                System.out.println("WARNING: x-coordinate " + value + " is out of dimension! File: " + this.fileName);
                this.settingsContainer.addOutOfDim();
            }
            // return success
            return true;
        }

        // if the readed characters should be the fixX coordinate ...
        if (this.fixY) {

            // ... store it
            this.fixYTmp = Integer.parseInt(value);

            // return success
            return true;
        }

        // if the readed characters should be the fixY coordinate ...
        if (this.fixX) {

            // ... store it
            this.fixXTmp = Integer.parseInt(value);

            // return success
            return true;
        }

        // if the readed characters should be the timestamp ...
        if (this.timestamp) {

            // ... store it
            this.timastampTmp = Long.parseLong(value);

            // return success
            return true;
        }

        // if the readed should be the dimension ...
        if (this.dimension) {

            // ... store it
            this.dimensionTmp = Integer.parseInt(value);

            // test dimensions
            if (this.usedDimension < this.dimensionTmp)
                System.out.println("WARNING: choosed dimension is smaller than the stored one (" + this.dimensionTmp + ")!" + " File: " + this.fileName);

            // initialize setting container
            this.settingsContainer = new SettingsContainer(this.dimensionTmp, this.screenTmp, this.settingTmp);

            // return success
            return true;
        }

        // if the readed characters should be the setting brightness ...
        if (this.setting) {

            // ... store it
            this.settingTmp = value;

            // return success
            return true;
        }

        // if the readed characters should be the screen brightness ...
        if (this.screen) {

            // ... store it
            this.screenTmp = value;

            // return success
            return true;
        }

        // indicate error and return failure
        System.out.println("parsing failed on '" + value + "' in " + this.fileName);
        return false;
    }

    /**
     * called if a starttag is readed
     * 
     * @param value
     * @return true if successful
     */
    // TODO: add a counter or something instead of all these booleans
    private boolean handleStartElement(String value) {
        // start of container
        if (value.equals("alldata") && !this.timestamp && !this.mouseX && !this.mouseY && !this.dimension && !this.firstComment && !this.fixX && !this.fixY && !this.left && !this.right && !this.screen && !this.setting)
            return true;

        // screen brightness tag 
        if (value.equals("screenBrightness") && !this.timestamp && !this.mouseX && !this.mouseY && !this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && !this.screen && !this.setting) {
            this.screen = true;
            return true;
        }

        // setting brightness tag 
        if (value.equals("settingBrightness") && !this.timestamp && !this.mouseX && !this.mouseY && !this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && this.screen && !this.setting) {
            this.setting = true;
            return true;
        }

        // dimension tag 
        if (value.equals("dimension") && !this.timestamp && !this.mouseX && !this.mouseY && !this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.dimension = true;
            return true;
        }

        // start of each step-container
        if (value.equals("step") && !this.timestamp && !this.mouseX && !this.mouseY && this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && this.screen && this.setting)
            return true;

        // timestamp
        if (value.equals("timestamp") && !this.timestamp && !this.mouseX && !this.mouseY && this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.timestamp = true;
            return true;
        }

        // fixation tag
        if (value.equals("fixation") && this.timestamp && !this.mouseX && !this.mouseY && this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && this.screen && this.setting)
            return true;

        // fixX 
        if (value.equals("x") && this.timestamp && !this.mouseX && !this.mouseY && this.dimension && !this.fixX && !this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.fixX = true;
            return true;
        }

        // fixY
        if (value.equals("y") && this.timestamp && !this.mouseX && !this.mouseY && this.dimension && this.fixX && !this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.fixY = true;
            return true;
        }

        // mouseposition 
        if (value.equals("mouseposition") && this.timestamp && !this.mouseX && !this.mouseY && this.dimension && this.fixX && this.fixY && !this.left && !this.right && this.screen && this.setting)
            return true;

        // mouseX
        if (value.equals("x") && this.timestamp && !this.mouseX && !this.mouseY && this.dimension && this.fixX && this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.mouseX = true;
            return true;
        }

        // mouseY 
        if (value.equals("y") && this.timestamp && this.mouseX && !this.mouseY && this.dimension && this.fixX && this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.mouseY = true;
            return true;
        }

        // pupils
        if (value.equals("pupils") && this.timestamp && this.mouseX && this.mouseY && this.dimension && this.fixX && this.fixY && !this.left && !this.right && this.screen && this.setting)
            return true;

        // left
        if (value.equals("left") && this.timestamp && this.mouseX && this.mouseY && this.dimension && this.fixX && this.fixY && !this.left && !this.right && this.screen && this.setting) {
            this.left = true;
            return true;
        }

        // right 
        if (value.equals("right") && this.timestamp && this.mouseX && this.mouseY && this.dimension && this.fixX && this.fixY && this.left && !this.right && this.screen && this.setting) {
            this.right = true;
            return true;
        }
        // indicate error and return failure
        System.out.println("parsing failed on '" + value + "' in " + this.fileName);
        return false;
    }

    /**
     * called if a comment is readed
     * 
     * @param value
     * @return true if successful
     */
    private boolean handleComment(String value) {
        // if it is the first comment and the identifier is not recognized ...
        if (this.firstComment && !(value.equals("Project Lightning (Desktop) - evaluation data"))) {

            // indicate error and return failure
            System.out.println(this.fileName + " isn't a valid file");
            return false;
        }

        this.firstComment = false;

        // return success
        return true;
    }

    /**
     * counts the contained steps of the given file
     * 
     * @param file
     * @return the number of steps
     */
    public int count(File file) {
        // initialize counter
        int counter = 0;

        // initialize other variables
        this.firstComment = true;

        try {
            // initialize reader
            FileInputStream inputStream = new FileInputStream(file);
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            // read all data from file ...
            while (reader.hasNext()) {
                switch (reader.next()) {

                // ... and if a starttag is found ...
                case XMLStreamConstants.START_ELEMENT:

                    // ... and it is 'step' ...
                    if (reader.getName().toString().trim().equals("step"))

                    // ... increase counter
                        counter++;

                    break;

                case XMLStreamConstants.COMMENT:
                    // unsure check if the file is valid
                    if (this.firstComment && !reader.getText().trim().equals("Project Lightning (Desktop) - evaluation data"))
                        return 0;

                    this.firstComment = false;
                    break;

                // all other stuff
                default:
                    break;
                }
            }

            // close reader
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        // return number of steps
        return counter;
    }
}

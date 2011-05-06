/*
 * DataXMLParser.java
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
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import de.dfki.km.text20.lightning.worker.evaluationmode.StorageContainer;

/**
 * the XML-files which stores the evaluation datas are processed by this class
 * 
 * @author Christoph Käding
 *
 */
public class DataXMLParser {

    /** readed data */
    private ArrayList<StorageContainer> data;

    /** indicates if the next entry should be the timestamp */
    private boolean timestamp;

    /** indicates if the next entry should be the x coordinate of the related position */
    private boolean relatedX;

    /** indicates if the next entry should be the y coordinate of the related position */
    private boolean relatedY;

    /** indicates if the next entry should be the dimension of the screenshots */
    private boolean dimension;

    /** in data stored dimension */
    private int dimensionTmp;

    /** temporary stored timestamp */
    private long timastampTmp;

    /** temporary stored relatedX coordinate */
    private int relatedXTmp;

    /** temporary stored relatedY coordinate */
    private int relatedYTmp;

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

    /** indicates if the next entry should be the screen brightness */
    private boolean screen;

    /** temporary stored screen brightness */
    private int screenTmp;

    /** indicates if the next entry should be the setting brightness */
    private boolean setting;

    /** temporary stored setting brightness */
    private int settingTmp;

    /** included setting will be stored in this container */
    private SettingsContainer settingsContainer;

    /** temporary stored calibration */
    private boolean recalibrationTmp;

    /** indicates if the next value should be the calibration */
    private boolean recalibration;

    /** indicates if the next value should be the amount of steps */
    private boolean amount;

    /** indicates if the related point was out of dimension */
    private boolean outOfDim;

    /** indicates which mode was used */
    private int evaluationModeTmp;

    /** indicates that the next value should be the evaluation mode */
    private boolean evaluationMode;

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
        this.timestamp = false;
        this.fileName = file.getName();
        this.relatedX = false;
        this.relatedY = false;
        this.dimension = false;
        this.timastampTmp = 0;
        this.relatedXTmp = 0;
        this.relatedYTmp = 0;
        this.dimensionTmp = 0;
        this.usedDimension = choosedDimension;
        this.fixX = false;
        this.fixY = false;
        this.fixXTmp = 0;
        this.fixYTmp = 0;
        this.screen = false;
        this.setting = false;
        this.screenTmp = 0;
        this.settingTmp = 0;
        this.recalibration = false;
        this.recalibrationTmp = false;
        this.amount = false;
        this.outOfDim = false;
        this.evaluationModeTmp = 0;
        this.evaluationMode = false;
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
                    handleStartElement(reader.getName().toString().trim());
                    break;

                // if some characters are found    
                case XMLStreamConstants.CHARACTERS:
                    if (!handleCharacters(reader.getText().trim()))
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
        try {
            // if there are a empty char return, this can be happen because there are some whitespace killed by .trim()
            if (value.equals("")) return true;

            // if the readed characters should be the relatedY coordinate ...
            if (this.relatedY) {

                // ... store it
                this.relatedYTmp = Integer.parseInt(value);

                // indicate warning
                if (Math.abs(this.relatedYTmp - this.fixYTmp) > (this.usedDimension / 2)) {
                    System.out.println("WARNING: y-coordinate " + value + " is out of dimension!");
                    this.outOfDim = true;
                }

                // ... add a new container to the data
                this.data.add(new StorageContainer(this.timastampTmp, new Point(this.fixXTmp, this.fixYTmp), new Point(this.relatedXTmp, this.relatedYTmp)));

                // check ood
                if (this.outOfDim) this.settingsContainer.addOutOfDim();

                // reset variables
                this.timestamp = false;
                this.relatedX = false;
                this.relatedY = false;
                this.fixX = false;
                this.fixY = false;
                this.outOfDim = false;
                this.relatedXTmp = 0;
                this.relatedYTmp = 0;
                this.timastampTmp = 0;
                this.fixXTmp = 0;
                this.fixYTmp = 0;
                
                // return success
                return true;
            }

            // if the readed characters should be the relatedX coordinate ...
            if (this.relatedX) {

                // ... store it
                this.relatedXTmp = Integer.parseInt(value);

                // indicate warning
                if (Math.abs(this.relatedXTmp - this.fixXTmp) > (this.usedDimension / 2)) {
                    System.out.println("WARNING: x-coordinate " + value + " is out of dimension!");
                    this.outOfDim = true;
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

            // if the readed characters should be the amount
            if (this.amount) return true;

            // if the readed should be the dimension ...
            if (this.dimension) {

                // ... store it
                this.dimensionTmp = Integer.parseInt(value);

                // test dimensions
                if (this.usedDimension < this.dimensionTmp)
                    System.out.println("WARNING: choosed dimension is smaller than the stored one (" + this.dimensionTmp + ")!" + " File: " + this.fileName);

                // initialize setting container
                this.settingsContainer = new SettingsContainer(this.dimensionTmp, this.recalibrationTmp, this.screenTmp, this.settingTmp, this.evaluationModeTmp);

                // return success
                return true;
            }

            // if the readed characters should be the evaluation mode ...
            if (this.evaluationMode) {

                // ... store it
                this.evaluationModeTmp = Integer.parseInt(value);

                // return success
                return true;
            }

            // if the readed characters should be the recalibration ...
            if (this.recalibration) {

                // ... store it
                this.recalibrationTmp = Boolean.parseBoolean(value);

                // return success
                return true;
            }

            // if the readed characters should be the setting brightness ...
            if (this.setting) {

                // ... store it
                this.settingTmp = Integer.parseInt(value);

                // return success
                return true;
            }

            // if the readed characters should be the screen brightness ...
            if (this.screen) {

                // ... store it
                this.screenTmp = Integer.parseInt(value);

                // return success
                return true;
            }
        } catch (Exception e) {
            // indicate error and return failure
            System.out.println("parsing failed on '" + value + "' in " + this.fileName);
            return false;
        }
        // indicate error and return failure
        System.out.println("parsing failed on '" + value + "' in " + this.fileName);
        return false;
    }

    /**
     * called if a starttag is readed
     * 
     * @param value
     */
    private void handleStartElement(String value) {

        // screen brightness tag 
        if (value.equals("screenbrightness")) {
            this.screen = true;
            return;
        }

        // setting brightness tag 
        if (value.equals("settingbrightness")) {
            this.setting = true;
            return;
        }

        // dimension tag 
        if (value.equals("recalibration")) {
            this.recalibration = true;
            return;
        }

        // dimension tag 
        if (value.equals("evaluationmode")) {
            this.evaluationMode = true;
            return;
        }

        // dimension tag 
        if (value.equals("dimension")) {
            this.dimension = true;
            return;
        }

        // amount tag 
        if (value.equals("amount")) {
            this.amount = true;
            return;
        }

        // timestamp
        if (value.equals("timestamp")) {
            this.timestamp = true;
            return;
        }

        // fixX 
        if (value.equals("x") && !this.fixX && !this.relatedX) {
            this.fixX = true;
            return;
        }

        // fixY
        if (value.equals("y") && !this.fixY && !this.relatedY) {
            this.fixY = true;
            return;
        }

        // relatedX
        if (value.equals("x") && !this.relatedX && this.fixX) {
            this.relatedX = true;
            return;
        }

        // relatedY 
        if (value.equals("y") && !this.relatedY && this.fixY) {
            this.relatedY = true;
            return;
        }
    }

    /**
     * @param file
     * @return the number of steps in given file
     */
    public int count(File file) {
        // initialize boolean
        boolean found = false;
        try {
            // initialize reader
            FileInputStream inputStream = new FileInputStream(file);
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            // read all data from file ...
            while (reader.hasNext()) {
                switch (reader.next()) {

                // ... and if a starttag is found ...
                case XMLStreamConstants.START_ELEMENT:

                    // ... and it is 'amount', set found
                    if (reader.getName().toString().trim().equals("amount"))
                        found = true;

                    break;

                // if some characters are found ...    
                case XMLStreamConstants.CHARACTERS:

                    // ... and they should be the amount, return it
                    if (found) return Integer.parseInt(reader.getText().trim());

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

        // return if not found
        return 0;
    }

    /**
     * checks given XML-file 
     * 
     * @param xmlFile 
     * @return true is the given file is valid
     */
    public boolean isValid(File xmlFile) {
        // create xsd file
        File xsdFile = new File(xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "DataPattern.xsd");
        if (!xsdFile.exists()) {
            System.out.println(xsdFile.getAbsolutePath() + " not found!");
            return false;
        }

        try {
            // create validation factory
            String schemaLang = "http://www.w3.org/2001/XMLSchema";
            SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

            // create validator
            Schema schema = factory.newSchema(xsdFile);
            Validator validator = schema.newValidator();

            // at last perform validation:
            validator.validate(new StreamSource(xmlFile.getAbsolutePath()));

        } catch (SAXException e) {
            System.out.println(xmlFile.getAbsolutePath() + " is not valid!");
            System.out.println();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

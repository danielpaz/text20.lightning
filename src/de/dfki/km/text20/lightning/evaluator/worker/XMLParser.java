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

import de.dfki.km.text20.lightning.worker.training.StorageContainer;

/**
 * the XML-files which stores the trainingsdatas are processed by this class
 * 
 * @author Christoph Käding
 *
 */
public class XMLParser {

    /** readed data */
    private ArrayList<StorageContainer> data;

    /** indicates if the readed comment should be the identifier */
    private boolean firstComment;

    /** indicates if the next tag should be the timestamp */
    private boolean timestamp;

    /** indicates if the next tag should be the x coordinate of the mouseposition */
    private boolean x;

    /** indicates if the next tag should be the y coordinate of the mouseposition */
    private boolean y;

    /** indicates if the next tag should be the dimension of the screenshots */
    private boolean dimension;

    /** temporary stored timestamp */
    private long timastampTmp;

    /** temporary stored x coordinate */
    private int xTmp;

    /** name of the actual file */
    private String fileName;

    /** stores used dimension */
    private int dimensionTmp;

    /**
     * tries to read the included StorageContainer of the given XML-file
     * 
     * @param file
     * @return the readed container
     */
    public ArrayList<StorageContainer> readFile(File file) {
        // initialize variables
        this.data = new ArrayList<StorageContainer>();
        this.firstComment = true;
        this.timestamp = false;
        this.fileName = file.getName();
        this.x = false;
        this.y = false;
        this.dimension = false;
        this.timastampTmp = 0;
        this.xTmp = 0;
        this.dimensionTmp = 0;
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

        // if the readed characters should be the y coordinate ...
        if (this.y) {

            // ... add a new container to the data
            this.data.add(new StorageContainer(this.timastampTmp, new Point(this.xTmp, Integer.parseInt(value))));

            //            System.out.println(this.data.get(this.data.size() - 1).getTimestamp() + " " + this.timastampTmp + " | " + this.data.get(this.data.size() - 1).getMousePoint() + " " + this.xTmp + " " + value);

            // indicate warning
            if (Integer.parseInt(value) > this.dimensionTmp)
                System.out.println("WARNING: " + value + " is out of dimension! File: " + this.fileName);

            // reset all variables
            this.timestamp = false;
            this.x = false;
            this.y = false;
            this.xTmp = 0;
            this.timastampTmp = 0;

            // return success
            return true;
        }

        // if the readed characters should be the x coordinate ...
        if (this.x) {

            // ... store it
            this.xTmp = Integer.parseInt(value);

            // indicate warning
            if (this.xTmp > this.dimensionTmp)
                System.out.println("WARNING: " + value + " is out of dimension! File: " + this.fileName);

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
    private boolean handleStartElement(String value) {
        // start of container
        if (value.equals("alldata") && !this.timestamp && !this.x && !this.y && !this.dimension)
            return true;

        // dimension tag 
        if (value.equals("dimension") && !this.timestamp && !this.x && !this.y && !this.dimension) {
            this.dimension = true;
            return true;
        }

        // start of each step-container
        if (value.equals("step") && !this.timestamp && !this.x && !this.y && this.dimension)
            return true;

        // timestamp
        if (value.equals("timestamp") && !this.timestamp && !this.x && !this.y && this.dimension) {
            this.timestamp = true;
            return true;
        }

        // mouseposition after timestamp
        if (value.equals("mouseposition") && this.timestamp && !this.x && !this.y && this.dimension)
            return true;

        // x after mouseposition and timestamp
        if (value.equals("x") && this.timestamp && !this.x && !this.y && this.dimension) {
            this.x = true;
            return true;
        }

        // y after x, mouseposition and timestamp
        if (value.equals("y") && this.timestamp && this.x && !this.y && this.dimension) {
            this.y = true;
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
        if (this.firstComment && !(value.equals("Project Lightning (Desktop) - trainingsdata"))) {

            // indicate error and return failure
            System.out.println(this.fileName + " isn't a valid file");
            this.firstComment = false;
            return false;
        }

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

        try {
            // initialize reader
            FileInputStream inputStream = new FileInputStream(file);
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            // read all data from file ...
            while (xmlStreamReader.hasNext()) {
                switch (xmlStreamReader.next()) {

                // ... and if a starttag is found ...
                case XMLStreamConstants.START_ELEMENT:

                    // ... and it is 'step' ...
                    if (xmlStreamReader.getName().toString().trim().equals("step"))

                    // ... increase counter
                        counter++;

                    break;

                // all other stuff
                default:
                    break;
                }
            }

            // close reader
            inputStream.close();
            xmlStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        // return number of steps
        return counter;
    }
}

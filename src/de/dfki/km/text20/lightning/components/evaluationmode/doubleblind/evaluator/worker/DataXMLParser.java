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
package de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.worker;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * reads stored positions from xml file
 * 
 * @author Christoph Käding
 */
public class DataXMLParser {

    /** readed data */
    private ArrayList<Point> data;

    /** indicates that the next entry should be the x coordinate */
    private boolean x;

    /** indicates that the next entry should be the y coordinate */
    private boolean y;

    /** temporary stored x coordinate */
    private int xTmp;

    /** temporary stored y coordinate */
    private int yTmp;

    /** name of the actual file */
    private String filePath;

    /** indicates that the next entry should be the name of the related file */
    private boolean relatedFile;

    /** temporary stored filename */
    private String relatedFileTmp;

    /** indicates that the next entry should be the amount of data */
    private boolean amount;

    /** temporary stored amount */
    private int amountTmp;

    /**
     * tries to read the included StorageContainer of the given XML-file
     * 
     * @param file
     * 
     * @return the readed container
     */
    public ArrayList<Point> readFile(File file) {
        // initialize variables
        this.data = new ArrayList<Point>();
        this.filePath = file.getAbsolutePath();
        this.x = false;
        this.y = false;
        this.xTmp = 0;
        this.yTmp = 0;
        this.amount = false;
        this.amountTmp = 0;
        this.relatedFile = false;
        this.relatedFileTmp = "";
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
                        return new ArrayList<Point>();
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
            return new ArrayList<Point>();
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

            // if the readed characters should be the y coordinate ...
            if (this.y) {

                // ... store it
                this.yTmp = Integer.parseInt(value);

                // add to data
                if (new File(this.filePath.substring(0, this.filePath.lastIndexOf(File.separator) + 1) + this.relatedFileTmp).exists()) this.data.add(new Point(this.xTmp, this.yTmp));
                else
                    System.out.println("'" + this.filePath.substring(0, this.filePath.lastIndexOf(File.separator) + 1) + this.relatedFileTmp + "' not found!");

                // reset variables
                this.x = false;
                this.y = false;
                this.xTmp = 0;
                this.yTmp = 0;

                // return success
                return true;
            }

            // if the readed characters should be the x coordinate ...
            if (this.x) {

                // ... store it
                this.xTmp = Integer.parseInt(value);

                // return success
                return true;
            }

            // if the readed characters should be the amount ...
            if (this.amount) {

                // ... store it
                this.amountTmp = Integer.parseInt(value);

                // return success
                return true;
            }

            // if the readed characters should be the related file ...
            if (this.relatedFile) {

                // ... store it
                this.relatedFileTmp = value;

                // return success
                return true;
            }

        } catch (Exception e) {
            // indicate error and return failure
            System.out.println("parsing failed on '" + value + "' in " + this.filePath);
            return false;
        }
        // indicate error and return failure
        System.out.println("parsing failed on '" + value + "' in " + this.filePath);
        return false;
    }

    /**
     * called if a starttag is readed
     * 
     * @param value
     */
    private void handleStartElement(String value) {

        // y 
        if (value.equals("file")) {
            this.relatedFile = true;
            return;
        }
        
        // y 
        if (value.equals("amount")) {
            this.amount = true;
            return;
        }
        
        // x
        if (value.equals("x") && !this.x) {
            this.x = true;
            return;
        }

        // y 
        if (value.equals("y") && !this.y) {
            this.y = true;
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
        File xsdFile = new File(xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "MarksPattern.xsd");
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

    /**
     * @param xmlFile
     * @return BufferedImage of related file
     */
    public BufferedImage getRelatedImage(File xmlFile) {
        // initialize boolean
        boolean found = false;
        try {
            // initialize reader
            FileInputStream inputStream = new FileInputStream(xmlFile);
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            // read all data from file ...
            while (reader.hasNext()) {
                switch (reader.next()) {

                // ... and if a starttag is found ...
                case XMLStreamConstants.START_ELEMENT:

                    // ... and it is 'file', set found
                    if (reader.getName().toString().trim().equals("file")) found = true;

                    break;

                // if some characters are found ...    
                case XMLStreamConstants.CHARACTERS:

                    // ... and they should be the filename, return its buffered image
                    if (found)
                        return ImageIO.read(new File(xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + reader.getText()));

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
            return null;
        }

        // return if not found
        return null;
    }
}

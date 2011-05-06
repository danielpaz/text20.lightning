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

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * the XML-files which stores the evaluation datas are processed by this class
 * 
 * @author Christoph Käding
 *
 */
public class PupilXMLParser {

    /** readed data */
    private PupilContainer data;

    /** indicates if the next entry should be the timestamp */
    private boolean timestamp;

    /** indicates if the next entry should be the x coordinate of the related position */
    private boolean relatedX;

    /** indicates if the next entry should be the y coordinate of the related position */
    private boolean relatedY;

    /** temporary stored timestamp */
    private long timastampTmp;

    /** temporary stored relatedX coordinate */
    private float relatedXTmp;

    /** temporary stored relatedY coordinate */
    private float relatedYTmp;

    /** name of the actual file */
    private String fileName;

    /**
     * tries to read the included StorageContainer of the given XML-file
     * 
     * @param file
     * @param choosedDimension 
     * @param worker 
     * 
     * @return the readed container
     */
    public PupilContainer readFile(File file, EvaluatorWorker worker) {
        // initialize variables
        this.data = new PupilContainer();
        this.timestamp = false;
        this.fileName = file.getName();
        this.relatedX = false;
        this.relatedY = false;
        this.timastampTmp = 0;
        this.relatedXTmp = 0;
        this.relatedYTmp = 0;
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
                        return new PupilContainer();
                    break;

                // all other things
                default:
                    break;
                }
            }

            // update settings map at worker
            worker.addToPupilMap(this.fileName, this.data);

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
            return new PupilContainer();
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

                // ... store it ...
                this.relatedYTmp = Float.parseFloat(value);

                // ... add a new container to the data
                this.data.addData(this.timastampTmp, new float[] { this.relatedXTmp, this.relatedYTmp });

                // reset variables
                this.timestamp = false;
                this.relatedX = false;
                this.relatedY = false;
                this.relatedXTmp = 0;
                this.relatedYTmp = 0;
                this.timastampTmp = 0;

                // return success
                return true;
            }

            // if the readed characters should be the relatedX coordinate ...
            if (this.relatedX) {

                // ... store it
                this.relatedXTmp = Float.parseFloat(value);

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

        // timestamp
        if (value.equals("timestamp")) {
            this.timestamp = true;
            return;
        }

        // relatedX
        if (value.equals("left")) {
            this.relatedX = true;
            return;
        }

        // relatedY 
        if (value.equals("right")) {
            this.relatedY = true;
            return;
        }
    }

    /**
     * checks given XML-file 
     * 
     * @param xmlFile 
     * @return true is the given file is valid
     */
    public boolean isValid(File xmlFile) {
        // create xsd file
        File xsdFile = new File(xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "PupilPattern.xsd");
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

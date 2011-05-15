/*
 * CoordinatesXMLParser.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.quickness.modus;

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

import de.dfki.km.text20.lightning.components.evaluationmode.quickness.modus.gui.QuicknessConfigGuiImpl;

/**
 * reads given xml file and returns included data
 * 
 * @author Christoph Käding
 *
 */
public class WordXMLParser {

    /** readed data */
    private ArrayList<String> data;

    /** indicates if the next entry should be the word */
    private boolean word;

    /** temporary stored word */
    private String wordTmp;

    /** name of the actual file */
    private String fileName;

    /** indicates that the next readed value should be filename */
    private boolean file;

    /** temporary stored filename */
    private String fileTmp;

    /**
     * tries to read the included data of the given XML-file
     *
     * @param configGui 
     * @param xmlFile 
     */
    public void readFile(QuicknessConfigGuiImpl configGui, File xmlFile) {
        // initialize variables
        this.data = new ArrayList<String>();
        this.fileName = xmlFile.getAbsolutePath();
        this.word = false;
        this.file = false;
        this.fileTmp = "";
        this.wordTmp = "";
        FileInputStream inputStream = null;
        XMLStreamReader reader = null;

        try {
            inputStream = new FileInputStream(xmlFile);
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
                    if (!handleCharacters(reader.getText().trim())) return;
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
            return;
        }

        // add readed data
        configGui.setCurrentFile(this.fileTmp);
        configGui.setCurrentWords(this.data);
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

            // if the readed characters should be the word coordinate ...
            if (this.word) {

                // ... store it ...
                this.wordTmp = value;

                // ...  and add it to the arraylist
                this.data.add(this.wordTmp);

                // reset variables
                this.word = false;
                this.wordTmp = "";

                // return success
                return true;
            }

            // if the readed characters should be the number ...
            if (this.file) {

                this.fileTmp = this.fileName.substring(0, this.fileName.lastIndexOf(File.separator) + 1) + value;

                // return success
                if (new File(this.fileTmp).exists()) return true;

                System.out.println("'" + this.fileTmp + "' does not exist.");
                return false;
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

        // file
        if (value.equals("file")) {
            this.file = true;
            return;
        }

        // word 
        if (value.equals("word")) {
            this.word = true;
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
        File xsdFile = new File(xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "PreparedTextPattern.xsd");
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

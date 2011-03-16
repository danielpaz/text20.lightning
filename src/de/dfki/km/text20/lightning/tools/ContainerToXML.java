/*
 * ContainerToXML.java
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
package de.dfki.km.text20.lightning.tools;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.dfki.km.text20.lightning.worker.training.StorageContainer;

/**
 * converts former used binary containers to an human readable xml
 * 
 * @author Christoph Käding
 *
 */
public class ContainerToXML {

    /** instance of ContainerToXML */
    private static ContainerToXML main;

    /**
     * main entry point
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        String[] tmp = new String[3];
        tmp[0] = "C:/Users/nesti/Desktop/robby01/robby01_1300182576108.training";
        tmp[1] = "C:/Users/nesti/Desktop/text01/text01_1300179408828.training";
        tmp[2] = "C:/Users/nesti/Desktop/all01/all01_1300179795843.training";
        
        ContainerToXML.getInstance().start(tmp);
    }

    /**
     * gives an object of the mainclass back
     * 
     * @return a singleton instance
     */
    private static ContainerToXML getInstance() {
        if (main == null) {
            main = new ContainerToXML();
        }
        return main;
    }

    protected void start(String[] args) {
        ArrayList<StorageContainer> data = new ArrayList<StorageContainer>();

        // run through all given files
        for (String arg : args) {
            File file = new File(arg);
            if (file.exists()) {
                data = this.readFile(file);
                
                // if the file exsists ...
                if (data != null) {
                     
                    // ... and contains data, convert it
                    System.out.print("File: " + file.getName() + ", included datasets: " + data.size());
                    this.writeXML(data, file.getAbsolutePath().replace(".training", ".xml"));
                    System.out.println(" -> done");
                }
            }
        }
        
        System.out.println("\nfinished");
    }

    /**
     * reads DataContainers from given file
     * 
     * @param file
     * @return readed DataContainers
     */
    private ArrayList<StorageContainer> readFile(File file) {
        // initialize some variables
        ObjectInputStream inputStream = null;
        Object object;
        ArrayList<StorageContainer> container = new ArrayList<StorageContainer>();

        try {

            // read from file till eof
            inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            while ((object = inputStream.readObject()) != null) {
                if (object instanceof StorageContainer) {
                    container.add((StorageContainer) object);
                }
            }

            // close stream and return readed content
            inputStream.close();
            return container;

        } catch (EOFException eofe) {
            try {

                // close stream and return readed content
                if (inputStream != null) inputStream.close();
                return container;

            } catch (IOException ioe) {
                ioe.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * writes training data into a XML-file
     * @param allData 
     * @param path 
     */
    public void writeXML(ArrayList<StorageContainer> allData, String path) {
        // only write file if there is some data
        if (allData.size() == 0) return;

        // create file
        File logfile = new File(path);

        // Create an output factory
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            // Create an XML stream writer
            FileOutputStream outputStream = new FileOutputStream(logfile);
            XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream, "UTF-8");

            // Write XML prologue
            writer.writeStartDocument();
            writer.writeCharacters("\n");

            // TODO: add a dtd, encodingtag, namespace, ...
            
            // set identifier
            writer.writeComment("Project Lightning (Desktop) - trainingsdata");
            writer.writeCharacters("\n");

            // Now start with root element
            writer.writeStartElement("alldata");
            writer.writeCharacters("\n");

            // write dimension
            writer.writeCharacters("\t");
            writer.writeStartElement("dimension");
            writer.writeCharacters("100");
            writer.writeEndElement();
            writer.writeCharacters("\n");            
            
            // run through all data
            for (StorageContainer data : allData) {

             // write datatag
                writer.writeCharacters("\t");
                writer.writeStartElement("step");
                writer.writeCharacters("\n");
                
                // write timestamp
                writer.writeCharacters("\t\t");
                writer.writeStartElement("timestamp");
                writer.writeCharacters("" + data.getTimestamp());
                writer.writeEndElement();
                writer.writeCharacters("\n");
                
                // writemouse position
                writer.writeCharacters("\t\t");
                writer.writeStartElement("mouseposition");
                writer.writeCharacters("\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("x");
                writer.writeCharacters("" + data.getMousePoint().x);
                writer.writeEndElement();
                writer.writeCharacters("\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("y");
                writer.writeCharacters("" + data.getMousePoint().y);
                writer.writeEndElement();
                writer.writeCharacters("\n");
                writer.writeCharacters("\t\t");
                writer.writeEndElement();
                writer.writeCharacters("\n");

                // close datatag
                writer.writeCharacters("\t");
                writer.writeEndElement();  
                writer.writeCharacters("\n");   
            }

            // Write document end. This closes all open structures
            writer.writeEndDocument();

            // Close the writer to flush the output
            outputStream.close();
            writer.close();

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

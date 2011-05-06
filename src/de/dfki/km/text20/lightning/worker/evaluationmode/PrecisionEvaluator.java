/*
 * PrecisionEvaluator.java
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
package de.dfki.km.text20.lightning.worker.evaluationmode;

import static net.jcores.CoreKeeper.$;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;

/**
 * The precision evaluator is used in evaluation mode. Here the collected data is
 * handled.
 * 
 * @author Christoph Käding
 * 
 */
public class PrecisionEvaluator {

    /** last fixation point */
    private Point fixationTmp;

    /** stored fixation point */
    private Point fixation;

    /** associated mouse position which shows the real target */
    private Point relatedPosition;

    /** offset between fixation point and mouse position */
    private Point mousePoint;

    /** robot for creating screenshots */
    private Robot robot;

    /** actual time */
    private long timestamp;

    /** screenshot of the target area */
    private BufferedImage screenShot;

    /** a list of all catched data */
    private ArrayList<StorageContainer> allData;

    /** global used properties */
    private Properties properties;

    /** name of current registered user */
    private String user;

    /** indicates if the mouseposition was anytime out of dimension */
    private boolean warning;

    /** indicates if already any processing is in progress */
    private boolean isProcessing;

    /** timestamp for logfiles */
    private long logTimeStamp;

    /** actual pupil file */
    private File pupilFile;
    
    /** indicates if the pupilfile should be updated */
    private boolean logPupils;

    /**
     * creates the precision evaluator
     */
    public PrecisionEvaluator() {

        // initialize variables
        this.fixation = new Point();
        this.fixationTmp = new Point();
        this.mousePoint = new Point();
        this.allData = new ArrayList<StorageContainer>();
        this.properties = MainClass.getInstance().getProperties();
        this.warning = false;
        this.isProcessing = false;
        this.logPupils = false;

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * stores fixation and pupil sizes
     * 
     * @param fixation
     */
    public void setFixationPoint(Point fixation) {
        this.fixationTmp = fixation;
    }

    /**
     * stores last fixation so that it can be used to create a evaluation step
     * 
     * @return true if successful 
     */
    public boolean storeFixation() {
        if (this.fixationTmp == null || this.isProcessing) return false;
        this.isProcessing = true;
        this.timestamp = System.currentTimeMillis();
        this.fixation = new Point(this.fixationTmp);
        this.fixationTmp = null;
        this.isProcessing = false;
        return true;
    }

    /**
     * sets the mouseposition to associate it with the stored fixation
     * 
     * @param relatedPosition
     * @return true if the position is valid
     */
    @SuppressWarnings("boxing")
    public EvaluationCode setRelatedPosition(Point relatedPosition) {
        if (this.fixation == null) return EvaluationCode.NO_FIXATION;
        if (this.isProcessing) return EvaluationCode.ALREADY_PROCESSING;
        this.isProcessing = true;
        this.user = MainClass.getInstance().getEvaluationSettings()[0];
        if (this.allData.size() == 0) this.startPupilStream();
        this.relatedPosition = relatedPosition;
        Rectangle screenShotRect = new Rectangle(-this.properties.getDimension() / 2, -this.properties.getDimension() / 2, Toolkit.getDefaultToolkit().getScreenSize().width + this.properties.getDimension(), Toolkit.getDefaultToolkit().getScreenSize().height + this.properties.getDimension());
        this.screenShot = this.robot.createScreenCapture(screenShotRect);

        // calculate offset
        this.mousePoint.setLocation(this.relatedPosition.x - this.fixation.x, this.relatedPosition.y - this.fixation.y);

        // collect data
        this.allData.add(new StorageContainer(new Long(this.timestamp), new Point(this.fixation), new Point(this.relatedPosition)));

        // write image
        try {
            File outputfile = new File(MainClass.getInstance().getEvaluationSettings()[3] + "/evaluation/data/" + this.user + "/" + this.user + "_" + this.timestamp + ".png");
            outputfile.mkdirs();
            ImageIO.write(this.screenShot, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // update logfile
        String logString = new String("Timestamp: " + this.timestamp + ", Fixation: (" + this.fixation.x + "," + this.fixation.y + "), Mouseposition: (" + this.relatedPosition.x + "," + this.relatedPosition.y + "), Dimension: " + this.properties.getDimension() + ", Recalibration is used: " + this.properties.isRecalibration());
        System.out.println("Evaluation - " + logString);
        MainClass.getInstance().getChannel().status("Evaluation - " + logString);
        MainClass.getInstance().addToStatistic("evaluation", logString);

        // update recalibration
        MainClass.getInstance().getRecalibrator().updateCalibration(this.fixation, this.mousePoint);

        // indicate error
        if ((Math.abs(this.mousePoint.x) > this.properties.getDimension() / 2) || (Math.abs(this.mousePoint.y) > this.properties.getDimension() / 2)) {
            this.warning = true;

            // reset status
            this.isProcessing = false;

            // return failure
            return EvaluationCode.OUT_OFF_DIMENSION;
        }

        // reset status
        this.isProcessing = false;

        // return success
        return EvaluationCode.OK;
    }

    /** 
     * initialize pupil stream
     */
    private void startPupilStream() {
        this.logTimeStamp = System.currentTimeMillis();

        // create file
        this.pupilFile = new File(MainClass.getInstance().getEvaluationSettings()[3] + "/evaluation/data/" + this.user + "/" + this.user + "_" + this.logTimeStamp + "_pupils.xml");

        try {
            // Create an XML stream writer
            FileOutputStream outputStream = new FileOutputStream(this.pupilFile);
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");

            // Write XML prologue
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\r\n");

            // set identifier
            writer.writeComment("Project Lightning (Desktop) - pupil data");
            writer.writeCharacters("\r\n");

            // start with root element
            writer.writeStartElement("pupildata");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:noNamespaceSchemaLocation", "PupilPattern.xsd");
            writer.writeCharacters("\r\n");

            // Close the writer to flush the output
            outputStream.close();
            writer.close();

            // set status
            this.logPupils = true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adds step to pupil file
     * 
     * @param pupils
     */
    public void addPupilData(float[] pupils) {
        if (this.logPupils)
            $(this.pupilFile).append("\t<step>\r\n\t\t<timestamp>" + System.currentTimeMillis() + "</timestamp>\r\n\t\t<pupils>\r\n\t\t\t<left>" + pupils[0] + "</left>\r\n\t\t\t<right>" + pupils[1] + "</right>\r\n\t\t</pupils>\r\n\t</step>\r\n");
    }

    /**
     * writes endtag to pupilfile and unzips xsd
     */
    private void closePupilStream() {
        this.logPupils = false;
        $(this.pupilFile).append("</pupildata>");
        File xsd = new File(MainClass.getInstance().getEvaluationSettings()[3] + "/evaluation/data/" + this.user + "/PupilPattern.xsd");
        if (!xsd.exists())
          $(PrecisionEvaluator.class.getResourceAsStream("resources/PupilPattern.zip")).zipstream().unzip(xsd.getAbsolutePath().substring(0, xsd.getAbsolutePath().lastIndexOf(File.separator) + 1));
    }

    /**
     * called when evaluation ends
     * writes evaluation data into a file
     */
    public void leaveEvaluation() {
        // only write file if there is some data
        if (this.allData.size() == 0) return;

        // close pupil file
        this.closePupilStream();

        // create file
        File logfile = new File(MainClass.getInstance().getEvaluationSettings()[3] + "/evaluation/data/" + this.user + "/" + this.user + "_" + this.logTimeStamp + "_data.xml");
        File xsd = new File(MainClass.getInstance().getEvaluationSettings()[3] + "/evaluation/data/" + this.user + "/DataPattern.xsd");

        try {
            // Create an XML stream writer
            FileOutputStream outputStream = new FileOutputStream(logfile);
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");

            // Write XML prologue
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\r\n");

            // set identifier
            writer.writeComment("Project Lightning (Desktop) - evaluation data");
            writer.writeCharacters("\r\n");

            // start with root element
            writer.writeStartElement("alldata");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:noNamespaceSchemaLocation", "DataPattern.xsd");
            writer.writeCharacters("\r\n");

            // write screen brightness
            writer.writeCharacters("\t");
            writer.writeStartElement("screenbrightness");
            writer.writeCharacters("" + MainClass.getInstance().getEvaluationSettings()[1]);
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // write setting brightness
            writer.writeCharacters("\t");
            writer.writeStartElement("settingbrightness");
            writer.writeCharacters("" + MainClass.getInstance().getEvaluationSettings()[2]);
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // write recalibration
            writer.writeCharacters("\t");
            writer.writeStartElement("recalibration");
            writer.writeCharacters("" + this.properties.isRecalibration());
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // write recalibration
            writer.writeCharacters("\t");
            writer.writeStartElement("evaluationmodus");
            writer.writeCharacters("" + MainClass.getInstance().getEvaluationSettings()[4]);
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // write dimension
            writer.writeCharacters("\t");
            writer.writeStartElement("dimension");
            writer.writeCharacters("" + this.properties.getDimension());
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // if mouseposition was anytime out of dimension
            if (this.warning) {
                writer.writeCharacters("\t");
                writer.writeComment("related position was anytime out of dimension");
                writer.writeCharacters("\r\n");
                this.warning = false;
            }

            // write count
            writer.writeCharacters("\t");
            writer.writeStartElement("amount");
            writer.writeCharacters("" + this.allData.size());
            writer.writeEndElement();
            writer.writeCharacters("\r\n");

            // run through all data
            for (StorageContainer data : this.allData) {

                // write datatag
                writer.writeCharacters("\t");
                writer.writeStartElement("step");
                writer.writeCharacters("\r\n");

                // write timestamp
                writer.writeCharacters("\t\t");
                writer.writeStartElement("timestamp");
                writer.writeCharacters("" + data.getTimestamp());
                writer.writeEndElement();
                writer.writeCharacters("\r\n");

                // write fixation
                writer.writeCharacters("\t\t");
                writer.writeStartElement("fixation");
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("x");
                writer.writeCharacters("" + data.getFixation().x);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("y");
                writer.writeCharacters("" + data.getFixation().y);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t");
                writer.writeEndElement();
                writer.writeCharacters("\r\n");

                // write mouse position
                writer.writeCharacters("\t\t");
                writer.writeStartElement("relatedposition");
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("x");
                writer.writeCharacters("" + data.getRelatedPoint().x);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t\t");
                writer.writeStartElement("y");
                writer.writeCharacters("" + data.getRelatedPoint().y);
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
                writer.writeCharacters("\t\t");
                writer.writeEndElement();
                writer.writeCharacters("\r\n");

                // close datatag
                writer.writeCharacters("\t");
                writer.writeEndElement();
                writer.writeCharacters("\r\n");
            }

            // Write document end. This closes all open structures
            writer.writeEndDocument();

            // Close the writer to flush the output
            outputStream.close();
            writer.close();

            // unzip xsd if not already there
            if (!xsd.exists())
                $(PrecisionEvaluator.class.getResourceAsStream("resources/DataPattern.zip")).zipstream().unzip(xsd.getAbsolutePath().substring(0, xsd.getAbsolutePath().lastIndexOf(File.separator) + 1));

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try to write file
        /*
         * try { ObjectOutputStream outputStream = new ObjectOutputStream(new
         * FileOutputStream(logfile)); for (StorageContainer temp :
         * this.allData) { outputStream.writeObject(temp); }
         * 
         * } catch (Exception e) { e.printStackTrace(); }
         */

        // reset data
        this.allData.clear();
    }

    /**
     * @return current number of datasets
     */
    public int getCount() {
        return this.allData.size();
    }
}

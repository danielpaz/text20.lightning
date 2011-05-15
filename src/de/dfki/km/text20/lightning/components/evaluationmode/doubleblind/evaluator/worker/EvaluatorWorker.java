/*
 * EvaluatorWorker.java
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

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import net.xeoh.plugins.diagnosis.local.DiagnosisChannel;
import de.dfki.km.text20.lightning.components.evaluationmode.doubleblind.evaluator.EvaluatorMain;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * The EvaluationWorker runs the given plugin with the given container, collects
 * the data, draws the results in a png-file and writes the log-file.
 * 
 * @author Christoph Käding
 * 
 */
public class EvaluatorWorker {

    /** timestamp of the start of this evaluation session */
    private long currentTimeStamp;

    /** stores over all results */
    private EvaluationContainer overAllResults;

    /** logging channel */
    private DiagnosisChannel<String> channel;

    /** current main class */
    private EvaluatorMain main;

    /** current used format */
    private WritableCellFormat format;

    /** coverage of last picture */
    private double formerCoverage;

    /** timestamp to identify screenshots */
    private long formerTimestamp;

    /**
     * creates a new evaluation worker and initializes necessary variables
     * 
     * @param main
     * @param currentTimeStamp
     * @param channel
     */
    public EvaluatorWorker(EvaluatorMain main, long currentTimeStamp,
                           DiagnosisChannel<String> channel) {
        // initialize some variables
        this.currentTimeStamp = currentTimeStamp;
        this.overAllResults = null;
        this.channel = channel;
        this.main = main;
        this.formerTimestamp = 0;

        // excel-stuff
        WritableFont arial10pt = new WritableFont(WritableFont.ARIAL, 10);
        this.format = new WritableCellFormat(arial10pt);
        try {
            this.format.setWrap(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * evaluates the given container with the given detector
     *
     * @param timestamp 
     * @param detector 
     * @param screenShot 
     * @param fixation 
     * @param relatedPoint 
     * @param path 
     */
    public void evaluate(long timestamp, SaliencyDetector detector,
                         BufferedImage screenShot, Point fixation, Point relatedPoint,
                         String path) {

        // initialize variables
        Point calculatedTarget;
        double coverage = 0;

        // transform screenshot
        try {
            BufferedImage tmp = new BufferedImage(screenShot.getWidth() + this.main.getDimension(), screenShot.getHeight() + this.main.getDimension(), screenShot.getType());
            Graphics2D graphics = tmp.createGraphics();
            graphics.drawImage(screenShot, this.main.getDimension() / 2, this.main.getDimension() / 2, null);
            screenShot = tmp;
            screenShot = screenShot.getSubimage(fixation.x, fixation.y, this.main.getDimension(), this.main.getDimension());
        } catch (RasterFormatException e) {
            e.printStackTrace();
            return;
        }

        // analyze text coverage
        if (this.main.writeLog()) {
            if (this.formerTimestamp != timestamp) {
                this.formerCoverage = this.main.getCoverageAnalyser().analyse(screenShot);
                this.formerTimestamp = timestamp;
            }
            coverage = this.formerCoverage;
        }

        // calculate offset by running the detector
        calculatedTarget = detector.analyse(screenShot);
        calculatedTarget.translate(screenShot.getHeight() / 2, screenShot.getWidth() / 2);
        Point translatedRelated = new Point(relatedPoint.x - fixation.x + screenShot.getHeight() / 2, relatedPoint.y - fixation.y + screenShot.getWidth() / 2);

        // write the png-file
        if (this.main.writeImages())
            this.drawPicture(detector, calculatedTarget, path + "/evaluated/Session_" + this.currentTimeStamp + "/" + this.formerTimestamp + "_evaluated.png", screenShot, translatedRelated);

        // add results to over all storage
        if (this.overAllResults == null) this.overAllResults = new EvaluationContainer(detector.getInformation().getId(), calculatedTarget.distance(translatedRelated), coverage, this.main.getCoverageThreshold(), path);
        else
            this.overAllResults.add(detector.getInformation().getId(), calculatedTarget.distance(translatedRelated), coverage, path);
    }

    /**
     * A call of this method indicates that the evaluation is finished and the
     * result files can be written.
     * 
     * @param detectors
     * 
     * @return name of best ranked detector
     */
    @SuppressWarnings("boxing")
    public String getBestResult(ArrayList<SaliencyDetector> detectors) {
        // test if some data are collected
        if (this.overAllResults == null) return "...nothing";
        if (this.overAllResults.getSizeOverAll() == 0) return "...nothing";

        // initialize variables
        double veryBestValue = Double.MAX_VALUE;
        ArrayList<Integer> veryBestKey = new ArrayList<Integer>();
        String bestMethods = "";
        String veryBestMethods = "";

        for (String path : this.overAllResults.getLogPath()) {

            // build path
            path = path + "/evaluated/Session_" + this.currentTimeStamp + "/" + System.currentTimeMillis() + "_evaluated.log";

            if (this.main.writeLog()) {
                $(path).file().append("- overall results for this session -\r\n\r\n#Overview#\r\n");
                $(path).file().append("- Number of different Locations: " + this.overAllResults.getLogPath().size() + "\r\n");
                $(path).file().append("- Number of DataSets overall: " + this.overAllResults.getSizeOverAll() + "\r\n");
                $(path).file().append("- Text Coverage Threshold: " + this.main.getCoverageThreshold() + "%\r\n");
                $(path).file().append("- Datasets with a Text Coverage higher than threshold: " + this.overAllResults.getSizeHigher() + "\r\n");
                $(path).file().append("- Datasets with a Text Coverage lower than threshold: " + this.overAllResults.getSizeLower() + "\r\n");
                $(path).file().append("\r\n#Results, over all#\r\n");
            }

            // run through all included ids, over all
            for (int id : this.overAllResults.getIds()) {

                // write result to log
                if (this.main.writeLog())
                    $(path).file().append(detectors.get(id).getInformation().getDisplayName() + ": " + ((double) Math.round(this.overAllResults.getAveragedDistanceOverAll(id) * 100) / 100) + " Pixel distance averaged.\r\n");

                // check if the current value is equal then the best value
                if (veryBestValue == this.overAllResults.getAveragedDistanceOverAll(id))
                    veryBestKey.add(id);

                // check if the current value is better then the best value
                if (veryBestValue > this.overAllResults.getAveragedDistanceOverAll(id)) {

                    // store new best value
                    veryBestKey.clear();
                    veryBestKey.add(id);
                    veryBestValue = this.overAllResults.getAveragedDistanceOverAll(id);
                }
            }
            if (this.main.writeLog()) {
                // single best key
                if (veryBestKey.size() == 1) {
                    $(path).file().append("-> best result for " + detectors.get(veryBestKey.get(0)).getInformation().getDisplayName() + "\r\n");

                    // multiple best keys
                } else {
                    $(path).file().append("-> best results for ");
                    for (int i = 0; i < veryBestKey.size() - 2; i++) {
                        $(path).file().append(detectors.get(veryBestKey.get(i)).getInformation().getDisplayName() + ", ");
                    }
                    $(path).file().append(detectors.get(veryBestKey.get(veryBestKey.size() - 2)).getInformation().getDisplayName());
                    $(path).file().append(" and " + detectors.get(veryBestKey.get(veryBestKey.size() - 1)).getInformation().getDisplayName() + "\r\n");
                }
            }

            // create best results string
            if (veryBestKey.size() == 1) {
                bestMethods = detectors.get(veryBestKey.get(0)).getInformation().getDisplayName();

                // multiple best keys
            } else {
                for (int i = 0; i < veryBestKey.size() - 2; i++) {
                    bestMethods = bestMethods + detectors.get(i).getInformation().getDisplayName() + ", ";
                }
                bestMethods = bestMethods + detectors.get(veryBestKey.size() - 2).getInformation().getDisplayName();
                bestMethods = bestMethods + " and " + detectors.get(veryBestKey.size() - 1).getInformation().getDisplayName();
            }

            // reset
            veryBestMethods = bestMethods;
            bestMethods = "";
            veryBestKey.clear();
            veryBestValue = Double.MAX_VALUE;

            if (this.main.writeLog()) {

                // write header
                $(path).file().append("\r\n#Results, higher than threshold#\r\n");

                // run through all included ids, higher than
                for (int id : this.overAllResults.getIds()) {

                    // write result to log
                    $(path).file().append(detectors.get(id).getInformation().getDisplayName() + ": " + ((double) Math.round(this.overAllResults.getAveragedDistanceHigher(id) * 100) / 100) + " Pixel distance averaged.\r\n");

                    // check if the current value is equal then the best value
                    if (veryBestValue == this.overAllResults.getAveragedDistanceHigher(id))
                        veryBestKey.add(id);

                    // check if the current value is better then the best value
                    if (veryBestValue > this.overAllResults.getAveragedDistanceHigher(id)) {

                        // store new best value
                        veryBestKey.clear();
                        veryBestKey.add(id);
                        veryBestValue = this.overAllResults.getAveragedDistanceHigher(id);
                    }
                }

                // single best key
                if (veryBestKey.size() == 1) {
                    $(path).file().append("-> best result for " + detectors.get(veryBestKey.get(0)).getInformation().getDisplayName() + "\r\n");

                    // multiple best keys
                } else {
                    $(path).file().append("-> best results for ");
                    for (int i = 0; i < veryBestKey.size() - 2; i++) {
                        $(path).file().append(detectors.get(veryBestKey.get(i)).getInformation().getDisplayName() + ", ");
                    }
                    $(path).file().append(detectors.get(veryBestKey.get(veryBestKey.size() - 2)).getInformation().getDisplayName());
                    $(path).file().append(" and " + detectors.get(veryBestKey.get(veryBestKey.size() - 1)).getInformation().getDisplayName() + "\r\n");
                }

                // reset
                veryBestKey.clear();
                veryBestValue = Double.MAX_VALUE;

                // write header
                $(path).file().append("\r\n#Results, lower than threshold#\r\n");

                // run through all included ids, higher than
                for (int id : this.overAllResults.getIds()) {

                    // write result to log
                    $(path).file().append(detectors.get(id).getInformation().getDisplayName() + ": " + ((double) Math.round(this.overAllResults.getAveragedDistanceLower(id) * 100) / 100) + " Pixel distance averaged.\r\n");

                    // check if the current value is equal then the best value
                    if (veryBestValue == this.overAllResults.getAveragedDistanceLower(id))
                        veryBestKey.add(id);

                    // check if the current value is better then the best value
                    if (veryBestValue > this.overAllResults.getAveragedDistanceLower(id)) {

                        // store new best value
                        veryBestKey.clear();
                        veryBestKey.add(id);
                        veryBestValue = this.overAllResults.getAveragedDistanceLower(id);
                    }
                }

                // single best key
                if (veryBestKey.size() == 1) {
                    $(path).file().append("-> best result for " + detectors.get(veryBestKey.get(0)).getInformation().getDisplayName() + "\r\n");

                    // multiple best keys
                } else {
                    $(path).file().append("-> best results for ");
                    for (int i = 0; i < veryBestKey.size() - 2; i++) {
                        $(path).file().append(detectors.get(veryBestKey.get(i)).getInformation().getDisplayName() + ", ");
                    }
                    $(path).file().append(detectors.get(veryBestKey.get(veryBestKey.size() - 2)).getInformation().getDisplayName());
                    $(path).file().append(" and " + detectors.get(veryBestKey.get(veryBestKey.size() - 1)).getInformation().getDisplayName() + "\r\n");
                }

                // reset
                veryBestKey.clear();
                veryBestValue = Double.MAX_VALUE;
            }
        }

        // log best result
        this.channel.status("best result: " + veryBestMethods + " with " + this.overAllResults.getSizeOverAll() + "datasets");

        // return the name of the very best detector
        return veryBestMethods;
    }

    /**
     * draws the png-file withe the calculated results
     * 
     * @param detector
     *            used to get the name
     * @param point
     *            where the detector recognized a target
     * @param path
     *            where the image will be written
     * @param screenShot
     *            screenshot where the data will be written in, maybe it will be
     *            not used when the screenshot is already drawn to a file
     * @param relatedPoint
     *            target that was given or was pointed by the mouse
     */
    private void drawPicture(SaliencyDetector detector, Point point, String path,
                             BufferedImage screenShot, Point relatedPoint) {
        // initialize variables
        int color = (int) (Math.random() * 255);
        int dimension = screenShot.getHeight();
        File file = new File(path);
        boolean alreadyExists = file.exists();

        try {
            // if the screenshot file already exists, the given screenshot is
            // overwritten by the existing one to update new data
            if (alreadyExists) screenShot = ImageIO.read(file);

            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            if (!alreadyExists) {
                // visualize fixation point
                graphic.setColor(new Color(255, 255, 0, 255));
                graphic.drawOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);
                graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + dimension / 2, 12 + dimension / 2);
                graphic.setColor(new Color(255, 255, 0, 32));
                graphic.fillOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);

                // visualize related point
                graphic.setColor(new Color(255, 0, 0, 255));
                graphic.drawOval(relatedPoint.x - 5, relatedPoint.y - 5, 10, 10);
                graphic.drawChars(("related target").toCharArray(), 0, 14, 12 + relatedPoint.x, 12 + relatedPoint.y);
                graphic.setColor(new Color(255, 0, 0, 32));
                graphic.fillOval(relatedPoint.x - 5, relatedPoint.y - 5, 10, 10);
            }

            // visualize calculations
            color = (50 + color) % 256;
            graphic.setColor(new Color(0, 255 - color, color, 255));
            graphic.drawOval(point.x - 5, point.y - 5, 10, 10);
            graphic.drawChars(detector.getInformation().getDisplayName().toCharArray(), 0, detector.getInformation().getDisplayName().toCharArray().length, point.x + 12, point.y + 12);
            graphic.setColor(new Color(0, 255 - color, color, 32));
            graphic.fillOval(point.x - 5, point.y - 5, 10, 10);

            // write the image
            file.mkdirs();
            ImageIO.write(screenShot, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

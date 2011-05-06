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
package de.dfki.km.text20.lightning.evaluator.worker;

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import net.xeoh.plugins.diagnosis.local.DiagnosisChannel;
import de.dfki.km.text20.lightning.evaluator.EvaluatorMain;
import de.dfki.km.text20.lightning.evaluator.plugins.CoverageAnalyser;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.worker.evaluationmode.StorageContainer;

/**
 * The EvaluationWorker runs the given plugin with the given container, collects
 * the data, draws the results in a png-file and writes the log-file.
 * 
 * @author Christoph Käding
 * 
 */
public class EvaluatorWorker {

    /**
     * map which stores evaluation container as value with given identifiers as
     * key
     */
    private Map<String, EvaluationContainer> results;

    /** timestamp of the start of this evaluation session */
    private long currentTimeStamp;

    /** stores over all results */
    private EvaluationContainer overAllResults;

    /** logging channel */
    private DiagnosisChannel<String> channel;

    /** list of different paths where the overallresults should be shown */
    private ArrayList<String> overAllPath;

    /** current main class */
    private EvaluatorMain main;

    /** map of all settings of the different datasets */
    private Map<String, SettingsContainer> settings;

    /** last file with error, used to kill double-error-output */
    private String errorKey;

    /** current used format */
    private WritableCellFormat format;

    /** last checked container */
    private StorageContainer formerContainer;

    /** stored pupil streams */
    private HashMap<String, PupilContainer> pupilData;

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
        this.results = new Hashtable<String, EvaluationContainer>();
        this.settings = new Hashtable<String, SettingsContainer>();
        this.currentTimeStamp = currentTimeStamp;
        this.overAllResults = null;
        this.channel = channel;
        this.overAllPath = new ArrayList<String>();
        this.pupilData = new HashMap<String, PupilContainer>();
        this.main = main;
        this.errorKey = "";
        this.formerContainer = null;

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
     * @param analyser 
     *            used coverage analyser
     * @param file
     *            which includes the data, used to create identifier etc
     * @param detector
     *            which should be used
     * @param container
     *            which should be used
     */
    public void evaluate(CoverageAnalyser analyser, File file, SaliencyDetector detector,
                         StorageContainer container) {
        // initialize variables
        BufferedImage screenShot = null;
        Point point = new Point();
        Point translatedRelatedPoint = new Point(container.getRelatedPoint().x - container.getFixation().x + this.main.getDimension() / 2, container.getRelatedPoint().y - container.getFixation().y + this.main.getDimension() / 2);
        String user = file.getName().substring(0, file.getName().lastIndexOf("_"));
        user = user.substring(0, user.lastIndexOf("_"));
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator + "data" + File.separator));
        String identifier = file.getName();
        String xmlTimeStamp = (file.getName().substring(0, file.getName().lastIndexOf("_"))).replace(user + "_", "");

        // test if the path is already known
        if (!this.overAllPath.contains(path + "/evaluated/Session_" + this.currentTimeStamp + "/evaluation.log"))
            this.overAllPath.add(path + "/evaluated/Session_" + this.currentTimeStamp + "/evaluation.log");

        // test if associated screenshot is available
        File screenFile = new File(path + "/data/" + user + "/" + user + "_" + container.getTimestamp() + ".png");
        if (!screenFile.exists()) return;

        // read screenshot
        try {
            screenShot = ImageIO.read(screenFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // transform screenshot
        try {
            screenShot = screenShot.getSubimage(container.getFixation().x, container.getFixation().y, this.main.getDimension(), this.main.getDimension());
        } catch (RasterFormatException e) {
            if (screenFile.getName().equals(this.errorKey)) return;
            System.out.println("ERROR: raster out of format!");
            this.errorKey = screenFile.getName();
            this.settings.get(identifier).addOutOfRaster();
            return;
        }

        // analyze text coverage
        if (this.main.writeLog()) {
            if (this.formerContainer == null) container.setTextCoverage(analyser.analyse(screenShot));
            else if (!this.formerContainer.equals(container))
                container.setTextCoverage(analyser.analyse(screenShot));
            this.formerContainer = container;
        }

        // calculate offset by running the detector
        point = detector.analyse(screenShot);
        point.translate(screenShot.getHeight() / 2, screenShot.getWidth() / 2);

        // write the png-file
        if (this.main.writeImages())
            this.drawPicture(detector, point, path + "/evaluated/Session_" + this.currentTimeStamp + "/" + user + "_" + xmlTimeStamp + "/" + user + "_" + container.getTimestamp() + "_evaluated.png", screenShot, translatedRelatedPoint);

        // add results to over all storage
        if (this.overAllResults == null) this.overAllResults = new EvaluationContainer(detector.getInformation().getId(), point.distance(translatedRelatedPoint), container, this.main.getCoverageThreshold(), "", user, this.currentTimeStamp);
        else
            this.overAllResults.add(detector.getInformation().getId(), point.distance(translatedRelatedPoint), container);

        // check if the identifier is already in the map
        if (this.results.containsKey(identifier)) {

            // add distance to the already existing identifier
            this.results.get(identifier).add(detector.getInformation().getId(), point.distance(translatedRelatedPoint), container);
        } else {

            // creates net map entry by identifier and adds new evaluation
            // container to it
            this.results.put(identifier, new EvaluationContainer(detector.getInformation().getId(), point.distance(translatedRelatedPoint), container, this.main.getCoverageThreshold(), path + "/evaluated/Session_" + this.currentTimeStamp + "/" + user + "_" + xmlTimeStamp + "/" + user + "_" + xmlTimeStamp + ".log", user, Long.parseLong(xmlTimeStamp)));
        }

    }

    /**
     * provides map to add entries
     * 
     * @param key
     * @param value
     */
    public void addToSettingMap(String key, SettingsContainer value) {
        this.settings.put(key, value);
        this.main.updateProgressBar();
    }

    /**
     * pupil maps, sorted by filename
     * 
     * @param key
     * @param value
     */
    public void addToPupilMap(String key, PupilContainer value) {
        this.pupilData.put(key, value);
        this.main.updateProgressBar();
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
        if (this.results.size() == 0) return "...nothing";

        // initialize variables
        double veryBestValue = Double.MAX_VALUE;
        ArrayList<Integer> veryBestKey = new ArrayList<Integer>();
        String bestMethods = "";
        String veryBestMethods = "";

        for (String path : this.overAllPath) {
            if (this.main.writeLog()) {
                $(path).file().append("- overall results for this session -\r\n\r\n#Overview#\r\n");
                $(path).file().append("- Timestamp: " + this.overAllResults.getTimeStamp() + "\r\n");
                $(path).file().append("- Number of different Locations: " + this.overAllPath.size() + "\r\n");
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

        if (this.main.writeLog()) {
            // write the individual *.log and *.xls
            this.writeIndividualResults(detectors);

            // write over all xml for derivation in association with brightness
            this.writeOverAllXls();
        }

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

    /**
     * writes individual result files
     * 
     * @param detectors
     */
    @SuppressWarnings("boxing")
    private void writeIndividualResults(ArrayList<SaliencyDetector> detectors) {
        // initialize variables
        double bestValue = Double.MAX_VALUE;
        ArrayList<Integer> bestKey = new ArrayList<Integer>();

        // initialize xls-stuff
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        // run through the keyset of the result map
        for (String key : this.results.keySet()) {

            // run through the ids of every entry of the result map, over all
            for (int i = 0; i < this.results.get(key).getIds().size(); i++) {

                // check if the current value is equal then the best value
                if (bestValue == this.results.get(key).getAveragedDistanceOverAll(this.results.get(key).getIds().get(i)))
                    bestKey.add(this.results.get(key).getIds().get(i));

                // check if the current value is better then the best value, the
                // best value is only for this identifier
                if (bestValue > this.results.get(key).getAveragedDistanceOverAll(this.results.get(key).getIds().get(i))) {

                    // store new best value
                    bestKey.clear();
                    bestKey.add(this.results.get(key).getIds().get(i));
                    bestValue = this.results.get(key).getAveragedDistanceOverAll(this.results.get(key).getIds().get(i));
                }

                // first entry
                if (i == 0) {
                    $(this.results.get(key).getLogPath()).file().append("#Session#\r\n- User: " + this.results.get(key).getName() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Timestamp: " + this.results.get(key).getTimeStamp() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Number of DataSets: " + this.results.get(key).getSizeOverAll() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Dimension: " + this.settings.get(key).getDimension() + ", OutOfDimensionCount: " + this.settings.get(key).getOutOfDim() + ", OutOfRatserCount: " + this.settings.get(key).getOutOfRaster() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Screen Brightness: " + this.settings.get(key).getScreenBright() + " -> " + StorageContainer.getScreenBrightnessOptions().get(this.settings.get(key).getScreenBright()) + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Setting Brightness: " + this.settings.get(key).getSettingBright() + " -> " + StorageContainer.getSettingBrightnessOptions().get(this.settings.get(key).getSettingBright()) + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Recalibration was used: " + this.settings.get(key).isRecalibration() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- used Evaluationmode: " + this.settings.get(key).getEvaluationMode() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Text Coverage Threshold: " + this.main.getCoverageThreshold() + "%\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Datasets with a Text Coverage higher than threshold: " + this.results.get(key).getSizeHigher() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("- Datasets with a Text Coverage lower than threshold: " + this.results.get(key).getSizeLower() + "\r\n");
                    $(this.results.get(key).getLogPath()).file().append("\r\n#Results, over all#\r\n");
                }

                // middle entries
                $(this.results.get(key).getLogPath()).file().append(detectors.get(this.results.get(key).getIds().get(i)).getInformation().getDisplayName() + ": " + ((double) Math.round(this.results.get(key).getAveragedDistanceOverAll(this.results.get(key).getIds().get(i)) * 100) / 100) + " Pixel distance averaged.\r\n");

                // last entry
                if (i == this.results.get(key).getIds().size() - 1) {
                    // single best key
                    if (bestKey.size() == 1) {
                        $(this.results.get(key).getLogPath()).file().append("-> best result for " + detectors.get(bestKey.get(0)).getInformation().getDisplayName() + "\r\n");

                        // multiple best keys
                    } else {
                        $(this.results.get(key).getLogPath()).file().append("-> best results for ");
                        for (int j = 0; j < bestKey.size() - 2; j++) {
                            $(this.results.get(key).getLogPath()).file().append(detectors.get(bestKey.get(j)).getInformation().getDisplayName() + ", ");
                        }
                        $(this.results.get(key).getLogPath()).file().append(detectors.get(bestKey.get(bestKey.size() - 2)).getInformation().getDisplayName());
                        $(this.results.get(key).getLogPath()).file().append(" and " + detectors.get(bestKey.get(bestKey.size() - 1)).getInformation().getDisplayName() + "\r\n");
                    }
                }
            }

            // reset
            bestValue = Double.MAX_VALUE;
            bestKey.clear();

            // run through the ids of every entry of the result map, higher
            for (int i = 0; i < this.results.get(key).getIds().size(); i++) {

                // check if the current value is equal then the best value
                if (bestValue == this.results.get(key).getAveragedDistanceHigher(this.results.get(key).getIds().get(i)))
                    bestKey.add(this.results.get(key).getIds().get(i));

                // check if the current value is better then the best value, the
                // best value is only for this identifier
                if (bestValue > this.results.get(key).getAveragedDistanceHigher(this.results.get(key).getIds().get(i))) {

                    // store new best value
                    bestKey.clear();
                    bestKey.add(this.results.get(key).getIds().get(i));
                    bestValue = this.results.get(key).getAveragedDistanceHigher(this.results.get(key).getIds().get(i));
                }

                // first entry
                if (i == 0) {
                    $(this.results.get(key).getLogPath()).file().append("\r\n#Results, higher than threshold#\r\n");
                }

                // middle entries
                $(this.results.get(key).getLogPath()).file().append(detectors.get(this.results.get(key).getIds().get(i)).getInformation().getDisplayName() + ": " + ((double) Math.round(this.results.get(key).getAveragedDistanceHigher(this.results.get(key).getIds().get(i)) * 100) / 100) + " Pixel distance averaged.\r\n");

                // last entry
                if (i == this.results.get(key).getIds().size() - 1) {
                    // single best key
                    if (bestKey.size() == 1) {
                        $(this.results.get(key).getLogPath()).file().append("-> best result for " + detectors.get(bestKey.get(0)).getInformation().getDisplayName() + "\r\n");

                        // multiple best keys
                    } else {
                        $(this.results.get(key).getLogPath()).file().append("-> best results for ");
                        for (int j = 0; j < bestKey.size() - 2; j++) {
                            $(this.results.get(key).getLogPath()).file().append(detectors.get(bestKey.get(j)).getInformation().getDisplayName() + ", ");
                        }
                        $(this.results.get(key).getLogPath()).file().append(detectors.get(bestKey.get(bestKey.size() - 2)).getInformation().getDisplayName());
                        $(this.results.get(key).getLogPath()).file().append(" and " + detectors.get(bestKey.get(bestKey.size() - 1)).getInformation().getDisplayName() + "\r\n");
                    }
                }
            }

            // reset
            bestValue = Double.MAX_VALUE;
            bestKey.clear();

            // run through the ids of every entry of the result map, lower
            for (int i = 0; i < this.results.get(key).getIds().size(); i++) {

                // check if the current value is equal then the best value
                if (bestValue == this.results.get(key).getAveragedDistanceLower(this.results.get(key).getIds().get(i)))
                    bestKey.add(this.results.get(key).getIds().get(i));

                // check if the current value is better then the best value, the
                // best value is only for this identifier
                if (bestValue > this.results.get(key).getAveragedDistanceLower(this.results.get(key).getIds().get(i))) {

                    // store new best value
                    bestKey.clear();
                    bestKey.add(this.results.get(key).getIds().get(i));
                    bestValue = this.results.get(key).getAveragedDistanceLower(this.results.get(key).getIds().get(i));
                }

                // first entry
                if (i == 0) {
                    $(this.results.get(key).getLogPath()).file().append("\r\n#Results, lower than threshold#\r\n");
                }

                // middle entries
                $(this.results.get(key).getLogPath()).file().append(detectors.get(this.results.get(key).getIds().get(i)).getInformation().getDisplayName() + ": " + ((double) Math.round(this.results.get(key).getAveragedDistanceLower(this.results.get(key).getIds().get(i)) * 100) / 100) + " Pixel distance averaged.\r\n");

                // last entry
                if (i == this.results.get(key).getIds().size() - 1) {
                    // single best key
                    if (bestKey.size() == 1) {
                        $(this.results.get(key).getLogPath()).file().append("-> best result for " + detectors.get(bestKey.get(0)).getInformation().getDisplayName() + "\r\n");

                        // multiple best keys
                    } else {
                        $(this.results.get(key).getLogPath()).file().append("-> best results for ");
                        for (int j = 0; j < bestKey.size() - 2; j++) {
                            $(this.results.get(key).getLogPath()).file().append(detectors.get(bestKey.get(j)).getInformation().getDisplayName() + ", ");
                        }
                        $(this.results.get(key).getLogPath()).file().append(detectors.get(bestKey.get(bestKey.size() - 2)).getInformation().getDisplayName());
                        $(this.results.get(key).getLogPath()).file().append(" and " + detectors.get(bestKey.get(bestKey.size() - 1)).getInformation().getDisplayName() + "\r\n");
                    }
                }
            }

            // write distance
            $(this.results.get(key).getLogPath()).file().append("\r\n#Data#\r\n");
            $(this.results.get(key).getLogPath()).file().append("Fixation-x\tFixation-y\tRelated-x\t\tRelated-y\t\tText Coverage\tDistance-Related-Fixation\r\n");
            for (StorageContainer container : this.results.get(key).getContainer())
                $(this.results.get(key).getLogPath()).file().append(container.getFixation().x + "  \t\t" + container.getFixation().y + "  \t\t" + container.getRelatedPoint().x + "  \t\t" + container.getRelatedPoint().y + "  \t\t" + ((double) Math.round(container.getTextCoverage() * 1000) / 1000) + "     \t\t" + container.getFixation().distance(container.getRelatedPoint()) + "\r\n");

            // reset values
            bestValue = Double.MAX_VALUE;
            bestKey.clear();

            // write xls-file
            try {
                // initialize
                workbook = Workbook.createWorkbook(new File(this.results.get(key).getLogPath().replace(".log", ".xls")), wbSettings);
                workbook.createSheet("Evaluation", 0);
                WritableSheet excelSheet = workbook.getSheet(0);

                // add captions
                excelSheet.addCell(new Label(0, 0, "Fixation-x", this.format));
                excelSheet.addCell(new Label(1, 0, "Fixation-y", this.format));
                excelSheet.addCell(new Label(2, 0, "Related-x", this.format));
                excelSheet.addCell(new Label(3, 0, "Related-y", this.format));
                excelSheet.addCell(new Label(5, 0, "Text Coverage", this.format));
                excelSheet.addCell(new Label(6, 0, "Distance-Related-Fixation", this.format));

                // iterate trough data
                for (int i = 0; i < this.results.get(key).getContainer().size(); i++) {
                    excelSheet.addCell(new Number(0, i + 1, this.results.get(key).getContainer().get(i).getFixation().x, this.format));
                    excelSheet.addCell(new Number(1, i + 1, this.results.get(key).getContainer().get(i).getFixation().y, this.format));
                    excelSheet.addCell(new Number(2, i + 1, this.results.get(key).getContainer().get(i).getRelatedPoint().x, this.format));
                    excelSheet.addCell(new Number(3, i + 1, this.results.get(key).getContainer().get(i).getRelatedPoint().y, this.format));
                    excelSheet.addCell(new Number(5, i + 1, this.results.get(key).getContainer().get(i).getTextCoverage(), this.format));
                    excelSheet.addCell(new Number(6, i + 1, this.results.get(key).getContainer().get(i).getFixation().distance(this.results.get(key).getContainer().get(i).getRelatedPoint()), this.format));
                }

                // write and close
                workbook.write();
                workbook.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            // update progress bar
            this.main.updateProgressBar();
        }
    }

    /**
     * writes the summary xls
     */
    private void writeOverAllXls() {
        double threshold = this.main.getCoverageThreshold();
        int offsetY = 0;
        int offsetX = StorageContainer.getScreenBrightnessOptions().size() + 3;

        /*
         * x-direction: screen brightness
         * y-direction: setting brightness
         */
        DerivationContainer[][] dataAllHigher = new DerivationContainer[StorageContainer.getScreenBrightnessOptions().size()][StorageContainer.getSettingBrightnessOptions().size()];
        DerivationContainer[][] dataAllLower = new DerivationContainer[StorageContainer.getScreenBrightnessOptions().size()][StorageContainer.getSettingBrightnessOptions().size()];
        DerivationContainer[][] dataWithHigher = new DerivationContainer[StorageContainer.getScreenBrightnessOptions().size()][StorageContainer.getSettingBrightnessOptions().size()];
        DerivationContainer[][] dataWithLower = new DerivationContainer[StorageContainer.getScreenBrightnessOptions().size()][StorageContainer.getSettingBrightnessOptions().size()];
        DerivationContainer[][] dataWithoutHigher = new DerivationContainer[StorageContainer.getScreenBrightnessOptions().size()][StorageContainer.getSettingBrightnessOptions().size()];
        DerivationContainer[][] dataWithoutLower = new DerivationContainer[StorageContainer.getScreenBrightnessOptions().size()][StorageContainer.getSettingBrightnessOptions().size()];

        // initialize xls-stuff
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        // initialize array
        for (int x = 0; x < StorageContainer.getScreenBrightnessOptions().size(); x++) {
            for (int y = 0; y < StorageContainer.getSettingBrightnessOptions().size(); y++) {
                dataAllHigher[x][y] = new DerivationContainer();
                dataAllLower[x][y] = new DerivationContainer();
                dataWithHigher[x][y] = new DerivationContainer();
                dataWithLower[x][y] = new DerivationContainer();
                dataWithoutHigher[x][y] = new DerivationContainer();
                dataWithoutLower[x][y] = new DerivationContainer();
            }
        }

        // run through the keyset of the result map to summarize derivation settingspecific
        for (String key : this.results.keySet()) {
            for (StorageContainer container : this.results.get(key).getContainer()) {
                if (container.getTextCoverage() > threshold) {
                    dataAllHigher[this.settings.get(key).getScreenBright()][this.settings.get(key).getSettingBright()].addDistance(container.getFixation().distance(container.getRelatedPoint()));
                    if (this.settings.get(key).isRecalibration()) {
                        dataWithHigher[this.settings.get(key).getScreenBright()][this.settings.get(key).getSettingBright()].addDistance(container.getFixation().distance(container.getRelatedPoint()));
                    } else {
                        dataWithoutHigher[this.settings.get(key).getScreenBright()][this.settings.get(key).getSettingBright()].addDistance(container.getFixation().distance(container.getRelatedPoint()));
                    }
                } else {
                    dataAllLower[this.settings.get(key).getScreenBright()][this.settings.get(key).getSettingBright()].addDistance(container.getFixation().distance(container.getRelatedPoint()));
                    if (this.settings.get(key).isRecalibration()) {
                        dataWithLower[this.settings.get(key).getScreenBright()][this.settings.get(key).getSettingBright()].addDistance(container.getFixation().distance(container.getRelatedPoint()));
                    } else {
                        dataWithoutLower[this.settings.get(key).getScreenBright()][this.settings.get(key).getSettingBright()].addDistance(container.getFixation().distance(container.getRelatedPoint()));
                    }
                }
            }
            this.main.updateProgressBar();
        }

        // run trough all locations
        for (String path : this.overAllPath) {

            // write xls-file
            try {
                // initialize
                workbook = Workbook.createWorkbook(new File(path.replace(".log", ".xls")), wbSettings);
                workbook.createSheet("Evaluation", 0);
                WritableSheet excelSheet = workbook.getSheet(0);

                // add label
                excelSheet.addCell(new Label(0, 0, "Text Coverage Threshold: " + threshold + "%", this.format));

                // add label
                excelSheet.addCell(new Label((StorageContainer.getScreenBrightnessOptions().size() + 2), offsetY, "Derivation without Recalibration", this.format));

                // add captions
                this.writeCaptions(excelSheet, offsetX, offsetY + 2);

                // iterate trough data
                for (int x = 0; x < StorageContainer.getScreenBrightnessOptions().size(); x++) {
                    for (int y = 0; y < StorageContainer.getSettingBrightnessOptions().size(); y++) {
                        excelSheet.addCell(new Number(x + 2, y + offsetY + 6, dataWithoutHigher[x][y].getAveragedDerivation(), this.format));
                    }
                }

                // iterate trough data
                for (int x = 0; x < StorageContainer.getScreenBrightnessOptions().size(); x++) {
                    for (int y = 0; y < StorageContainer.getSettingBrightnessOptions().size(); y++) {
                        excelSheet.addCell(new Number(x + 2 + offsetX, y + offsetY + 6, dataWithoutLower[x][y].getAveragedDerivation(), this.format));
                    }
                }

                offsetY = offsetY + 8 + StorageContainer.getSettingBrightnessOptions().size();

                // add label
                excelSheet.addCell(new Label((StorageContainer.getScreenBrightnessOptions().size() + 2), offsetY, "Derivation with Recalibration", this.format));

                // add captions
                this.writeCaptions(excelSheet, offsetX, offsetY + 2);

                // iterate trough data
                for (int x = 0; x < StorageContainer.getScreenBrightnessOptions().size(); x++) {
                    for (int y = 0; y < StorageContainer.getSettingBrightnessOptions().size(); y++) {
                        excelSheet.addCell(new Number(x + 2, y + offsetY + 6, dataWithHigher[x][y].getAveragedDerivation(), this.format));
                    }
                }

                // iterate trough data
                for (int x = 0; x < StorageContainer.getScreenBrightnessOptions().size(); x++) {
                    for (int y = 0; y < StorageContainer.getSettingBrightnessOptions().size(); y++) {
                        excelSheet.addCell(new Number(offsetX + x + 2, y + offsetY + 6, dataWithLower[x][y].getAveragedDerivation(), this.format));
                    }
                }

                // write and close
                workbook.write();
                workbook.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * adds captions to xls
     * 
     * @param excelSheet
     * @param offsetY
     * @throws RowsExceededException
     * @throws WriteException
     */
    @SuppressWarnings("boxing")
    private void writeCaptions(WritableSheet excelSheet, int offsetX, int offsetY)
                                                                                  throws RowsExceededException,
                                                                                  WriteException {
        excelSheet.addCell(new Label(StorageContainer.getScreenBrightnessOptions().size() / 2, offsetY, "text coverage higher than threshold", this.format));
        excelSheet.addCell(new Label(StorageContainer.getScreenBrightnessOptions().size() / 2 + offsetX, +offsetY, "text coverage lower than threshold", this.format));

        excelSheet.addCell(new Label(StorageContainer.getScreenBrightnessOptions().size() / 2 + 2, offsetY + 2, "screen brightness", this.format));
        excelSheet.addCell(new Label(0, StorageContainer.getSettingBrightnessOptions().size() / 2 + 2 + offsetY + 2, "setting brightness", this.format));

        excelSheet.addCell(new Label(StorageContainer.getScreenBrightnessOptions().size() / 2 + 2 + offsetX, offsetY + 2, "screen brightness", this.format));
        excelSheet.addCell(new Label(offsetX, StorageContainer.getSettingBrightnessOptions().size() / 2 + 2 + offsetY + 2, "setting brightness", this.format));

        for (int i = 0; i < StorageContainer.getScreenBrightnessOptions().size(); i++) {
            excelSheet.addCell(new Label(2 + i, offsetY + 3, StorageContainer.getScreenBrightnessOptions().get(i), this.format));
            excelSheet.addCell(new Label(2 + i + offsetX, offsetY + 3, StorageContainer.getScreenBrightnessOptions().get(i), this.format));
        }
        for (int i = 0; i < StorageContainer.getSettingBrightnessOptions().size(); i++) {
            excelSheet.addCell(new Label(1, 4 + i + offsetY, StorageContainer.getSettingBrightnessOptions().get(i), this.format));
            excelSheet.addCell(new Label(1 + offsetX, 4 + i + offsetY, StorageContainer.getSettingBrightnessOptions().get(i), this.format));
        }

    }
}

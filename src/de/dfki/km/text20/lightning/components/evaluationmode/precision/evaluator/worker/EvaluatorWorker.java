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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.evaluator.worker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.evaluator.EvaluatorMain;
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
    private EvaluationContainer results;

    /** current main class */
    private EvaluatorMain main;

    /** current used format */
    private WritableCellFormat format;

    /** coverage of last picture */
    private double formerCoverage;

    /** identifier for screenshots */
    private int formerIdentifier;

    /**
     * creates a new evaluation worker and initializes necessary variables
     * 
     * @param main
     * @param currentTimeStamp
     */
    public EvaluatorWorker(EvaluatorMain main, long currentTimeStamp) {
        // initialize some variables
        this.currentTimeStamp = currentTimeStamp;
        this.results = null;
        this.main = main;
        this.formerIdentifier = 0;

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
     * @param identifier 
     * @param detector 
     * @param screenShot 
     * @param fixation 
     * @param relatedPoint 
     * @param path 
     */
    public void evaluate(int identifier, SaliencyDetector detector,
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
            if (this.formerIdentifier != identifier) {
                this.formerCoverage = this.main.getCoverageAnalyser().analyse(screenShot);
                this.formerIdentifier = identifier;
            }
            coverage = this.formerCoverage;
        }

        // calculate offset by running the detector
        calculatedTarget = detector.analyse(screenShot);
        calculatedTarget.translate(screenShot.getHeight() / 2, screenShot.getWidth() / 2);
        Point translatedRelated = new Point(relatedPoint.x - fixation.x + screenShot.getHeight() / 2, relatedPoint.y - fixation.y + screenShot.getWidth() / 2);

        // write the png-file
        if (this.main.writeImages())
            this.drawPicture(detector, calculatedTarget, path + "/evaluated/Session_" + this.currentTimeStamp + "/" + this.formerIdentifier + "_evaluated.png", screenShot, translatedRelated);

        // add results to over all storage
        if (this.results == null) this.results = new EvaluationContainer(detector.getInformation().getId(), calculatedTarget.distance(translatedRelated), coverage, this.main.getCoverageThreshold(), path);
        else
            this.results.add(detector.getInformation().getId(), calculatedTarget.distance(translatedRelated), coverage, path);
    }

    /**
     * writes result files
     * 
     * @param detectors
     * 
     * @return locations where result files are written to
     */
    public String writeResults(ArrayList<SaliencyDetector> detectors) {
        // initialize variables
        String locations = "";

        // test if some data are collected
        if (this.results == null) return "...nowhere";
        if (this.results.getSize(EvaluationResultType.OVERALL) == 0) return "...nowhere";

        // test if resultfiles should be written#
        if (!this.main.writeLog()) return "...nowhere";

        for (String path : this.results.getLogPath()) {

            // build path
            path = path + "/evaluated/Session_" + this.currentTimeStamp + "/" + System.currentTimeMillis() + "_evaluated.xls";

            try {
                // initialize xls-stuff
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                WritableWorkbook workbook;

                // initialize file
                workbook = Workbook.createWorkbook(new File(path), wbSettings);
                workbook.createSheet("Evaluation", 0);
                WritableSheet excelSheet = workbook.getSheet(0);

                // add captions
                excelSheet.addCell(new Label(1, 0, "Overview", this.format));
                excelSheet.addCell(new Label(0, 2, "different locations", this.format));
                excelSheet.addCell(new Label(0, 3, "datasets overall", this.format));
                excelSheet.addCell(new Label(0, 4, "threshold in %", this.format));
                excelSheet.addCell(new Label(0, 5, "datasets, higher than", this.format));
                excelSheet.addCell(new Label(0, 6, "datasets, lower than", this.format));
                excelSheet.addCell(new Label(1, 9, "Results", this.format));
                excelSheet.addCell(new Label(0, 11, "detector", this.format));
                excelSheet.addCell(new Label(2, 11, "averaged distance", this.format));
                excelSheet.addCell(new Label(1, 12, "overall", this.format));
                excelSheet.addCell(new Label(2, 12, "higher than", this.format));
                excelSheet.addCell(new Label(3, 12, "lower than", this.format));
                excelSheet.addCell(new Label(5, 11, "median", this.format));
                excelSheet.addCell(new Label(4, 12, "overall", this.format));
                excelSheet.addCell(new Label(5, 12, "higher than", this.format));
                excelSheet.addCell(new Label(6, 12, "lower than", this.format));
                excelSheet.addCell(new Label(8, 11, "unit variance", this.format));
                excelSheet.addCell(new Label(7, 12, "overall", this.format));
                excelSheet.addCell(new Label(8, 12, "higher than", this.format));
                excelSheet.addCell(new Label(9, 12, "lower than", this.format));
                excelSheet.addCell(new Label(11, 11, "standard deviation", this.format));
                excelSheet.addCell(new Label(10, 12, "overall", this.format));
                excelSheet.addCell(new Label(11, 12, "higher than", this.format));
                excelSheet.addCell(new Label(12, 12, "lower than", this.format));

                // write head values
                excelSheet.addCell(new Number(1, 2, this.results.getLogPath().size(), this.format));
                excelSheet.addCell(new Number(1, 3, this.results.getSize(EvaluationResultType.OVERALL), this.format));
                excelSheet.addCell(new Number(1, 4, this.main.getCoverageThreshold(), this.format));
                excelSheet.addCell(new Number(1, 5, this.results.getSize(EvaluationResultType.HIGHER_THAN_THRESHOLD), this.format));
                excelSheet.addCell(new Number(1, 6, this.results.getSize(EvaluationResultType.LOWER_THEN_THRESHOLD), this.format));

                // run through detectors
                for (int i = 0; i < detectors.size(); i++) {
                    excelSheet.addCell(new Label(0, 13 + i, detectors.get(i).getInformation().getDisplayName(), this.format));
                    excelSheet.addCell(new Number(1, 13 + i, this.results.getAveragedDistance(detectors.get(i).getInformation().getId(), EvaluationResultType.OVERALL), this.format));
                    excelSheet.addCell(new Number(2, 13 + i, this.results.getAveragedDistance(detectors.get(i).getInformation().getId(), EvaluationResultType.HIGHER_THAN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(3, 13 + i, this.results.getAveragedDistance(detectors.get(i).getInformation().getId(), EvaluationResultType.LOWER_THEN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(4, 13 + i, this.results.getMedian(detectors.get(i).getInformation().getId(), EvaluationResultType.OVERALL), this.format));
                    excelSheet.addCell(new Number(5, 13 + i, this.results.getMedian(detectors.get(i).getInformation().getId(), EvaluationResultType.HIGHER_THAN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(6, 13 + i, this.results.getMedian(detectors.get(i).getInformation().getId(), EvaluationResultType.LOWER_THEN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(7, 13 + i, this.results.getUnitVariance(detectors.get(i).getInformation().getId(), EvaluationResultType.OVERALL), this.format));
                    excelSheet.addCell(new Number(8, 13 + i, this.results.getUnitVariance(detectors.get(i).getInformation().getId(), EvaluationResultType.HIGHER_THAN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(9, 13 + i, this.results.getUnitVariance(detectors.get(i).getInformation().getId(), EvaluationResultType.LOWER_THEN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(10, 13 + i, this.results.getStandardDeviation(detectors.get(i).getInformation().getId(), EvaluationResultType.OVERALL), this.format));
                    excelSheet.addCell(new Number(11, 13 + i, this.results.getStandardDeviation(detectors.get(i).getInformation().getId(), EvaluationResultType.HIGHER_THAN_THRESHOLD), this.format));
                    excelSheet.addCell(new Number(12, 13 + i, this.results.getStandardDeviation(detectors.get(i).getInformation().getId(), EvaluationResultType.LOWER_THEN_THRESHOLD), this.format));
                }

                // write and close
                workbook.write();
                workbook.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // update progress bar
            this.main.updateProgressBar();
        }

        // build location string
        if (this.results.getLogPath().size() > 1) {
            for (int i = 0; i < this.results.getLogPath().size() - 3; i++) {
                locations = locations + this.results.getLogPath().get(i) + "evaluated" + File.separator + "Session_" + this.currentTimeStamp + ", ";
            }
            locations = locations + this.results.getLogPath().get(this.results.getLogPath().size() - 2) + "evaluated" + File.separator + "Session_" + this.currentTimeStamp + " and " + this.results.getLogPath().get(this.results.getLogPath().size() - 1) + "evaluated" + File.separator + "Session_" + this.currentTimeStamp;
        } else {
            locations = this.results.getLogPath().get(0) + "evaluated" + File.separator + "Session_" + this.currentTimeStamp;
        }

        // return locations
        return locations;
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

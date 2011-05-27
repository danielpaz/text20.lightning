/*
 * EvaluationThread.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.worker;

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.DetectorEvaluator;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.textdetector.TextDetectorProperties;

/**
 * This thread runs through all given files and detectors and evaluates them.
 * 
 * @author Christoph Käding
 *
 */
public class EvaluationThread implements Runnable {

    /** contains all needed data */
    private DataPackage container;

    /** indicates if the thread should be stopped */
    private boolean stop;

    /** text detector */
    private SaliencyDetector detector;

    /** */
    private TextDetectorProperties properties;

    /**
     * initializes necessary variables
     * 
     * @param data 
     * @param textDetector 
     */
    public void init(DataPackage data, SaliencyDetector textDetector) {
        this.container = data;
        this.detector = textDetector;
    }

    /**
     * stops the processing
     */
    public void stop() {
        this.stop = true;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings({ "static-access", "boxing" })
    @Override
    public void run() {
        // initialize Variables
        DataXMLParser dataParser = new DataXMLParser();
        BufferedImage screenShot;
        BufferedImage subImage;
        Point target;
        Point offset;
        int pictureCount = 0;
        int rectangleCount = 0;
        int fixationCount = 0;
        int type = -1;
        int hit;
        int offsetX;
        int offsetY;
        Point translatedRelated;
        Rectangle translatedRectangle;
        double distance;
        String input;
        this.stop = false;

        // create directories
        new File("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp()).mkdirs();

        // write key file
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- headings -\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("type, image, rectangle, fixation, coverage, height, width, sensitivity, line, distance, hit, offsetX, offsetY\r\n\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- filename -\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("type_image_rectangle_fixation_coverage_height_width_sensitivity_line.png\r\n\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- values -\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("type: Text = 0, Code = 1, Icons = 2, Undefined = 3\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("hit: hit = 1, miss = 0\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("offset: left/top < 0, right/bottom >0\r\n\r\n");
        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- files -\r\n");
        
        System.out.println();
        System.out.println();

        // set start timestamp
        DetectorEvaluator.getInstance().setStartTimeStamp(System.currentTimeMillis());

        // run through every file ...
        for (File file : this.container.getFiles()) {

            System.out.println("- File " + file.getName() + " is the next one.");
            
            $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("index: " + pictureCount + ", name: " + file.getName() + "\r\n");

            // set type
            if (file.getName().contains("_Text_")) {
                type = 0;
            } else if (file.getName().contains("_Code_")) {
                type = 1;
            } else if (file.getName().contains("_Icons_")) {
                type = 2;
            } else {
                type = 3;
            }

            // read placed targets
            for (Rectangle rectangle : dataParser.readFile(file)) {

                // read screenshot
                screenShot = dataParser.getRelatedImage(file);

                // reste fixations
                fixationCount = 0;

                // create target
                target = new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

                // run through all calculated fixations
                for (Point fixation : this.calculateFixations(screenShot, target)) {

                    // transform screenshot
                    try {
                        subImage = new BufferedImage(screenShot.getWidth() + this.container.getDimension(), screenShot.getHeight() + this.container.getDimension(), screenShot.getType());
                        Graphics2D graphics = subImage.createGraphics();
                        graphics.drawImage(screenShot, this.container.getDimension() / 2, this.container.getDimension() / 2, null);
                        subImage = subImage.getSubimage(fixation.x, fixation.y, this.container.getDimension(), this.container.getDimension());
                    } catch (RasterFormatException e) {
                        e.printStackTrace();
                        return;
                    }

                    // run through parameters
                    for (int coverage = this.container.getCoverageMin(); coverage <= this.container.getCoverageMax(); coverage++) {
                        for (int height = this.container.getHeightMin(); height <= this.container.getHeightMax(); height++) {
                            for (int width = this.container.getWidthMin(); width <= this.container.getWidthMax(); width++) {
                                for (double sensitivity = this.container.getSensitivityMin(); sensitivity <= this.container.getSensitivityMax(); sensitivity = sensitivity + 0.1) {
                                    for (int line = this.container.getLineMin(); line <= this.container.getLineMax(); line++) {

                                        // wtf why?
                                        sensitivity = (double) Math.round(sensitivity * 10) / 10;

                                        // set values
                                        this.properties.getInstance().setThreshold(coverage);
                                        this.properties.getInstance().setLetterHeight(height);
                                        this.properties.getInstance().setLetterWidth(width);
                                        this.properties.getInstance().setSensitivity(sensitivity);
                                        this.properties.getInstance().setLineSize(line);

                                        // start detector        
                                        this.detector.start();

                                        // calculate offset
                                        offset = this.detector.analyse(subImage);
                                        offset.translate(subImage.getHeight() / 2, subImage.getWidth() / 2);

                                        // translate
                                        translatedRelated = new Point(target.x - fixation.x + subImage.getHeight() / 2, target.y - fixation.y + subImage.getWidth() / 2);
                                        translatedRectangle = new Rectangle(translatedRelated.x - rectangle.width / 2, translatedRelated.y - rectangle.height / 2, rectangle.width, rectangle.height);

                                        // calculate distance
                                        distance = offset.distance(translatedRelated);

                                        // calculate x offset
                                        if (offset.x < translatedRectangle.x) {
                                            offsetX = translatedRectangle.x - translatedRectangle.x;
                                        } else if (offset.x > (translatedRectangle.x + translatedRectangle.width)) {
                                            offsetX = offset.x - (translatedRectangle.x + translatedRectangle.width);
                                        } else {
                                            offsetX = 0;
                                        }

                                        // calculate y offset
                                        if (offset.y < translatedRectangle.y) {
                                            offsetY = offset.y - translatedRectangle.y;
                                        } else if (offset.y > (translatedRectangle.y + translatedRectangle.height)) {
                                            offsetY = offset.y - (translatedRectangle.y + translatedRectangle.height);
                                        } else {
                                            offsetY = 0;
                                        }

                                        // calculate hit
                                        if ((offsetX == 0) && (offsetY == 0)) {
                                            hit = 1;
                                        } else {
                                            hit = 0;
                                        }

                                        // update file
                                        input = $(type, pictureCount, rectangleCount, fixationCount, coverage, height, width, sensitivity, line, distance, hit, offsetX, offsetY).string().join(",");
                                        $("./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluation.txt").file().append(input + "\r\n");

                                        // draw image
                                        if (this.container.isDrawImages())
                                            this.drawPicture(offset, "./evaluation/text detector evaluation/Session_" + this.container.getTimestamp() + "/" + type + "_" + pictureCount + "_" + rectangleCount + "_" + fixationCount + "_" + coverage + "_" + height + "_" + width + "_" + sensitivity + "_" + line + ".png", subImage, translatedRectangle);

                                        // check if should continue
                                        if (this.stop) return;

                                        // check for big steps
                                        if (this.container.isBigSteps()) line = line + 9;

                                        // update progress bar
                                        DetectorEvaluator.getInstance().updateProgressBar();
                                    }

                                    // check for big steps
                                    if (this.container.isBigSteps())
                                        sensitivity = sensitivity + 0.9;
                                }

                                // check for big steps
                                if (this.container.isBigSteps()) width = width + 9;
                            }

                            // check for big steps
                            if (this.container.isBigSteps()) height = height + 9;
                        }

                        // check for big steps
                        if (this.container.isBigSteps()) coverage = coverage + 9;
                    }

                    // step forward
                    fixationCount++;
                }

                // step forward
                rectangleCount++;
            }

            // step forward
            pictureCount++;

            System.out.println("- File " + file.getName() + " finished.\r\n");
        }

        // finish the evaluation
        DetectorEvaluator.getInstance().finish();
    }

    /**
     * calculates randomized fixations
     * 
     * @param image
     * @param point
     * @return list of fixations
     */
    private ArrayList<Point> calculateFixations(BufferedImage image, Point point) {
        // initialize variables
        int deviation = 20;
        int x;
        int y;
        ArrayList<Point> calculatedFixations = new ArrayList<Point>();
        Random random = new Random();

        // calculate fixations
        for (int i = 0; i < this.container.getAmount(); i++) {
            x = (int) (point.x + (random.nextGaussian() * deviation));
            x = Math.max(point.x - this.container.getDimension() / 2, Math.min(point.x + this.container.getDimension() / 2, x));
            y = (int) (point.y + (random.nextGaussian() * deviation));
            y = Math.max(point.y - this.container.getDimension() / 2, Math.min(point.y + this.container.getDimension() / 2, y));
            calculatedFixations.add(new Point(x, y));
        }

        // return list
        return calculatedFixations;
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
     * @param picture
     *            screenshot where the data will be written in, maybe it will be
     *            not used when the screenshot is already drawn to a file
     * @param relatedRectangle
     *            target that was given or was pointed by the mouse
     */
    private void drawPicture(Point point, String path, BufferedImage picture,
                             Rectangle relatedRectangle) {
        // initialize variables
        int dimension = picture.getHeight();
        File file = new File(path);
        BufferedImage screenShot = new BufferedImage(picture.getWidth(), picture.getHeight(), picture.getType());
        Graphics2D graphics = screenShot.createGraphics();
        graphics.drawImage(picture, 0, 0, null);

        try {
            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            // visualize fixation point
            graphic.setColor(new Color(255, 255, 0, 255));
            graphic.drawOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);
            graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + dimension / 2, 12 + dimension / 2);
            graphic.setColor(new Color(255, 255, 0, 32));
            graphic.fillOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);

            // visualize related point
            graphic.setColor(new Color(255, 0, 0, 255));
            graphic.drawRect(relatedRectangle.x, relatedRectangle.y, relatedRectangle.width, relatedRectangle.height);
            graphic.setColor(new Color(255, 0, 0, 32));
            graphic.fillRect(relatedRectangle.x, relatedRectangle.y, relatedRectangle.width, relatedRectangle.height);

            // visualize calculations
            graphic.setColor(new Color(0, 0, 255, 255));
            graphic.drawOval(point.x - 5, point.y - 5, 10, 10);
            graphic.drawChars(this.detector.getInformation().getDisplayName().toCharArray(), 0, this.detector.getInformation().getDisplayName().toCharArray().length, point.x + 12, point.y + 12);
            graphic.setColor(new Color(0, 0, 255, 32));
            graphic.fillOval(point.x - 5, point.y - 5, 10, 10);

            // write the image
            file.mkdirs();
            ImageIO.write(screenShot, "png", file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

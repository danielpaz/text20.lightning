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

import net.jcores.interfaces.functions.F1;
import net.jcores.options.Option;
import net.jcores.options.OptionIndexer;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.textdetectorevaluator.DetectorEvaluator;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * This thread runs through all given files and detectors and evaluates them.
 * 
 * @author Christoph Käding
 *
 */
public class EvaluationThread implements Runnable {

    /** contains all needed data */
    private DataPackage container;

    /** text detector */
    private SaliencyDetector detector;

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

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings({ "static-access", "boxing" })
    @Override
    public void run() {
        // initialize Variables
        final DataXMLParser dataParser = new DataXMLParser();
        int _pictureCount = 0;
        int _type = -1;
        
        // start detector
        this.detector.start();

        // create directories
        new File("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp()).mkdirs();

        // write key file
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- headings -\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("type, image, rectangle, fixation, coverage, height, width, sensitivity, line, distance, hit, offsetX, offsetY\r\n\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- filename -\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("type_image_rectangle_fixation_coverage_height_width_sensitivity_line.png\r\n\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- values -\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("type: Text = 0, Code = 1, Icons = 2, Undefined = 3\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("hit: hit = 1, miss = 0\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("offset: left/top < 0, right/bottom >0\r\n\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- dataset - \r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("files: " + this.container.getFiles().size() + "\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("amount of synthetic fixations per rectangle: " + this.container.getAmount() + "\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("datasets overall: " + this.container.getSize() + "\r\n\r\n");
        $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("- files -\r\n");

        System.out.println();
        System.out.println();

        // set start timestamp
        DetectorEvaluator.getInstance().setStartTimeStamp(System.currentTimeMillis());

        // run through every file ...
        for (final File file : this.container.getFiles()) {

            System.out.println("- File " + file.getName() + " is the next one.");

            $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp() + "/TextDetectorEvaluationKeys.log").file().append("index: " + _pictureCount + ", name: " + file.getName() + "\r\n");

            // set type
            if (file.getName().contains("_Text_")) {
                _type = 0;
            } else if (file.getName().contains("_Code_")) {
                _type = 1;
            } else if (file.getName().contains("_Icons_")) {
                _type = 2;
            } else {
                _type = 3;
            }

            final int pictureCount = _pictureCount;
            final int type = _type;

            final OptionIndexer i = Option.INDEXER();
            String output = $(dataParser.readFile(file)).map(new F1<Rectangle, String[]>() {

                @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
                public String[] f(Rectangle rectangle) {
                    Point offset;
                    int hit;
                    int offsetX;
                    int offsetY;
                    Point translatedRelated;
                    Rectangle translatedRectangle;
                    double distance;
                    String input;
                    ArrayList<String> rval = new ArrayList<String>();

                    // read screenshot
                    BufferedImage screenShot = dataParser.getRelatedImage(file);
                    BufferedImage subImage;

                    // reste fixations
                    int fixationCount = 0;

                    // create target
                    Point target = new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

                    // run through all calculated fixations
                    for (Point fixation : calculateFixations(screenShot, target)) {

                        // transform screenshot
                        try {
                            subImage = new BufferedImage(screenShot.getWidth() + container.getDimension(), screenShot.getHeight() + container.getDimension(), screenShot.getType());
                            Graphics2D graphics = subImage.createGraphics();
                            graphics.drawImage(screenShot, container.getDimension() / 2, container.getDimension() / 2, null);
                            subImage = subImage.getSubimage(fixation.x, fixation.y, container.getDimension(), container.getDimension());

                        } catch (RasterFormatException e) {
                            e.printStackTrace();
                            return $(rval).array(String.class);
                        }

                        // run through parameters
                        for (int coverage = container.getCoverageMin(); coverage <= container.getCoverageMax(); coverage++) {
                            for (int height = container.getHeightMin(); height <= container.getHeightMax(); height++) {
                                for (int width = container.getWidthMin(); width <= container.getWidthMax(); width++) {
                                    for (double sensitivity = container.getSensitivityMin(); sensitivity <= container.getSensitivityMax(); sensitivity = sensitivity + 0.1) {
                                        for (int line = container.getLineMin(); line <= container.getLineMax(); line++) {

                                            // wtf why?
                                            sensitivity = (double) Math.round(sensitivity * 10) / 10;

                                            // set values
                                            String[] options = { "TDthreshold=" + coverage, "TDheight=" + height, "TDwidth=" + width, "TDsensitivity=" + sensitivity, "TDline=" + line };

                                            // calculate offset
                                            offset = detector.analyse(subImage, options);
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
                                            input = $(type, pictureCount, i.i(), fixationCount, coverage, height, width, sensitivity, line, distance, hit, offsetX, offsetY).string().join(",");
                                            rval.add(input);
                                            
                                            // draw image
                                            if (container.isDrawImages())
                                                drawPicture(offset, "./evaluation/results/text detector evaluation/Session_" + container.getTimestamp() + "/" + type + "_" + pictureCount + "_" + i.i() + "_" + fixationCount + "_" + coverage + "_" + height + "_" + width + "_" + sensitivity + "_" + line + ".png", subImage, translatedRectangle);

                                            // check if should continue
                                            if (!DetectorEvaluator.getInstance().isRunning())
                                                return $(rval).array(String.class);

                                            // check for big steps
                                            if (container.isBigSteps()) line = line + 9;

                                            // update progress bar
                                            DetectorEvaluator.getInstance().updateProgressBar();
                                        }

                                        // check for big steps
                                        if (container.isBigSteps())
                                            sensitivity = sensitivity + 0.9;
                                    }

                                    // check for big steps
                                    if (container.isBigSteps()) width = width + 9;
                                }

                                // check for big steps
                                if (container.isBigSteps()) height = height + 9;
                            }

                            // check for big steps
                            if (container.isBigSteps()) coverage = coverage + 9;
                        }

                        // step forward
                        fixationCount++;
                    }

                    return $(rval).array(String.class);
                }
            }, i).expand(String.class).string().join("\r\n");

            $("./evaluation/results/text detector evaluation/Session_" + this.container.getTimestamp()  + "/DetectorEvaluation.txt").file().append(output + "\r\n");

            if (!DetectorEvaluator.getInstance().isRunning()) return;

            // step forward
            _pictureCount++;

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
        int deviation = 25;
        int x;
        int y;
        int tmp;
        ArrayList<Point> calculatedFixations = new ArrayList<Point>();
        Random random = new Random();

        // calculate fixations
        for (int i = 0; i < this.container.getAmount(); i++) {
            // reset tmp
            tmp = Integer.MAX_VALUE;

            // calculate x
            while (Math.abs(tmp) > deviation)
                tmp = (int) random.nextGaussian() * deviation;
            x = point.x + tmp;
            x = Math.max(1, Math.min(image.getWidth() - 1, x));

            // reset tmp
            tmp = Integer.MAX_VALUE;

            // calculate y
            while (Math.abs(tmp) > deviation)
                tmp = (int) random.nextGaussian() * deviation;
            y = point.y + tmp;
            y = Math.max(1, Math.min(image.getHeight() - 1, y));
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

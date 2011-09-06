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
package de.dfki.km.text20.lightning.components.evaluationmode.precision.evaluator.worker;

import static net.jcores.jre.CoreKeeper.$;

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

import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.Indexer;
import de.dfki.km.text20.lightning.components.evaluationmode.precision.evaluator.EvaluatorMain;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * This thread runs through all given files and detectors and evaluates them.
 * 
 * @author Christoph Käding
 * 
 */
public class EvaluationThread implements Runnable {

    /** list of the from listDetectors selected detectors */
    private ArrayList<SaliencyDetector> selectedDetectors;

    /** selected *.xml files */
    private ArrayList<File> files;

    /** given main class */
    private EvaluatorMain mainClass;

    /** */
    private int dimension;

    /** */
    private boolean drawImages;

    /** */
    private long timestamp;

    /** */
    private int exceptionCounter;

    /**
     * initializes necessary variables
     * 
     * @param main
     */
    public void init(EvaluatorMain main) {
        this.mainClass = main;
        this.files = main.getFiles();
        this.selectedDetectors = main.getSelectedDetectors();
        this.dimension = main.getDimension();
        this.drawImages = main.writeImages();
        this.timestamp = main.getCurrentTimeStamp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings({ "boxing", "unqualified-field-access" })
    @Override
    public void run() {
        // initialize Variables
        final DataXMLParser dataParser = new DataXMLParser();
        int _pictureCount = 0;
        int _type = -1;

        // create directories
        new File("./evaluation/results/detector evaluation/Session_" + this.timestamp).mkdirs();

        // write key file
        this.addToFile("- options -");
        this.addToFile("dimension: " + this.mainClass.getDimension());
        this.addToFile("amount of synthetic fixations per rectangle: " + this.mainClass.getAmount() + "\r\n");
        this.addToFile("- headings -");
        this.addToFile("type, image, rectangle, fixation, detector, distance, hit, offsetX, offsetY\r\n");
        this.addToFile("- filename -");
        this.addToFile("type_image_rectangle_fixation.png\r\n");
        this.addToFile("- values -");
        this.addToFile("type: Text = 0, Code = 1, Icons = 2, Undefined = 3");
        this.addToFile("hit: hit = 1, miss = 0");
        this.addToFile("offset: left/top < 0, right/bottom >0\r\n");
        this.addToFile("- dataset -");
        this.addToFile("datasets overall: " + this.mainClass.getSize() + "\r\n");
        this.addToFile("- detectors -");

        // start all detectors and updfate keyfile
        for (int i = 0; i < this.selectedDetectors.size(); i++) {
            this.selectedDetectors.get(i).start();
            this.addToFile("index: " + i + ", name: " + this.selectedDetectors.get(i).getInformation().getDisplayName());
        }
        this.addToFile("\r\n- files -");

        System.out.println();
        System.out.println();

        // set start timestamp
        this.mainClass.setStartTimeStamp(System.currentTimeMillis());

        // run through every file ...
        for (final File file : this.files) {

            System.out.println("- File " + file.getName() + " is the next one.");

            this.addToFile("index: " + _pictureCount + ", name: " + file.getName());

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

            final Indexer i = Indexer.NEW();
            String output = $(dataParser.readFile(file)).map(new F1<Rectangle, String[]>() {

                @SuppressWarnings("synthetic-access")
                public String[] f(Rectangle rectangle) {
                    Point offset;
                    int detectorCount = 0;
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
                            subImage = new BufferedImage(screenShot.getWidth() + dimension, screenShot.getHeight() + dimension, screenShot.getType());
                            Graphics2D graphics = subImage.createGraphics();
                            graphics.drawImage(screenShot, dimension / 2, dimension / 2, null);
                            subImage = subImage.getSubimage(fixation.x, fixation.y, dimension, dimension);

                        } catch (RasterFormatException e) {
                            e.printStackTrace();
                            return $(rval).array(String.class);
                        }

                        // reset detector
                        detectorCount = 0;

                        // ... and every detector
                        for (SaliencyDetector detector : selectedDetectors) {

                            // calculate offset
                            offset = detector.analyse(subImage);
                            offset.translate(subImage.getHeight() / 2, subImage.getWidth() / 2);

                            // translate
                            translatedRelated = new Point(target.x - fixation.x + subImage.getHeight() / 2, target.y - fixation.y + subImage.getWidth() / 2);
                            translatedRectangle = new Rectangle(translatedRelated.x - rectangle.width / 2, translatedRelated.y - rectangle.height / 2, rectangle.width, rectangle.height);

                            // calculate distance
                            distance = offset.distance(translatedRelated);

                            // calculate x offset
                            if (offset.x < translatedRectangle.x) {
                                offsetX = offset.x - translatedRectangle.x;
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
                            input = $(type, pictureCount, i.i(), fixationCount, detectorCount, distance, hit, offsetX, offsetY).string().join(",");
                            rval.add(input);

                            // draw image
                            if (drawImages)
                                drawPicture(detector, offset, "./evaluation/results/detector evaluation/Session_" + timestamp + "/" + type + "_" + pictureCount + "_" + i.i() + "_" + fixationCount + ".png", subImage, translatedRectangle);

                            // check if should continue
                            if (!mainClass.isRunning())
                                return $(rval).array(String.class);

                            // update progress bar
                            mainClass.updateProgressBar();

                            // next detector
                            detectorCount++;
                        }

                        // step forward
                        fixationCount++;
                    }

                    return $(rval).array(String.class);
                }
            }, i).expand(String.class).string().join("\r\n");

            $("./evaluation/results/detector evaluation/Session_" + timestamp + "/DetectorEvaluationData_raw.txt").file().append(output + "\r\n");

            if (!this.mainClass.isRunning()) return;

            // step forward
            _pictureCount++;

            System.out.println("- File " + file.getName() + " finished.\r\n");
        }

        // stop all detectors
        for (SaliencyDetector detector : this.selectedDetectors)
            detector.stop();

        // run evaluator
        new LogEvaluator().evaluateLog("./evaluation/results/detector evaluation/Session_" + timestamp + "/DetectorEvaluationData_raw.txt");

        // finish the evaluation
        this.mainClass.finish();
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
        int deviation = 5;
        int deviationMax = 25;
        int x;
        int y;
        int tmp;
        ArrayList<Point> calculatedFixations = new ArrayList<Point>();
        Random random = new Random();

        // calculate fixations
        for (int i = 0; i < this.mainClass.getAmount(); i++) {
            // reset tmp
            tmp = Integer.MAX_VALUE;

            // calculate x
            while (Math.abs(tmp) > deviationMax)
                tmp = (int) (random.nextGaussian() * 2 * deviation);
            x = point.x + tmp;
            x = Math.max(1, Math.min(image.getWidth() - 1, x));

            // reset tmp
            tmp = Integer.MAX_VALUE;

            // calculate y
            while (Math.abs(tmp) > deviationMax)
                tmp = (int) (random.nextGaussian() * 2 * deviation);
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
     * used to get the name
     * @param point
     * where the detector recognized a target
     * @param path
     * where the image will be written
     * @param image
     * screenshot where the data will be written in, maybe it will be
     * not used when the screenshot is already drawn to a file
     * @param relatedRectangle
     * target that was given or was pointed by the mouse
     */
    private void drawPicture(SaliencyDetector detector, Point point, String path,
                             BufferedImage image, Rectangle relatedRectangle) {
        // initialize variables
        int color = (int) (Math.random() * 255);
        File file = new File(path);
        BufferedImage screenShot;
        boolean alreadyExists = file.exists();

        try {
            // if the screenshot file already exists, the given screenshot is
            // overwritten by the existing one to update new data
            if (alreadyExists) {
                screenShot = ImageIO.read(file);
            } else {
                screenShot = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
                Graphics2D graphics = screenShot.createGraphics();
                graphics.drawImage(image, 0, 0, null);
            }

            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            if (!alreadyExists) {
                // visualize fixation point
                graphic.setColor(new Color(255, 255, 0, 255));
                graphic.drawOval(this.dimension / 2 - 5, this.dimension / 2 - 5, 10, 10);
                graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + this.dimension / 2, 12 + this.dimension / 2);
                graphic.setColor(new Color(255, 255, 0, 32));
                graphic.fillOval(this.dimension / 2 - 5, this.dimension / 2 - 5, 10, 10);

                // visualize related point
                graphic.setColor(new Color(255, 0, 0, 255));
                graphic.drawRect(relatedRectangle.x, relatedRectangle.y, relatedRectangle.width, relatedRectangle.height);
                graphic.setColor(new Color(255, 0, 0, 32));
                graphic.fillRect(relatedRectangle.x, relatedRectangle.y, relatedRectangle.width, relatedRectangle.height);
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

            // increase exception count
            this.exceptionCounter++;

            // indicate exception
            System.out.println("exception number " + this.exceptionCounter + ": " + e.toString());

            // test if deadlock
            if (this.exceptionCounter >= 10) return;

            // wait to give the os some time to handle files
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            // retry
            this.drawPicture(detector, point, path, image, relatedRectangle);
        }
    }

    /**
     * adds given string to file
     * 
     * @param input
     */
    private void addToFile(String input) {
        $("./evaluation/results/detector evaluation/Session_" + this.timestamp + "/DetectorEvaluationKeys_raw.log").file().append(input + "\r\n");
    }
}

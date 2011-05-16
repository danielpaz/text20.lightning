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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

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

    /** evaluation worker which runs the detectors */
    private EvaluatorWorker worker;

    /** selected *.xml files */
    private ArrayList<File> files;

    /** given main class */
    private EvaluatorMain mainClass;

    /** indicates if the thread should be stopped */
    private boolean stop;

    /**
     * initializes necessary variables
     * 
     * @param main
     */
    public void init(EvaluatorMain main) {
        this.mainClass = main;
        this.files = main.getFiles();
        this.selectedDetectors = main.getSelectedDetectors();
        this.worker = main.getWorker();
        this.stop = false;
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
    @Override
    public void run() {
        // initialize Variables
        DataXMLParser dataParser = new DataXMLParser();
        BufferedImage screenshot;
        long timestamp = 0;

        // start all detectors
        for (SaliencyDetector detector : this.selectedDetectors)
            detector.start();

        System.out.println();
        System.out.println();

        // run through every file ...
        for (File file : this.files) {

            System.out.println("- File " + file.getName() + " is the next one.");

            // read placed targets
            for (Point data : dataParser.readFile(file)) {

                // read screenshot
                screenshot = dataParser.getRelatedImage(file);

                // run through all calculated fixations
                for (Point fixation : this.calculateFixations(screenshot, data)) {

                    timestamp = System.currentTimeMillis();

                    // ... and every detector
                    for (SaliencyDetector detector : this.selectedDetectors) {

                        System.out.println("- Detector: " + detector.getInformation().getDisplayName());

                        // process evaluation
                        this.worker.evaluate(timestamp, detector, screenshot, fixation, data, file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator) + 1));

                        // stops the processing if needed
                        if (this.stop) return;

                        // update progress bar
                        this.mainClass.updateProgressBar();
                    }
                }
            }

            System.out.println("- File " + file.getName() + " finished.\r\n");
        }

        // stop all detectors
        for (SaliencyDetector detector : this.selectedDetectors)
            detector.stop();

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
        int derivation = 10;
        int x;
        int y;
        ArrayList<Point> calculatedFixations = new ArrayList<Point>();
        Random random = new Random();

        // calculate fixations
        for (int i = 0; i < this.mainClass.getAmount(); i++) {
            x = (int) (point.x + (random.nextGaussian() * derivation));
            x = Math.max(point.x - this.mainClass.getDimension() / 2, Math.min(point.x + this.mainClass.getDimension() / 2, x));
            y = (int) (point.y + (random.nextGaussian() * derivation));
            y = Math.max(point.y - this.mainClass.getDimension() / 2, Math.min(point.y + this.mainClass.getDimension() / 2, y));
            calculatedFixations.add(new Point(x, y));
        }

        // return list
        return calculatedFixations;
    }
}

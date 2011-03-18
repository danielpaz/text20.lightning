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
package de.dfki.km.text20.lightning.evaluator.worker;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JProgressBar;

import de.dfki.km.text20.lightning.evaluator.EvaluatorMain;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.worker.training.StorageContainer;

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

    /** selected *.training files */
    private ArrayList<File> files;

    /** the current shown progressbar */
    private JProgressBar progressBar;

    /** given main class */
    private EvaluatorMain mainClass;
    
    /** indicates if the thread should be stopped */
    private boolean stop;
    
    /** current used dimension */
    private int dimension;

    /**
     * initializes necessary variables
     * 
     * @param main
     */
    public void init(EvaluatorMain main) {
        this.mainClass = main;
        this.files = main.getFiles();
        this.selectedDetectors = main.getSelectedDetectors();
        this.progressBar = main.getProgressBar();
        this.worker = main.getWorker();
        this.stop = false;
        this.dimension = main.getDimension();
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
        XMLParser parser = new XMLParser();
        String user = "";
        int i = 1;
        
        // run through every file ...
        for (File file : this.files) {

            // create name from filename 
            user = file.getName().substring(0, file.getName().lastIndexOf("_"));

            // ... and through every container in it ...
            for (StorageContainer container : parser.readFile(file, this.dimension)) {

                // ... and every detector
                for (SaliencyDetector detector : this.selectedDetectors) {
                    
                    // process evaluation
                    this.worker.evaluate(file.getName(), user, detector, container,file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator + "data" + File.separator)));

                    // stops the processing if needed
                    if(this.stop) return;

                    // update progress bar
                    this.progressBar.setValue(i++);
                    this.progressBar.paint(this.progressBar.getGraphics());
                }
            }
        }
        
        // finish the evaluation
        this.mainClass.finish();
    }
}

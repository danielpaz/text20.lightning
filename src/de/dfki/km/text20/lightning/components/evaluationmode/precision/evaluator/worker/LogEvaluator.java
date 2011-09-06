/*
 * LogEvaluator.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * @author Christoph Käding
 */
public class LogEvaluator {

    /** singleton instance */
    private static LogEvaluator main;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("LogConverter started...");
        System.out.println();

        // run through all files
        for (File file : new File("evaluation/results/detector evaluation").listFiles()) {
            
            // indicate file
            System.out.println("next file: " + file.getAbsolutePath());
            System.out.println();

            // build path
            String path = file.getAbsolutePath();

            // check if file exists
            if (!(new File(path + "/DetectorEvaluationData_raw.txt").exists())) continue;

            LogEvaluator.getInstance().evaluateLog(path + "/DetectorEvaluationData_raw.txt");
        }
        
        // inidicate finish
        System.out.println();
        System.out.println("Done.");
    }

    /**
     * @return instance
     */
    private static LogEvaluator getInstance() {
        if (main == null) main = new LogEvaluator();
        return main;
    }

    /**
     * @param path
     */
    public void evaluateLog(String path) {

        try {
            $(path.replace("DetectorEvaluationData_raw.txt", "DetectorEvaluationData_evaluated.txt")).file().delete();
            $(path.replace("DetectorEvaluationData_raw.txt", "DetectorEvaluationKeys_evaluated.log")).file().delete();
            $(path.replace("DetectorEvaluationData_raw.txt", "DetectorEvaluationKeys_evaluated.log")).file().append("- headings -\r\n");
            $(path.replace("DetectorEvaluationData_raw.txt", "DetectorEvaluationKeys_evaluated.log")).file().append("coverage, height, width, sensitivity, line, hits, overall, percentage");

            // initialize reader
            String[] line = null;
            String currentLine;
            int hit;
            HashMap<String, long[]> values = new HashMap<String, long[]>();
            BufferedReader fileReader = new BufferedReader(new FileReader(new File(path)));

            // read
            // NOTE: -> Run -> Arguments -> VM Arguments -> -Xms1024m -Xmx1024m
            while ((currentLine = fileReader.readLine()) != null) {
                // read line
                line = currentLine.split(",");

                // parse variables
                hit = Integer.parseInt(line[6]);
                
                // add values
                if (!values.containsKey(line[4])) {
                    values.put(line[4], new long[2]);
                }
                values.get(line[4])[0] = values.get(line[4])[0] + hit;
                values.get(line[4])[1]++;
            }

            // write evaluation
            for (String curKey : values.keySet()) {
                $(path.replace("DetectorEvaluationData_raw.txt", "DetectorEvaluationData_evaluated.txt")).file().append(curKey + "," + values.get(curKey)[0] + "," + values.get(curKey)[1] + "," + (((double) values.get(curKey)[0] / values.get(curKey)[1]) * 100) + "\r\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

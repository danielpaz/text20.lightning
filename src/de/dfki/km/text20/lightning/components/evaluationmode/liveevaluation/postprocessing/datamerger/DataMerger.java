/*
 * DataMerger.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.datamerger;

import static net.jcores.jre.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * merges survey and live evaluation results
 * 
 * @author Christoph Käding
 */
public class DataMerger {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("DataMerger started...");
        System.out.println();

        // run through all files
        for (File file : new File("evaluation/results/live evaluation").listFiles()) {
            try {
                // build path
                String path = file.getAbsolutePath();

                // check if file exists
                if (!(new File(path + "/LiveEvaluation_evaluated.txt").exists()) || !(new File(path + "/survey_results.txt").exists()))
                    continue;

                // indicate file
                System.out.println("next files: LiveEvaluation_evaluated.txt and survey_results.txt in " + new File(path).getAbsolutePath());
                System.out.println();

                // delete and write files
                $(path + "/combined.log").file().delete();
                $(path + "/combined.log").file().append("- hints -\r\nsee also LiveEvaluation_evaluated.log and survey_results.log\r\nheadings are included in combined.txt\r\n");
                $(path + "/combined.txt").file().delete();
                $(path + "/combined.txt").file().append("user,types,file,word,distance,timeHit,timeAll,hotkeys,buttons,A_text_good,B_text_good,AvsMouse_text,BvsMouse_text,A_code_good,B_code_good,AvsMouse_code,BvsMouse_code,A_horizontal,A_vertical,B_horizontal,B_vertical,A_pref,B_pref,mouseVsKeyboard,gender,birth,study,exp_years,perDay,aids,clearlyVisible,numberInFile,numberOverall,allButtons,velocityHit,velocityAll\n");

                // initialize lists
                ArrayList<String[]> surveyResults = new ArrayList<String[]>();

                // initialize reader
                BufferedReader lineReader = new BufferedReader(new FileReader(path + "/survey_results.txt"));
                String line = null;

                // read survey
                while ((line = lineReader.readLine()) != null) {
                    surveyResults.add(line.split(","));
                }

                lineReader = new BufferedReader(new FileReader(path + "/LiveEvaluation_evaluated.txt"));
                int user = -1;
                int fileNumber = -1;
                int method = -1;
                int formerMethod = -1;
                int countInFile = -1;
                int countOverall = -1;
                int steps = -1;

                // read survey
                while ((line = lineReader.readLine()) != null) {

                    // read id
                    user = Integer.parseInt(line.split(",")[0]);

                    // read method and set counter
                    method = Integer.parseInt(line.split(",")[1]);
                    if (formerMethod != method) {
                        formerMethod = method;
                        steps = countInFile + 1;
                        countInFile = -1;
                    }
                    countInFile++;

                    // read file and set counter
                    fileNumber = Integer.parseInt(line.split(",")[2]);
                    if (fileNumber > 0) {
                        countOverall = steps * fileNumber + countInFile;
                    } else {
                        countOverall = countInFile;
                    }

                    // copy dataset
                    $(path + "/combined.txt").file().append(line);

                    // add survey data
                    for (int i = 1; i < surveyResults.get(user).length; i++)
                        $(path + "/combined.txt").file().append("," + surveyResults.get(user)[i]);

                    // add count and line break
                    $(path + "/combined.txt").file().append("," + countInFile + "," + countOverall + "," + (Integer.parseInt(line.split(",")[7]) + Integer.parseInt(line.split(",")[8])) + "," + (Double.parseDouble(line.split(",")[4]) / Integer.parseInt(line.split(",")[5])) + "," + (Double.parseDouble(line.split(",")[4]) / Integer.parseInt(line.split(",")[6])) + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("DataMerger finished.");
    }
}

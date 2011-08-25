/*
 * SurveyConverter.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.surveyconverter;

import static net.jcores.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author Christoph Käding
 *
 */
public class SurveyConverter {

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("SurveyConverter started...");
        System.out.println();

        // run through all files
        for (File file : new File("evaluation/results/live evaluation").listFiles()) {
            try {
                // build path
                String path = file.getAbsolutePath();

                // check if file exists
                if (!(new File(path + "/survey_results.txt").exists())) continue;

                // indicate file
                System.out.println("next files: LiveEvaluation_evaluated.txt and survey_results.txt in " + new File(path).getAbsolutePath());
                System.out.println();

                // delete and write files
                $(path + "/survey_converted.log").file().delete();
                $(path + "/survey_converted.log").file().append("- hints -\r\nheadings are included in survey_converted.txt\r\nid equals number of question in survey\n\r");
                $(path + "/survey_converted.txt").file().delete();
                $(path + "/survey_converted.txt").file().append("id,user,answer\n");

                // initialize lists
                ArrayList<String[]> surveyResults = new ArrayList<String[]>();

                // initialize reader
                BufferedReader lineReader = new BufferedReader(new FileReader(path + "/survey_results.txt"));
                String line = null;
                String user = null;

                // read survey
                while ((line = lineReader.readLine()) != null) {
                    surveyResults.add(line.split(","));
                }

                // converted survey
                for (int j = 0; j < surveyResults.size(); j++) {
                    user = surveyResults.get(j)[0];
                    for (int i = 1; i < 23; i++) {
                        $(path + "/survey_converted.txt").file().append((i - 1) + "," + user + "," + surveyResults.get(j)[i] + "\n");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("SurvayConverter finished.");
    }
}
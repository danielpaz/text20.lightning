/*
 * MiniStatistics.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.ministatistics;

import static net.jcores.jre.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author Christoph Käding
 */
public class MiniStatistics {

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("MiniStatistics started...");
        System.out.println();

        // run through all files
        for (File file : new File("evaluation/results/live evaluation").listFiles()) {
            try {
                // build path
                String path = file.getAbsolutePath();

                // check if file exists
                if (!(new File(path + "/survey_results.txt").exists())) continue;

                // indicate file
                System.out.println("next file: " + new File(path + "/survey_results.txt").getAbsolutePath());
                System.out.println();

                // delete file
                $(path + "/survey_MiniStatistics.log").file().delete();

                // initialize reader
                BufferedReader lineReader = new BufferedReader(new FileReader(path + "/survey_results.txt"));
                String tmp = null;
                String line[] = null;

                // initialize variables
                int userOverall = 0;
                int male = 0;
                int female = 0;
                double birth = 0;
                int birthError = 0;
                int birthMin = Integer.MAX_VALUE;
                int birthMax = Integer.MIN_VALUE;
                int study = 0;
                int studyError = 0;
                double expYears = 0;
                int expYearsError = 0;
                int expYearsMin = Integer.MAX_VALUE;
                int expYearsMax = Integer.MIN_VALUE;
                double perDay = 0;
                int perDayError = 0;
                int perDayMin = Integer.MAX_VALUE;
                int perDayMax = Integer.MIN_VALUE;
                int aidsNone = 0;
                int aidsGlasses = 0;
                int aidsLenses = 0;
                int visible = 0;
                int visibleError = 0;

                // read survey
                while ((tmp = lineReader.readLine()) != null) {

                    // split string
                    line = tmp.split(",");

                    // increment user
                    userOverall++;

                    // gender
                    if (Integer.parseInt(line[16]) == 0) {
                        female++;
                    } else if (Integer.parseInt(line[16]) == 1) {
                        male++;
                    }

                    // year of birth
                    if (Double.parseDouble(line[17]) > 0) {
                        birth = birth + Double.parseDouble(line[17]);
                    } else {
                        birthError++;
                    }
                    if ((birthMin > Integer.parseInt(line[17])) && (Integer.parseInt(line[17]) > 0))
                        birthMin = Integer.parseInt(line[17]);
                    if ((birthMax < Integer.parseInt(line[17])) && (Integer.parseInt(line[17]) > 0))
                        birthMax = Integer.parseInt(line[17]);

                    // field of study
                    if (Integer.parseInt(line[18]) == 1) {
                        study++;
                    } else if (Integer.parseInt(line[18]) == -1) {
                        studyError++;
                    }

                    // experience in years
                    if (Double.parseDouble(line[19]) > 0) {
                        expYears = expYears + Double.parseDouble(line[19]);
                    } else {
                        expYearsError++;
                    }
                    if ((expYearsMin > Integer.parseInt(line[19])) && (Integer.parseInt(line[19]) > 0))
                        expYearsMin = Integer.parseInt(line[19]);
                    if ((expYearsMax < Integer.parseInt(line[19])) && (Integer.parseInt(line[19]) > 0))
                        expYearsMax = Integer.parseInt(line[19]);

                    // hours per day 
                    if (Double.parseDouble(line[20]) > 0) {
                        perDay = perDay + Double.parseDouble(line[20]);
                    } else {
                        perDayError++;
                    }
                    if ((perDayMin > Integer.parseInt(line[20])) && (Integer.parseInt(line[20]) > 0))
                        perDayMin = Integer.parseInt(line[20]);
                    if ((perDayMax < Integer.parseInt(line[20])) && (Integer.parseInt(line[20]) > 0))
                        perDayMax = Integer.parseInt(line[20]);

                    // vision aids
                    switch (Integer.parseInt(line[21])) {
                    case 0:
                        aidsNone++;
                        break;
                    case 1:
                        aidsGlasses++;
                        break;
                    case 2:
                        aidsLenses++;
                        break;
                    default:
                        break;
                    }

                    // field of study
                    if (Integer.parseInt(line[22]) == 1) {
                        visible++;
                    } else if (Integer.parseInt(line[22]) == -1) {
                        visibleError++;
                    }
                }

                // write statistics
                $(path + "/survey_MiniStatistics.log").file().append("- user -\r\noverall = " + userOverall + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- gender -\r\nmale = " + male + ", female = " + female + ", undefined = " + (userOverall - male - female) + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- year of birth -\r\naverage = " + ((double) Math.round((birth / (userOverall - birthError)) * 1000) / 1000) + ", min = " + birthMin + ", max = " + birthMax + ", undefinded = " + birthError + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- field of study -\r\ncomputer science (or similar) = " + study + ", other fileds = " + (userOverall - study - studyError) + ", undefined = " + studyError + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- experience in years -\r\naverage = " + ((double) Math.round((expYears / (userOverall - expYearsError)) * 1000) / 1000) + ", min = " + expYearsMin + ", max = " + expYearsMax + ", undefinded = " + expYearsError + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- hours per day -\r\naverage = " + ((double) Math.round((perDay / (userOverall - perDayError)) * 1000) / 1000) + ", min = " + perDayMin + ", max = " + perDayMax + ", undefinded = " + perDayError + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- vision aids -\r\nnone = " + aidsNone + ", glasses = " + aidsGlasses + ", contact lenses = " + aidsLenses + ", undefined = " + (userOverall - aidsNone - aidsGlasses - aidsLenses) + "\r\n\r\n");
                $(path + "/survey_MiniStatistics.log").file().append("- screen clearly visible -\r\nyes = " + visible + ", no = " + (userOverall - visibleError - visible) + ", undefined = " + visibleError + "\r\n\r\n");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("MiniStatistics finished.");
    }
}

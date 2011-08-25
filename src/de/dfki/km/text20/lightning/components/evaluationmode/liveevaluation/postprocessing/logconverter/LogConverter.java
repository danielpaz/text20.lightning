/*
 * LogConverter.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.logconverter;

import static net.jcores.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Christoph Käding
 */
public class LogConverter {

    /**
     * @param args
     */
    @SuppressWarnings("boxing")
    public static void main(String[] args) {
        System.out.println("LogConverter started...");
        System.out.println();

        // run through all files
        for (File file : new File("evaluation/results/live evaluation").listFiles()) {
            try {
                // build path
                String path = file.getAbsolutePath();

                // check if file exists
                if (!(new File(path + "/LiveEvaluation_raw.txt").exists())) continue;

                // indicate file
                System.out.println("next file: " + new File(path + "/LiveEvaluation_raw.txt").getAbsolutePath());
                System.out.println();

                // initialize cvs stuff
                ArrayList<String> types = new ArrayList<String>();
                ArrayList<String> files = new ArrayList<String>();
                ArrayList<String> words = new ArrayList<String>();
                ArrayList<Long> methA = new ArrayList<Long>();
                ArrayList<Long> methB = new ArrayList<Long>();
                ArrayList<Long> methC = new ArrayList<Long>();
                ArrayList<Long> methAAll = new ArrayList<Long>();
                ArrayList<Long> methBAll = new ArrayList<Long>();
                ArrayList<Long> methCAll = new ArrayList<Long>();
                int indexTypes;
                int indexFiles;
                int indexWords;
                int user = -1;
                $(path + "/LiveEvaluation_evaluated.txt").file().delete();
                $(path + "/LiveEvaluationKeys_evaluated.log").file().delete();
                $(path + "/tHit_median_perUser.txt").file().delete();
                $(path + "/tHit_median_overAll.txt").file().delete();

                // write tHitLog
                $(path + "/tHit_median.log").file().delete();
                $(path + "/tHit_median.log").file().append("- headings -\r\nuser, type, median");

                // initialize reader
                BufferedReader lineReader = new BufferedReader(new FileReader(path + "/LiveEvaluation_raw.txt"));
                String line = null;

                // read
                while ((line = lineReader.readLine()) != null) {

                    // test if line contains data
                    if (!line.startsWith("- ")) {

                        // test if user changed
                        if (line.startsWith("Session: ")) {
                            if ((methA.size() > 0) && (methB.size() > 0) && (methC.size() > 0)) {
                                calcMedian(user, 0, path + "/tHit_median_perUser.txt", methA);
                                methA.clear();
                                calcMedian(user, 1, path + "/tHit_median_perUser.txt", methB);
                                methB.clear();
                                calcMedian(user, 2, path + "/tHit_median_perUser.txt", methC);
                                methC.clear();
                            }
                            user++;
                        }
                        continue;
                    }

//                    System.out.print("|" + user + "|");

                    // extract type
                    String typeTmp = line.substring(line.indexOf("-") + 2, line.indexOf(":"));
//                    System.out.print(typeTmp + "|");

                    // cut method of
                    line = line.substring(line.indexOf(":") + 2);

                    // split line
                    String[] components = line.split(" ");

                    // extract file
                    String fileTmp = components[2].substring(0, components[2].length() - 1);
//                    System.out.print(fileTmp + "|");

                    // extract word
                    String wordTmp = components[5].substring(0, components[5].length() - 1);
//                    System.out.print(wordTmp + "|");

                    // extract distance
                    double distanceTmp = Double.parseDouble(components[8].substring(0, components[8].length()));
//                    System.out.print(distanceTmp + "|");

                    // extract time
                    long timeOneTmp = Integer.parseInt(components[12].substring(0, components[12].length()));
//                    System.out.print(timeOneTmp + "|");

                    // extract time
                    long timeTwoTmp = Integer.parseInt(components[16].substring(0, components[16].length()));
//                    System.out.print(timeTwoTmp + "|");

                    // extract hotkeys
                    int hotkeys = Integer.parseInt(components[20].substring(0, components[20].length() - 1));
//                    System.out.print(hotkeys + "|");

                    // extract buttons
                    int buttonsTmp = line.substring(line.lastIndexOf("=") + 2).split(" ").length;
//                    System.out.println(buttonsTmp + "|");

                    // reset index
                    indexTypes = -1;
                    indexFiles = -1;
                    indexWords = -1;

                    // types, update array 
                    if (!types.contains(typeTmp)) {
                        types.add(typeTmp);
                    }

                    // types, convert to index
                    for (int i = 0; i < types.size(); i++)
                        if (types.get(i).equals(typeTmp)) indexTypes = i;

                    // files, update array
                    if (!files.contains(fileTmp)) {
                        files.add(fileTmp);
                    }

                    // files, convert to index
                    for (int i = 0; i < files.size(); i++)
                        if (files.get(i).equals(fileTmp)) indexFiles = i;

                    // words, update array
                    if (!words.contains(wordTmp)) {
                        words.add(wordTmp);
                    }

                    // words, convert to index
                    for (int i = 0; i < words.size(); i++)
                        if (words.get(i).equals(wordTmp)) indexWords = i;

                    // add value to arraylists
                    if (indexTypes == 0) {
                        methA.add(timeOneTmp);
                        methAAll.add(timeOneTmp);
                    } else if (indexTypes == 1) {
                        methB.add(timeOneTmp);
                        methBAll.add(timeOneTmp);
                    } else if (indexTypes == 2) {
                        methC.add(timeOneTmp);
                        methCAll.add(timeOneTmp);
                    } else {
                        System.out.println("unexpected method");
                    }

                    // write to txt
                    $(path + "/LiveEvaluation_evaluated.txt").file().append($(user, indexTypes, indexFiles, indexWords, distanceTmp, timeOneTmp, timeTwoTmp, hotkeys, buttonsTmp - 4).string().join(",") + "\n");
                    new File(path + "/individual").mkdirs();
                    $(path + "/individual/LiveEvaluation_user" + user + "_evaluated.txt").file().delete();
                    $(path + "/individual/LiveEvaluation_user" + user + "_evaluated.txt").file().append($(user, indexTypes, indexFiles, indexWords, distanceTmp, timeOneTmp, timeTwoTmp, hotkeys, buttonsTmp - 4).string().join(",") + "\n");
                }

                // write last median
                if ((methA.size() > 0) && (methB.size() > 0) && (methC.size() > 0)) {
                    calcMedian(user, 0, path + "/tHit_median_perUser.txt", methA);
                    calcMedian(user, 1, path + "/tHit_median_perUser.txt", methB);
                    calcMedian(user, 2, path + "/tHit_median_perUser.txt", methC);
                }

                // write overall median
                if ((methAAll.size() > 0) && (methBAll.size() > 0) && (methCAll.size() > 0)) {
                    calcMedian(user + 1, 0, path + "/tHit_median_overAll.txt", methAAll);
                    calcMedian(user + 1, 1, path + "/tHit_median_overAll.txt", methBAll);
                    calcMedian(user + 1, 2, path + "/tHit_median_overAll.txt", methCAll);
                }

                // write key file
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("- headings -\r\nuser, types, file, word, distance, timeHit, timeAll, hotkeys, buttons\r\n\r\n");
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("- user -\r\nparticipants = " + (user + 1) + "\r\n\r\n");
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("- detectors -\r\n");
                for (int i = 0; i < types.size(); i++)
                    $(path + "/LiveEvaluationKeys_evaluated.log").file().append("index: " + i + " detector: " + types.get(i) + "\r\n");
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("\r\n");
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("- files -\r\n");
                for (int i = 0; i < files.size(); i++)
                    $(path + "/LiveEvaluationKeys_evaluated.log").file().append("index: " + i + " file: " + files.get(i) + "\r\n");
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("\r\n");
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("- words -\r\n");
                for (int i = 0; i < words.size(); i++)
                    $(path + "/LiveEvaluationKeys_evaluated.log").file().append("index: " + i + " word: " + words.get(i) + "\r\n");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // inidicate finish
        System.out.println("LogConverter finished.");
    }

    /**
     * @param user
     * @param type
     * @param path
     * @param values
     */
    @SuppressWarnings("boxing")
    private static void calcMedian(int user, int type, String path, ArrayList<Long> values) {
        // convert and sort array
        long[] array = new long[values.size()];
        for (int i = 0; i < values.size(); i++)
            array[i] = values.get(i);
        Arrays.sort(array);

        // get median
        long median;
        int medianIndex = (values.size() / 2) - 1;
        if (values.size() % 2 == 1) {
            median = array[medianIndex];
        } else {
            int medianIndexTwo = values.size() / 2;
            median = (array[medianIndex] + array[medianIndexTwo]) / 2;
        }

        // write median
        $(path).file().append($(user, type, median).string().join(",") + "\n");
    }
}

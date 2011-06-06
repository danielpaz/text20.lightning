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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.logconverter;

import static net.jcores.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

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
                int indexTypes;
                int indexFiles;
                int indexWords;
                $(path + "/LiveEvaluation_evaluated.txt").file().delete();
                $(path + "/LiveEvaluationKeys_evaluated.log").file().delete();

                // initialize reader
                BufferedReader lineReader = new BufferedReader(new FileReader(path + "/LiveEvaluation_raw.txt"));
                String line = null;
                double distanceTmp;
                long timeOneTmp;
                long timeTwoTmp;
                String typeTmp;
                String wordTmp;
                String fileTmp;
                int buttonsTmp;
                int hotkeys;
                String[] components;

                // read
                while ((line = lineReader.readLine()) != null) {

                    // test if line contains data
                    if (!line.startsWith("- ")) continue;

                    // extract type
                    typeTmp = line.substring(line.indexOf("-") + 2, line.indexOf(":"));
                    System.out.print("|" + typeTmp + "|");

                    // cut method of
                    line = line.substring(line.indexOf(":") + 2);

                    // split line
                    components = line.split(" ");

                    // extract file
                    fileTmp = components[2].substring(0, components[2].length() - 1);
                    System.out.print(fileTmp + "|");

                    // extract word
                    wordTmp = components[5].substring(0, components[5].length() - 1);
                    System.out.print(wordTmp + "|");

                    // extract distance
                    distanceTmp = Double.parseDouble(components[8].substring(0, components[8].length()));
                    System.out.print(distanceTmp + "|");

                    // extract time
                    timeOneTmp = Integer.parseInt(components[12].substring(0, components[12].length()));
                    System.out.print(timeOneTmp + "|");

                    // extract time
                    timeTwoTmp = Integer.parseInt(components[16].substring(0, components[16].length()));
                    System.out.print(timeTwoTmp + "|");

                    // extract time
                    if (components[18].equals("hotkeys")) {
                        hotkeys = Integer.parseInt(components[20].substring(0, components[20].length() - 1));
                        System.out.print(hotkeys + "|");
                    } else {
                        hotkeys = -1;
                        System.out.print("no hotkeys|");
                    }

                    // extract buttons
                    buttonsTmp = line.substring(line.lastIndexOf("=") + 2).split(" ").length;
                    System.out.println(buttonsTmp + "|");

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

                    // write to txt
                    $(path + "/LiveEvaluation_evaluated.txt").file().append($(indexTypes, indexFiles, indexWords, distanceTmp, timeOneTmp, timeTwoTmp, hotkeys, buttonsTmp - 4).string().join(",") + "\n");
                }

                // write key file
                $(path + "/LiveEvaluationKeys_evaluated.log").file().append("- headings -\r\ntypes, file, word, distance, time, hotkeys, buttons\r\n\r\n");
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

                // inidicate finish
                System.out.println();
                System.out.println("Done.");
                System.out.println();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

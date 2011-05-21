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
package de.dfki.km.text20.lightning.components.evaluationmode.quickness.logconverter;

import static net.jcores.CoreKeeper.$;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author Christoph Käding
 */
public class LogConverter {

    /**
     * @param args
     */
    @SuppressWarnings("boxing")
    public static void main(String[] args) {
        try {
            if (!new File("Quickness.log").exists()) {
                System.out.println("Quickness.log does not exist.");
                System.exit(0);
            }

            // initialize cvs stuff
            ArrayList<String> types = new ArrayList<String>();
            ArrayList<String> files = new ArrayList<String>();
            ArrayList<String> words = new ArrayList<String>();
            int indexTypes;
            int indexFiles;
            int indexWords;
            $("Quickness.txt").file().delete();
            $("QuicknessKeys.txt").file().delete();

            // initialize xls-stuff
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            WritableFont arial10pt = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat format = new WritableCellFormat(arial10pt);
            try {
                format.setWrap(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // initialize file
            workbook = Workbook.createWorkbook(new File("Quickness.xls"), wbSettings);
            workbook.createSheet("Evaluation", 0);
            WritableSheet excelSheet = workbook.getSheet(0);

            // add captions
            excelSheet.addCell(new Label(0, 0, "Type", format));
            excelSheet.addCell(new Label(1, 0, "File", format));
            excelSheet.addCell(new Label(2, 0, "Word", format));
            excelSheet.addCell(new Label(3, 0, "Distance", format));
            excelSheet.addCell(new Label(4, 0, "Time", format));

            // initialize reader
            BufferedReader lineReader = new BufferedReader(new FileReader("Quickness.log"));
            String line = null;
            int lineNumber = 1;
            double distanceTmp;
            long timeTmp;
            String typeTmp;
            String wordTmp;
            String fileTmp;

            // indicate initialize
            System.out.println("Initializing done.");
            System.out.println();

            // read
            while ((line = lineReader.readLine()) != null) {

                // test if line contains data
                if (!line.startsWith("- ")) continue;

                // extract type
                typeTmp = line.substring(line.indexOf("-") + 2, line.indexOf(":"));
                System.out.print("|" + typeTmp + "|");

                // extract file
                fileTmp = line.substring(line.indexOf("file") + 7, line.indexOf(","));
                System.out.print(fileTmp + "|");

                // extract word
                wordTmp = line.substring(line.indexOf("word") + 7, line.indexOf("distance") - 2);
                System.out.print(wordTmp + "|");

                // extract distance
                distanceTmp = Double.parseDouble(line.substring(line.indexOf("distance") + 10, line.indexOf(" Pixel")));
                System.out.print(distanceTmp + "|");

                // extract time
                timeTmp = Integer.parseInt(line.substring(line.indexOf("time") + 7, line.indexOf(" ms")));
                System.out.println(timeTmp + "|");

                // write to xls
                excelSheet.addCell(new Label(0, lineNumber, typeTmp, format));
                excelSheet.addCell(new Label(1, lineNumber, fileTmp, format));
                excelSheet.addCell(new Label(2, lineNumber, wordTmp, format));
                excelSheet.addCell(new Number(3, lineNumber, distanceTmp, format));
                excelSheet.addCell(new Number(4, lineNumber, timeTmp, format));
                lineNumber++;

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
                $("Quickness.txt").file().append($(indexTypes, indexFiles, indexWords, distanceTmp, timeTmp).string().join(",") + "\n");
            }

            // write and close
            workbook.write();
            workbook.close();

            // write key file
            for (int i = 0; i < types.size(); i++)
                $("QuicknessKeys.txt").file().append("index: " + i + " detector: " + types.get(i) + "\r\n");
            $("QuicknessKeys.txt").file().append("\r\n");
            for (int i = 0; i < files.size(); i++)
                $("QuicknessKeys.txt").file().append("index: " + i + " file: " + files.get(i) + "\r\n");
            $("QuicknessKeys.txt").file().append("\r\n");
            for (int i = 0; i < words.size(); i++)
                $("QuicknessKeys.txt").file().append("index: " + i + " word: " + words.get(i) + "\r\n");

            // inidicate finish
            System.out.println();
            System.out.println("Done.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

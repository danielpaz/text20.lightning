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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public static void main(String[] args) {
        try {
            if (!new File("Quickness.log").exists()) {
                System.out.println("Quickness.log does not exist.");
                System.exit(0);
            }

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
            excelSheet.addCell(new Label(1, 0, "Distance", format));
            excelSheet.addCell(new Label(2, 0, "Time", format));

            // initialize reader
            BufferedReader lineReader = new BufferedReader(new FileReader("Quickness.log"));
            String line = null;
            int lineNumber = 1;
            double distanceTmp;
            long timeTmp;
            String typeTmp;

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

                // extract distance
                distanceTmp = Double.parseDouble(line.substring(line.indexOf("=") + 2, line.indexOf(" Pixel")));
                System.out.print(line.substring(line.indexOf("=") + 2, line.indexOf(" Pixel")) + "|");

                // extract time
                timeTmp = Integer.parseInt(line.substring(line.lastIndexOf("=") + 2, line.indexOf(" ms")));
                System.out.println(line.substring(line.lastIndexOf("=") + 2, line.indexOf(" ms")) + "|");

                // write to xls
                excelSheet.addCell(new Label(0, lineNumber, typeTmp, format));
                excelSheet.addCell(new Number(1, lineNumber, distanceTmp, format));
                excelSheet.addCell(new Number(2, lineNumber, timeTmp, format));
                lineNumber++;
            }

            // write and close
            workbook.write();
            workbook.close();

            // inidicate finish
            System.out.println();
            System.out.println("Done.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

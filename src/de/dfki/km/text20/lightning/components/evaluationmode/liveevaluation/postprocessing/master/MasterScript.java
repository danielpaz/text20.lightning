/*
 * MasterScript.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.master;

import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.datamerger.DataMerger;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.logconverter.LogConverter;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.ministatistics.MiniStatistics;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.postprocessing.surveyconverter.SurveyConverter;

/**
 * @author Christoph Käding
 *
 */
public class MasterScript {

    /**
     * runs all necessary scripts in right order
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("MasterScript started...");
        System.out.println();
        LogConverter.main(null);
        System.out.println();
        DataMerger.main(null);
        System.out.println();
        SurveyConverter.main(null);
        System.out.println();
        MiniStatistics.main(null);
        System.out.println();
        System.out.println("Masterscript finished.");
    }

}

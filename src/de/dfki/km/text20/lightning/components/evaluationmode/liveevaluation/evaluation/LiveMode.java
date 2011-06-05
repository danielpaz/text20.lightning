/*
 * LiveMode.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation;

import java.util.ArrayList;
import java.util.HashMap;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui.LiveConfigGuiImpl;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui.LiveStepGuiImpl;
import de.dfki.km.text20.lightning.components.evaluationmode.liveevaluation.evaluation.gui.LiveTrainingGuiImpl;

/**
 * opens config dialog, then evaluation gui
 * 
 * @author Christoph Käding
 */
public class LiveMode {

    /** 
     * stored words for evaluation
     * 
     * key = filename of related html file
     * value = list of word which should be highlighted
     */
    private HashMap<String, ArrayList<String>> data;

    /** */
    private int steps;

    /** id of algorithm one */
    private int one;

    /** id of algorithm two */
    private int two;

    /**
     * creates instance, initializes some stuff and opens config gui
     */
    @SuppressWarnings("unused")
    public LiveMode() {
        // initialize variables 
        this.data = new HashMap<String, ArrayList<String>>();

        // open config gui
        new LiveConfigGuiImpl(this);
    }

    /**
     * starts training
     */
    @SuppressWarnings("unused")
    public void startTraining() {
        int index = this.one;
        if (MainClass.getInstance().getInternalPluginManager().getSaliencyDetectors().get(this.one).getInformation().getDisplayName().equals("Dummy Filter"))
            index = this.two;
        new LiveTrainingGuiImpl(this, index, this.data);
    }

    /**
     * starts evaluation
     */
    @SuppressWarnings("unused")
    public void startEvaluation() {
        new LiveStepGuiImpl(this.steps, this.data, this.one, this.two);
    }

    /**
     * @param fileName 
     * @param words 
     */
    public void addToData(String fileName, ArrayList<String> words) {
        ArrayList<String> tmp = new ArrayList<String>(words);
        this.data.put(fileName, tmp);
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     * @param one the one to set
     */
    public void setOne(int one) {
        this.one = one;
    }

    /**
     * @param two the two to set
     */
    public void setTwo(int two) {
        this.two = two;
    }
}

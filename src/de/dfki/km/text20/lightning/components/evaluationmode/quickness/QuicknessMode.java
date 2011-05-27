/*
 * QuicknessMode.java
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
package de.dfki.km.text20.lightning.components.evaluationmode.quickness;

import java.util.ArrayList;
import java.util.HashMap;

import de.dfki.km.text20.lightning.components.evaluationmode.quickness.gui.QuicknessConfigGuiImpl;
import de.dfki.km.text20.lightning.components.evaluationmode.quickness.gui.QuicknessStepGuiImpl;

/**
 * opens config dialog, then evaluation gui
 * 
 * @author Christoph Käding
 */
public class QuicknessMode {

    /** 
     * stored words for evaluation
     * 
     * key = filename of related html file
     * value = list of word which should be highlighted
     */
    private HashMap<String, ArrayList<String>> data;

    /** */
    private int steps;

    /** */
    private boolean startWithDetector;

    /**
     * creates instance, initializes some stuff and opens config gui
     */
    @SuppressWarnings("unused")
    public QuicknessMode() {
        // initialize variables 
        this.data = new HashMap<String, ArrayList<String>>();

        // open config gui
        new QuicknessConfigGuiImpl(this);
    }

    /**
     * starts quickness mode
     */
    @SuppressWarnings("unused")
    public void start() {
        new QuicknessStepGuiImpl(this.steps, this.startWithDetector, this.data);
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
     * @param startWithDetector the startWithDetector to set
     */
    public void setStartWithDetector(boolean startWithDetector) {
        this.startWithDetector = startWithDetector;
    }
}

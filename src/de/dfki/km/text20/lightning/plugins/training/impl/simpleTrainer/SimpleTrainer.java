/*
 * SimpleTrainer.java
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
package de.dfki.km.text20.lightning.plugins.training.impl.simpleTrainer;

import static net.jcores.CoreKeeper.$;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.training.Trainer;

/**
 * Simple version of detector algorithm evaluation.
 * 
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class SimpleTrainer implements Trainer {

    /** list of all recognized data of every step */
    private ArrayList<TrainingContainer> allData;

    /** information object */
    private PluginInformation information = new PluginInformation("Simple Trainer", "Simple Trainer");

    /** list of calculated results for every detector */
    private Map<String, Double> results;

    /**
     * creates new simple trainer object
     */
    public SimpleTrainer() {
        this.allData = new ArrayList<TrainingContainer>();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.training.Trainer#setStep(java.util.Map, java.awt.Point, java.awt.Point)
     */
    @Override
    public void setStep(Map<String, Point> calculations, Point fixation, Point mouseOffset) {
        this.allData.add(new TrainingContainer(calculations, mouseOffset));
        this.results = new Hashtable<String, Double>();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.training.Trainer#leaveTraing()
     */
    @SuppressWarnings("boxing")
    @Override
    public void leaveTraining() {
        // local variables
        double distanceTemp = 0;
        double bestResultValue = Double.MAX_VALUE;
        String bestResultName = "";

        // if no training was performed this session, return
        if (this.allData.size() == 0) return;

        // run through all container == number of training steps
        for (TrainingContainer container : this.allData) {

            // run through all calculations in this container
            for (String name : container.getCalculations().keySet()) {

                // if it is not the first calculation...
                if (this.results.containsKey(name))
                // ... store the result
                    distanceTemp = this.results.get(name);

                // add distance from mouse cursor to calculated target
                distanceTemp = distanceTemp + container.getMouseOffset().distance(container.getCalculations().get(name));

                // update results
                this.results.put(name, distanceTemp);

                // reset temporary variable
                distanceTemp = 0;
            }
        }

        // log results
        $("training.log").file().append("Timestamp: " + System.currentTimeMillis() + " - resluts for " + this.allData.size() + " step(s)\n");
        for (String name : this.results.keySet()) {
            distanceTemp = this.results.get(name) / this.allData.size();
            $("training.log").file().append(name + ": " + distanceTemp + " Pixels distance averaged\n");
            if (distanceTemp == bestResultValue)
                bestResultName += " and " + name;
            if (distanceTemp < bestResultValue) {
                bestResultValue = distanceTemp;
                bestResultName = name;
            }
        }
        $("training.log").file().append(bestResultName + " achived the best results.\n\n");
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.training.Trainer#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

}

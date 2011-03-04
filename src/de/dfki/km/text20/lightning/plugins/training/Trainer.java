/*
 * Trainer.java
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
package de.dfki.km.text20.lightning.plugins.training;

import java.awt.Point;
import java.util.Map;

import net.xeoh.plugins.base.Plugin;
import de.dfki.km.text20.lightning.plugins.PluginInformation;

/**
 * Trainer evaluates the different detectors and give an overview about their results. 
 * 
 * @author Christoph Käding
 *
 */
public interface Trainer extends Plugin {

    /**
     * A trainings step consists of the fixation and its related real target which is caught by showing with the mouse cursor on it.
     * These data will be processed by the different detectors. All these results will be given to this method. 
     * 
     * @param calculations
     * @param fixation
     * @param mouseOffset 
     */
    public void setStep(Map<String, Point> calculations, Point fixation, Point mouseOffset);
    
    /**
     * When trainings mode is leaved or the program is closed, this method will be called.
     */
    public void leaveTraining();
    
    /**
     * ... some information about this plugin ...
     * Make sure you always return the same object, because a id will be set in it!!
     * 
     * @return information
     */
    public PluginInformation getInformation();
}

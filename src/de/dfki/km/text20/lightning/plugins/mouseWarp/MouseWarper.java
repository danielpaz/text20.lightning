/*
 * MouseWarper.java
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
package de.dfki.km.text20.lightning.plugins.mouseWarp;

import java.awt.Point;

import net.xeoh.plugins.base.Plugin;
import de.dfki.km.text20.lightning.plugins.PluginInformation;

/**
 * @author Christoph Käding
 *
 */
public interface MouseWarper extends Plugin {
    
    /**
     * initializes necessary values 
     * 
     * @param angleThreshold
     * @param distanceThreshold
     * @param durationThreshold
     * @param homeRadius 
     * @param setRadius 
     */
    public void initValues(int angleThreshold, int distanceThreshold,
                           long durationThreshold, int homeRadius, int setRadius);

    /**
     * sets current fixation point
     * 
     * @param fixation
     */
    public void setFixationPoint(Point fixation);

    /**
     * adds current mouse position
     * 
     * @param position
     */
    // TODO: maybe add interval (see WarpCommander ~ 100ms)
    public void addMousePosition(Point position);
    
    /**
     * some information about this plugin
     * 
     * @return information
     */
    public PluginInformation getInformation();
}

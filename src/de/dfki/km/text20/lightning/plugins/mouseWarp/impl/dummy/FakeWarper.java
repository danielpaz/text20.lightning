/*
 * FakeWarper.java
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
package de.dfki.km.text20.lightning.plugins.mouseWarp.impl.dummy;

import java.awt.Point;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;

/**
 * doesn't do anything
 * 
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class FakeWarper implements MouseWarper {
    

    private PluginInformation information = new PluginInformation("Fake Warper","Fake Warper");

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#initValues(int, int, long, int, int)
     */
    @Override
    public void initValues(int angleThreshold, int distanceThreshold,
                           long durationThreshold, int homeRadius, int setRadius) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#setFixationPoint(java.awt.Point)
     */
    @Override
    public void setFixationPoint(Point fixation) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#addMousePosition(java.awt.Point)
     */
    @Override
    public void addMousePosition(Point position) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper#getInformation()
     */
    @Override
    public PluginInformation getInformation() {        
        return this.information;
    }

}

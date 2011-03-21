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
import de.dfki.km.text20.lightning.plugins.CommonPluginInterface;

/**
 * A mouse warper moves the cursor to a fixation point.
 * This action starts when the cursor moves to the fixation point and fulfills the given restrictions.
 * 
 * @author Christoph Käding
 *
 */
public interface MouseWarper extends Plugin, CommonPluginInterface {
    
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
    // TODO: maybe add interval (see WarpCommander ~ 20ms)
    public void addMousePosition(Point position);
}

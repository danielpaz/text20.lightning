/*
 * CommonPluginInterface.java
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
package de.dfki.km.text20.lightning.plugins;

import javax.swing.JFrame;

/**
 * Interface for all common functions which are necessary for any plugin. 
 * 
 * @author Christoph Käding
 *
 */
public interface CommonPluginInterface {

    /** 
     * @return plugin specific gui for configuration
     */ 
    public JFrame getGui();

    /**
     * indicates that the work for this plugin starts
     */
    public void start();

    /**
     * indicates that the work for this plugin is over
     */
    public void stop();

    /**
     * ... some information about this plugin ...
     * Make sure you always return the same object, because a id will be set in it!!
     * 
     * @return information
     */
    public PluginInformation getInformation();
}

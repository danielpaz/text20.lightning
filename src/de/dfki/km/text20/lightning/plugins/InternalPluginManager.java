/*
 * MethodManager.java
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
 *
 */
package de.dfki.km.text20.lightning.plugins;

import java.util.ArrayList;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.mouseWarp.impl.simpleWarper.SimpleWarper;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.impl.dummy.FakePositionFinder;
import de.dfki.km.text20.lightning.plugins.saliency.impl.simplesobel.SimpleSobel;

/**
 * All the given plugins were added and provided for use. Also switching of the active plugin is handeled. 
 * 
 * @author Christoph Käding
 */
public class InternalPluginManager {

    
    /** utility which can create a list of all available plugins */
    private PluginManagerUtil pluginManagerUtil;
    
    /** current elected saliency detector */
    private SaliencyDetector currentSaliencyDetector;
    
    /** a list of all saliency detectors*/
    private ArrayList<SaliencyDetector> saliencyDetectors;
    
    /** a list of all mouse warpers*/
    private ArrayList<MouseWarper> mouseWarpers;
    
    /** current elected mouse warper */
    private MouseWarper currentMouseWarper;

    /**
     * creates a new MethodManager object and loads plugins
     * 
     * @param manager  
     */
    //TODO: store current plugin in properties
    public InternalPluginManager(PluginManager manager) {
               
        this.pluginManagerUtil = new PluginManagerUtil(manager);
        this.saliencyDetectors = new ArrayList<SaliencyDetector>(this.pluginManagerUtil.getPlugins(SaliencyDetector.class));
        this.mouseWarpers = new ArrayList<MouseWarper>(this.pluginManagerUtil.getPlugins(MouseWarper.class));
        
        // TODO: get this from properties
        this.currentSaliencyDetector = this.saliencyDetectors.get(0);
        this.currentMouseWarper = this.mouseWarpers.get(0);
    }

    /**
     * gives the current saliency detecting method back
     * 
     * @return currentSaliencyDetector
     */
    public SaliencyDetector getCurrentSaliencyDetector() {
        return this.currentSaliencyDetector;
    }

    /**
     * Returns a ArrayList of all the given plugins for position finding.
     * 
     * @return saliencyDetectors
     */
    public ArrayList<SaliencyDetector> getSaliencyDetectors() {
        return this.saliencyDetectors;
    }

    /**
     * Changes the active plugin to change the used method.
     * 
     * @param choice
     */
    public void setCurrentSaliencyDetector(SaliencyDetector choice) {
        this.currentSaliencyDetector = choice;
    }
    
    /**
     * gives the current mouse warping method back
     * 
     * @return currentSaliencyDetector
     */
    public MouseWarper getCurrentMouseWarper() {
        return this.currentMouseWarper;
    }

    /**
     * Returns a ArrayList of all the given plugins for mouse warping.
     * 
     * @return saliencyDetectors
     */
    public ArrayList<MouseWarper> getMouseWarpers() {
        return this.mouseWarpers;
    }

    /**
     * Changes the active plugin to change the used method.
     * 
     * @param choice
     */
    public void setCurrentMouseWarper(MouseWarper choice) {
        this.currentMouseWarper = choice;
    }
}

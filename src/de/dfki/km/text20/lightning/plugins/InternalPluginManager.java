/*
 * InternalPluginManager.java
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
import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.plugins.mouseWarp.MouseWarper;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.training.Trainer;

/**
 * All the given plugins were added and provided for use. Also switching of the active plugin is handeled. 
 * 
 * @author Christoph Käding
 */
public class InternalPluginManager {

    /** utility which can create a list of all available plugins */
    private PluginManagerUtil pluginManagerUtil;

    /** global properties */
    private Properties properties;

    /** a list of all saliency detectors*/
    private ArrayList<SaliencyDetector> saliencyDetectors;

    /** a list of all mouse warpers*/
    private ArrayList<MouseWarper> mouseWarpers;
    
    /** list of all available trainings methods */
    private ArrayList<Trainer> trainer;
    
    /** current elected saliency detector */
    private int currentSaliencyDetectorId;
    
    /** current elected mouse warper */
    private int currentMouseWarperId;

    /** current used trainings method */
    private int currentTrainerId;

    /**
     * creates a new MethodManager object and loads plugins
     * 
     * @param manager  
     */
    public InternalPluginManager(PluginManager manager) {
        this.properties = MainClass.getInstance().getProperties();
        boolean found = false;

        // initialize plugin lists
        this.pluginManagerUtil = new PluginManagerUtil(manager);
        this.saliencyDetectors = new ArrayList<SaliencyDetector>(this.pluginManagerUtil.getPlugins(SaliencyDetector.class));
        this.mouseWarpers = new ArrayList<MouseWarper>(this.pluginManagerUtil.getPlugins(MouseWarper.class));
        this.trainer = new ArrayList<Trainer>(this.pluginManagerUtil.getPlugins(Trainer.class));

        // generate id and set former used plugin if this is available or set default value ,if it is needed, for ...
        // ...saliency detectors
        for (int i = 0; i < this.saliencyDetectors.size(); i++) {
            this.saliencyDetectors.get(i).getInformation().setId(i);
            if (this.properties.getDetectorName().equals(this.saliencyDetectors.get(i).getInformation().getDisplayName())) {
                this.setCurrentSaliencyDetector(i);
                found = true;
            }
        }
        if ((this.saliencyDetectors.size() > 0) && !found)
            this.setCurrentSaliencyDetector(0);

        // ...mouse warpers
        found = false;
        for (int i = 0; i < this.mouseWarpers.size(); i++) {
            this.mouseWarpers.get(i).getInformation().setId(i);
            if (this.properties.getWarperName().equals(this.mouseWarpers.get(i).getInformation().getDisplayName())) {
                this.setCurrentMouseWarper(i);
                found = true;
            }
        }
        if ((this.mouseWarpers.size() > 0) && !found) this.setCurrentMouseWarper(0);

        // ... trainer
        found = false;
        for (int i = 0; i < this.trainer.size(); i++) {
            this.trainer.get(i).getInformation().setId(i);
            if (this.properties.getTrainerName().equals(this.trainer.get(i).getInformation().getDisplayName())) {
                this.setCurrentTrainer(i);
                found = true;
            }
        }
        if ((this.trainer.size() > 0) && !found) this.setCurrentTrainer(0);

    }

    /**
     * gives the current saliency detecting method back
     * 
     * @return currentSaliencyDetector
     */
    public SaliencyDetector getCurrentSaliencyDetector() {
        return this.saliencyDetectors.get(this.currentSaliencyDetectorId);
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
    public void setCurrentSaliencyDetector(int choice) {
        this.currentSaliencyDetectorId = choice;
        this.properties.setDetectorName(this.saliencyDetectors.get(choice).getInformation().getDisplayName());
    }

    /**
     * gives the current mouse warping method back
     * 
     * @return currentSaliencyDetector
     */
    public MouseWarper getCurrentMouseWarper() {
        return this.mouseWarpers.get(this.currentMouseWarperId);
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
    public void setCurrentMouseWarper(int choice) {
        this.currentMouseWarperId = choice;
        this.properties.setWarperName(this.mouseWarpers.get(choice).getInformation().getDisplayName());
          }

    /**
     * Returns a ArrayList of all the given plugins for training.
     * 
     * @return saliencyDetectors
     */
    public ArrayList<Trainer> getTrainer() {
        return this.trainer;
    }

    /**
     * Changes the active plugin to change the used method.
     * 
     * @param choice
     */
    public void setCurrentTrainer(int choice) {
        this.currentTrainerId = choice;
        this.properties.setTrainerName(this.trainer.get(choice).getInformation().getDisplayName());
    }

    /**
     * gives the current trainings method back
     * 
     * @return currentSaliencyDetector
     */
    public Trainer getCurrentTrainer() {
        return this.trainer.get(this.currentTrainerId);
    }
}

/*
 * WarpCommander.java
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
package de.dfki.km.text20.lightning.worker.warpMouse;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.Properties;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;

/**
 * This class tracks the position of the mouse pointer every 100ms. 
 * This timer can be started ore stopped by this class. 
 * By stopping the timer mouse warping is disabled.
 * 
 * changing the 100ms means also change this value in gui and the plugins.
 * 
 * @author Christoph Käding 
 * 
 */
public class WarpCommander {

    /** timer which clocks the mouse position tracking */
    private Timer timer;

    /** internal used plugin manager */
    private InternalPluginManager manager;

    /** global used properties */
    private Properties properties;
    
    /**
     * creates a new WarpCommander and initializes the timer and the currently used warp plugin.
     */
    public WarpCommander() {
        // initialize variables
        this.properties = MainClass.getInstance().getProperties();
        this.manager = MainClass.getInstance().getInternalPluginManager();
        
        // initialize current warp plugin
        int angle = this.properties.getAngleThreshold();
        int distance = this.properties.getDistanceThreshold();
        long duration = this.properties.getDurationThreshold();
        int home = this.properties.getHomeRadius();
        int set = this.properties.getSetRadius();
        if (this.manager.getCurrentMouseWarper() != null)
            this.manager.getCurrentMouseWarper().initValues(angle, distance, duration, home, set);

        // initialize timer
        this.timer = new Timer(100, new ActionListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void actionPerformed(ActionEvent arg0) {

                if (manager.getCurrentMouseWarper() != null)
                    manager.getCurrentMouseWarper().addMousePosition(MouseInfo.getPointerInfo().getLocation());
            }
        });
    }

    /**
     * starts the timer
     */
    public void start() {
        if (this.properties.isUseWarp()) this.timer.start();
    }

    /**
     * stops the timer
     */
    public void stop() {
        this.timer.stop();
    }

}

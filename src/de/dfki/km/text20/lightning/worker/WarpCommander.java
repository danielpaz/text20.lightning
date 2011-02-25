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
package de.dfki.km.text20.lightning.worker;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.dfki.km.text20.lightning.MainClass;

/**
 * @author Christoph Käding
 *
 */
public class WarpCommander {

    private Timer timer;
    
    public WarpCommander() {
        MainClass.getInstance().getInternalPluginManager().getCurrentMouseWarper().initValues(10, 50, 100, 200, 20);
        
        this.timer = new Timer(100, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                MainClass.getInstance().getInternalPluginManager().getCurrentMouseWarper().addMousePosition(MouseInfo.getPointerInfo().getLocation());
            }
        });
    }
 
    public void start() {
        this.timer.start();
    }
    
    public void stop() {
        this.timer.stop();
    }
    
}

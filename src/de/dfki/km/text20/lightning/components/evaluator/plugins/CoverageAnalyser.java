/*
 * CoverageAnalyser.java
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
package de.dfki.km.text20.lightning.components.evaluator.plugins;

import java.awt.image.BufferedImage;

import de.dfki.km.text20.lightning.plugins.CommonPluginInterface;

import net.xeoh.plugins.base.Plugin;

/**
 * Interface for different algorithms of text coverage analyse.
 * 
 * @author Christoph Käding
 */
public interface CoverageAnalyser extends Plugin, CommonPluginInterface{
    
         /**
         * Analyzes the given screenshot and returns the percentage of text coverage.
         * 
         * @param screenShot
         * @return percentage of text coverage
         */
        public double analyse(BufferedImage screenShot);
}

/*
 * FakePositionFinder.java
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
package de.dfki.km.text20.lightning.plugins.saliency.impl.dummy;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * The name says everything ;)
 * 
 * @author Christoph Käding
 * 
 */
@PluginImplementation
public class FakePositionFinder implements SaliencyDetector {


    private PluginInformation information = new PluginInformation("Dummy Filter","returns always 0,0");
    
    /** */
    public FakePositionFinder() {}

    /*
     * (non-Javadoc)
     * 
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#analyse(java.awt.image.BufferedImage)
     */
    @Override
    public Point analyse(BufferedImage derivatedScreenShot) {
        return new Point(0, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }
}

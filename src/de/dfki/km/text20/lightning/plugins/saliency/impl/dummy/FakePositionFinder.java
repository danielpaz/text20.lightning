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
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyPluginInformation;

/**
 * The name says everything ;)
 * 
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class FakePositionFinder implements SaliencyDetector {

    /** */
    public FakePositionFinder() {
    }

    @Override
    public Point analyse(BufferedImage derivatedScreenShot) {
        return new Point(0,0);
    }

    @Override
    public SaliencyPluginInformation getInformation() {
        return new SaliencyPluginInformation();
    }

    @Override
    public String getDisplayName() {
        return new String("fake method");
    }
}

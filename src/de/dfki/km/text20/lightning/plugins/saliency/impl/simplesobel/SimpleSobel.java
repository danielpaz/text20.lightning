/*
 * SimpleSobel.java
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
package de.dfki.km.text20.lightning.plugins.saliency.impl.simplesobel;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;

/**
 * Simple implementation of the algorithm to search sth. interesting around the provided point on the screen.
 * 
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class SimpleSobel implements SaliencyDetector {

    /** information object*/
    private PluginInformation information;

    /**
     * creates new simple sobel object
     */
    public SimpleSobel() {
        this.information = new PluginInformation("Simple Sobel", "Simple Sobel");
    }
    
    /**
     * Derivates the given screenshot in y-direction. 
     * This painted pixels which have any different color as their following ones in white or grey color.
     * All the other ones will be colored black.
     */
    private BufferedImage derivate(BufferedImage screenShot) {

        BufferedImage derivatedScreenShot = new BufferedImage(screenShot.getHeight(), screenShot.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        // Go through each pixel position in the derivate image ... 
        for (int x = 0; x < screenShot.getHeight(); x++) {
            for (int y = 0; y < screenShot.getHeight() - 1; y++) {
                // ... and subtract the grey value of a pixel by the value of the following one to 
                // derivate the screenshot in y-direction
                derivatedScreenShot.setRGB(x, y, (screenShot.getRGB(x, y) & 0xFF) - (screenShot.getRGB(x, y + 1) & 0xFF));
            }
        }
        return derivatedScreenShot;
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#analyse(java.awt.image.BufferedImage)
     */
    // TODO: add comments
    @Override
    public Point analyse(BufferedImage screenShot) {
        int direction = 0;
        int size = 1;

        //encoding for direction:
        // 0 = down
        // 1 = left
        // 2 = up
        // 3 = right 
        final BufferedImage derivatedScreenShot = derivate(screenShot);
        final Point point = new Point(derivatedScreenShot.getHeight() / 2, derivatedScreenShot.getHeight() / 2);
        final Point offset = new Point(0, 0);

        while ((size < derivatedScreenShot.getHeight()) && (Math.abs(offset.x) < derivatedScreenShot.getHeight() / 2) && (Math.abs(offset.y) < derivatedScreenShot.getHeight() / 2)) {
            switch (direction) {
            case 0:
                for (int y = 0; (y < size) && (offset.y < derivatedScreenShot.getHeight() / 2 - 1); y++) {
                    offset.setLocation(offset.x, offset.y + 1);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                break;

            case 1:
                for (int x = 0; (x < size) && (-offset.x < derivatedScreenShot.getHeight() / 2 - 1); x++) {
                    offset.setLocation(offset.x - 1, offset.y);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                size++;
                break;

            case 2:
                for (int y = 0; (y < size) && (-offset.y < derivatedScreenShot.getHeight() / 2 - 1); y++) {
                    offset.setLocation(offset.x, offset.y - 1);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                break;

            case 3:
                for (int x = 0; (x < size) && (offset.x < derivatedScreenShot.getHeight() / 2 - 1); x++) {
                    offset.setLocation(offset.x + 1, offset.y);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                size++;
                break;
            }
        }
        offset.setLocation(0, 0);
        return offset;
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

}

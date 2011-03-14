/*
 * ImprovedSimpleSobel.java
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
package de.dfki.km.text20.lightning.plugins.saliency.impl.improvedsimplesobel.copy;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

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
public class ImprovedSimpleSobel implements SaliencyDetector {

    /** information object*/
    private PluginInformation information;

    /**
     * creates new simple sobel object
     */
    public ImprovedSimpleSobel() {
        this.information = new PluginInformation("Improved Simple Sobel", "Improved Simple Sobel");
    }

    /**
     * Derivates the given screenshot in y-direction. 
     * This painted pixels which have any different color as their following ones in white or grey color.
     * All the other ones will be colored black.
     */
    private BufferedImage derivate(BufferedImage screenShot) {
        
        ColorConvertOp converter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        screenShot = converter.filter(screenShot, null);
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
    /**
     * A kind of spiral grows from the fixation point and returns the nearest point which is not black (works with derivated screenshot).
     */
    @Override
    public Point analyse(BufferedImage screenShot) {

        //encoding for direction:
        // 0 = down
        // 1 = left
        // 2 = up
        // 3 = right
        int direction = 0;

        // size of the spiral in one direction
        int size = 1;

        // derivated screenshot
        BufferedImage derivatedScreenShot = derivate(screenShot);

        // fixation point
        Point point = new Point(derivatedScreenShot.getHeight() / 2, derivatedScreenShot.getHeight() / 2);

        // indicates if a non-black-point had found
        boolean found = false;

        // temporary point which represents the current point of the spiral
        Point temp = new Point(0, 0);

        // offset from fixation to calculated target
        Point offset = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        // grows till the whole screenshot is checked
        while ((size < derivatedScreenShot.getHeight()) && (Math.abs(temp.x) < derivatedScreenShot.getHeight() / 2) && (Math.abs(temp.y) < derivatedScreenShot.getHeight() / 2)) {
            
         // grow in every direction
            switch (direction) {
            
            // run through line
            case 0:
                for (int y = 0; (y < size) && (temp.y < derivatedScreenShot.getHeight() / 2 - 1); y++) {
                    
                    // move temporary point
                    temp.setLocation(temp.x, temp.y + 1);

                    // check if something which is not black there
                    if ((derivatedScreenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {
                        
                        // check if the point is closer than the current offset
                        if (new Point(0,0).distance(temp) < new Point(0,0).distance(offset)) {
                            
                            // set new offset
                            offset.setLocation(temp);
                            
                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }
                
                // if a point was found, return it
                if (found) return offset;

                // change direction
                direction++;
                direction = direction % 4;
                break;

            case 1:
                
                // run through line
                for (int x = 0; (x < size) && (-temp.x < derivatedScreenShot.getHeight() / 2 - 1); x++) {
                    
                    // move temporary point
                    temp.setLocation(temp.x - 1, temp.y);

                    // check if something which is not black there
                    if ((derivatedScreenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {
                        
                        // check if the point is closer than the current offset
                        if (new Point(0,0).distance(temp) < new Point(0,0).distance(offset)) {
                            
                            // set new offset
                            offset.setLocation(temp);
                            
                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }
                
                // if a point was found, return it
                if (found) return offset;

                // change direction
                direction++;
                direction = direction % 4;
                
                // increase size
                size++;
                break;

            case 2:
                
                // run through line
                for (int y = 0; (y < size) && (-temp.y < derivatedScreenShot.getHeight() / 2 - 1); y++) {
                    
                    // move temporary point
                    temp.setLocation(temp.x, temp.y - 1);

                    // check if something which is not black there
                    if ((derivatedScreenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {
                        
                        // check if the point is closer than the current offset
                        if (new Point(0,0).distance(temp) < new Point(0,0).distance(offset)) {
                            
                            // set new offset
                            offset.setLocation(temp);
                            
                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }
                
                // if a point was found, return it
                if (found) return offset;

                // change direction
                direction++;
                direction = direction % 4;
                break;

            case 3:
                
                // run through line
                for (int x = 0; (x < size) && (temp.x < derivatedScreenShot.getHeight() / 2 - 1); x++) {
                    
                    // move temporary point
                    temp.setLocation(temp.x + 1, temp.y);

                    // check if something which is not black there
                    if ((derivatedScreenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {
                        
                        // check if the point is closer than the current offset
                        if (new Point(0,0).distance(temp) < new Point(0,0).distance(offset)) {
                            
                            // set new offset
                            offset.setLocation(temp);
                            
                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }
                
                // if a point was found, return it
                if (found) return offset;

                // change direction
                direction++;
                direction = direction % 4;
                
                // increase size
                size++;
                break;
            }
        }

        // if nothing had found
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

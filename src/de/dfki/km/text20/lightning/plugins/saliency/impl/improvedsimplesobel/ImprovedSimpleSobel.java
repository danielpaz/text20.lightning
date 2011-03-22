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
package de.dfki.km.text20.lightning.plugins.saliency.impl.improvedsimplesobel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;

import javax.imageio.ImageIO;

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

    /** current timestamp for debugging */
    private long timestamp;

    /**
     * creates new simple sobel object
     */
    public ImprovedSimpleSobel() {
        this.information = new PluginInformation("Improved Simple Sobel", "Improved Simple Sobel", false);
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
        // set current timestamp
        this.timestamp = System.currentTimeMillis();

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

                        // TODO: comment this out, its only for debugging
//                        this.drawPicture(derivatedScreenShot, new Point(point.x + temp.x, point.y + temp.y));

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < new Point(point.x + offset.x, point.y + offset.y).distance(point)) {

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

                        // TODO: comment this out, its only for debugging
//                        this.drawPicture(derivatedScreenShot, new Point(point.x + temp.x, point.y + temp.y));

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < new Point(point.x + offset.x, point.y + offset.y).distance(point)) {

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

                        // TODO: comment this out, its only for debugging
//                        this.drawPicture(derivatedScreenShot, new Point(point.x + temp.x, point.y + temp.y));

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < new Point(point.x + offset.x, point.y + offset.y).distance(point)) {

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

                        // TODO: comment this out, its only for debugging
//                        this.drawPicture(derivatedScreenShot, new Point(point.x + temp.x, point.y + temp.y));

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < new Point(point.x + offset.x, point.y + offset.y).distance(point)) {

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

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#getGui()
     */
    @Override
    public void showGui() {
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#shutDown()
     */
    @Override
    public void stop() {
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#start()
     */
    @Override
    public void start() {
    }

    /**
     * writes given point in given image to tmp-directory
     * only for debugging
     * 
     * @param screenShot
     * @param point
     */
    @SuppressWarnings("unused")
    private void drawPicture(BufferedImage screenShot, Point point) {
        // initialize variables
        int dimension = screenShot.getHeight();
        File file = new File("tmp/IMprovedSimpleSobel_" + this.timestamp + ".png");
        boolean alreadyExists = file.exists();

        try {
            // if the screenshot file already exists, the given screenshot is overwritten by the existing one to update new data 
            if (alreadyExists) screenShot = ImageIO.read(file);

            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            if (!alreadyExists) {
                // visualize fixation point 
                graphic.setColor(new Color(255, 255, 0, 255));
                graphic.drawOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);
                graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + dimension / 2, 12 + dimension / 2);
                graphic.setColor(new Color(255, 255, 0, 32));
                graphic.fillOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);
            }

            // visualize calculations
            graphic.setColor(new Color(0, 128, 128, 255));
            graphic.drawOval(point.x - 5, point.y - 5, 10, 10);
            graphic.setColor(new Color(0, 128, 128, 32));
            graphic.fillOval(point.x - 5, point.y - 5, 10, 10);

            // write the image
            file.mkdirs();
            ImageIO.write(screenShot, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

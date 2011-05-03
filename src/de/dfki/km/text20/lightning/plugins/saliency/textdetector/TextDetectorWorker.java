/*
 * TextDetectorWorker.java
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
package de.dfki.km.text20.lightning.plugins.saliency.textdetector;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * @author Christoph Käding
 *
 */
public class TextDetectorWorker {

    /**
     * equal to StandartSobel
     * 
     * @param screenShot
     * @return offset
     */
    public Point normalAnalyse(BufferedImage screenShot) {
        //encoding for direction:
        // 0 = down
        // 1 = left
        // 2 = up
        // 3 = right
        int direction = 0;

        // size of the spiral in one direction
        int size = 1;

        // temporary stored distance between founded point and fixation
        double storedDistance = Double.MAX_VALUE;

        // fixation point
        Point point = new Point(screenShot.getHeight() / 2, screenShot.getHeight() / 2);

        // indicates if a non-black-point had found
        boolean found = false;

        // temporary point which represents the current point of the spiral
        Point temp = new Point(0, 0);

        // offset from fixation to calculated target
        Point offset = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        // grows till the whole screenshot is checked
        while ((size < screenShot.getHeight()) && (Math.abs(temp.x) < screenShot.getHeight() / 2) && (Math.abs(temp.y) < screenShot.getHeight() / 2)) {

            // grow in every direction
            switch (direction) {

            case 0:
                // check if it is possible to find a better offset
                if (storedDistance < temp.x) return offset;

                // run through line
                for (int y = 0; (y < size) && (temp.y < screenShot.getHeight() / 2 - 1); y++) {

                    // move temporary point
                    temp.setLocation(temp.x, temp.y + 1);

                    // check if something which is not black there
                    if ((screenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < storedDistance) {

                            // set new offset
                            offset.setLocation(temp);

                            // store distance
                            storedDistance = new Point(point.x + offset.x, point.y + offset.y).distance(point);

                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }

                // change direction
                direction++;
                direction = direction % 4;
                break;

            case 1:
                // check if it is possible to find a better offset
                if (storedDistance < temp.y) return offset;

                // run through line
                for (int x = 0; (x < size) && (-temp.x < screenShot.getHeight() / 2 - 1); x++) {

                    // move temporary point
                    temp.setLocation(temp.x - 1, temp.y);

                    // check if something which is not black there
                    if ((screenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < storedDistance) {

                            // set new offset
                            offset.setLocation(temp);

                            // store distance
                            storedDistance = new Point(point.x + offset.x, point.y + offset.y).distance(point);

                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }

                // change direction
                direction++;
                direction = direction % 4;

                // increase size
                size++;
                break;

            case 2:
                // check if it is possible to find a better offset
                if (storedDistance < -temp.x) return offset;

                // run through line
                for (int y = 0; (y < size) && (-temp.y < screenShot.getHeight() / 2 - 1); y++) {

                    // move temporary point
                    temp.setLocation(temp.x, temp.y - 1);

                    // check if something which is not black there
                    if ((screenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < storedDistance) {

                            // set new offset
                            offset.setLocation(temp);

                            // store distance
                            storedDistance = new Point(point.x + offset.x, point.y + offset.y).distance(point);

                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }

                // change direction
                direction++;
                direction = direction % 4;
                break;

            case 3:
                // check if it is possible to find a better offset
                if (storedDistance < -temp.y) return offset;

                // run through line
                for (int x = 0; (x < size) && (temp.x < screenShot.getHeight() / 2 - 1); x++) {

                    // move temporary point
                    temp.setLocation(temp.x + 1, temp.y);

                    // check if something which is not black there
                    if ((screenShot.getRGB(point.x + temp.x, point.y + temp.y) & 0xFF) > 0) {

                        // check if the point is closer than the current offset
                        if (new Point(point.x + temp.x, point.y + temp.y).distance(point) < storedDistance) {

                            // set new offset
                            offset.setLocation(temp);

                            // store distance
                            storedDistance = new Point(point.x + offset.x, point.y + offset.y).distance(point);

                            // indicate that a non-black-point was found
                            found = true;
                        }
                    }
                }

                // change direction
                direction++;
                direction = direction % 4;

                // increase size
                size++;
                break;
            }
        }

        // if nothing had found
        if (!found) offset.setLocation(0, 0);

        return offset;
    }

    /**
     * @param boxes 
     * @param height 
     * @return offset
     */
    @SuppressWarnings("rawtypes")
    public Point textAnalyse(LinkedList boxes, int height) {
        Point fixation = new Point(height / 2, height / 2);
        Point offset = new Point(0, 0);
        Point tmp = new Point();
        double minDistance = Double.MAX_VALUE;

        for (Object textRegion : boxes) {
            if (textRegion instanceof TextRegion) {
                if ((Math.abs(((TextRegion) textRegion).y1 - height / 2) <= minDistance) || (Math.abs(((TextRegion) textRegion).y2 - height / 2) <= minDistance))
                    for (int i = 0; i < ((TextRegion) textRegion).width(); i++) {
                        tmp.setLocation(((TextRegion) textRegion).x1 + i, ((TextRegion) textRegion).y1 + ((TextRegion) textRegion).height() / 2);
                        if (tmp.distance(fixation) < minDistance) {
                            offset.setLocation(tmp);
                            minDistance = tmp.distance(fixation);
                        }
                    }
            }
        }
        offset.translate(-height / 2, -height / 2);

        return offset;
    }
}

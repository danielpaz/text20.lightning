/*
 * TrainingsContainer.java
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
package de.dfki.km.text20.lightning.worker.training;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * @author Christoph Käding
 *
 */
public class DataContainer implements Serializable {
    
    @Element
    private Point mouseOffset;
    
    @Attribute
    private String user;
    
    @Attribute
    private long timestamp;
    
    public DataContainer(String user, long timestamp, Point mouseOffset){
        this.user = user;
        this.timestamp = timestamp;
        this.mouseOffset = mouseOffset;
    }

    /**
     * @return the mouseOffset
     */
    public Point getMouseOffset() {
        return this.mouseOffset;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return this.user;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }
    
    
}

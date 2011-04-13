/*
 * EvaluationCode.java
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
package de.dfki.km.text20.lightning.worker.evaluationmode;

/**
 * return codes for evaluation mode
 * 
 * @author Christoph Käding
 *
 */
public enum EvaluationCode {
    /**
     * if no fixation is stored
     * theoretical never reached
     */
    NO_FIXATION, 
    
    /**
     * if the evaluation process is already running
     */
    ALREADY_PROCESSING, 
    
    /**
     * if the mouse was out of dimension
     */
    OUT_OFF_DIMENSION, 
    
    /**
     * if all things are fine
     */
    OK;
}

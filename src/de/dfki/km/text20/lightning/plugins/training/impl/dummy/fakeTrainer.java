/*
 * fakeTrainer.java
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
package de.dfki.km.text20.lightning.plugins.training.impl.dummy;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.training.Trainer;

/**
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class fakeTrainer implements Trainer{

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.training.Trainer#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        final PluginInformation information = new PluginInformation();
        information.displayName = "Fake Trainer";
        
        return information;
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.training.Trainer#setStep(java.awt.image.BufferedImage, java.awt.Point)
     */
    @Override
    public void setStep(BufferedImage screenShot, Point target) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.training.Trainer#leaveTraing()
     */
    @Override
    public void leaveTraining() {
        // TODO Auto-generated method stub
        
    }

}

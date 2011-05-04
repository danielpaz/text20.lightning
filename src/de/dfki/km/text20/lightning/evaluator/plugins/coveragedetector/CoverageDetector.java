/*
 * CoverageDetectorConfig.java
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
package de.dfki.km.text20.lightning.evaluator.plugins.coveragedetector;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.evaluator.plugins.CoverageAnalyser;
import de.dfki.km.text20.lightning.evaluator.plugins.coveragedetector.gui.CoverageDetectorConfigImpl;
import de.dfki.km.text20.lightning.plugins.PluginInformation;

/**
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class CoverageDetector implements CoverageAnalyser {

    private PluginInformation information;

    private CoverageDetectorProperties properties;

    private long timeStamp;

    private GetImageText analyzer;

    private LinkedList<TextRegion> boxes;
    
    /**
     * 
     */
    public CoverageDetector() {
        this.information = new PluginInformation("Coverage Detector", "alpha 0.1", true);
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#showGui()
     */
    @Override
    public JFrame getGui() {
        return new CoverageDetectorConfigImpl();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#start()
     */
    @Override
    public void start() {
        this.properties = CoverageDetectorProperties.getInstance();
        this.timeStamp = System.currentTimeMillis();
        this.boxes = new LinkedList<TextRegion>();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#stop()
     */
    @Override
    public void stop() {
        this.properties.writeProperties();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#analyse(java.awt.image.BufferedImage)
     */
    @Override
    public double analyse(BufferedImage screenShot) {
        // initialize variables
        int textSize = 0;
        this.analyzer = new GetImageText(screenShot, this.properties.getLetterHeight(), this.properties.getLineSize(), this.properties.getSenitivity());
        for (Object textRegion : this.analyzer.getTextBoxes()) {
            if (textRegion instanceof TextRegion) {
                this.boxes.add((TextRegion) textRegion);
            }
        }

        if (this.properties.isDebug()) {
            try {
                new File("./plugins/TextDetector/debug/Session_" + this.timeStamp).mkdirs();
                ImageIO.write(this.analyzer.isolateText(this.boxes), "png", new File("./plugins/TextDetector/debug/Session_" + this.timeStamp + "/" + System.currentTimeMillis() + "_TextDetectorDebug.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // calculate coverage
        for (TextRegion textRegion : this.boxes) {
            textSize = textSize + textRegion.width() * textRegion.height();
        }
        
        // reset boxes
        this.boxes.clear();
        
        return ((double) 100 / (double) (screenShot.getWidth() * screenShot.getHeight())) * textSize;
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

}

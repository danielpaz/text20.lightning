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
    @SuppressWarnings("rawtypes")
    @Override
    public double analyse(BufferedImage screenShot) {
        // initialize variables
        int textSize = 0;
        GetImageText analyzer = new GetImageText(screenShot, this.properties.getLetterHeight(), this.properties.getLineSize(), this.properties.getSenitivity());
        LinkedList boxes = analyzer.getTextBoxes();

        if (this.properties.isDebug()) {
            try {
                new File("./plugins/CoverageDetector/debug/Session_" + this.timeStamp).mkdirs();
                ImageIO.write(analyzer.isolateText(boxes), "png", new File("./plugins/CoverageDetector/debug/Session_" + this.timeStamp + "/" + System.currentTimeMillis() + "_TextDetectorDebug.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // calculate coverage
        for (Object textRegion : boxes) {
            if (textRegion instanceof TextRegion) {
                textSize = textSize + (((TextRegion) textRegion).width() * ((TextRegion) textRegion).height());
            }
        }
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

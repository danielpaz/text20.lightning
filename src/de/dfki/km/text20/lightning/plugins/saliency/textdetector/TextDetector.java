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
package de.dfki.km.text20.lightning.plugins.saliency.textdetector;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.textdetector.gui.TextDetectorConfigImpl;

/**
 * Detector which analyses the text coverage of the given screenshot. If this coverage is higher than the threshold
 * a special textsearch is processed. If not, a method similar to StandartSobel is started.
 * 
 * @author Christoph Käding
 */
@PluginImplementation
public class TextDetector implements SaliencyDetector {

    /** stored information about this plugin */
    private PluginInformation information;

    /** stored configuration */
    private TextDetectorProperties properties;

    /** current time stamp */
    private long timeStamp;

    /**
     * creates new instance and initializes its variables
     */
    public TextDetector() {
        this.information = new PluginInformation("Text Detector", "alpha 0.1", true);
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#showGui()
     */
    @Override
    public JFrame getGui() {
        return new TextDetectorConfigImpl();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#start()
     */
    @Override
    public void start() {
        this.properties = TextDetectorProperties.getInstance();
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
    @Override
    public Point analyse(BufferedImage screenShot, Object... options) {
        // initialize variables
        int textSize = 0;
        double coverage = 0;
        double coverageThresh = this.properties.getThreshold();
        int height = this.properties.getLetterHeight();
        int width = this.properties.getLetterWidth();
        double sensitivity = this.properties.getSenitivity();
        int line = this.properties.getLineSize();

        // set variables
        for (Object option : options) {
            if (option instanceof String) {
                if (((String) option).startsWith("TDthreshold=")) {
                    coverageThresh = Double.parseDouble(((String) option).substring(((String) option).lastIndexOf("=") + 1));
                } else if (((String) option).startsWith("TDheight=")) {
                    height = Integer.parseInt(((String) option).substring(((String) option).lastIndexOf("=") + 1));
                } else if (((String) option).startsWith("TDwidth=")) {
                    width = Integer.parseInt(((String) option).substring(((String) option).lastIndexOf("=") + 1));
                } else if (((String) option).startsWith("TDsensitivity=")) {
                    sensitivity = Double.parseDouble(((String) option).substring(((String) option).lastIndexOf("=") + 1));
                } else if (((String) option).startsWith("TDline=")) {
                    line = Integer.parseInt(((String) option).substring(((String) option).lastIndexOf("=") + 1));
                }
            }
        }

        GetImageText analyser = new GetImageText(screenShot, height, line, sensitivity);
        LinkedList<TextRegion> boxes = new LinkedList<TextRegion>();

        // get text boxes
        for (Object textRegion : analyser.getTextBoxes()) {
            if (textRegion instanceof TextRegion)
                if ((((TextRegion) textRegion).width() >= width) && (((TextRegion) textRegion).height() >= height))
                    boxes.add((TextRegion) textRegion);
        }

        // calculate coverage
        for (TextRegion textRegion : boxes) {
            textSize = textSize + textRegion.width() * textRegion.height();
        }
        coverage = ((double) 100 / (double) (screenShot.getWidth() * screenShot.getHeight())) * textSize;

        // write image if debug is enabled
        if (this.properties.isDebug()) {
            try {
                new File("./plugins/TextDetector/debug/Session_" + this.timeStamp).mkdirs();
                ImageIO.write(analyser.isolateText(boxes), "png", new File("./plugins/TextDetector/debug/Session_" + this.timeStamp + "/" + System.currentTimeMillis() + "_TextDetectorDebug.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // decide which method should be used and return its results
        if (coverage > coverageThresh) { return new TextDetectorWorker().textAnalyse(analyser.getShrinkedBoxes(), screenShot.getHeight()); }
        return new TextDetectorWorker().normalAnalyse(analyser.getContrastImage());
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

}

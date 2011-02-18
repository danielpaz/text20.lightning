package de.dfki.km.text20.lightning.plugins.saliency.impl.dummy;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyPluginInformation;

/**
 * 
 * @author Christoph Kaeding
 *
 */
@PluginImplementation
public class FakePositionFinder implements SaliencyDetector {

    public FakePositionFinder() {
    }

    @Override
    public BufferedImage derivate(BufferedImage screenShot) {
        return screenShot;
    }

    @Override
    public Point analyse(BufferedImage derivatedScreenShot) {
        return new Point(0,0);
    }

    @Override
    public SaliencyPluginInformation getInformation() {
        return new SaliencyPluginInformation();
    }
}

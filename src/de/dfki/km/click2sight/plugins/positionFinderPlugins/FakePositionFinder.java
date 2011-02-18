package de.dfki.km.click2sight.plugins.positionFinderPlugins;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.click2sight.plugins.PositionFinder;

/**
 * 
 * @author Christoph Kaeding
 *
 */
@PluginImplementation
public class FakePositionFinder implements PositionFinder {

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
    public String getInformation() {
        return new String("fake method");
    }

    @Override
    public String toString() {
        return new String("fake method");
    }
}

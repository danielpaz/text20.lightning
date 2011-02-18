package de.dfki.km.text20.lightning.plugins.saliency.impl.simplesobel;

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
public class SimpleSobel implements SaliencyDetector {

    /**
     * Derivates the given screenshot in y-direction.
     */
    @Override
    public BufferedImage derivate(BufferedImage screenShot) {
        BufferedImage derivatedScreenShot = new BufferedImage(screenShot.getHeight(), screenShot.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < screenShot.getHeight(); x++) {
            for (int y = 0; y < screenShot.getHeight() - 1; y++) {
                derivatedScreenShot.setRGB(x, y, (screenShot.getRGB(x, y) & 0xFF) - (screenShot.getRGB(x, y + 1) & 0xFF));
            }
        }
        return derivatedScreenShot;
    }

    /**
     * Analyses the given screenshot. A kind of spiral grows from the middle of the image and gives the first point, which is not black back. 
     * 
     * encoding for direction:
     * 0 = down
     * 1 = left
     * 2 = up
     * 3 = right 
     */
    @Override
    public Point analyse(BufferedImage derivatedScreenShot) {
        int direction = 0;
        int size = 1;
        Point point = new Point(derivatedScreenShot.getHeight() / 2, derivatedScreenShot.getHeight() / 2);
        Point offset = new Point(0, 0);
        while ((size < derivatedScreenShot.getHeight()) && (Math.abs(offset.x) < derivatedScreenShot.getHeight() / 2) && (Math.abs(offset.y) < derivatedScreenShot.getHeight() / 2)) {
            switch (direction) {
            case 0:
                for (int y = 0; (y < size) && (offset.y < derivatedScreenShot.getHeight() / 2 - 1); y++) {
                    offset.setLocation(offset.x, offset.y + 1);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                break;

            case 1:
                for (int x = 0; (x < size) && (-offset.x < derivatedScreenShot.getHeight() / 2 - 1); x++) {
                    offset.setLocation(offset.x - 1, offset.y);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                size++;
                break;

            case 2:
                for (int y = 0; (y < size) && (-offset.y < derivatedScreenShot.getHeight() / 2 - 1); y++) {
                    offset.setLocation(offset.x, offset.y - 1);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                break;

            case 3:
                for (int x = 0; (x < size) && (offset.x < derivatedScreenShot.getHeight() / 2 - 1); x++) {
                    offset.setLocation(offset.x + 1, offset.y);
                    if ((derivatedScreenShot.getRGB(point.x + offset.x, point.y + offset.y) & 0xFF) > 0) { return offset; }
                }
                direction++;
                direction = direction % 4;
                size++;
                break;
            }
        }
        offset.setLocation(0, 0);
        return offset;
    }

    /**
     * some informations...
     */
    @Override
    public SaliencyPluginInformation getInformation() {
        return new SaliencyPluginInformation();
    }
}

package de.dfki.km.click2sight.plugins;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.xeoh.plugins.base.Plugin;

/**
 * Interface for different algorithms for screen analyzing.
 * 
 * @author Christoph Kaeding
 *
 */
public interface PositionFinder extends Plugin{

    /**
     * Processes the given screenshot to allow the analyze of it.
     *     
     * @param screenShot
     * @return derivated screenshot
     */
    public BufferedImage derivate(BufferedImage screenShot);
    
    /**
     * Analyzes the given processed screenshot and calculates an offset which is added to the fixation point.
     * 
     * @param derivatedScreenShot
     * @return offset of the next position which is realized as the real target
     */
    public Point analyse(BufferedImage derivatedScreenShot);
    
    /**
     * Returns some information about the plugin.
     * 
     * @return
     */
    public String getInformation();
    
    /**
     * Name which is shown in the comboboxes of the gui.
     * 
     * @return
     */
    @Override
    public String toString();
}

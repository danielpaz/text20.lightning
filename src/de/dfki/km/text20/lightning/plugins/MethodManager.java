package de.dfki.km.text20.lightning.plugins;

import java.util.ArrayList;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.impl.dummy.FakePositionFinder;
import de.dfki.km.text20.lightning.plugins.saliency.impl.simplesobel.SimpleSobel;

/**
 * All the given plugins were added and provided for use. Also switching of the active plugin is handeled. 
 * 
 * @author Christoph Kaeding
 */
public class MethodManager {

    /** */
    private PluginManager pluginManager;
    
    /** */
    private PluginManagerUtil pluginManagerUtil;
    
    /** */
    private SaliencyDetector currentPositionFinder;
    
    /** */
    private ArrayList<SaliencyDetector> positionFinder;

    /** */
    //TODO: change plugins to jars
    public MethodManager() {
        // Create a new plugin manager 
        this.pluginManager = PluginManagerFactory.createPluginManager();
        
        // Add internal plugins
        this.pluginManager.addPluginsFrom(new ClassURI(FakePositionFinder.class).toURI());
        this.pluginManager.addPluginsFrom(new ClassURI(SimpleSobel.class).toURI());
        
        
        this.pluginManagerUtil = new PluginManagerUtil(this.pluginManager);
        this.positionFinder = new ArrayList<SaliencyDetector>(this.pluginManagerUtil.getPlugins(SaliencyDetector.class));
        this.currentPositionFinder = this.positionFinder.get(0);
    }

    /** 
     * Returns the active pluginmanager to shut it down from outside to leave the program cleanly.
     * 
     * @return
     */
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    /**
     * 
     * @return
     */
    public SaliencyDetector getCurrentPositionFinder() {
        return this.currentPositionFinder;
    }

    /**
     * Returns a ArrayList of all the given plugins for position finding.
     * 
     * @return
     */
    public ArrayList<SaliencyDetector> getPositionFinder() {
        return this.positionFinder;
    }

    /**
     * Changes the active plugin to change the used method.
     * 
     * @param choice
     */
    public void setCurrentPositionFinder(SaliencyDetector choice) {
        this.currentPositionFinder = choice;
        System.out.println("search method: " + this.currentPositionFinder);
    }
}

package de.dfki.km.click2sight.plugins;

import java.util.ArrayList;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;
import de.dfki.km.click2sight.plugins.positionFinderPlugins.FakePositionFinder;
import de.dfki.km.click2sight.plugins.positionFinderPlugins.SimplePositionFinder;

/**
 * 
 * @author Christoph Kaeding
 *
 * All the given plugins were added and provided for use. Also switching of the active plugin is handeled. 
 */
public class MethodManager {

    /** */
    private PluginManager pluginManager;
    /** */
    private PluginManagerUtil pluginManagerUtil;
    /** */
    private PositionFinder currentPositionFinder;
    /** */
    private ArrayList<PositionFinder> positionFinder;

    /** */
    //TODO: change plugins to jars
    public MethodManager() {
        this.pluginManager = PluginManagerFactory.createPluginManager();
        this.pluginManager.addPluginsFrom(new ClassURI(FakePositionFinder.class).toURI());
        this.pluginManager.addPluginsFrom(new ClassURI(SimplePositionFinder.class).toURI());
        this.pluginManagerUtil = new PluginManagerUtil(this.pluginManager);
        this.positionFinder = new ArrayList<PositionFinder>(this.pluginManagerUtil.getPlugins(PositionFinder.class));
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
    public PositionFinder getCurrentPositionFinder() {
        return this.currentPositionFinder;
    }

    /**
     * Returns a ArrayList of all the given plugins for position finding.
     * 
     * @return
     */
    public ArrayList<PositionFinder> getPositionFinder() {
        return this.positionFinder;
    }

    /**
     * Changes the active plugin to change the used method.
     * 
     * @param choice
     */
    public void setCurrentPositionFinder(PositionFinder choice) {
        this.currentPositionFinder = choice;
        System.out.println("search method: " + this.currentPositionFinder);
    }
}

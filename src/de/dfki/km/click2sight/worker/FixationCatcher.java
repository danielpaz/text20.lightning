package de.dfki.km.click2sight.worker;

import java.awt.MouseInfo;
import java.net.URI;
import java.net.URISyntaxException;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;
import de.dfki.km.click2sight.MainClass;
import de.dfki.km.click2sight.tools.Hotkey;
import de.dfki.km.click2sight.tools.Tools;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluator;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluatorManager;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationEvent;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationEventType;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationListener;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDevice;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDeviceProvider;

/**
 * 
 * @author Christoph Kaeding
 *
 * Central class were the catching of fixations is done and its processing depending on the modus is started.
 */
public class FixationCatcher {

    /** */
    private FixationEvaluator fixationEvaluator;
    /** */
    private PluginManager pluginManager;
    /** */
    private EyeTrackingDeviceProvider deviceProvider;
    /** */
    private EyeTrackingDevice device;
    /** */
    private GazeEvaluatorManager evaluatorManager;
    /** */
    private GazeEvaluator evaluator;
    /** */
    private PrecisionTrainer precisionTrainer;

    /** */
    public FixationCatcher(FixationEvaluator evaluator, PrecisionTrainer trainer) {
        this.fixationEvaluator = evaluator;
        this.precisionTrainer = trainer;

        this.pluginManager = PluginManagerFactory.createPluginManager();
        try {
            this.pluginManager.addPluginsFrom(new URI("classpath://*"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            MainClass.exit();
        }
        this.deviceProvider = this.pluginManager.getPlugin(EyeTrackingDeviceProvider.class, new OptionCapabilities("eyetrackingdevice:trackingserver"));
        this.device = this.deviceProvider.openDevice("discover://nearest");
        this.evaluatorManager = this.pluginManager.getPlugin(GazeEvaluatorManager.class);
        this.evaluator = this.evaluatorManager.createEvaluator(this.device);
    }

    /**
     * The whole algorithm for fixation catching and its processing is started by a call of this method.   
     */
    public void startCatching() {
        this.evaluator.addEvaluationListener(new FixationListener() {

            @Override
            public void newEvaluationEvent(FixationEvent event) {
                if (MainClass.isActivated()) {
                    if (MainClass.isNormalMode()) {
                        if (event.getType() == FixationEventType.FIXATION_START) {
                            fixationEvaluator.setFixationPoint(event.getFixation().getCenter());
                        }
                        if (Hotkey.isTyped()) {
                            fixationEvaluator.evaluateLocation();
                            Hotkey.resetHotkeyTyped();
                        }
                    } else {
                        if (event.getType() == FixationEventType.FIXATION_START) {
                            precisionTrainer.setFixationPoint(event.getFixation().getCenter());
                        }
                        if (Hotkey.isTyped()) {
                            Hotkey.resetHotkeyTyped();
                            MainClass.showTrayMessage("training", "fixation position recognized, now place the mouse to the point you look at and press " + Hotkey.getCurrentHotkey(1) + " again...");
                            while (!Hotkey.isTyped() && !MainClass.isNormalMode())
                                ;
                            if (!MainClass.isNormalMode()) {
                                precisionTrainer.setMousePosition(MouseInfo.getPointerInfo().getLocation());
                                MainClass.showTrayMessage("training", "mouse position recognized, now look to the next point and press " + Hotkey.getCurrentHotkey(1) + " again...");
                            }
                            Hotkey.resetHotkeyTyped();
                        }
                    }
                }
            }
        });
    }
}

/*
 * FixationCatcher.java
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
 *
 */
package de.dfki.km.text20.lightning.worker;

import java.awt.MouseInfo;

import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;
import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.hotkey.Hotkey;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;
import de.dfki.km.text20.lightning.worker.clickTo.FixationEvaluator;
import de.dfki.km.text20.lightning.worker.training.PrecisionTrainer;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluator;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluatorManager;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationEvent;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationEventType;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationListener;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDevice;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDeviceProvider;

/**
 * Central class were catching of fixations is done and its processing depending on the modus is started.
 * 
 * @author Christoph Käding
 */
public class FixationCatcher {

    /** this is necessary for move the mouse and click in normal mode */
    FixationEvaluator fixationEvaluator;

    /** evaluates eyetracking events */
    private GazeEvaluator evaluator;

    /** this is needed for trainings mode */
    private PrecisionTrainer precisionTrainer;

    /** indicates if the tracking device is working probably */
    private boolean status;

    /** internal used plugin manager */
    private InternalPluginManager manager;

    /** singleton instance of the main class */
    private MainClass main;

    /**
     * create fixation catcher
     * 
     * @param evaluator
     * @param trainer
     */
    public FixationCatcher(FixationEvaluator evaluator, PrecisionTrainer trainer) {
        this.fixationEvaluator = evaluator;
        this.precisionTrainer = trainer;
        this.main = MainClass.getInstance();
        this.manager = MainClass.getInstance().getInternalPluginManager();

        // create stuff which is needed to get eyetracking data
        EyeTrackingDeviceProvider deviceProvider = this.main.getPluginManager().getPlugin(EyeTrackingDeviceProvider.class, new OptionCapabilities("eyetrackingdevice:trackingserver"));
        EyeTrackingDevice device = deviceProvider.openDevice("discover://nearest");
        GazeEvaluatorManager evaluatorManager = this.main.getPluginManager().getPlugin(GazeEvaluatorManager.class);
        this.evaluator = evaluatorManager.createEvaluator(device);

        if (device == null) {
            String msg = new String("Trackingserver was not found! Please start it and restart this tool.");
            this.main.showTrayMessage(msg);
            System.out.println(msg);
            this.main.getChannel().status(msg);
            this.status = false;
        } else {
            this.status = true;
        }
    }

    /**
     * shows status of tracking device
     * 
     * @return true if it is working probably
     */
    public boolean getStatus() {
        return this.status;
    }

    /**
     * The whole algorithm for fixation catching and its processing is started by a call of this method.
     */
    public void startCatching() {
        this.evaluator.addEvaluationListener(new FixationListener() {

            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            @Override
            public void newEvaluationEvent(FixationEvent event) {
                if (!main.isActivated()) return;
                if (event.getType() != FixationEventType.FIXATION_START) return;

                // if the tool is activated and a fixation occurs, it will be stored 
                fixationEvaluator.setFixationPoint(event.getFixation().getCenter());
                precisionTrainer.setFixationPoint(event.getFixation().getCenter());
                if (manager.getCurrentMouseWarper() != null)
                    manager.getCurrentMouseWarper().setFixationPoint(event.getFixation().getCenter());
            }
        });
    }
}

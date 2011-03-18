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

import java.util.ArrayList;

import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;
import de.dfki.km.text20.lightning.MainClass;
import de.dfki.km.text20.lightning.plugins.InternalPluginManager;
import de.dfki.km.text20.lightning.worker.clickTo.FixationEvaluator;
import de.dfki.km.text20.lightning.worker.training.PrecisionTrainer;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluator;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluatorManager;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationEvent;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationEventType;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.fixation.FixationListener;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.raw.RawDataEvent;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.raw.RawDataListener;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDevice;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDeviceProvider;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingEventValidity;

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

    /** indicates if the current fixation is valid */
    private boolean isValid;

    /** a list of last fixation events */
    private ArrayList<RawDataEvent> lastEvents;

    /** list of things which will be proved to check validity */
    private EyeTrackingEventValidity[] eventValidity;
    
    /** current size of pupils
     *  0 = left
     *  1 = right
     */
    private float[] pupils;

    /**
     * create fixation catcher
     * 
     * @param evaluator
     * @param trainer
     */
    public FixationCatcher(FixationEvaluator evaluator, PrecisionTrainer trainer) {
        // initialize variables
        this.fixationEvaluator = evaluator;
        this.precisionTrainer = trainer;
        this.main = MainClass.getInstance();
        this.manager = MainClass.getInstance().getInternalPluginManager();
        this.isValid = true;
        this.pupils = new float[2];
        this.lastEvents = new ArrayList<RawDataEvent>();
        this.eventValidity = new EyeTrackingEventValidity[6];
        this.eventValidity[0] = EyeTrackingEventValidity.CENTER_POSITION_VALID;
        this.eventValidity[1] = EyeTrackingEventValidity.HEAD_POSITION_VALID;
        this.eventValidity[2] = EyeTrackingEventValidity.LEFT_EYE_POSITION_VALID;
        this.eventValidity[3] = EyeTrackingEventValidity.LEFT_GAZE_POSITION_VALID;
        this.eventValidity[4] = EyeTrackingEventValidity.RIGHT_EYE_POSITION_VALID;
        this.eventValidity[5] = EyeTrackingEventValidity.RIGHT_GAZE_POSITION_VALID;

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
        // add fixation listener
        this.evaluator.addEvaluationListener(new FixationListener() {

            @SuppressWarnings({ "unqualified-field-access", "synthetic-access" })
            @Override
            public void newEvaluationEvent(FixationEvent event) {
                // check if the fixation should be stored
                if (!main.isActivated()) return;
                if (event.getType() != FixationEventType.FIXATION_START) return;
                if (!isValid) return;

                // if the tool is activated and a fixation occurs, it will be stored 
                fixationEvaluator.setFixationPoint(event.getFixation().getCenter());
                precisionTrainer.setFixationPoint(event.getFixation().getCenter());
                if (manager.getCurrentMouseWarper() != null)
                    manager.getCurrentMouseWarper().setFixationPoint(event.getFixation().getCenter());
            }
        });

        //add rawdata listener
        this.evaluator.addEvaluationListener(new RawDataListener() {

            @SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
            @Override
            public void newEvaluationEvent(RawDataEvent event) {
                // check if the tool is running
                if (!main.isActivated()) return;
                
                // reset status
                isValid = true;
                
                // add current event to storage
                lastEvents.add(event);
                
                // cut the storage down
                if (lastEvents.size() > 10) lastEvents.remove(0);
                
                // check validity of storage
                for (RawDataEvent storedEvent : lastEvents) {
                    isValid &= storedEvent.getTrackingEvent().areValid(eventValidity);
                }
//                System.out.println(isValid);
                
                // set pupil size
                pupils[0] = event.getTrackingEvent().getPupilSizeLeft();
                pupils[1] = event.getTrackingEvent().getPupilSizeRight();
                main.setPupils(pupils);
                
                // set valid
                main.setTrackingValid(isValid);
            }

            @Override
            public boolean requireUnfilteredEvents() {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }
}

package de.dfki.km.click2sight;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDevice;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingDeviceProvider;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingEvent;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingListener;

public class OtherClass implements KeyListener, HotkeyListener{

	boolean fire = false;
	
	void doSth() throws Exception {
		// Start the framework
		PluginManager pluginManager = PluginManagerFactory.createPluginManager();
		pluginManager.addPluginsFrom(new URI("classpath://*"));
		// Obtain a tracking device
		EyeTrackingDeviceProvider deviceProvider = pluginManager.getPlugin(EyeTrackingDeviceProvider.class, new OptionCapabilities("eyetrackingdevice:trackingserver"));
		EyeTrackingDevice device = deviceProvider.openDevice("discover://nearest");
		
		final Robot robot = new Robot();
		JIntellitype.getInstance();
		JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN, (int)'A');
		JIntellitype.getInstance().addHotKeyListener(this);

		
		// Register a listener for new eye tracking data
		device.addTrackingListener(new EyeTrackingListener() {
			
			public void newTrackingEvent(EyeTrackingEvent event) {
				if(fire){
					Point location = MouseInfo.getPointerInfo().getLocation();
					robot.mouseMove(event.getGazeCenter().x, event.getGazeCenter().y);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.delay(5);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.mouseMove(location.x, location.y);
					fire = false;
				}		        
			}				
		});	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stubaa
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ALT) {
			fire = true;
		}
		System.out.println(e);
	}

	@Override
	public void onHotKey(int arg0) {
		fire = true;
		
	}
}

package de.dfki.km.click2sight;

import java.net.URISyntaxException;

public class MainClass {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 */	
	public static void main(String[] args) {
		OtherClass oc = new OtherClass();
		try {
		oc.doSth();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

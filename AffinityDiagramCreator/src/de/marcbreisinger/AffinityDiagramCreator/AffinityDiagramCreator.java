package de.marcbreisinger.AffinityDiagramCreator;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.apple.eawt.Application;

import de.marcbreisinger.AffinityDiagramCreator.OsCheck.OSType;


public class AffinityDiagramCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(OsCheck.getOperatingSystemType() == OSType.MacOS){
			System.setProperty("de.marcbreisinger.AffinityDiagramCreator.apple.menu.about.name", "Affinity Diagram Creator");
			
			Application application = Application.getApplication();
			URL url = AffinityDiagramCreator.class.getResource("icon.gif");
			Image image = null;
			try {
				image = ImageIO.read(url);
				application.setDockIconImage(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new GUI();
            }
        });

	}

}

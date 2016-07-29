package de.marcbreisinger.AffinityDiagramCreator;

import java.util.Observable;

public class Model extends Observable {
	private static double[] zoomStages = {0.01, 0.015, 0.02, 0.03, 0.04, 0.05, 0.0625, 0.0833, 0.125, 0.1667, 0.25, 0.3333, 0.5, 0.6667, 0.825, 1.0, 2.0, 3.0, 4.0};
	public static final int maxZoomIndex = 15;
	private static int zoomFactorIndex = 15;
	
	public double getZoomFactor(){
		return zoomStages[zoomFactorIndex];
	}
	public int getZoomFactorIndex(){
		return zoomFactorIndex;
	}
	public int setZoomFactorIndex(int i){
		
		if(i>maxZoomIndex)zoomFactorIndex = maxZoomIndex;
		else if(i>0)zoomFactorIndex = i;
		setChanged();
		notifyObservers();
		clearChanged();
		return zoomFactorIndex;
	}
}

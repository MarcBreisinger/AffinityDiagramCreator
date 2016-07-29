package de.marcbreisinger.AffinityDiagramCreator;

public class Level3 {
	
	String[] data;
	String title;
	Level4[] children;
	
	public Level3(String[] data, int startIndex){
		this.data = data;
		title = data[startIndex].substring(2, data[startIndex].length() - 1);
		int numChildren = 0;
		for(int i = startIndex+1; i < data.length; i++){
			if(data[i].startsWith(",,,")){
				numChildren++;
			} else {//Level1
				break;
			}
		}
		children = new Level4[numChildren];
		numChildren = 0;
		for(int i = startIndex+1; i < data.length; i++){
			if(data[i].startsWith(",,,")){
				children[numChildren++] = new Level4(data, i);
			} else {//Level1 or 2 or 3
				break;
			}
		}
		
	}

}

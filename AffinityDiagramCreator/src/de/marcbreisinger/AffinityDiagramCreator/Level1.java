package de.marcbreisinger.AffinityDiagramCreator;

public class Level1 {
	
	String[] data;
	String title;
	Level2[] children;
	
	public Level1(String[] data, int startIndex){
		this.data = data;
		title = data[startIndex].substring(0, data[startIndex].length() - 3);
		int numChildren = 0;
		for(int i = startIndex+1; i < data.length; i++){
			if(data[i].startsWith(",,,")){
				//leave it to Level3
			} else if(data[i].startsWith(",,")){
				//leave it to Level2
			} else if(data[i].startsWith(",")){
				numChildren++;
			} else {//Level1
				break;
			}
		}
		children = new Level2[numChildren];
		numChildren = 0;
		for(int i = startIndex+1; i < data.length; i++){
			if(data[i].startsWith(",,,")){
				//leave it to Level3
			} else if(data[i].startsWith(",,")){
				//leave it to Level2
			} else if(data[i].startsWith(",")){
				children[numChildren++] = new Level2(data, i);
			} else {//Level1
				break;
			}
		}
		
	}

}

package de.marcbreisinger.AffinityDiagramCreator;

public class Level2 {
	
	String[] data;
	String title;
	Level3[] children;
	
	public Level2(String[] data, int startIndex){
		this.data = data;
		title = data[startIndex].substring(1, data[startIndex].length() - 2);
		int numChildren = 0;
		for(int i = startIndex+1; i < data.length; i++){
			if(data[i].startsWith(",,,")){
				//leave it to Level3
			} else if(data[i].startsWith(",,")){
				numChildren++;
			} else {//Level1 or 2
				break;
			}
		}
		children = new Level3[numChildren];
		numChildren = 0;
		for(int i = startIndex+1; i < data.length; i++){
			if(data[i].startsWith(",,,")){
				//leave it to Level3
			} else if(data[i].startsWith(",,")){
				children[numChildren++] = new Level3(data, i);
			} else {//Level1 or 2
				break;
			}
		}
		
	}

}

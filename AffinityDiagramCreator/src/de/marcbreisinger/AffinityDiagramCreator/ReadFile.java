package de.marcbreisinger.AffinityDiagramCreator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadFile {
	
	String path;
	
	public ReadFile(String path){
		this.path = path;
	}
	
	public String[] openFile(boolean trim) throws IOException {
		
		String[] str = null;
		
			InputStreamReader fr = new InputStreamReader(new FileInputStream(path));
			BufferedReader textReader = new BufferedReader(fr);
			int numLines = readLines(path);
			str = new String[numLines];
			for (int i = 0; i < numLines; i++){
				str[i] = textReader.readLine();
				if(trim)
					str[i] = str[i].trim();
			}
			textReader.close();
			
		
		return str;
	}
	
	
	
	public int readLines(String path) throws IOException{
		
		int numberOfLines = 0;
		
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		
		while((textReader.readLine()) != null){
			numberOfLines++;
		}
		textReader.close();
		return numberOfLines;
	}

}

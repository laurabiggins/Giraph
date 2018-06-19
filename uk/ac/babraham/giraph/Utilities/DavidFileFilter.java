package uk.ac.babraham.giraph.Utilities;

import java.io.File;
import java.io.FileFilter;

public class DavidFileFilter implements FileFilter{
	
	private final String [] acceptableFileExtensions = new String[] {"txt", "gmt"}; 
	
	public boolean accept(File file){
		
		for (String extension : acceptableFileExtensions){
			
			if (file.getName().toLowerCase().endsWith(extension)){
				return true;
			}
		}
		return false;
	}
}

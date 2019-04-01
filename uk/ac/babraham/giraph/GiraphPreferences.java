package uk.ac.babraham.giraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


public class GiraphPreferences {

	/** The single instantiated instance of preferences */
	private static GiraphPreferences p = new GiraphPreferences();
	
	/** The default data location. */
	public File dataLocation = new File(System.getProperty("user.home"));
	
	/** The last used data location. */
	private File lastUsedDataLocation = null;
	
	/** The default save location. */
	private File saveLocation = new File(System.getProperty("user.home"));
	
	/** The last used save location. */
	private File lastUsedSaveLocation = null;		
	
	//private String gmtFileLocation = null;
	
	//private File GMTFileLocation = new File(System.getProperty("user.home") + "/gmt_files");
	//private Files GMTFileLocation = new Files(Files.createDirectories(Paths.get(System.getProperty("user.home") + "/gmt_files")));
	
	private Path GMTPath = Paths.get(System.getProperty("user.home") + "/Giraph_gmt_files");
	
	
	//private File GMTFileLocation = 
	
	/** The network address from where we can download new genomes */
	//private String gmtDownloadLocation = "http://download.baderlab.org/EM_Genesets/current_release/";

	//private String gmtDownloadLocation = "http://download.baderlab.org/EM_Genesets/current_release/Human/symbol/Human_GO_AllPathways_no_GO_iea_September_01_2018_symbol.gmt
	//private String gmtDownloadLocation =  "http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/Mouse_GO_AllPathways_no_GO_iea_March_01_2019_symbol.gmt";
	
	/**
	 * Sets the save location to record in the preferences file
	 * 
	 * @param f The new save location
	 */
	public void setSaveLocation (File f) {
		saveLocation = f;
	}
	
	public void createGMTFilepath () {
		
		try {
			Files.createDirectories(GMTPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setGMTFilepath (Path p) {
		
		GMTPath = p;
	}
	
	public File getGMTFilepath () {
		
		System.out.println("gmt filepath = " + GMTPath);
		
		if(GMTPath == null){
			createGMTFilepath();
		}
		return GMTPath.toFile();
	}
	
	
/*	public File getGMTFileLocation () throws FileNotFoundException {
		if (GMTFileLocation == null){
			throw new FileNotFoundException("Couldn't find GMT file folder");
		}	
		return GMTFileLocation;
	}	
	
	/**
	 * Sets the last used save location.  This is a temporary setting and will
	 * not be recorded in the preferences file.
	 * 
	 * @param f The new last used save location
	 */
	public void setLastUsedSaveLocation (File f) {
		if (f.isDirectory()) {
			lastUsedSaveLocation = f;
		}
		else {
			lastUsedSaveLocation = f.getParentFile();
		}
	}
	
	public static GiraphPreferences getInstance () {		
		return p;
	}
	
	/**
	 * Sets the default data location which will be saved in the preferences
	 * file.
	 * 
	 * @param f The new data location
	 */
	public void setDataLocation (File f) {
		dataLocation = f;
	}
	
	public File getDataLocation () {
		if (lastUsedDataLocation != null) return lastUsedDataLocation;
		return dataLocation;
	}
	
	/**
	 * Sets the last used data location.  This value is only stored until the
	 * program exits, and won't be saved in the preferences file.
	 * 
	 * @param f The new last used data location
	 */
	public void setLastUsedDataLocation (File f) {
		if (f.isDirectory()) {
			lastUsedDataLocation = f;
		}
		else {
			lastUsedDataLocation = f.getParentFile();
		}
	}
	
	public File getSaveLocation () {
		if (lastUsedSaveLocation != null) return lastUsedSaveLocation;
		return saveLocation;
	}
	
	
	//private File geneInfoBase = new File("gene_info_files");
	
	// This isn't right.....
/*	public File getGMTbase () throws FileNotFoundException {
		
		/*
		 * This method returns a file which represents the directory
		 * under which the genomes are stored.  If a custom location
		 * has not been specified then the default Genomes folder in
		 * the install dir is returned.  If that can't be found then
		 * a FileNotFound exception is thrown
		 * 
		 * If a custom location has been set then this is returned
		 * regardless of it it exists or can be used.
		 */
		
/*		File f;
		
		if (GMTFileLocation == null) {

			try {
				//f = new File(ClassLoader.getSystemResource("Functional_category_reference_files").getFile().replaceAll("%20"," "));
				f = new File(ClassLoader.getSystemResource("Functional_category_reference_files").getFile().replaceAll("%20"," "));
			}
			catch (NullPointerException npe) {
				throw new FileNotFoundException("Couldn't find default reference folder");
			}
		}
		else {
			f = GMTFileLocation;
		}
				
		return f;
	}
	
/*	public File getGeneInfobase () throws FileNotFoundException {
		
		/*
		 * This method returns a file which represents the directory
		 * under which the genomes are stored.  If a custom location
		 * has not been specified then the default Genomes folder in
		 * the install dir is returned.  If that can't be found then
		 * a FileNotFound exception is thrown
		 * 
		 * If a custom location has been set then this is returned
		 * regardless of it it exists or can be used.
		 */
		
/*		File f;
		
		if (GMTbase == null) {
			// Check for the default genomes folder.  This should always be present, but
			// you can't be too careful!
			try {
				f = new File(ClassLoader.getSystemResource("Functional_category_reference_files").getFile().replaceAll("%20"," "));
			}
			catch (NullPointerException npe) {
				throw new FileNotFoundException("Couldn't find default reference folder");
			}
		}
		else {
			f = geneInfoBase;
		}
				
		return f;
	}
*/	
	/** The network address from where we can download new genomes */
//	public String gmtDownloadLocationMouse = "http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/GO/";
	
//	public String gmtDownloadLocationHuman = "http://download.baderlab.org/EM_Genesets/current_release/Human/symbol/GO/";
	
}

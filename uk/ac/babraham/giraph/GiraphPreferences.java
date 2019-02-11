package uk.ac.babraham.giraph;

import java.io.File;
import java.io.FileNotFoundException;



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
	
	/** The network address from where we can download new genomes */
	private String gmtDownloadLocation = "http://download.baderlab.org/EM_Genesets/current_release/";

	// http://download.baderlab.org/EM_Genesets/current_release/Human/symbol/Human_GO_AllPathways_no_GO_iea_September_01_2018_symbol.gmt
	// http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/Mouse_GO_AllPathways_no_GO_iea_September_01_2018_symbol.gmt
	
	/**
	 * Sets the save location to record in the preferences file
	 * 
	 * @param f The new save location
	 */
	public void setSaveLocation (File f) {
		saveLocation = f;
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
	
	
	
	/** The network address from where we can download new genomes */
//	public String gmtDownloadLocationMouse = "http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/GO/";
	
//	public String gmtDownloadLocationHuman = "http://download.baderlab.org/EM_Genesets/current_release/Human/symbol/GO/";
	
	private File GMTbase = new File("gmt_GO_files");
	private File geneInfoBase = new File("gene_info_files");
	
	//private File geneInfoBase = new File("O:/Training/FAGL/Laura/gene_info_files");
	
	// This isn't right.....
	public File getGMTbase () throws FileNotFoundException {
		
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
		
		File f;
		
		if (GMTbase == null) {

			try {
				f = new File(ClassLoader.getSystemResource("Functional_category_reference_files").getFile().replaceAll("%20"," "));
			}
			catch (NullPointerException npe) {
				throw new FileNotFoundException("Couldn't find default reference folder");
			}
		}
		else {
			f = GMTbase;
		}
				
		return f;
	}
	
	public File getGeneInfobase () throws FileNotFoundException {
		
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
		
		File f;
		
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
	
	public static GiraphPreferences getInstance () {		
		return p;
	}
	
	
	
}

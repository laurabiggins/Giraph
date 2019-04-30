package uk.ac.babraham.giraph.Network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import uk.ac.babraham.giraph.GiraphPreferences;
import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataParser.ProgressListener;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.Dialogs.Cancellable;

public class GMTDownloader implements Runnable, Cancellable{
	
	String homeDirectory = getHomeDirectory();
	
	String species;
	
	boolean cancel;
	
	File downloadedFile;
	
	public String getHomeDirectory(){
		
		return System.getProperty("user.home");
	}
	
	public void setCancel(boolean cancel){
		this.cancel = cancel;
	}

	public void startDownloading () {
		Thread t = new Thread(this);
		t.start();
	}
	
	public GMTDownloader(String species){
		
		this.species = species;
	}
	
	public void run(){

		downloadedFile = downloadFile();

		if (downloadedFile == null) new giraphException("couldn't download gmt file");
		
		progressComplete(downloadedFile);
	}
	
	
	public File downloadFile(){
		
		try {
			
			URL downloadURL = new URL("http://download.baderlab.org/EM_Genesets/current_release/" + species + "/symbol/");
			
			System.err.println("url = " + downloadURL.toString());
			
			URLConnection connection = downloadURL.openConnection();
			connection.setUseCaches(false);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
		
			String firstPartOfPattern = species + "_GO_AllPathways_no_GO_iea";
			Pattern pattern = Pattern.compile("("+firstPartOfPattern+"[a-zA-Z0-9_]*.gmt)");
			
			Matcher matcher;
			
			String urlPart = "";
			
			WHILE_LOOP: while ((line = br.readLine()) != null) {
				
				if(line.contains(firstPartOfPattern)) {
				
					System.out.println(line);
					matcher = pattern.matcher(line);
					if(matcher.find()) {  
						urlPart = matcher.group(1);
						 break WHILE_LOOP;
					}	
				}
			}	

			String urlString = downloadURL + urlPart;
			URL fullDownloadURL = new URL(urlString);
			
			String msg = "Downloading " + urlString;
			progressUpdated(msg, 0, 0);

			connection = fullDownloadURL.openConnection();
			connection.setUseCaches(false);
			
			Path basePath = Paths.get(GiraphPreferences.getInstance().getGMTFilepath() + "/" + species);		
			Files.createDirectories(basePath);
			
			String filename = new String(basePath + "/" + urlPart);
			
			File f = new File(filename);
			
			if (cancel) {
				progressCancelled();
				return null;
			}
			
			// This can't be cancelled
			FileUtils.copyURLToFile(fullDownloadURL, f);
			
			if(f.exists() && !f.isDirectory()) {
				return f;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			new giraphException("Couldn't download gmt file ");
		}
		return null;
	}
	
	public Vector<ProgressListener>listeners = new Vector<ProgressListener>();

	protected void progressCancelled () {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressCancelled();
		}
	}

	protected void progressUpdated (String message, int current, int max) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressUpdated(message, current, max);
		}
	}

	protected void progressWarningReceived (Exception e) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressWarningReceived(e);
		}
	}

	protected void progressExceptionReceived (Exception e) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressExceptionReceived(e);
		}
	}

	protected void progressComplete (Object o) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressComplete("gmt_downloader", o);;
		}
	}

	public void addProgressListener(ProgressListener pl){
		if (!listeners.contains(pl)) {
			listeners.add(pl);
		}
	}

	@Override
	public void cancel() {
		System.err.println("setting cancel to true");
		cancel = true;
		// TODO Auto-generated method stub
		
	}
}

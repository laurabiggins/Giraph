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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import uk.ac.babraham.giraph.GiraphPreferences;
import uk.ac.babraham.giraph.giraphApplication;

public class GMTDownloader {
	
	private static String latestVersion = null;
	
	String homeDirectory = getHomeDirectory();
		
	public String getHomeDirectory(){
		
		return System.getProperty("user.home");
	}
	
	public void downloadFile(String species){
	
		try {
			
			URL downloadURL = new URL("http://download.baderlab.org/EM_Genesets/current_release/" + species + "/symbol/");
			
			System.err.println("url = " + downloadURL.toString());
			
			URLConnection connection = downloadURL.openConnection();
			connection.setUseCaches(false);
			
//<<<<<<< download_gmt
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
			String msg = "will try and download " + urlString;
			
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "Downloading...", JOptionPane.INFORMATION_MESSAGE);
			
			/** ok or cancel option **/
			
			connection = fullDownloadURL.openConnection();
			connection.setUseCaches(false);
			
			Path basePath = Paths.get(GiraphPreferences.getInstance().getGMTFilepath() + "/" + species);		
			Files.createDirectories(basePath);
			
			String filename = new String(basePath + "/" + urlPart);
			
			File f = new File(filename);
			
			FileUtils.copyURLToFile(fullDownloadURL, f);
			
		}
		catch (IOException e) {
			e.printStackTrace();
			//throw new giraphException("Couldn't contact the update server to check for updates");
		}	
	}
	
}

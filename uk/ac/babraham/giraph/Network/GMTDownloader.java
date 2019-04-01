package uk.ac.babraham.giraph.Network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import uk.ac.babraham.giraph.giraphApplication;

// TODO: mouse or human option - check the options panel

public class GMTDownloader {
	
	private static String latestVersion = null;
	
	String homeDirectory = getHomeDirectory();
		
	public String getHomeDirectory(){
		
		return System.getProperty("user.home");
	}
	
	public void downloadFile(String species){
	
		try {
			
			//URL downloadURL = new URL("http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/");
			URL downloadURL = new URL("http://download.baderlab.org/EM_Genesets/current_release/" + species + "/symbol/");
			
			System.err.println("url = " + downloadURL.toString());
			
			URLConnection connection = downloadURL.openConnection();
			connection.setUseCaches(false);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
		
			String firstPartOfPattern = species + "_GO_AllPathways_no_GO_iea";
			Pattern pattern = Pattern.compile("("+firstPartOfPattern+"[a-zA-Z0-9_]*.gmt)");
			
			//Pattern pattern = Pattern.compile("(Mouse_GO_AllPathways_no_GO_iea[a-zA-Z0-9_]*.gmt)");
			
			Matcher matcher;
			
			String urlPart = "";
			
			WHILE_LOOP: while ((line = br.readLine()) != null) {
				
				if(line.contains(firstPartOfPattern)) {
				
					System.out.println(line);
					matcher = pattern.matcher(line);
					if(matcher.find()) {  
						urlPart = matcher.group(1);
						 break WHILE_LOOP;
						//System.out.println(line);
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
			
			//String filename = new String(getHomeDirectory() + "/downloadedGMT.gmt");
			String filename = new String(getHomeDirectory() + "/" + urlPart);
			//String filename = new String(getHomeDirectory() + "/" + species + "/" + urlPart);
			File f = new File(filename);
			
			FileUtils.copyURLToFile(fullDownloadURL, f);
			
			/*DataInputStream d = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
			//byte [] data = new byte[2000000000]; // This was used for a version number for SeqMonk - is it still appropriate to use for a large file? 
			byte [] data = new byte[255]; 
			int bytesRead = d.read(data);

			byte [] actualData = new byte[bytesRead];
			System.err.println("bytesRead " + bytesRead);
			for (int i=0;i<bytesRead;i++) {
				actualData[i] = data[i];
			}
			
			String filename = new String(getHomeDirectory() + "/downloadedGMT.gmt");
			FileOutputStream outputStream = new FileOutputStream(filename);
		   // byte[] strToBytes = str.getBytes();
		    //outputStream.write(strToBytes);
			System.err.println("trying to write to " + filename);
			String str = "hello";
			
		    outputStream.write(actualData);
		 
		    outputStream.close();
		    */
		}
		catch (IOException e) {
			e.printStackTrace();
			//throw new giraphException("Couldn't contact the update server to check for updates");
		}	
	}
	
}

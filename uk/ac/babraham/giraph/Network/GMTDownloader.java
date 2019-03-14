package uk.ac.babraham.giraph.Network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class GMTDownloader {
	
	private static String latestVersion = null;
	
	String homeDirectory = getHomeDirectory();
		
	public String getHomeDirectory(){
		
		return System.getProperty("user.home");
	}
	
	public void downloadFile(){
	
		try {
			
			URL downloadURL = new URL("http://download.baderlab.org/EM_Genesets/current_release/Mouse/symbol/Mouse_GO_AllPathways_no_GO_iea_March_01_2019_symbol.gmt");
			
			URLConnection connection = downloadURL.openConnection();
			connection.setUseCaches(false);
			
			DataInputStream d = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
	
			byte [] data = new byte[255]; // A version number should never be more than 255 bytes
			int bytesRead = d.read(data);
			
			byte [] actualData = new byte[bytesRead];
			for (int i=0;i<bytesRead;i++) {
				actualData[i] = data[i];
			}
			
			String filename = new String(getHomeDirectory() + "downloadedGMT.gmt");
			FileOutputStream outputStream = new FileOutputStream(filename);
		   // byte[] strToBytes = str.getBytes();
		    //outputStream.write(strToBytes);
		    outputStream.write(actualData);
		 
		    outputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			//throw new giraphException("Couldn't contact the update server to check for updates");
		}	
		
		/*URL updateURL = new URL("http","www.bioinformatics.babraham.ac.uk","/projects/seqmonk/current_version.txt");
		
		URLConnection connection = updateURL.openConnection();
		connection.setUseCaches(false);
		
		DataInputStream d = new DataInputStream(new BufferedInputStream(connection.getInputStream()));

		byte [] data = new byte[255]; // A version number should never be more than 255 bytes
		int bytesRead = d.read(data);
		
		byte [] actualData = new byte[bytesRead];
		for (int i=0;i<bytesRead;i++) {
			actualData[i] = data[i];
		}
		
		/*latestVersion = new String(actualData);
		latestVersion.replaceAll("[\\r\\n]", "");
		latestVersion = latestVersion.trim();
		
		return latestVersion;
		*/
	}
	
}

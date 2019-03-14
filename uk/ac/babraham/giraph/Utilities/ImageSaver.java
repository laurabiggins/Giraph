package uk.ac.babraham.giraph.Utilities;
	
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;



import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.Displays.GraphPanel;

	/** Taken from SeqMonk
	 * A utility class which acts as a wrapper for the SVG or PNG generating
	 * code which can be used to save (almost) any component which uses the
	 * standard Graphics interface to draw itself.
	 */
public class ImageSaver {
		/**
		 * Launches a file selector to select which type of file to create and
		 * then create it.
		 * 
		 * @param c The component to save.
		 * @throws giraphException 
		 */
	public static void saveImage (Component c) throws giraphException {
		// first we need to set the graphics object in the paint method to not be Graphics2D
	//	if(c instanceof GraphPanel){
	//		((GraphPanel)c).exportImage = true;
	//	}
		
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new SVGFileFilter());
		PNGFileFilter pff = new PNGFileFilter();
		chooser.addChoosableFileFilter(pff);
		chooser.setFileFilter(pff);
		
		int result = chooser.showSaveDialog(c);
		if (result == JFileChooser.CANCEL_OPTION) return;

		File file = chooser.getSelectedFile();
		
		if (file.isDirectory()) return;

		FileFilter filter = chooser.getFileFilter();
		
		if (filter instanceof PNGFileFilter) {		
			if (! file.getPath().toLowerCase().endsWith(".png")) {
				file = new File(file.getPath()+".png");
			}
		}
		else if (filter instanceof SVGFileFilter) {
			if (! file.getPath().toLowerCase().endsWith(".svg")) {
				file = new File(file.getPath()+".svg");
				
			}			
		}
		else {
			System.err.println("Unknown file filter type "+filter+" when saving image");
			return;
		}
		
		// Check if we're stepping on anyone's toes...
		if (file.exists()) {
			int answer = JOptionPane.showOptionDialog(c,file.getName()+" exists.  Do you want to overwrite the existing file?","Overwrite file?",0,JOptionPane.QUESTION_MESSAGE,null,new String [] {"Overwrite and Save","Cancel"},"Overwrite and Save");

			if (answer > 0) {
				return;
			}
		}

		try {					
			if (filter instanceof PNGFileFilter) {
				BufferedImage b = new BufferedImage(c.getWidth(),c.getHeight(),BufferedImage.TYPE_INT_RGB);
				Graphics g = b.getGraphics();			
				c.paint(g);

				ImageIO.write((BufferedImage)(b),"PNG",file);
			}
			else if (filter instanceof SVGFileFilter) {
				PrintWriter pr = new PrintWriter(new FileWriter(file));
				if(c instanceof GraphPanel){
					((GraphPanel)c).exportImage = true;
				}
				pr.print(SVGGenerator.convertToSVG(c));
				pr.close();
			}
			else {
				System.err.println("Unknown file filter type "+filter+" when saving image");
				return;
			}
		}

		catch (IOException e) {
		//catch (giraphException e) {
			String error = e.toString();
			throw new giraphException(error);	
		}
		if(c instanceof GraphPanel){
			((GraphPanel)c).exportImage = false;
		}
		
		
	}
	
}


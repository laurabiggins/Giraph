package uk.ac.babraham.giraph.Displays;

/**
 * This is where the query genes are entered, the background gene list is set and the initial filters are set.
 * This panel goes inside the OptionsFrame.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import uk.ac.babraham.giraph.GiraphPreferences;
import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataParser.GMTParser;
import uk.ac.babraham.giraph.Utilities.NumberKeyListener;

public class GeneUploadPanel extends JPanel implements ActionListener, KeyListener {
	
	JComboBox featureTypeBox;
	JComboBox speciesBox;
	JComboBox backgroundGenesBox;
	JTextArea queryGenes;
	JTextArea backgroundGenesArea;
	JTextField pValueField;
	JTextField noOfGenesInCategory;
	
	/* The p value (or q value) threshold that we use to filter the results */
	private Double pValueLimit = 0.05;
	
	// set min number of genes required in category
	private JTextField minGenesInCategory;
	
	// set max number of genes required in category
	private JTextField maxGenesInCategory;
	
	/* minimum number of genes in geneset for it to be imported */
	private int minGenesInSet = 10;
	
	/* maximum number of genes in geneset for it to be imported */
	private int maxGenesInSet = 500;
	
	giraphApplication giraphApp;
	LoadingMessage loadingMessage;
	private Thread loadingMessageThread;
	
	public int minGenesInSet(){
		return minGenesInSet;
	}
	
	public int maxGenesInSet(){
		return maxGenesInSet;
	}
	
	public GeneUploadPanel (giraphApplication app) {
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.weightx=0.5;
		gbc.weighty=0.5;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		add(new JLabel("Select species"),gbc);

		gbc.gridx=2;

		speciesBox = new JComboBox(new String [] {"Mouse","Human"});

		add(speciesBox,gbc);
				
		gbc.gridy++;
		gbc.gridx=1;
		
		add(new JLabel("Enter query genes "),gbc);

		gbc.gridx=2;
		
		Font font1 = new Font("Dialog", Font.PLAIN, 10);
		
		queryGenes = new JTextArea();
		queryGenes.setRows(5);
		setTextAreaParameters(queryGenes, font1);
		
		JScrollPane sp1 = new JScrollPane(queryGenes);
		sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(sp1, gbc);
		
		gbc.gridy++;
		gbc.gridx=1;

		
		add(new JLabel("Background genes"),gbc);

		gbc.gridx=2;

		backgroundGenesBox = new JComboBox(new String [] {"Use all genes in genome", "Enter custom background genes"});
		backgroundGenesBox.setActionCommand("selectBackgroundGenes");
		backgroundGenesBox.addActionListener(this);
		
		add(backgroundGenesBox,gbc);

		gbc.gridy++;
		
		backgroundGenesArea = new JTextArea();
		backgroundGenesArea.setRows(5);
		setTextAreaParameters(backgroundGenesArea, font1);
		backgroundGenesArea.setEditable(false);
		backgroundGenesArea.setBackground(Color.LIGHT_GRAY);
		
		JScrollPane sp2 = new JScrollPane(backgroundGenesArea);
		sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(sp2, gbc);
		
		
		gbc.gridy++;
		gbc.gridx=1;

		add(new JLabel("P-value"),gbc);

		gbc.gridx=2;

		pValueField = new JTextField("0.05");
		pValueField.addKeyListener(new NumberKeyListener(true, false, 1));
		pValueField.addKeyListener(this);
		add(pValueField,gbc);	
		
		gbc.gridy++;
		gbc.gridx=1;

		
		add(new JLabel("Number of genes in functional category"), gbc);		
		
		gbc.gridx=2;	
		gbc.weightx=0.9;
		JPanel minMaxPanel = new JPanel();
		minMaxPanel.setLayout(new BoxLayout(minMaxPanel, BoxLayout.X_AXIS));
		
		minMaxPanel.add(new JLabel("Minimum "));
		
		minGenesInCategory = new JTextField(""+minGenesInSet);			
		minGenesInCategory.addKeyListener(new NumberKeyListener(false, false));
		minGenesInCategory.addKeyListener(this);
		minMaxPanel.add(minGenesInCategory);
		
		minMaxPanel.add(new JLabel("  Maximum "));
		
		maxGenesInCategory = new JTextField(""+maxGenesInSet);
		maxGenesInCategory.addKeyListener(new NumberKeyListener(false, false));
		maxGenesInCategory.addKeyListener(this);
		minMaxPanel.add(maxGenesInCategory);	
		
		add(minMaxPanel, gbc);
		
	}
	
	public void setTextAreaParameters(JTextArea ta, Font f){
		ta.setEditable(true);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setFont(f);
		ta.setCaretPosition(0);
	}
	
	public float pValue () {
		if (pValueField.getText().trim().length() > 0) {
			return Float.parseFloat(pValueField.getText());
		}
		else {
			return (float)0.05;
		}
	}
	
/*	public int minGenesInCategory(){
		if (noOfGenesInCategory.getText().trim().length() > 0){
			return Integer.parseInt(noOfGenesInCategory.getText());
		}
		else {
			return 3;
		}
		
	}
*/	
	public String queryGenes(){
		return queryGenes.getText();
	}
	
	public String backgroundGenes(){
		return backgroundGenesArea.getText();
	}
	
	public String species (){
		return (String)speciesBox.getSelectedItem();
	}
	
	public String getBackgroundGenesOption () {
		return (String)backgroundGenesBox.getSelectedItem();
	}


	public void actionPerformed(ActionEvent ae) {
		
		
		if (ae.getActionCommand().equals("selectBackgroundGenes")) {
												
			if (getBackgroundGenesOption().equals(new String("Enter custom background genes"))){
				
				backgroundGenesArea.setBackground(Color.white);
				backgroundGenesArea.setEditable(true);
			}
			else if (getBackgroundGenesOption().equals("Use all genes in genome")){

				backgroundGenesArea.setText("");
				backgroundGenesArea.setBackground(Color.LIGHT_GRAY);
				backgroundGenesArea.setEditable(false);
			}
		}
		
	}

	// use this method to show which genes are not found in the background set
	public String[] identifyUnmatchedGenes(){
		
		return null;
	}
		
	/**
	 * if the file starts with Mouse or Human and ends with text, accept.
	 * not bothering about the latest date for now
	 */
		
	public static File findGMTFile(File dir, String species) {
	    			
		File [] files = dir.listFiles();
		
		for (int i = 0; i < files.length; i++){
		
			if (files[i].getName().startsWith(species) && (files[i].getName().endsWith(".txt") || files[i].getName().endsWith(".gmt"))){

				System.out.println("file is " + files[i].getName());
				return files[i];				
			}
	    }
		
		System.err.println("No reference file found in " + dir.getAbsolutePath());		
		return null;
	}
	
	public Thread loadingMessageThread(){
		
		return loadingMessageThread;
	}
	
	private class LoadingMessage implements Runnable {
		
		String message;
				
		public LoadingMessage(String filepath){
			
			this.message = ("Loading functional information from " + filepath);
		}
		
		public void run(){
	        
	        JOptionPane.showMessageDialog(null, message, "Loading file", JOptionPane.INFORMATION_MESSAGE);
	    }
    }


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent ke) {
		JTextField f = (JTextField)ke.getSource();

		Double d = null;

		if (f.getText().length()>0) {

			if (f.getText().equals("-")) {
				d = 0d;
			}
			else {
				try {
					d = Double.parseDouble(f.getText());
				}
				catch (NumberFormatException e) {
					f.setText(f.getText().substring(0,f.getText().length()-1));
					return;
				}
			}
		}

		if (f == pValueField) {
			pValueLimit = d;
			System.err.println("adjusting the p value to " + d);
		}
		
		else if(f == minGenesInCategory){
			// bit messy
			if(d == null || (d<1)){
				minGenesInSet = 1;
			}	
			else{
				minGenesInSet = d.intValue();
			}
		}
		else if(f == maxGenesInCategory){
			if(d == null  || (d<1)){
				maxGenesInSet = 100000;
			}	 						
			else{
				maxGenesInSet = d.intValue();
			}
		}											
		else {
			throw new IllegalStateException("Unexpected text field "+f+" sending data to keylistener in differences filter");
		}
	}			
}		

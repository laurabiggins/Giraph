package uk.ac.babraham.giraph.DataParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.FunctionalSetInfo;
import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.DataTypes.PValue;
import uk.ac.babraham.giraph.Dialogs.Cancellable;

/** This class is for parsing an external results file so that we can display it without having to rerun the analysis within Giraph.
 * 
 * Error checking - skip a line if we don't have the right info
 * 
 * rearrange the options on screen
 * 
 * remove some of the options - check what we actually need.
 * 
 * 
 * @author bigginsl
 *
 */

public class ExternalResultsParser implements Cancellable{

	// filepath for the gmt file
	private File file;
	
	// create a new geneCollection then set this as the query genes in the application
	//public GeneCollection gc;
		
	// create a new geneListCollection
	//public GeneListCollection glc;
	
	// so we can notify the app when we're ready
	public ProgressListener pl;
	
	private boolean cancel;
	
	private boolean gorilla = false;
	private boolean david = false;
	
	private String delimitersValue = "\t";
	private String geneDelimitersValue = ",";
	
	private static int startRowValue = 0;
	private static int queryGeneColValue = -1;
//  private static int totalGenesInCategoryColValue = -1;
//	private static int categorySourceColValue = -1;
//	private static int categoryIDColValue = -1;
	private static int categoryNameColValue = -1;
	private static int categoryDescriptionColValue = -1;
	private static int pValueColValue = -1;
	
	private JDialog options = null;
	
	public ExternalResultsParser(File file,  boolean gorilla, boolean david){
		
		this.file = file;
		this.gorilla = gorilla;
		this.david = david;
	}
	
	public void run(){
		
		try {
			GeneListCollection geneListCollection = parseResultsFile();
			
			if(geneListCollection != null){
				pl.externalResultsParsed(geneListCollection);
			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}	
	
	private GeneListCollection parseResultsFile() throws Exception{
	
		if (options == null) {
			options = new JDialog(giraphApplication.getInstance());
			options.setModal(true);
			options.setContentPane(new GenericAnnotationParserOptions(options));
			options.setSize(700,400);
			options.setLocationRelativeTo(null);
		}

		// We have to set cancel to true as a default so we don't try to 
		// proceed with processing if the user closes the options using
		// the X on the window.

		options.setTitle("Format for "+file.getName()+"...");
		
		cancel = true;
		options.setVisible(true);
				
		if (cancel) {
			progressCancelled();
			return null;
		}
		
		// When continue is pressed, cancel is set to false.
		// check we've got the required info
		
		BufferedReader br;

		if (file.getName().toLowerCase().endsWith(".gz")) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));	
		}
		else {
			br = new BufferedReader(new FileReader(file));
		}

		
		String line;
		// First skip the header lines
		for (int i=0;i<startRowValue;i++) {
			line = br.readLine();
			if (line == null) {
				throw new Exception ("Ran out of file before skipping all of the header lines");
			}
		}

		int maxIndexValue = 0;
		if (queryGeneColValue > maxIndexValue) maxIndexValue = queryGeneColValue;
/*		if (totalGenesInCategoryColValue > maxIndexValue) maxIndexValue = totalGenesInCategoryColValue;
		if (categorySourceColValue > maxIndexValue) maxIndexValue = categorySourceColValue;
		if (categoryIDColValue > maxIndexValue) maxIndexValue = categoryIDColValue;
*/		if (categoryNameColValue > maxIndexValue) maxIndexValue = categoryNameColValue;
		if (categoryDescriptionColValue > maxIndexValue) maxIndexValue = categoryDescriptionColValue;
		if (pValueColValue > maxIndexValue) maxIndexValue = pValueColValue;
		
		
		// create the gene collection
		GeneCollection gc = new GeneCollection();
		
		// create the genelist collection
		GeneListCollection glc = new GeneListCollection();
		
		int lineCount = 0;
		// Now process the rest of the file
		while ((line = br.readLine())!= null) {
			++lineCount;

			if (cancel) {
				progressCancelled();
				return null;
			}

			if (lineCount%1000 == 0) {
				progressUpdated("Read "+lineCount+" lines from "+file.getName(),0,1);
			}

			String [] sections = line.split(delimitersValue,-1);

			// Check to see if we've got enough data to work with
			if (maxIndexValue >= sections.length) {
				progressWarningReceived(new giraphException("Not enough data ("+sections.length+") to get a probe name on line '"+line+"'"));
				continue; // Skip this line...						
			}

			String [] genesInCategory = null;
			
			if(gorilla){
				GOrillaParser gorillaParser = new GOrillaParser(sections[queryGeneColValue], geneDelimitersValue);
				genesInCategory = gorillaParser.parseGenes();
			}
			else if(david){
				DavidParser davidParser = new DavidParser(sections[queryGeneColValue], geneDelimitersValue);
				genesInCategory = davidParser.parseGenes(); 
			}

			// either we're not parsing the list of genes properly, or it may be that some reports contain lists of genes with only one gene in - clearly this should not be included.
			if(genesInCategory.length < 2){
				
				//JOptionPane.showMessageDialog(giraphApplication.getInstance(), "could not find enough genes in the gene list ", "genes not parsed",  JOptionPane.ERROR_MESSAGE);
				progressWarningReceived(new giraphException("Not enough genes ("+genesInCategory.length+") to get a probe name on line '"+line+"'"));
				System.err.println("fewer than 2 genes found in gene list, line skipped");
				continue; // Skip this line...	
				//return null;
			}
						
			// Create new functional category for each line
			FunctionalSetInfo functionalSetInfo = new FunctionalSetInfo();
			
			
			/** These must have been specified */
			functionalSetInfo.setName(sections[categoryNameColValue]);
						
			// description isn't required
			if(categoryDescriptionColValue >=0) functionalSetInfo.setDescription(sections[categoryDescriptionColValue]);
			else functionalSetInfo.setDescription(sections[categoryNameColValue]);
						
			
			/** This will be changed into a gene list once all the genes have been processed */ 
			ArrayList<Gene> geneArrayList = new ArrayList<Gene>();
			
			
			/** cycle through all the genes, this is going to assume that there are no duplicated genes within the gene set category */ 
			for (int i = 0; i < genesInCategory.length; i++){
				
				String geneSym = cleanGene(genesInCategory[i].toUpperCase());			
				
				// if gene doesn't already exist, create it
				if (gc.getGene(geneSym) == null){
					
					gc.addGene(new Gene(geneSym));
				}	
				
				// add the gene to the geneArrayList
				geneArrayList.add(gc.getGene(geneSym));
			}						
			
			// create gene list from the gene list array
			if(geneArrayList.size() > 0){
								
				GeneList gl = new GeneList();
				gl.setGenes(geneArrayList.toArray(new Gene[0]));
				
				gl.setFunctionalSetInfo(functionalSetInfo);
				
				PValue pvalue = new PValue();
				//pvalue.setP(Double.parseDouble(result[4]));
				pvalue.setQ(Double.parseDouble(sections[pValueColValue]));
				
				gl.setPvalue(pvalue);
				
				// add the gene list to the collection
				glc.addGeneList(gl);
			}				
		}
		// We're finished with the file.
		br.close();
		
		options.dispose();
		options = null;
		
		return glc;
	}	
		
	private Vector<ProgressListener>listeners = new Vector<ProgressListener>();
	
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
	
	public void cancel() {
		// TODO Auto-generated method stub		
	}
	
	public void addProgressListener(ProgressListener pl){
		this.pl = pl;
	}
	
	private String cleanGene(String str) {
		
		String str1 = str.replaceAll("\t", "");
		String str2 = str1.replaceAll("\\s+", "");
		String str3 = str2.replaceAll("\"", "");
		
		return str3;
	}
	
	/**
	 * The GenericSeqReadParserOptions.
	 */
	private class GenericAnnotationParserOptions extends JPanel implements ActionListener, KeyListener {

		private JComboBox delimiters;
		private JComboBox geneListDelimiters;
		private JComboBox startRow;
		private JComboBox categoryNameCol;
		private JComboBox categoryDescriptionCol;
		private JComboBox categorySourceCol;
		private JComboBox categoryIDCol;
		private JComboBox queryGeneCol;
		private JComboBox totalGenesInCategoryCol;
		private JComboBox pValueCol;
		private JButton continueButton;
		private String [] previewData = new String [50];
		private ProbeTableModel model;
		private JTable table;
		private JScrollPane tablePane = null;
		private JDialog dialog;

		/**
		 * Instantiates a new generic seq read parser options.
		 */
		public GenericAnnotationParserOptions (JDialog dialog) {
			this.dialog = dialog;

			// We can now read the first 50 lines from the first file
			try {
				
				BufferedReader br;

				if (file.getName().toLowerCase().endsWith(".gz")) {
					br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));	
				}
				else {
					br = new BufferedReader(new FileReader(file));
				}

				int x=0;
				String line;
				while (true) {
					line = br.readLine();
					// We could (theoretically) have less than 50 lines of text
					if (line == null) line = "";
					if (x>49) break;
					previewData[x] = new String(line);
					x++;
				}
				br.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}


			setLayout(new BorderLayout());

			JPanel optionPanel = new JPanel();

			optionPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=0;
			c.gridy=0;
			c.weightx=0.5;
			c.weighty=0.5;
			c.fill = GridBagConstraints.NONE;

			optionPanel.add(new JLabel("Column Delimiter"),c);
			delimiters = new JComboBox(new String [] {"Tab","Space","Comma","Semicolon"});
			delimiters.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(delimiters,c);

			c.gridx=0;
			c.weightx=0.5;
			c.gridy++;
			optionPanel.add(new JLabel("Start at Row"),c);

			String [] rowList = new String[51];
			for (int i=0;i<51;i++) {
				rowList[i] = ""+i;
			}

			startRow = new JComboBox(rowList);
			startRow.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(startRow,c);

			c.gridx=0;
			c.weightx=0.5;
			c.gridy++;
			optionPanel.add(new JLabel("category name"),c);
			categoryNameCol = new JComboBox();
			categoryNameCol.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(categoryNameCol,c);

			c.gridx=0;
			c.weightx=0.5;
			c.gridy++;
			optionPanel.add(new JLabel("Genes in category"),c);
			queryGeneCol = new JComboBox();
			queryGeneCol.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(queryGeneCol,c);
			
			c.gridx=0;
			c.weightx=0.5;
			c.gridy++;
			optionPanel.add(new JLabel("gene list delimiter"),c);
			geneListDelimiters = new JComboBox(new String [] {"Comma", "Tab","Space","Semicolon"});
			geneListDelimiters.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(geneListDelimiters,c);
			
			c.gridx=0;
			c.weightx=0.5;
			c.gridy++;
			optionPanel.add(new JLabel("p value"),c);
			pValueCol = new JComboBox();
			pValueCol.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(pValueCol,c);
						
			c.gridx=0;
			c.weightx=0.5;
			c.gridy++;
			optionPanel.add(new JLabel("category description"),c);
			categoryDescriptionCol = new JComboBox();
			categoryDescriptionCol.addActionListener(this);
			c.gridx=1;
			c.weightx=0.1;
			optionPanel.add(categoryDescriptionCol,c);
			
			
			add(optionPanel,BorderLayout.EAST);
			model = new ProbeTableModel();

			updateTable();
			
			JPanel buttonPanel = new JPanel();
			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);
			
			continueButton = new JButton("Continue");
			continueButton.setActionCommand("continue");
			continueButton.addActionListener(this);
			continueButton.setEnabled(false);
			buttonPanel.add(continueButton);
			
			setDefault(startRow, startRowValue);
			setDefault(categoryNameCol, categoryNameColValue+1);
			setDefault(categoryDescriptionCol, categoryDescriptionColValue+1);
			setDefault(pValueCol, pValueColValue+1);
			setDefault(categoryDescriptionCol, categoryDescriptionColValue+1);
//			setDefault(categorySourceCol, categorySourceColValue+1);
			setDefault(queryGeneCol, queryGeneColValue+1);
			//setDefault(totalGenesInCategoryCol, totalGenesInCategoryColValue+1);
			
			add(buttonPanel,BorderLayout.SOUTH);

		}
		
		private void setDefault (JComboBox box, int value) {
			String text = ""+value;
			for (int i=0;i<box.getItemCount();i++) {
				if (value == 0 && box.getItemAt(i) == null) {
					box.setSelectedIndex(i);
					return;
				}
				if (box.getItemAt(i) != null && box.getItemAt(i).equals(text)) {
					box.setSelectedIndex(i);
					return;
				}
			}
			
			// If we get here then there was no match so we explicitly set the
			// first value to trigger an update to the appropriate value
			box.setSelectedIndex(0);
						
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		public Dimension getPreferredSize () {
			return new Dimension(800,600);
		}
		
		/**
		 * Updates the preview table when new options have been set.
		 */
		private void updateTable () {

			// We need to rebuild the list of column numbers.
			String [] columnList = new String [model.getColumnCount()];
			columnList[0] = null;
			for (int i=1;i<model.getColumnCount();i++) {
				columnList[i] = ""+i;
			}
			categoryNameCol.removeAllItems();
			categoryDescriptionCol.removeAllItems();			
			queryGeneCol.removeAllItems();			
			pValueCol.removeAllItems();

			for (int i=0;i<columnList.length;i++) {
				categoryNameCol.addItem(columnList[i]);
				categoryDescriptionCol.addItem(columnList[i]);			
				queryGeneCol.addItem(columnList[i]);			
				pValueCol.addItem(columnList[i]);
			}
			categoryNameCol.validate();
			categoryDescriptionCol.validate();		
			queryGeneCol.validate();			
			pValueCol.validate();
			
			if (tablePane != null) {
				model.fireTableStructureChanged();
				model.fireTableDataChanged();
			}
			else {
				table = new JTable(model);
				TableCellRenderer r = new MyTableCellRenderer();
				table.setDefaultRenderer(Object.class, r);
				tablePane = new JScrollPane(table);
				add(tablePane,BorderLayout.CENTER);
				validate();
			}
		}

		/**
		 * Gets the delimiter between fields.
		 * 
		 * @return The delimiter
		 */
		private String getDelimiter (JComboBox delim) {
			if (((String)(delim.getSelectedItem())).equals("Tab")) {
				return "\t";
			}
			if (((String)(delim.getSelectedItem())).equals("Space")) {
				return " ";
			}
			if (((String)(delim.getSelectedItem())).equals("Comma")) {
				return ",";
			}
			if (((String)(delim.getSelectedItem())).equals("Semicolon")) {
				return ";";
			}
			throw new IllegalArgumentException("Unknown delimiter option selected '"+delim.getSelectedItem()+"'");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ae) {

			if (ae.getActionCommand().equals("continue")) {
				System.out.println("Setting user cancelled to false");
				cancel = false;
				dialog.setVisible(false);

				// Don't dispose() here as it messes up the signal we get at the
				// calling end.  We need to extract the data from the preferences
				// window before we dispose of it.
				return;
			}

			if (ae.getActionCommand().equals("cancel")) {
				cancel = true;
				dialog.setVisible(false);
				// Don't dispose() here as it messes up the signal we get at the
				// calling end.  We need to extract the data from the preferences
				// window before we dispose of it.
				return;
			}
			
			if (ae.getSource() == delimiters) {
				delimitersValue = getDelimiter(delimiters);
				updateTable();
				return;
			}
			else if (ae.getSource() == geneListDelimiters) {
				geneDelimitersValue = getDelimiter(geneListDelimiters);
				
				System.err.println("geneDelimitersValue = " + geneDelimitersValue); 
				
				//updateTable();
				return;
			}
			else if (ae.getSource() == startRow) {
				startRowValue = Integer.parseInt((String)startRow.getSelectedItem());
				model.fireTableDataChanged();
				return;
			}
			else if (ae.getSource() == categoryNameCol) {
				if (categoryNameCol.getSelectedItem() == null) {
					categoryNameColValue = -1;
				}
				else {
					categoryNameColValue = Integer.parseInt((String)categoryNameCol.getSelectedItem())-1;
					System.err.println("categoryNameColValue = " + categoryNameColValue );
				}
			}
			else if (ae.getSource() == categoryDescriptionCol) {
				if (categoryDescriptionCol.getSelectedItem() == null) {
					categoryDescriptionColValue = -1;
				}
				else {
					categoryDescriptionColValue = Integer.parseInt((String)categoryDescriptionCol.getSelectedItem())-1;
				}
			}
			else if (ae.getSource() == queryGeneCol) {
				if (queryGeneCol.getSelectedItem() == null) {
					queryGeneColValue = -1;
				}
				else {
					queryGeneColValue = Integer.parseInt((String)queryGeneCol.getSelectedItem())-1;
				}
			}
			/*else if (ae.getSource() == totalGenesInCategoryCol) {
				if (totalGenesInCategoryCol.getSelectedItem() == null) {
					totalGenesInCategoryColValue = -1;
				}
				else {
					totalGenesInCategoryColValue = Integer.parseInt((String)totalGenesInCategoryCol.getSelectedItem())-1;
				}
			}
		*/	else if (ae.getSource() == pValueCol) {
				if (pValueCol.getSelectedItem() == null) {
					pValueColValue = -1;
				}
				else {
					pValueColValue = Integer.parseInt((String)pValueCol.getSelectedItem())-1;
				}
			}
			
			if (categoryNameColValue >=0 && queryGeneColValue >=0 && pValueColValue >=0) {
				continueButton.setEnabled(true);
			}
			else {
				continueButton.setEnabled(false);
			}
			
		}

		public void keyPressed(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}

		public void keyTyped(KeyEvent e) {}


		/**
		 * The ProbeTableModel.
		 */
		private class ProbeTableModel extends AbstractTableModel {

			/* (non-Javadoc)
			 * @see javax.swing.table.TableModel#getRowCount()
			 */
			public int getRowCount() {
				int startRowValue = 1;
				if (startRow.getSelectedItem()!=null) {
					startRowValue = Integer.parseInt((String)startRow.getSelectedItem());
				}

				if (startRowValue < 1 || startRowValue > 50) {
					startRowValue = 1;
				}

				return 51-startRowValue;
			}

			/* (non-Javadoc)
			 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
			 */
			public String getColumnName (int column) {
				if (column==0) {
					return "Row number";
				}
				else {
					return "Col "+column;
				}
			}

			/* (non-Javadoc)
			 * @see javax.swing.table.TableModel#getColumnCount()
			 */
			public int getColumnCount() {
				int max = 1;
				for (int i=0;i<previewData.length;i++) {
					String [] sections = previewData[i].split(delimitersValue);
					if (sections.length > max) {
						max = sections.length;
					}
				}
				return max+1;
			}



			/* (non-Javadoc)
			 * @see javax.swing.table.TableModel#getValueAt(int, int)
			 */
			public Object getValueAt(int r, int c) {

				if (c==0) {
					return ""+r;
				}

				c-=1;

				int startRowValue = 1;
				//			if (startRow.getText().length() > 0) {
				//				startRowValue = Integer.parseInt(startRow.getText());
				//			}
				//			
				//			if (startRowValue < 1 || startRowValue > 50) {
				//				startRowValue = 1;
				//			}


				String [] sections = previewData[r+(startRowValue-1)].split("["+getDelimiter(delimiters)+"]");
				if (sections.length <=c) {
					return null;
				}
				else {
					return sections[c];
				}
			}
		}

		/**
		 * MyTableCellRenderer is used to shade parts of the data which will be skipped
		 * when importing.
		 */
		private class MyTableCellRenderer extends DefaultTableCellRenderer {

			// The only point of this class is so we can tell which rows we are ignoring.
			// We do this by selecting the ignored ones so they go grey.

			/* (non-Javadoc)
			 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
			 */
			public Component getTableCellRendererComponent (JTable table,Object value,boolean isSelected, boolean hasFocus, int r, int c) {

				if (r<Integer.parseInt((String)startRow.getSelectedItem())) {
					isSelected = true;
				}
				else {
					isSelected = false;
				}
				Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);

				return cell;
			}

		}


	}			
}

	

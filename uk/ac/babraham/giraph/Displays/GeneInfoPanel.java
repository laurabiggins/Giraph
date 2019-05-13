package uk.ac.babraham.giraph.Displays;

/** This is quite old code and could do with cleaning up a bit. 
 * it's the panel that pops up  
 * 
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneList;


public class GeneInfoPanel  extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextArea textArea1;
	private JScrollPane sp1;
	private JTextArea textArea2;
	private JScrollPane sp2;
	private JTextArea overlappingGenes;
	private JScrollPane sp3;
	private JButton clearGenes1;
	private JButton clearGenes2;
	private JButton getOverlappingGenes;
	GeneList gl1;
	GeneList gl2;
	int count = 0;
	Font font1;
	Font font2;
	
	
	public GeneInfoPanel(giraphApplication application){
		
		setTitle("info");
		
		setSize(250,600);
		setMinimumSize(new Dimension(150,150));
		setVisible(true);
		
		getContentPane().add(createMainPanel());
		this.setAlwaysOnTop(true);
		setLocationRelativeTo(giraphApplication.getInstance());
	}	
	
	public JPanel createMainPanel(){
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		font1 = new Font("Dialog", Font.BOLD, 10);
		font2 = new Font("Dialog", Font.PLAIN, 10);
		
		textArea1 = new JTextArea();//"", 5,20);
		textArea1.setRows(5);
		setTextAreaParameters(textArea1, font1);
		
		textArea2 = new JTextArea("");
		textArea2.setRows(5);
		setTextAreaParameters(textArea2, font1);
		
		overlappingGenes = new JTextArea();
		setTextAreaParameters(overlappingGenes, font2);

		sp1 = new JScrollPane(textArea1);
		sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		sp2 = new JScrollPane(textArea2);
		sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		sp3 = new JScrollPane(overlappingGenes);
		sp3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		clearGenes1 = new JButton("clear");
		clearGenes1.setActionCommand("clearGenes1");
		clearGenes1.addActionListener(this);
		
		clearGenes2 = new JButton("clear");
		clearGenes2.setActionCommand("clearGenes2");
		clearGenes2.addActionListener(this);
		
		getOverlappingGenes = new JButton("get overlapping genes");
		getOverlappingGenes.setActionCommand("getOverlappingGenes");
		getOverlappingGenes.addActionListener(this);
				
		mainPanel.add(sp1);
		mainPanel.add(clearGenes1);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		mainPanel.add(sp2);
		mainPanel.add(clearGenes2);
		mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
		mainPanel.add(getOverlappingGenes);
		mainPanel.add(sp3);	
		
		return mainPanel;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();	
		
		if (command.equals("clearGenes1")) {
			textArea1.setText("");
			overlappingGenes.setText("");
			textArea1.setBorder(null);
			gl1 = null;
		}
		else if (command.equals("clearGenes2")) {
			textArea2.setText("");
			overlappingGenes.setText("");
			textArea2.setBorder(null);
			gl2 = null;
		}
		else if (command.equals("getOverlappingGenes")){
			overlappingGenes.setText("");
			String[] gs;
			if(gl1 == null){
				JOptionPane.showMessageDialog(this, "Two gene lists need to be selected", "", JOptionPane.ERROR_MESSAGE);
			}
			else if(gl2 == null){
				JOptionPane.showMessageDialog(this, "Two gene lists need to be selected", "", JOptionPane.ERROR_MESSAGE);
			}
			else if (gl1.equals(gl2)){
				JOptionPane.showMessageDialog(this, "All the genes overlap because the same circle has been selected twice", "", JOptionPane.ERROR_MESSAGE);	
			}
			
			else {
				Gene[] overlaps = gl1.getOverlappingGenes(gl2);
				gs = new String[overlaps.length];
				for(int i =0; i < overlaps.length; i++){
					gs[i] = overlaps[i].getGeneSymbol();
				}

				setGeneInfo(overlappingGenes, gs);
			}	
		}		
	}	
	
	public void closePanel() {
		dispose();
	}
	
	public void setTextAreaParameters(JTextArea ta, Font f){
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setFont(f);
		ta.setCaretPosition(0);
	}
		
	public void removeGeneInfo(){
		textArea1.setText("");
		textArea2.setText("");
		overlappingGenes.setText("");
		textArea1.setBorder(null);
		textArea2.setBorder(null);
	}
	
	public void setGeneListInfo(GeneList gl, int x, Color colour){

		/** If either of the panels has been cleared by the user, then use the free panel, else overwrite the 2nd panel */
		if (textArea1.getText().trim().length() == 0){
			setGeneListInfo(textArea1, gl, x, gl.getGeneSymbols());
			textArea1.setBorder(BorderFactory.createTitledBorder(textArea1.getBorder(), gl.getFunctionalSetInfo().name(), 
					TitledBorder.CENTER, TitledBorder.TOP, font1, colour));
			gl1 = gl;
			overlappingGenes.setText("");
		}	
		else if (textArea2.getText().trim().length() == 0){
			setGeneListInfo(textArea2, gl, x, gl.getGeneSymbols());
			textArea2.setBorder(BorderFactory.createTitledBorder(textArea2.getBorder(),  gl.getFunctionalSetInfo().name(), 
					TitledBorder.CENTER, TitledBorder.TOP, font1, colour));
			gl2 = gl;
			overlappingGenes.setText("");
		}
		else{
			textArea2.setText("");
			overlappingGenes.setText("");
			textArea2.setBorder(null);
			setGeneListInfo(textArea2, gl, x, gl.getGeneSymbols());
			textArea2.setBorder(BorderFactory.createTitledBorder(textArea2.getBorder(),  gl.getFunctionalSetInfo().name(), 
					TitledBorder.CENTER, TitledBorder.TOP, font1, colour));
			gl2 = gl;
			overlappingGenes.setText("");
		}
	}
	
	// for the overlapping genes
	public void setGeneInfo(JTextArea ta, String[] geneSymbols){		
	
		if (geneSymbols.length == 0){
			ta.setFont(font1);
			ta.setText("no overlapping genes");
			return;
		}
		else if(geneSymbols.length == 1){
			ta.setFont(font1);
			ta.setText("1 overlapping gene");
		}
		else{
			ta.setFont(font1);
			ta.setText(geneSymbols.length + " overlapping genes");
		}
		
		for (int i = 0; i< geneSymbols.length; i++){
			ta.setFont(font2);
			ta.append("\n");
			ta.append(geneSymbols[i]);
			ta.setCaretPosition(0);
		}		
	}
	
	// the list of genes within the functional category
	public void setGeneListInfo(JTextArea ta, GeneList gl, int x, String[] geneSymbols){
		ta.setText("");
		ta.append("Number of genes: ");
		ta.append(Integer.toString(gl.getGenes().length));
		// we only have this information if query genes have been entered, not if a results file has been entered.
		if(gl.getFunctionalSetInfo().totalNoOfGenesInCategory() > 0){		
			ta.append("\nTotal number of genes in category: ");
			ta.append(Integer.toString(gl.getFunctionalSetInfo().totalNoOfGenesInCategory()));
		}	
		ta.append("\np value: ");
		ta.append(Double.toString((double)Math.round(gl.getPvalue().q()*1000000)/1000000));
		for (int i = 0; i< geneSymbols.length; i++){
			ta.append("\n");
			ta.append(geneSymbols[i]);
		ta.setCaretPosition(0);				
		}	
	}	
}

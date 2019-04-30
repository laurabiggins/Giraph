package uk.ac.babraham.giraph.Filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;



import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;

public class DataFilter implements ActionListener{

	
	GeneListCollection geneListCollection;
	
	//int geneListSizeCutoff;
	
	float stringency;
	
	private Vector<FilterListener> filterListeners;
	
	public DataFilter(GeneListCollection geneListCollection, float stringency){
		
		this.geneListCollection = geneListCollection;
		//this.geneListSizeCutoff = geneListSizeCutoff;
		this.stringency = stringency;
		this.filterListeners = new Vector<FilterListener>();
	}
	
	public DataFilter filterBySizeAndStringency(){	
		
		int noRemaining = checkNoOfFilteredGenelistsRemaining();
		
		if (noRemaining > 500){
			//JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Using your list of genes and this filter (adjusted p value " + stringency + ", min no of genes in category " + geneListSizeCutoff + 
			//		" would leave " + noRemaining + " siginficant gene ontology categories. This is too many to display in Giraph, try entering a different list of genes or increase the stringency of the filters.", "Too many circles", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Using your list of genes and this filter (p value " + stringency +
					") would leave " + noRemaining + " gene ontology categories. \nThis is too many to display in Giraph, try entering a different "
							+ "list of genes or increase the stringency of the filters.", "Too many circles", JOptionPane.ERROR_MESSAGE);

			return null;
		}
		
		if (noRemaining <= 1){
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), "This filter (adjusted p value " + stringency + ") would leave 0 or 1 significant gene ontology categories, "
					+ "try entering a different list of genes or adjust the filters to be more lenient", "Filter too strict", JOptionPane.ERROR_MESSAGE);

			return null;
		}
		else{
			
		/*	if (noRemaining > 500){
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Using your list of genes and this filter (adjusted p value " + stringency + ", min no of genes in category " + geneListSizeCutoff + 
						" would leave " + noRemaining + " siginficant gene ontology categories. Giraph might be able to cope with this, but it may be slow. Try increasing the stringency of the filters.", "Too many circles", JOptionPane.ERROR_MESSAGE);	
			}
			else{
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Setting p value threshold to " + stringency + " and minimum number of genes in functional category to " + geneListSizeCutoff + 
					" leaves "+ noRemaining + " circles", "filter results", JOptionPane.OK_CANCEL_OPTION);
			}
		*/	setValidities();	
			return this;
		}		
	}
	
	/** This isn't doing anything at the moment */
	public void actionPerformed(ActionEvent ae) {

		if (ae.getActionCommand().equals("ok")) {
			System.out.println("You've pressed ok");
			setValidities();
		}
		else if (ae.getActionCommand().equals("cancel")) {
			System.out.println("You've pressed cancel");
			//close dialog box();
		}
	}
	
	
	private int checkNoOfFilteredGenelistsRemaining(){
		
		int count = 0;
		GeneList [] allGeneLists = geneListCollection.getAllGeneLists();
		// To check how many genelists/circles would be left after the filter had been applied
		for (int i = 0; i < allGeneLists.length; i++){	
			
			//if((allGeneLists[i].getGenes().length >= geneListSizeCutoff) && (allGeneLists[i].getPvalue().q() <= stringency)){				
			//	count++;
			//}
			
			if(allGeneLists[i].getPvalue().q() <= stringency){
				
				count++;
			}
						
		}
		System.err.println("count of no of gene lists = " + count);
		return count;
	}
		
	public void addFilterListener(FilterListener fl){
		
		this.filterListeners.add(fl);
	}
	
	private void updateListeners(){
		
		for(int i=0; i<filterListeners.size(); i++){
			filterListeners.get(i).filtersUpdated(stringency);//, geneListSizeCutoff);
		}					
	}
	
//	public int getMinNoGenes(){
//		return geneListSizeCutoff;
//	}
	
	public float getPvalueCutoff(){
		return stringency;
	}
	
	// set the validity of the genelist to be true or false according to whether it fits in with the parameters specified by the user (	
	private void setValidities(){	
		
		//System.out.println("At start of setValidities in dataFilter");
		
		GeneList [] allGeneLists = geneListCollection.getAllGeneLists();
		
		for (int i = 0; i < allGeneLists.length; i++){
			
			if((allGeneLists[i].getPvalue().q() > stringency)){// || (allGeneLists[i].getGenes().length < geneListSizeCutoff)){
				allGeneLists[i].setValidity(false);
			}
			else{
				allGeneLists[i].setValidity(true);
			}
		}
		//System.out.println("At end of setValidities in dataFilter");
		updateListeners();
		//giraphApplication.getInstance().filtersUpdated(stringency);
	}	
}

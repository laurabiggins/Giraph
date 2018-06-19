package uk.ac.babraham.giraph.DataParser;

import uk.ac.babraham.giraph.DataTypes.GeneList;

public interface OptionsListener {
	
	
	public void gmtFileParsed();

	//public void queryGenesAnalysed(GeneList [] gl);
	
	public void geneInfoFileParsed();
	
	public void queryGenesImported();
	
	public void customBackgroundGenesImported();
	
}

package edu.univalle.vigtech_ir.utils;

import java.io.File;
import java.util.ArrayList;

import edu.univalle.vigtech_ir.Indexer;

public class IndexUtils {
	
	public ArrayList<Index> getIndexList(){
		
		ArrayList<Index> indexes = new ArrayList<Index>();
		
		Indexer indexer = new Indexer();
		
	    File indexDir = new File(PropertiesManager.getInstance().getProperty("index.path"));
	    for(String index : indexDir.list())
	    	if(new File(indexDir+"/"+index).isDirectory())
	    		indexes.add(indexer.getIndex(index));
	    		
		
		
		return indexes;
		
	}

}

package TE;

import gov.nih.nlm.nls.metamap.MetaMapApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.CosineSimilarity;
import util.Dataset;
import DS.ConceptsDiscovery;
import DS.QueryEngine;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class pruning {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static void URIspruning( Map<String, Dataset> resources ) throws IOException
	{
		Map<String, Dataset> uris =  new HashMap <String,Dataset>(resources) ;
		
        //get the most top five 
		int top = 3 ; 
		int count = 0 ; 
	    for (String concept: uris.keySet())
   	 	{
	    	Map<String, Double> Topconfident = new HashMap <String, Double>() ;
	    	Map<String, Double> confident = new HashMap <String, Double>() ;
	   		Dataset dataset = uris.get(concept) ;
   			List<String> UIRs = dataset.Sorturiconfident(3) ;
   			double max = 0.0 ; 
	    	for (String URI: UIRs)
	    	{
	    		String[] words = URI.split("!");  
	    		double sim = Double.parseDouble(words[1]);
	    		if ( sim > max)
	    		{
	    		 Topconfident.put(words[0], sim) ;
	    		 max = sim ; 
	    		}
	    		confident.put(words[0], sim) ;
	    	}
	   		dataset.SetTopuriconfident(Topconfident);
	   		dataset.Seturiconfident(confident);
	   			
   	 	}
	    
	}
	

}

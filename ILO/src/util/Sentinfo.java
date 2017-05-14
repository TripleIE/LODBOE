package util;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Sentinfo {
	
	public Model graph = ModelFactory.createDefaultModel(); 
	public Map<String, Dataset> lookupresources =  new HashMap<String, Dataset>();
	
	public Model getfullgraph()
	{
		Model wholegraph = ModelFactory.createDefaultModel(); 
		wholegraph = wholegraph.union(graph) ;
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graphcandidate = dataset.getcandidateGraph();
	   		wholegraph = wholegraph.union(graphcandidate) ;
	   		
   	 	}
		
		return wholegraph ; 
		
	}
	

}

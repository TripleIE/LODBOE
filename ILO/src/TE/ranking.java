package TE;

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import util.CosineSimilarity;
import util.Dataset;
import util.readfiles;
import DS.ConceptsDiscovery;
import DS.QueryEngine;
public class ranking {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Map<String, Dataset> URIRanking(Map<String, Integer> Sentconcepts, Map<String, Dataset> resources, MetaMapApi api ) throws IOException
	{
		Map<String, Dataset> uris =  new HashMap <String,Dataset>() ;
		

	    for (String concept: resources.keySet())
   	 	{
	   		Dataset dataset = resources.get(concept) ;
	   		List<String> statements=  new ArrayList<String>() ;
	   		Map<String,Integer> uri =  new HashMap <String, Integer>() ;
	   		List<String> uriranking=  new ArrayList<String>() ;
	   		for (String onto: dataset.getonto().keySet())
	   		{
	   			List<String> UIRs = dataset.getontoURIs(onto) ;
		    	for (String URI: UIRs)
		    	{
		    		// The DESCRIBE form returns a single result RDF graph containing RDF data about resources.
		    		// This data  is determined by the SPARQL query processor.
		    		Model  graph = QueryEngine.describe(URI,onto);
		    		if(graph == null)
		    		{
		    			continue ; 
		    		}
		    		Map<String, List<String>> lexical = new HashMap<String, List<String>>();
		    		List<String> info = new ArrayList<String>() ; 
		    		
		    		// list the statements in the Model
		    		StmtIterator iter = graph.listStatements();
		    		
		    		// print out the predicate, subject and object of each statement
		    		while (iter.hasNext())
		    		{
		    		    Statement stmt      = iter.nextStatement();  // get next statement
		    		    Resource  subject   = stmt.getSubject();     // get the subject
		    		    Property  predicate = stmt.getPredicate();   // get the predicate
		    		    RDFNode   object    = stmt.getObject();      // get the object

		    		    System.out.print(subject.toString());
		    		    System.out.print(" " + predicate.toString() + " ");
		    		    if (object instanceof Resource) {
		    		       System.out.print(object.toString());
		    		    } else {
		    		        // object is a literal
		    		        System.out.print(" \"" + object.toString() + "\"");
		    		        info.add(object.toString()) ; 
		    		        
		    		    }
		    		  //  lexical.put(URI, info) ;
		    		    //System.out.println(" .");
		    		}
		    		if (info.size() == 0)
		    			continue ; 
		    		  HashMap<String, Map<String, List<String>>> result = null ; 
		    	       try 
		    	       {
		    		             result  = ConceptsDiscovery.getcachconcepts(info,api);
		    	       }
		    	       catch (Exception e)
		    	       {
		    	    	   System.out.println(e) ;
		    	       }
		    		Map<String,Integer> allconcept =  new HashMap <String, Integer>() ;
		    		for (String temURI: result.keySet())
		    		{
		    			List<String> concp = null;
			    		for (String literal: result.get(temURI).keySet())
			    		{
			    			concp =   result.get(temURI).get(literal) ;
				    		for (String cpt:concp )
				    		{
				    			if(cpt.length() > 2)
				    			 allconcept.put(cpt, 1);
				    		}
			    		}
			    		

		    		}
		    		
		    		
		    		// get consign similarity 
		    		
		    		double ret = CosineSimilarity.cosineSimilarity( allconcept, Sentconcepts) ;
		    		URI = URI+ "!" + Double.toString(ret);
		    		uriranking.add(URI);

		    	}
		    	
		    	dataset.Setonto(onto, uriranking);
	   		}
	   		
	   		uris.put(concept, dataset) ;
   	 	}
	    
	    return uris ;
	}
	
	
	public static Map<String, Dataset> URIRankingLLD(Map<String, Integer> Sentconcepts, Map<String, Dataset> resources, MetaMapApi api ) throws IOException
	{
		Map<String, Map<String, List<String>>> uris =  new HashMap <String,Map<String, List<String>>>() ;
		

	    for (String concept: resources.keySet())
   	 	{
	   		Dataset dataset = resources.get(concept) ;
	   		List<String> statements=  new ArrayList<String>() ;
	   		Map<String,Integer> uri =  new HashMap <String, Integer>() ;
	   		List<String> uriranking=  new ArrayList<String>() ;
   			Map<String, List<String>> Stype =  new HashMap <String,List<String>>() ;
   			Map<String,Integer> Semlist =  new HashMap <String, Integer>() ;
   			Map<String,String> grouptype =  new HashMap <String, String>() ;
   			
	    	List<String>  lines = null ;
	    	
	    	File fFile = new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\TripleExtraction\\umls\\SemGroups_2013.txt");
	    	lines = readfiles.readLinesbylines(fFile.toURL()) ; 
	    	for ( String lin:lines)
	    	{
	    		String[] tokens = lin.split("\\|");
	    		grouptype.put(tokens[2],tokens[1]); 
	    		
	    	}
	    	
	   		for (String onto: dataset.getonto().keySet())
	   		{

	   			List<String> UIRs = dataset.getontoURIs(onto) ;
		    	for (String URI: UIRs)
		    	{
		    		
		    		 if (URI.contains("http://linkedlifedata.com/resource/#"))
		    			 continue ; 
		    		 
		    		 
		    	     ResultSet results1 = QueryEngine.UMLSsemantictype(URI,onto);
		    	     List<String> semantictype =  new ArrayList<String>() ;
		    	     
		    	     if(results1 == null)
		    	    	 continue ;
		    	     
	    	         for (; results1.hasNext();) 
	    	         {

	    			     QuerySolution soln = results1.nextSolution() ;
	    		         //String type = soln.get("semantictype").toString();  //get the subject
	    		         String type = soln.get("semantictype").asResource().getLocalName();
	    		        // String nmn = soln.get("semantictype").asResource().getNameSpace();
	    		         System.out.println(type) ;

	    		         String groupt = grouptype.get(type);
	    		         if (groupt != null)
	    		         {
		    		         semantictype.add(type) ;
		    		         semantictype.add(groupt) ;
		    		         
		    		         // the full list of all type 
		    		         Semlist.put(type, 0) ;
	    		        	 Semlist.put(groupt, 0) ; 
	    		         }
	    		         
	    	         }		    	         
	    	         Stype.put(URI, semantictype) ;  
		    	}
		    	

		    	
	   		}
	   		
	   		Map<String, List<String>> Stypein =  new HashMap <String,List<String>>(Stype) ;
	   		Map<String, Double> uriconfident =  new HashMap <String, Double>() ;
	   		// URI
	   		for(String uriL:Stype.keySet())
	   		{
	   		    List<String> typelist = Stype.get(uriL) ; // semantic type list 
	   		    Map<String,Integer> Semm =  new HashMap <String, Integer>(Semlist) ;
   		    	double avrg = 0.0 ;
   		    	
   		    	// build vector for the semantic type 
	   		    for(String tt :typelist)
	   		    {
	   		    	Semm.put(tt, 1) ;  		    		
	   		    }
	   			
	   		    // get other URIs 
   		    	for(String LL:Stypein.keySet())
   		    	{
   		    		Map<String,Integer> Semmin =  new HashMap <String, Integer>(Semlist) ;
   		    		List<String> typelistin = Stype.get(LL) ;
   		    		
   		    		// build vector for URIs
   		    		for(String ttt :typelistin)
   		    		{
   		    			Semmin.put(ttt, 1) ;
   		    			
   		    		}

   		    		// cal relatedness
   		    		double ret = CosineSimilarity.cosineSimilarity(Semmin, Semm) ;
   		    		avrg = avrg + ret ; 
   		    	}
   		    	
	   		    avrg = avrg /Stypein.size() ;
		    	uriconfident.put(uriL, avrg) ;
	
	   		}
	   		dataset.Seturiconfident(uriconfident) ;
	   		
   	 	}
	    
        
	    
	    

		
		// get consign similarity 
		
	/*	double ret = CosineSimilarity.cosineSimilarity( allconcept, Sentconcepts) ;
		URI = URI+ "!" + Double.toString(ret);
		uriranking.add(URI);*/
	    return null ;
	}
}

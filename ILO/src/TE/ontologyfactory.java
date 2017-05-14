package TE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Dataset;
import TextProcess.removestopwords;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ontologyfactory {

   static  String skos = "http://www.w3.org/2004/02/skos/core#" ;
   static String  rdfs = "http://www.w3.org/2000/01/rdf-schema#" ;
   static String  rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
   static String  owl = "http://www.w3.org/2002/07/owl#" ;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static void ontowrite(String Concept, Map<String, Dataset> lookupresources)
	{

			Dataset dataset = lookupresources.get(Concept) ;
			Model graph = dataset.getcandidateGraph();
			
			graph.write(System.out, "RDF/XML-ABBREV") ; 
	}
	
	public static void ontoWrite( Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			Dataset dataset = lookupresources.get(concept) ;
			Model graph = dataset.getcandidateGraph();
			graph.write(System.out, "RDF/XML-ABBREV") ; 
   	 	}
	}
	
	
	public static void ontoWriteWhole( Map<String, Dataset> lookupresources, Model graph) throws IOException
	{
		graph.setNsPrefix( "skos", skos ) ;
		graph.setNsPrefix( "owl", owl ) ;
		

		
		
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			Dataset dataset = lookupresources.get(concept) ;
			graph = dataset.getcandidateGraph().union(graph); 
			
   	 	}
		
		     graph.setNsPrefix( "skos", skos ) ;
		     graph.setNsPrefix( "owl", owl ) ;
		     graph.write(System.out, "RDF/XML-ABBREV") ; 
			 getontoPropertyHierarchy("http://www.w3.org/2000/01/rdf-schema#type", "type",graph) ;
			 getontoPropertyHierarchy("http://www.w3.org/2000/01/rdf-schema#label", "label",graph) ;
			 getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#altLabel", "altLabel",graph) ;
			 getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#definition", "definition",graph) ;
			 getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#definition", "definition",graph) ;
			 getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#broader", "broader",graph) ;
			 getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#broader", "narrower",graph) ;
		     graph.write(System.out, "RDF/XML-ABBREV") ; 
	}
	
	public static void ontoWriteWholetofile( Map<String, Dataset> lookupresources, Model graph,String output) throws IOException
	{
		 graph.setNsPrefix( "skos", skos ) ;
		 graph.setNsPrefix( "owl", owl ) ;
		
		getontoPropertyHierarchy("http://www.w3.org/2000/01/rdf-schema#type", "type",graph) ;
		getontoPropertyHierarchy("http://www.w3.org/2000/01/rdf-schema#label", "label",graph) ;
		getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#altLabel", "altLabel",graph) ;
		getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#definition", "definition",graph) ;
		getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#broader", "broader",graph) ;
	   getontoPropertyHierarchy("http://www.w3.org/2004/02/skos/core#broader", "narrower",graph) ;
		
		
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			Dataset dataset = lookupresources.get(concept) ;
			if (graph.isEmpty())
			{
				graph = dataset.getcandidateGraph() ;
			}
			else
			{
				graph = graph.union(dataset.getcandidateGraph()) ; 
			}
			
   	 	}
		       graph.setNsPrefix( "skos", skos ) ;
		       graph.setNsPrefix( "owl", owl ) ;
	
		     
		     

		      /**************************************************************/ 
		      /* Write it to File  */
		      /**************************************************************/ 
				FileOutputStream fop = null;
				File file;
				file = new File(output);
				fop = new FileOutputStream(file) ;
			    graph.write(fop, "RDF/XML-ABBREV") ; 
	}
	
	
	public static void getontosyntax(Map<String,List<String>> TripleCandidates, Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String concept: TripleCandidates.keySet())
   	 	{
			Dataset dataset = lookupresources.get(concept) ;
			Model graph = dataset.getcandidateGraph();
			 for (String triple: TripleCandidates.get(concept) )
			 {
				 
				 // start getting the object and predicate from the triples 
				 String tokens[] =  triple.split("~") ;
				 String Object ;
				 if(tokens.length < 3)
					 continue ; 

				 {
					 String _tokens[] =  tokens[2].split("\\[|\\^+");
					// removing the lang 
					 String Objects[] = _tokens[0].split("@") ;
					 Object = Objects[0] ;
				 }
				 
				 String temp  = tokens[0] ;
				 String subj  = temp.replaceAll("http://linkedlifedata.com/sparql","")  ;
				 String predicate = tokens[1] ;
				 
				 Resource entity = graph.createResource(subj);
	 	         final Property p = ResourceFactory.createProperty(predicate) ;
	 	         entity.addProperty(p, Object);	 
			 }
	   		
   	 	}
	}
	public static void getontoSyntaticPattern(ArrayList<String> RelInstances , Model Sentgraph, Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String rel: RelInstances)
   	 	{
			String[] tokens = rel.split(",") ;
   			String uri1 = ""; 
   			
 		    Dataset dataset = lookupresources.get(tokens[1].trim().toLowerCase()) ;
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
 
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri1 = onto ;
	   		}
			
   			String uri2 = ""; 
   			
 		    dataset = lookupresources.get(tokens[2].trim().toLowerCase()) ;
   			Topuriconfident = dataset.gettopuriconfident() ;
 
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri2 = onto ;
	   		}

		  Resource rec1 = Sentgraph.createResource(uri1);
		  Resource rec2 = Sentgraph.createResource(uri2);
		  final Property p = ResourceFactory.createProperty(tokens[3]) ;
          rec1.addProperty(p, rec2);	  		
   	 	}
	}

	public static void getonto (Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
   			String uri = ""; 
	   		for (String onto: dataset.getonto().keySet())
	   		{
	   			List<String> UIRs = dataset.getontoURIs(onto) ;
	   			double max = 0.0 ;

		    	for (String URI: UIRs)
		    	{    		
		    		String[] words = URI.split("!");  
		    		if (Double.parseDouble(words[1]) > max)
		    		{
		    			max = Double.parseDouble(words[1]) ;
		    			uri = words[0] ; 
		    		}

		    	}
	   		}
	   		
	   		// set the lexical alt label
	   		 List<String> altlabels = dataset.getaltlebel() ;
	   		if (altlabels != null)
	   		{
	   			for (String label: altlabels)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty("skos:altLabel") ;
	 	         	rec.addProperty(p, label);	
	   			}
	   			
	   		}
	   		
   	 	}
	}
	
	
	public static void getontoHierarchy (Map<String, Dataset> lookupresources)
	{
		System.out.println("***********************************getontoHierarchy************************************************");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
	   		graph.setNsPrefix( "rdfs", rdfs ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Hierarchy = dataset.Hierarchy ; 
	   		if (Hierarchy != null)
	   		{
	   			for (int i = Hierarchy.size()-2 ; i > -1; i--)
	   			{
	   				String hier = Hierarchy.get(i) ;
	   				String tokens[] = hier.split("!") ;
	   				Resource child = graph.createResource(uri);
	   				Resource parent = graph.createResource(tokens[0]);
	   				
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( skos + "broader") ;
	 	         	child.addProperty(p, parent);
	 	         	final Property pp = ResourceFactory.createProperty(rdfs + "label") ;
	 	         	parent.addProperty(pp, tokens[1]);
	 	         	uri = tokens[0] ;
	   			}
	   			
	   		}
	   		
   	 	}
	}
	public static void getontoNHierarchy (Map<String, Dataset> lookupresources)
	{
		System.out.println("*******************************getontoNHierarchy******************************************");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
	   		graph.setNsPrefix( "rdfs", rdfs ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Hierarchy = dataset.Hierarchy ;
	   		 
	   		if (Hierarchy != null)
	   		{
	   			for (int i = Hierarchy.size()-2 ; i > -1; i--)
	   			{
	   				String hier = Hierarchy.get(i) ;
	   				String tokens[] = hier.split("!") ;
	   				Resource child = graph.createResource(uri);
	   				Resource parent = graph.createResource(tokens[0]);
	   				
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( skos + "narrower") ;
	 	         	child.addProperty(p, parent);
	 	         	final Property pp = ResourceFactory.createProperty(rdfs + "label") ;
	 	         	parent.addProperty(pp, tokens[1]);
	 	         	uri = tokens[0] ;
	   			}
	   			
	   		}
	   		
   	 	}
	}
	public static void getontoSemanticType (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoSemanticType");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "rdf", rdf ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Category = dataset.Category ;
	   		if (Category != null)
	   		{
	   			for (String Definition: Category)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty(rdf  + "type") ;
	 	         	rec.addProperty(p, Definition);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	
	public static void getontoclass (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontoclass");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		OntModel graph = dataset.getontocandidateGraph();
	   		graph.setNsPrefix( "owl", owl ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
    			break ; 
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   	    OntClass rec = graph.createClass(uri);
	   	    graph.write(System.out, "RDF/XML-ABBREV") ;
   	 	}
	}
	
	

	
	public static void getontosameAs (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontosameas");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		
   			String topuri = ""; 
   			Map<String, Double> uriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: uriconfident.keySet())
	   		{
    			topuri = onto ;
	   		}
	   		
	   		if (topuri.isEmpty())
	   			   continue ; 
	   		
	   		
	   		
	   		graph.setNsPrefix( "owl", owl ) ;
   			
   			List<String> Topuriconfident = dataset.getTopBesturiconfident(3,0.5) ;
   			
   			if (Topuriconfident.size() > 1 )
   			{

		   		int count = 0 ; 
		   		for (String tempuri: Topuriconfident)
		   		{
		   			count++ ;
	    			if (count == 1)
	    				continue ; 
	    			
	    			
	    			
	    			
	    			// create sameas relation 
	    			String[] uri  = tempuri.split("!", 2) ;
	    			if(uri.length > 0 )
	    			{
		   				Resource rec = graph.createResource(topuri);
	 	        		// add the property
		 	         	final Property p = ResourceFactory.createProperty(owl  + "sameAs") ;
		 	         	rec.addProperty(p, uri[0]);
	    			}
	    			
		   		}
   			}
	   		
   	 	}
	}
	

	public static void getontodefinition (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontodefinition");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Definitions = dataset.Definition ;
	   		if (Definitions != null)
	   		{
	   			for (String Definition: Definitions)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( skos + "definition") ;
	 	         	rec.addProperty(p, Definition);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	
	public static void getontoscheme (Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ;  
	   		
	   		// set the lexical alt label
	   		 List<String> scheme = dataset.ontology ;
	   		if (scheme != null)
	   		{
	   			for (String label: scheme)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty(skos  + "inScheme") ;
	 	         	rec.addProperty(p, label);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	public static void getontoPreflabel (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoPreflabel");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
   			
   			if (Topuriconfident.size() == 0 )
   			   continue ; 	
   			 
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
   			
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
   			Map<String,List<String>> syn = new HashMap<String, List<String>>();
   			String PrefLabel = dataset.PrefLabel ;
   			
   			if (PrefLabel == null )
   				continue ; 
   			String tokens[] = PrefLabel.split(" ") ;
			Resource rec = graph.createResource(uri);
			Resource rec1 = graph.createResource(tokens[0]);
    		// add the property
         	final Property p = ResourceFactory.createProperty(skos+ "PrefLabel") ;
         	rec.addProperty(p, rec1);	
         	final Property p1 = ResourceFactory.createProperty(skos + "PrefLabel") ;
         	String Label = tokens[2] ;
         	
         	System.out.println(Label);
         	Label = Label.replace(")", " ") ;
         	Label = Label.replace("(", " ") ;
         	Label = Label.trim() ;
         	rec1.addProperty(p, Label);
   	 	}
	}
	
	public static void getontoSynonym (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoSynonym");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
   			Map<String,List<String>> syn = new HashMap<String, List<String>>();
   			List<String> Syns = dataset.Synonym ;
   			if (Syns == null )	
   				continue ;
   			
   			
   			double max = 0.0 ;
   			List<String> alts = new ArrayList<String>()  ;
   			
	    	for (String synon: Syns)
	    	{    		
	    		String[] words = synon.split("!");  
	    		
	    		if (syn.containsKey(words[1].toLowerCase()))
	    		{
	    			alts = syn.get(words[1].toLowerCase());
	    			if (!alts.contains(words[2].toLowerCase()))  
	    			{
	    				alts.add(words[2].toLowerCase()) ;
	    			}
	    		}
	    		else
	    		{
	    			alts.add(words[2].toLowerCase()) ;
	    			syn.put(words[1].toLowerCase(), alts) ;
	    		}

	    	}
	    	
	    	for (String label: syn.keySet())
	    	{
   				Resource rec = graph.createResource(uri);
	        		// add the property
 	         	final Property p = ResourceFactory.createProperty(skos + "altLabel") ;
 	         	rec.addProperty(p, label);
	    		
	    		
	    		
	    	}
   	 	}
	}
	
	
	public static void getontoassociate (Map<String, Dataset> lookupresources)
	{
		 Map<String, Dataset> tempresources = new HashMap<String, Dataset>();
		 tempresources.putAll(lookupresources);
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			System.out.println("******************" + concept + "******************");
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getGraph();
	   		Model candidategraph = dataset.getcandidateGraph() ;
			// list the statements in the Model
			StmtIterator iter =  graph.listStatements();
	   		
	    	while (iter.hasNext())
			{
			    Statement stmt      = iter.nextStatement();  // get next statement
			    Resource  subject   = stmt.getSubject();     // get the subject
			    Property  predicate = stmt.getPredicate();   // get the predicate
			    RDFNode   object    = stmt.getObject();      // get the object
			    for (String conceptin: tempresources.keySet()) 
			    {
			    	
			    	if (concept.equals(conceptin))
			    		continue ; 
			   		Dataset datasetin = lookupresources.get(concept) ;
			   		Model graphin = dataset.getGraph();
					// list the statements in the Model
					StmtIterator iterin =  graph.listStatements();
					while (iterin.hasNext())
					{
					    Statement stmtin      = iterin.nextStatement();  // get next statement
					    Resource  subjectin   = stmtin.getSubject();     // get the subject
					    Property  predicatein = stmtin.getPredicate();   // get the predicate
					    RDFNode   objectin    = stmtin.getObject();      // get the object
					    
					    // add a resource
					    if (object.toString().equals(subjectin.toString()) )
					    {
			   				Resource rec = candidategraph.createResource(subject);
		 	        		// add the property
			 	         	final Property p = ResourceFactory.createProperty(predicate.toString()) ;
			 	         	rec.addProperty(p, subjectin.toString());
			 	         	System.out.println(object.toString() + ", " + predicate.toString() + ", " + subjectin.toString());
					    }
					    
					    // add a resource
					    if(object.toString().equals(objectin.toString()) && predicate.toString().equals(predicatein))
					    {
			   				Resource rec = candidategraph.createResource(subject);
		 	        		// add the property
			 	         	final Property p = ResourceFactory.createProperty(predicate.toString()) ;
			 	         	rec.addProperty(p, subjectin.toString());
			 	         	System.out.println(object.toString() + ", " + predicate.toString() + ", " + subjectin.toString());
					    }
			    	
					}

			    }
	   		
   	 		}
   	 	}
	}
	
	
	public static void getontoPropertyHierarchy (String uri,String property ,Model graph) throws IOException
	{
		System.out.println("***********************************getontoHierarchy************************************************");

			
	   		graph.setNsPrefix( "skos", skos ) ;
	   		graph.setNsPrefix( "rdfs", rdfs ) ;
	   		
	   		
	   		
	   		List<String> Hierarchy = Enrichment.LLDHierarchyProperty(uri,property)  ;
	   		
	   		if (Hierarchy != null)
	   		{
	   			for (int i = Hierarchy.size()-2 ; i > -1; i--)
	   			{
	   				String hier = Hierarchy.get(i) ;
	   				String tokens[] = hier.split("!") ;
	   				Resource child = graph.createResource(uri);
	   				Resource parent = graph.createResource(tokens[0]);
	   				
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( rdfs + "subPropertyOf") ;
	 	         	child.addProperty(p, parent);
	 	         	
	 	         	
	 	         	final Property pp = ResourceFactory.createProperty(rdfs + "label") ;
	 	         	parent.addProperty(pp, tokens[1]);
	 	         	uri = tokens[0] ;
	   			}
	   			
	   		}
	   		

	}

}

package TE;

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import util.Dataset;
import util.ReadXMLFile;
import util._Entity;
import util.surfaceFormDiscovery;
import DS.ConceptsDiscovery;
import DS.QueryEngine;
public class inferenc {

	static String pred = "rdfs:label|skos:prefLabel|skos:altLabel" ;
    static int urilimit = 100 ; 
	static Map<String, Integer> _resources; 
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MetaMapApi api = new MetaMapApiImpl();
		List<String> theOptions = new ArrayList<String>();
	    theOptions.add("-y");  // turn on Word Sense Disambiguation
	    theOptions.add("-u");  //  unique abrevation 
	    theOptions.add("--negex");  
	    theOptions.add("-v");
	    theOptions.add("-c");   // use relaxed model that  containing internal syntactic structure, such as conjunction.
	    if (theOptions.size() > 0) {
	      api.setOptions(theOptions);
	    }
	    
		Map<String, Integer> allconcepts = null ; 
		Map<String,List<String>> TripleCandidates = new HashMap<String, List<String>>();
		TripleCandidates =  ReadXMLFile.Deserialize("F:\\eclipse64\\eclipse\\Finaltriples ver2") ; 
		if (TripleCandidates == null)
		{
			Map<String, List<String>> triples = null ; //ReadXMLFile.Deserialize("F:\\eclipse64\\eclipse\\triples ver2") ;
			if (triples == null)
			{
				Map<String, List<String>> titles =  ReadXMLFile.ReadCDR_TestSet_BioC()  ;
				Map<String, List<String>> temptitles = new HashMap<String, List<String>>(); 
				for(String title : titles.keySet())
				{
					
					List<String> GoldSndconcepts = titles.get(title); 
					//title = "common cold is a disease" ;
					//title = "Antiandrogenic therapy can cause coronary arterial disease." ;
					//title = "Influenza is the most common cause of Runny nose" ;
					temptitles.put(title,GoldSndconcepts) ;
				}
				
				allconcepts = null ; //  ReadXMLFile.Deserializedir("F:\\eclipse64\\eclipse\\conceptDictlld ver2") ; 
				if (allconcepts == null)
				{
				   allconcepts  = ConceptsDiscovery.getconcepts(temptitles,api);
				   ReadXMLFile.Serializeddir(allconcepts, "F:\\eclipse64\\eclipse\\conceptDictlld ver2");
				}
				//likehoodrelation(allconcepts) ;
				Map<String,List<String>> ConcpForms = new HashMap<String, List<String>>();
				Map<String,Integer> wordforms = new HashMap<String, Integer>();
				for (String concp:allconcepts.keySet())
				{
					 List<String> form1 = new ArrayList<String>()  ;
					wordforms  = surfaceFormDiscovery.getsurfaceFormMesh(concp);
					for (String form:wordforms.keySet())
					{
						form1.add(form) ;
					}
					ConcpForms.put(concp, form1)  ;
				}
				//ReadXMLFile.Serialized(ConcpForms, "F:\\eclipse64\\eclipse\\wordforms ver1");
				
				
				

				
				Map<String, Dataset> lookupresources = null ; //ReadXMLFile.Deserializersrc( "F:\\eclipse64\\eclipse\\resources ver2"); 
				if (lookupresources == null)
				{
					lookupresources = resourcesSemanticLookupofconcepts(allconcepts) ;
					ReadXMLFile.Serializedsrc(lookupresources, "F:\\eclipse64\\eclipse\\resources ver2");
				}
				
				triples = TriplesRetrieval (lookupresources) ;
				ReadXMLFile.Serialized(triples, "F:\\eclipse64\\eclipse\\triples ver1");
			}


			Map<String,List<String>> Tripleresult = new HashMap<String, List<String>>();
	
			
			Tripleresult  = syntactic.getExactMatch(triples,"runny nose is the symptom of common cold.") ;	
			if (Tripleresult != null)
				TripleCandidates = addTriple (Tripleresult,TripleCandidates,"getExactMatch");
			printtriples(TripleCandidates) ;
			Tripleresult  =  syntactic.getSimilairMatch(triples,"runny nose is the symptom of common cold.") ;
			if (Tripleresult != null)
				TripleCandidates = addTriple (Tripleresult,TripleCandidates,"getSimilairMatch"); 
			printtriples(TripleCandidates) ;
			
			Tripleresult  =  semantic.getSemanticMatch(triples,"runny nose is the symptom of common cold.") ;
			if (Tripleresult != null)
				TripleCandidates = addTriple (Tripleresult,TripleCandidates,"semantic.getSemanticMatch"); 
			
			
			printtriples(TripleCandidates) ;
			TripleCandidates = getProperty(TripleCandidates) ;
			ReadXMLFile.Serialized(TripleCandidates, "F:\\eclipse64\\eclipse\\Finaltriples ver1");
		}
		
		
		printtriples(TripleCandidates) ;
		TripleCandidates = pruning(TripleCandidates,"http://www.w3.org/") ;
		printtriples(TripleCandidates) ;
		ReadXMLFile.Serialized(TripleCandidates, "F:\\eclipse64\\eclipse\\result ver1");
		
		
		
		
		allconcepts =  ReadXMLFile.Deserializedir("F:\\eclipse64\\eclipse\\conceptDictlld") ; 
	//	TripleCandidates = semantic.getSemanticRelationbyDefinition(allconcepts, "runny nose is the symptom of common cold.");
		printtriples(TripleCandidates) ;
		//TripleCandidates = syntactic.getPatternRelation(allconcepts,"runny nose is the symptom of common cold.") ;
		printtriples(TripleCandidates) ;
		
 
		
		
		
		
		
        return ; 
	}
	
	
	
	
	
	public static void _main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Map<String, Integer> allconcepts = null ; 
		Map<String,List<String>> TripleCandidates = new HashMap<String, List<String>>();
		TripleCandidates =  ReadXMLFile.Deserialize("F:\\eclipse64\\eclipse\\Finaltriples ver2") ; 
		
		
		MetaMapApi api = new MetaMapApiImpl();
		List<String> theOptions = new ArrayList<String>();
	    theOptions.add("-y");  // turn on Word Sense Disambiguation
	    theOptions.add("-u");  //  unique abrevation 
	    theOptions.add("--negex");  
	    theOptions.add("-v");
	    theOptions.add("-c");   // use relaxed model that  containing internal syntactic structure, such as conjunction.
	    if (theOptions.size() > 0) {
	      api.setOptions(theOptions);
	    }
		
		
		if (TripleCandidates == null)
		{
			Map<String, List<String>> triples = ReadXMLFile.Deserialize("F:\\eclipse64\\eclipse\\triples ver2") ;
			if (triples == null)
			{
				Map<String, List<String>> titles =  ReadXMLFile.ReadCDR_TestSet_BioC()  ;
				Map<String, List<String>> temptitles = new HashMap<String, List<String>>(); 
				for(String title : titles.keySet())
				{
					
					List<String> GoldSndconcepts = titles.get(title); 
					//title = "common cold is a disease" ;
					//title = "Antiandrogenic therapy can cause coronary arterial disease." ;
					title = "Influenza is the most common cause of Runny nose" ;
					temptitles.put(title,GoldSndconcepts) ;
					break ; 
				}
				
				allconcepts =  ReadXMLFile.Deserializedir("F:\\eclipse64\\eclipse\\conceptDictlld ver2") ; 
				if (allconcepts == null)
				{
				   allconcepts  = ConceptsDiscovery.getconcepts(temptitles,api);
				   ReadXMLFile.Serializeddir(allconcepts, "F:\\eclipse64\\eclipse\\conceptDictlld ver2");
				}
				likehoodrelation(allconcepts) ;
				Map<String,List<String>> ConcpForms = new HashMap<String, List<String>>();
				Map<String,Integer> wordforms = new HashMap<String, Integer>();
				for (String concp:allconcepts.keySet())
				{
					 List<String> form1 = new ArrayList<String>()  ;
					wordforms  = surfaceFormDiscovery.getsurfaceFormMesh(concp);
					for (String form:wordforms.keySet())
					{
						form1.add(form) ;
					}
					ConcpForms.put(concp, form1)  ;
				}
				ReadXMLFile.Serialized(ConcpForms, "F:\\eclipse64\\eclipse\\wordforms ver1");
				
				
				

				
				Map<String, Dataset> lookupresources = ReadXMLFile.Deserializersrc( "F:\\eclipse64\\eclipse\\resources ver2"); 
				if (lookupresources == null)
				{
					lookupresources = resourcesSemanticLookupofconcepts(allconcepts) ;
					ReadXMLFile.Serializedsrc(lookupresources, "F:\\eclipse64\\eclipse\\resources ver2");
				}
				
				triples = TriplesRetrieval (lookupresources) ;
				ReadXMLFile.Serialized(triples, "F:\\eclipse64\\eclipse\\triples ver1");
			}


			Map<String,List<String>> Tripleresult = new HashMap<String, List<String>>();
	
			
			Tripleresult  = syntactic.getExactMatch(triples,"runny nose is the symptom of common cold.") ;	
			if (Tripleresult != null)
				TripleCandidates = addTriple (Tripleresult,TripleCandidates,"getExactMatch");
			printtriples(TripleCandidates) ;
			Tripleresult  =  syntactic.getSimilairMatch(triples,"runny nose is the symptom of common cold.") ;
			if (Tripleresult != null)
				TripleCandidates = addTriple (Tripleresult,TripleCandidates,"getSimilairMatch"); 
			printtriples(TripleCandidates) ;
			
			Tripleresult  =  semantic.getSemanticMatch(triples,"runny nose is the symptom of common cold.") ;
			if (Tripleresult != null)
				TripleCandidates = addTriple (Tripleresult,TripleCandidates,"getSemanticMatch"); 
			
			
			printtriples(TripleCandidates) ;
			TripleCandidates = getProperty(TripleCandidates) ;
			ReadXMLFile.Serialized(TripleCandidates, "F:\\eclipse64\\eclipse\\Finaltriples ver1");
		}
		
		
		printtriples(TripleCandidates) ;
		TripleCandidates = pruning(TripleCandidates,"http://www.w3.org/") ;
		printtriples(TripleCandidates) ;
		ReadXMLFile.Serialized(TripleCandidates, "F:\\eclipse64\\eclipse\\result ver1");
		
		
		
		
		allconcepts =  ReadXMLFile.Deserializedir("F:\\eclipse64\\eclipse\\conceptDictlld") ; 
	//	TripleCandidates = semantic.getSemanticRelationbyDefinition(allconcepts, "runny nose is the symptom of common cold.");
		printtriples(TripleCandidates) ;
		//TripleCandidates = syntactic.getPatternRelation(allconcepts,"runny nose is the symptom of common cold.") ;
		printtriples(TripleCandidates) ;
		
 
		
		
		
		
		
        return ; 
	}
	
	
	
	
	public static void likehoodrelation(Map<String, Integer> allconcepts) throws IOException 
	{
        List<String> lookupresources = null ; 
        Map<String,List<String>> nodes = new HashMap<String, List<String>>(); 
		for (String concept: allconcepts.keySet())
		{
			lookupresources = resourcesLookupLL(concept) ;
			nodes.put(concept, lookupresources) ;
		}
		
		
		
		for (String node : nodes.keySet())
		{
			List<String> resulturis = new ArrayList<String>()  ; 
			List<String> uris = nodes.get(node);
			resourcesLookupLLrec(1,uris,resulturis) ;
			for(String uri :resulturis)
			{
				 String tokens[] =  uri.split("/");
				 String litral1 = tokens[tokens.length -1] ;
				 tokens =  litral1.split("#");
				 litral1 = tokens[0] ;
				 
				for (String othernode : nodes.keySet())
				{
					if (othernode.equals(node))
					{
						continue ; 
					}
					
					
					
					List<String> resulturis1 = new ArrayList<String>()  ; 
					List<String> urisin = nodes.get(othernode);
					resourcesLookupLLrec(1,urisin,resulturis1) ;
					
					for(String uriin :resulturis1)
					{
						 String _tokens[] =  uriin.split("/");
						 String litral = _tokens[_tokens.length -1] ;
						 _tokens =  litral.split("#");
						 litral = _tokens[0] ;
						if (litral.toLowerCase() == litral1.toLowerCase())
						{
							int i = 0 ; 
							i = 0 ; 
						}
					}
				}
			}
		}
	}
	
	
	public static  List<String>  resourcesLookup(String concept) throws IOException
	{
		
		QueryEngine queryengine = new QueryEngine() ;
		List<String> tempuris3 = null ;
		  

			   List<String> uris3 = new ArrayList<String>() ;
			   tempuris3 =  queryengine.LODResourceLookup(concept, pred,urilimit) ;

				 if ( tempuris3 != null && tempuris3.size() > 0)  
				 {
                     uris3.addAll(tempuris3) ;
                     
				 }

		return uris3 ;
	}
	
	public static void likehoodrel(String lookupresource,boolean isLiteral,int maxdepth, int level,Model gragh) throws IOException 
	{
		
        if (isLiteral || maxdepth < level )
        {
		    	//System.out.println(object); 
		    	return ; 
        } 
        
     // create the resource
        Resource rec = gragh.createResource(lookupresource);
        Map<String,List<String>> nodes = new HashMap<String, List<String>>(); 
        ResultSet results = null ; 
        results = QueryEngine.JenaSparqlExample(lookupresource) ;
        
        ++level ;
		for (; results != null && results.hasNext();) 
		{

//		    Statement stmt      = iter.nextStatement();  // get next statement
//		    Resource  subject   = stmt.getSubject();     // get the subject
//		    Property  predicate = stmt.getPredicate();   // get the predicate
//		    RDFNode   object    = stmt.getObject();      // get the objecttain
		    
		    
		    // Result processing is done here.
	         QuerySolution soln = results.nextSolution() ;
	         RDFNode   object    = soln.get("o");      // get the object
	         RDFNode   predicate    = soln.get("p");

	         if(object != null)
	         {
	        	 if (object.isLiteral())
	        	 {
	        		 if(predicate!= null &&  predicate.toString().contains("http://") )
	 	        	{
	 	        		// add the property
	 	         	   final Property p = ResourceFactory.createProperty(predicate.toString()) ;
	 	         	   rec.addProperty(p, object.toString());
	 	        	}
	 	        	else
	 	        	{
	 	        		// add the property
	 	         	   final Property p = ResourceFactory.createProperty("rdf:none") ;
	 	         	   rec.addProperty(p, object.toString());
	 	        	}
	        		// System.out.println(lookupresource + "," + predicate + "," + object);
	        	 }
	        	  likehoodrel(object.toString(),object.isLiteral(),maxdepth,level,gragh) ;
	 	         if (!object.isLiteral())
	 	         {
	 	        	 
	 	        	if( predicate!= null && predicate.toString().contains("http://") )
	 	        	{
	 	        		// add the property
	 	         	   final Property p = ResourceFactory.createProperty(predicate.toString()) ;
	 	         	   rec.addProperty(p, object.toString());
	 	        	}
	 	        	else
	 	        	{
	 	        		// add the property
	 	         	   final Property p = ResourceFactory.createProperty("rdf:none") ;
	 	         	   rec.addProperty(p, object.toString());
	 	        	}
	
	         	    // System.out.println(lookupresource + "," + predicate + "," + object); 
	 	         }
	         }
	         

	         

		}
		
		
	}
	
	
	
	public static Map<String,List<String>> pruning(Map<String,List<String>>  TripleCandidates , String Voc)
	{
		Map<String,List<String>> _TripleCandidates = new HashMap<String, List<String>>(); 
		for (String concept : TripleCandidates.keySet())
		{
			
			System.out.println("***********************" + concept + "**************************") ;
			// get triples for specific concepts
			List<String> _triples = TripleCandidates.get(concept) ;
			List<String> _triplelist  = new ArrayList<String>()  ;
			Map<String,Integer> _TriplesC = new HashMap<String, Integer>();
			
			 for (String triple: _triples)
			 {
				 String onto[] = triple.split("\\*") ;
				 String tokens[] = onto[1].split("~") ;
				 if(tokens[1].contains(Voc))  	
				    _TriplesC.put(triple, 1) ;
			 }

			 for (String t: _TriplesC.keySet())
			 {
				 _triplelist.add(t);
			 }
			 
			 Collections.sort(_triplelist);
		 
			 _TripleCandidates.put(concept,_triplelist) ;
		}
		
		return _TripleCandidates ;
	}
	public static Map<String,List<String>> addTriple(Map<String,List<String>> source, Map<String,List<String>> dest, String type)
	{
		Map<String,List<String>> TripleCandidates = new HashMap<String, List<String>>();
		
		if (dest != null)
			TripleCandidates.putAll(dest);
		
		if (TripleCandidates.size() <= 0 )
		{
			TripleCandidates.putAll(source);
			return TripleCandidates ; 
		}
		

		
		for (String concept : source.keySet())
		{
			
			Map<String,Integer> Tripled = new HashMap<String, Integer>();
			List<String> _triplestemp = TripleCandidates.get(concept) ;
			if (_triplestemp == null)
				continue ;
			 for (String tripledes: _triplestemp)
			 {
				 Tripled.put(tripledes, 1) ;
			 }
			 System.out.println("") ;
			System.out.println("***********************" + concept +  type + "**************************") ;
			// get triples for specific concepts
			List<String> _triplesdest = TripleCandidates.get(concept) ;
			List<String> _triplesrc = source.get(concept) ;
			
			
			
			 for (String triple: _triplesrc)
			 {
				  if ( Tripled.put(triple, 1) == null) 
				  {
					  _triplesdest.add(triple) ;
					  System.out.println(triple)  ;
				  }
			 }
		}
		
		return TripleCandidates ;
	}
	
	public static Map<String,List<String>> getProperty(Map<String,List<String>>  TripleCandidates )
	{
		Map<String,List<String>> _TripleCandidates = new HashMap<String, List<String>>(); 
		QueryEngine queryengine = new QueryEngine() ;
		for (String concept : TripleCandidates.keySet())
		{
			
			System.out.println("***********************" + concept + "**************************") ;
			// get triples for specific concepts
			List<String> _triples = TripleCandidates.get(concept) ;
			List<String> _triplelist  = new ArrayList<String>()  ;

			
			 for (String triple: _triples)
			 {
				 String onto[] = triple.split("\\*") ;
				 String tokens[] = onto[1].split("~") ;
				 ResultSet results = queryengine.TriplePropertyaquery(tokens[0],onto[0]) ;  
				 
				 _triplelist.addAll(getstatements(results,tokens[0],onto[0]));

			 }
			 _TripleCandidates.put(concept,_triplelist) ;
		}
		
		return _TripleCandidates ;
	}

	public static  List<String>  resourcesLookupLL(String concept) throws IOException
	{
		
		QueryEngine queryengine = new QueryEngine() ;
		List<String> tempuris3 = null ;
		  

			   List<String> uris3 = new ArrayList<String>() ;
			   tempuris3 =  queryengine.lifedataResourceLookup(concept, pred,urilimit) ;

				 if ( tempuris3 != null && tempuris3.size() > 0)  
				 {
                     uris3.addAll(tempuris3) ;
                     
				 }

		return uris3 ;
	}
	
	public static  List<String>  resourcesLookupLLrec(int depth,String uri, List<String> uris3) throws IOException
	{	

		if (depth == 0)
		{
			return  uris3 ;
		}
		
		depth = depth - 1 ;
		QueryEngine queryengine = new QueryEngine() ;

			   List<String> tempuris = null ;
			   tempuris =  queryengine.lifedataqueryuris(uri) ;
				 if ( tempuris != null && tempuris.size() > 0)  
				 {
                   uris3.addAll(tempuris) ;
                   
				 }
			    
			   List<String> tempuris3 = resourcesLookupLLrec(depth,tempuris,uris3) ;
		return  uris3 ;
	}
	
	
	public static  List<String>  resourcesLookupLLrec(int depth,List<String> uris, List<String> uris3) throws IOException
	{	

		if (depth == 0)
		{
			return  uris3 ;
		}
		depth = depth - 1 ;
		QueryEngine queryengine = new QueryEngine() ;
		for (String uri1 :uris)  
		{
			   List<String> tempuris = null ;
			   tempuris =  queryengine.lifedataqueryuris(uri1) ;
				 if ( tempuris != null && tempuris.size() > 0)  
				 {
                   uris3.addAll(tempuris) ;
                   
				 }
			    
			   List<String> tempuris3 = resourcesLookupLLrec(depth,tempuris,uris3) ;

		}
		
		return  uris3 ;
	}
	public static Map<String, Dataset>  resourcesLookup(Map<String, Integer> concepts) throws IOException
	{
		
		Map<String, Dataset> conceptURIs = new HashMap<String, Dataset>();
		QueryEngine queryengine = new QueryEngine() ;
		Map<String, Integer> surfaceForm = null ;
		List<String> tempuris = null ; 
		List<String> tempuris1 = null ; 
		List<String> tempuris2 = null ; 
		List<String> tempuris3 = null ;
		List<String> tempuris4 = null ;
		List<String> tempuris5 = null ;

		
		for(String concept : concepts.keySet())
			
		{		  
			   List<String> uris = new ArrayList<String>() ;
			   List<String> uris1 = new ArrayList<String>() ;
			   List<String> uris2 = new ArrayList<String>() ;
			   List<String> uris3 = new ArrayList<String>() ;
			   List<String> uris4 = new ArrayList<String>() ;
			   List<String> uris5 = new ArrayList<String>() ;
			   
			   Dataset dataset = new Dataset();  		  
			   tempuris = queryengine.bio2rdfResourceLookup(concept, pred, urilimit) ;
			   tempuris1 =  QueryEngine.BioResourceLookup(concept, urilimit) ;
			   tempuris2 =  queryengine.LODResourceLookup(concept, pred,urilimit) ;
			   //tempuris3 =  queryengine.lifedataResourceLookup(term, pred,urilimit) ;
			   tempuris4 =  QueryEngine.BeeResourceLookup(concept, urilimit) ;
			   tempuris5 =  QueryEngine.DBpResourceLookup(concept, pred, urilimit) ;
			 
			   
				 if ( tempuris != null && tempuris.size() > 0 ) 
				 {
                      uris.addAll(tempuris) ; 
				 }
				 if ( tempuris1 != null && tempuris1.size() > 0 )  
				 {
                     uris1.addAll(tempuris1) ;
				 }
				 if ( tempuris2 != null && tempuris2.size() > 0)  
				 {
                     uris2.addAll(tempuris2) ;
				 }
				 if ( tempuris3 != null && tempuris3.size() > 0)  
				 {
                     uris3.addAll(tempuris3) ;
                     
				 }
				 if ( tempuris4 != null && tempuris4.size() > 0)  
				 {
                     uris4.addAll(tempuris4) ;
                     
				 }
				 if ( tempuris5 != null && tempuris5.size() > 0)  
				 {
                     uris5.addAll(tempuris5) ;
                     
				 }
			   

				   
				 if ( uris.size() > 0 ) 
				 {
                      dataset.Setonto("http://bio2rdf.org/sparql", uris);
				 }
				 if ( uris1.size() > 0 )  
				 {

                     dataset.Setonto("http://sparql.bioontology.org/sparql/", uris1);
				 }
				 if ( uris2.size() > 0)  
				 {
                     dataset.Setonto("http://lod.openlinksw.com/sparql", uris2);
				 }
				 if (uris3.size() > 0)  
				 {

                     dataset.Setonto("http://linkedlifedata.com/sparql", uris3);
                     
                     
				 }
				 if ( uris4.size() > 0)  
				 {
                     dataset.Setonto("http://sparql.hegroup.org/sparql/", uris4);
                     
				 }
			   
				 if ( uris5.size() > 0)  
				 {
                     dataset.Setonto("http://dbpedia.org/sparql", uris5);
                     
				 }
				 
			   conceptURIs.put(concept, dataset) ;

		}
		
		return conceptURIs ;
	}
	
	public static Map<String, Dataset>  resourcesSemanticLookup( Map<String, Dataset> lookupresources) throws IOException
	{
		
		Map<String, Dataset> conceptURIs = lookupresources ;
		QueryEngine queryengine = new QueryEngine() ;
		Map<String, Integer> surfaceForm = null ;
		List<String> tempuris = null ; 
		List<String> tempuris1 = null ; 
		List<String> tempuris2 = null ; 
		List<String> tempuris3 = null ;
		List<String> tempuris4 = null ;
		List<String> tempuris5 = null ;

		
		for(String concept : lookupresources.keySet())
			
		{		  
			   List<String> uris = new ArrayList<String>() ;
			   List<String> uris1 = new ArrayList<String>() ;
			   List<String> uris2 = new ArrayList<String>() ;
			   List<String> uris3 = new ArrayList<String>() ;
			   List<String> uris4 = new ArrayList<String>() ;
			   List<String> uris5 = new ArrayList<String>() ;
			   
			   Dataset dataset =  lookupresources.get(concept);
			   surfaceForm  =  surfaceFormDiscovery.getsurfaceFormLLD(concept); // new HashMap<String, Integer>() ;
			  // surfaceForm.put(concept, 1) ;
			   int limitCount = 0 ;
			   if (surfaceForm != null)
			   {
				   
				  for(String term : surfaceForm.keySet()) 
				   {
					  
					  // we here limited only for the first 40 altlabel 
/*					  limitCount++ ;
					  if (limitCount == 10 )
						  break ; */
					  
					   //String term = concept ;
					   String[] tokens  = term.split("@") ;
					   term = tokens[0] ;
					  
					  // tempuris = queryengine.bio2rdfResourceLookup(term, pred, urilimit) ;
					 //  tempuris1 =  QueryEngine.BioResourceLookup(term, urilimit) ;
					   //  tempuris2 =  queryengine.LODResourceLookup(term, pred,urilimit) ;
					     tempuris3 =  queryengine.lifedataResourceLookupnew(term, pred,urilimit) ;
					 //  tempuris4 =  QueryEngine.BeeResourceLookup(term, urilimit) ;
					 //  tempuris5 =  QueryEngine.DBpResourceLookup(term, pred, urilimit) ;
					 
					   
						 if ( tempuris != null && tempuris.size() > 0 ) 
						 {
							 if(!uris.contains(tempuris))
		                      uris.addAll(tempuris) ; 
						 }
						 if ( tempuris1 != null && tempuris1.size() > 0 )  
						 {
							 if(!uris1.contains(tempuris1))
							  uris1.addAll(tempuris1) ;
						 }
						 if ( tempuris2 != null && tempuris2.size() > 0)  
						 {
							 if(!uris2.contains(tempuris2))
		                      uris2.addAll(tempuris2) ;
						 }
						 if ( tempuris3 != null && tempuris3.size() > 0)  
						 {
							 
							 for (String uri : tempuris3)
							 {
								 
								 if(!uris3.contains(uri))
									 uris3.add(uri) ;
							 }
							 
							 break ; 
							 

		                     
						 }
						 if ( tempuris4 != null && tempuris4.size() > 0)  
						 {
							 if(!uris4.contains(tempuris4))
								 uris4.addAll(tempuris4) ;
		                     
						 }
						 if ( tempuris5 != null && tempuris5.size() > 0)  
						 {
							 if(!uris5.contains(tempuris5))
								 uris5.addAll(tempuris5) ;
		                     
						 }
				   }

				   
					 if ( uris.size() > 0 ) 
					 {
	                      dataset.Setonto("http://bio2rdf.org/sparql", uris);
					 }
					 if ( uris1.size() > 0 )  
					 {

	                     dataset.Setonto("http://sparql.bioontology.org/sparql/", uris1);
					 }
					 if ( uris2.size() > 0)  
					 {
	                     dataset.Setonto("http://lod.openlinksw.com/sparql", uris2);
					 }
					 if (uris3.size() > 0)  
					 {

	                     dataset.Setonto("http://linkedlifedata.com/sparql", uris3);
	                     
	                     
					 }
					 if ( uris4.size() > 0)  
					 {
	                     dataset.Setonto("http://sparql.hegroup.org/sparql/", uris4);
	                     
					 }
				   
					 if ( uris5.size() > 0)  
					 {
	                     dataset.Setonto("http://dbpedia.org/sparql", uris5);
	                     
					 }

			   }
			   
			   conceptURIs.put(concept, dataset) ;

		}
		
		return conceptURIs ;
	}
	
	public static Map<String, Dataset>  resourcesSemanticLookupofconcepts(Map<String, Integer> concepts) throws IOException
	{
		
		Map<String, Dataset> conceptURIs = new HashMap<String, Dataset>();
		QueryEngine queryengine = new QueryEngine() ;
		Map<String, Integer> surfaceForm = null ;
		List<String> tempuris = null ; 
		List<String> tempuris1 = null ; 
		List<String> tempuris2 = null ; 
		List<String> tempuris3 = null ;
		List<String> tempuris4 = null ;
		List<String> tempuris5 = null ;

		
		for(String concept : concepts.keySet())
			
		{		  
			   List<String> uris = new ArrayList<String>() ;
			   List<String> uris1 = new ArrayList<String>() ;
			   List<String> uris2 = new ArrayList<String>() ;
			   List<String> uris3 = new ArrayList<String>() ;
			   List<String> uris4 = new ArrayList<String>() ;
			   List<String> uris5 = new ArrayList<String>() ;
			   
			   Dataset dataset = new Dataset(); 
			   surfaceForm  = surfaceFormDiscovery.getsurfaceFormLLD(concept); 
			   if (surfaceForm != null)
			   {
				   
				  for(String term : surfaceForm.keySet()) 
				   {
					   //String term = concept ;
					   String[] tokens  = term.split("@") ;
					   term = tokens[0] ;
					  
					  // tempuris = queryengine.bio2rdfResourceLookup(term, pred, urilimit) ;
					 //  tempuris1 =  QueryEngine.BioResourceLookup(term, urilimit) ;
					   //  tempuris2 =  queryengine.LODResourceLookup(term, pred,urilimit) ;
					     tempuris3 =  queryengine.lifedataResourceLookupnew(term, pred,urilimit) ;
					 //  tempuris4 =  QueryEngine.BeeResourceLookup(term, urilimit) ;
					 //  tempuris5 =  QueryEngine.DBpResourceLookup(term, pred, urilimit) ;
					 
					   
						 if ( tempuris != null && tempuris.size() > 0 ) 
						 {
		                      uris.addAll(tempuris) ; 
						 }
						 if ( tempuris1 != null && tempuris1.size() > 0 )  
						 {
		                     uris1.addAll(tempuris1) ;
						 }
						 if ( tempuris2 != null && tempuris2.size() > 0)  
						 {
		                     uris2.addAll(tempuris2) ;
						 }
						 if ( tempuris3 != null && tempuris3.size() > 0)  
						 {
		                     uris3.addAll(tempuris3) ;
		                     
						 }
						 if ( tempuris4 != null && tempuris4.size() > 0)  
						 {
		                     uris4.addAll(tempuris4) ;
		                     
						 }
						 if ( tempuris5 != null && tempuris5.size() > 0)  
						 {
		                     uris5.addAll(tempuris5) ;
		                     
						 }
				   }

				   
					 if ( uris.size() > 0 ) 
					 {
	                      dataset.Setonto("http://bio2rdf.org/sparql", uris);
					 }
					 if ( uris1.size() > 0 )  
					 {

	                     dataset.Setonto("http://sparql.bioontology.org/sparql/", uris1);
					 }
					 if ( uris2.size() > 0)  
					 {
	                     dataset.Setonto("http://lod.openlinksw.com/sparql", uris2);
					 }
					 if (uris3.size() > 0)  
					 {

	                     dataset.Setonto("http://linkedlifedata.com/sparql", uris3);
	                     
	                     
					 }
					 if ( uris4.size() > 0)  
					 {
	                     dataset.Setonto("http://sparql.hegroup.org/sparql/", uris4);
	                     
					 }
				   
					 if ( uris5.size() > 0)  
					 {
	                     dataset.Setonto("http://dbpedia.org/sparql", uris5);
	                     
					 }

			   }
			   
			   conceptURIs.put(concept, dataset) ;

		}
		
		return conceptURIs ;
	}
	public static  Map<String, List<String>> TriplesRetrieval (Map<String, Dataset> resources) throws  IOException
	 { 
	   Map<String, List<String>> triples =  new HashMap <String, List<String>>() ;

       try 
       {

    	 ResultSet results = null ; 
    	 QueryEngine queryengine = new QueryEngine() ;
    	 for (String concept: resources.keySet())
    	 {
    		 Dataset dataset = resources.get(concept) ;
    		 List<String> statements=  new ArrayList<String>() ;
    		 Map<String,Integer> uri =  new HashMap <String, Integer>() ;
        	 for (String onto: dataset.getonto().keySet())
        	 { 
        		 Map<String, Double> UIRs = dataset.geturiconfident() ;
		    	 for (String URI: UIRs.keySet())
		    	 {	
		    		 if (uri.put(URI, 1) != null)
		    			 continue ; 
		    		try 
		    		{
		    			if (onto.contains("bio2rdf"))
		    			{
			    		   results = QueryEngine.bio2rdfquery(URI);
	                       statements.addAll(getstatements(results,URI,onto)) ;

		    			}
		    			
		    			if (onto.contains("bioontology"))
		    			{
			    		   results = QueryEngine.bioPortaluery(URI);
			    		   statements.addAll(getstatements(results,URI,onto)) ;

		    			}
		    			
		    			if (onto.contains("openlinksw"))
		    			{
		    				// construct sub graph from a given resource in RDF Graph
		    				
		    			   QueryEngine.construct(URI,onto) ;
			    		   results = QueryEngine.LODquery(URI);
			    		   statements.addAll(getstatements(results,URI,onto)) ;

		    			}
		    			
		    			if (onto.contains("linkedlifedata"))
		    			{
			    		   results = QueryEngine.lifedataquery(URI);
			    		   statements.addAll(getstatements(results,URI,onto)); 

		    			}
		    			
		    			if (onto.contains("hegroup"))
		    			{
			    		   results = QueryEngine.beerdfquery(URI);
			    		   statements.addAll(getstatements(results,URI,onto)) ;
		    			}
		    			
		    			if (onto.contains("dbpedia"))
		    			{
			    		   results = QueryEngine.dbpediaquery(URI);
			    		   statements.addAll(getstatements(results,URI,onto)) ;
		    			}
			        	
		    		}
	    	       catch (Exception e)
	    	       {
	    	    	   System.out.println(e) ;
	    	       }
		    	 }
        	 }
	    	 triples.put(concept, statements);
    	 }
       }
       catch (Exception e)
       {
    	   System.out.println(e) ;
       }
	     
	   return   triples ; 
	       
	 }
	
	public static  Map<String, List<Model>> TriplesModelRetrieval (Map<String, Dataset> resources) throws  IOException
	 { 
	   Map<String, List<Model>> triples =  new HashMap <String, List<Model>>() ;

      try 
      {

   	 
   	 QueryEngine queryengine = new QueryEngine() ;
   	 for (String concept: resources.keySet())
   	 {
   		 Dataset dataset = resources.get(concept) ;
   		 List<String> statements=  new ArrayList<String>() ;
   		 Map<String,Integer> uri =  new HashMap <String, Integer>() ;
   		 Model graph = null ;
       	 for (String onto: dataset.getonto().keySet())
       	 { 
       		 List<String> UIRs = dataset.getontoURIs(onto) ;
		    	 for (String URI: UIRs)
		    	 {	
		    		 String token[] = URI.split("!") ; 
		    		 URI = token[0] ; 
		    		 if (uri.put(URI, 1) != null)
		    			 continue ; 
		    		try 
		    		{
		    			if (onto.contains("bio2rdf"))
		    			{


		    			}
		    			
		    			if (onto.contains("bioontology"))
		    			{


		    			}
		    			
		    			if (onto.contains("openlinksw"))
		    			{
		    				
		    				System.out.println("******************************************************************************************************************" ) ;
		    				// construct sub graph from a given resource in RDF Graph
		    				ResultSet results = null ;
		    				results = QueryEngine.construct(URI,onto) ;

		    				for (; results.hasNext();) 
		    	 			{
		    	 			    // Result processing is done here.
		    	 		         QuerySolution soln = results.nextSolution() ;
		    	 		         String subject = soln.get("s").toString();
		    	 		         String Prediate = soln.get("p").toString();  //get the predicate
		    	 		         String object = soln.get("o").toString();  //get the object
		    	 		         String objectlabel = "" ;
		    	 		         try
		    	 		         {
		    	 		        	System.out.println(subject + ", " + Prediate + ", "+ object ) ;
		    	 		         }
		    	 		         catch (Exception e)
		    	 		         {
		    		    	    	  objectlabel = "" ;
		    	 		         }
		    	 			}


		    			}
		    			
		    			if (onto.contains("linkedlifedata"))
		    			{


		    			}
		    			
		    			if (onto.contains("hegroup"))
		    			{

		    			}
		    			
		    			if (onto.contains("dbpedia"))
		    			{

		    			}
			        	
		    		}
	    	       catch (Exception e)
	    	       {
	    	    	   System.out.println(e) ;
	    	       }
		    	 }
       	 }
	    	// triples.put(concept, statements);
   	 }
      }
      catch (Exception e)
      {
   	   System.out.println(e) ;
      }
	     
	   return   triples ; 
	       
	 }
	
	public static List<String> getstatements(ResultSet results,String URI,String onto)
	{
		List<String> statements=  new ArrayList<String>() ;
		Map<String, Integer> triples =  new HashMap <String, Integer>() ;
		try 
		{

        	for (; results.hasNext();) 
 			{
 			    // Result processing is done here.
 		         QuerySolution soln = results.nextSolution() ;
 		         String Prediate = soln.get("p").toString();  //get the predicate
 		         String object = soln.get("o").toString();  //get the object
 		         String objectlabel = "" ;
 		         try
 		         {
 		        	 objectlabel = soln.getLiteral("label").toString();  //get the object
 		         }
 		         catch (Exception e)
 		         {
	    	    	  objectlabel = "" ;
 		         }
				   String[] tokens  = object.split("@") ;
				   object = tokens[0] ;
 		         String triple = onto + "*" +  URI +"~"+ Prediate + "~" + object + "~"+ objectlabel;
 		         if (triples.put(triple, 1) == null) 
 		         {
 		            statements.add(triple);
 		            System.out.println(triple) ;
 		         }
 			}
		}
       catch (Exception e)
       {
    	   System.out.println(e) ;
       }
		
		return statements ;
	}
	
	public static  Map<String, List<String>> TriplesRetrievalsigle (Map<String, List<String>> resources) throws  IOException
	 { 
	   Map<String, List<String>> triples =  new HashMap <String, List<String>>() ;

      try 
      {

   	  
   	 ResultSet results = null ; 
   	 QueryEngine queryengine = new QueryEngine() ;
   	 for (String concept: resources.keySet())
   	 {
   		 List<String> UIRs = resources.get(concept) ;
   		 List<String> statements=  new ArrayList<String>() ;
	    	 for (String URI: UIRs)
	    	 {	
	    		try 
	    		{
		    		results = QueryEngine.bioPortaluery(URI);
		        	for (; results.hasNext();) 
		 			{
		 			    // Result processing is done here.
		 		         QuerySolution soln = results.nextSolution() ;
		 		         String Prediate = soln.get("p").toString();  //get the predicate
		 		         String object = soln.get("o").toString();  //get the object
		 		         String objectlabel = "" ;
		 		         try
		 		         {
		 		        	 objectlabel = soln.getLiteral("label").toString();  //get the object
		 		         }
		 		         catch (Exception e)
		 		         {
	 	    	    	  objectlabel = "" ;
		 		         }
		 		         String triple = URI +"~"+ Prediate + "~" + object + "~"+ objectlabel;
		 		         statements.add(triple);
		 		         System.out.println(triple) ;
		 			}
	    		}
   	       catch (Exception e)
   	       {
   	    	   System.out.println(e) ;
   	       }
	    	 }
	    	 triples.put(concept, statements);
   	 }
      }
      catch (Exception e)
      {
   	   System.out.println(e) ;
      }
	     
	   return   triples ; 
	       
	 }
	
	public static void printtriples(Map<String, List<String>> triples)
	{
		for (String concept : triples.keySet())
		{
			
			System.out.println("***********************" + concept + "**************************") ;
			// get triples for specific concepts
			List<String> _triples = triples.get(concept) ;
			
			 for (String triple: _triples)
			 {
				 System.out.println(triple) ;
			 }
		}
		
	}
}

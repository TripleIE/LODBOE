package TE;

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import DS.ConceptsDiscovery;
import DS.QueryEngine;
import util.Dataset;
import util.ReadXMLFile;
import util.Sentinfo;
import util.readfiles;
import util.surfaceFormDiscovery;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
public class client {


	public static void main(String[] args) throws IOException 
	
	{
		
		// TODO Auto-generated method stub
		
		
		// input 
		
		File FILE_NAME_Input = new File("C:\\LODBOEtemp\\input.txt");
	    String[] Literatures =  readfiles.readLines(FILE_NAME_Input.toURL())  ;    
	    List<String> bioLiteratures  = new ArrayList<String>() ;
	    for(String Literature: Literatures)
	    {
		   bioLiteratures.add(Literature) ;
	    }
	    
	    
	    // Output 
	    
	    String FILE_NAME_Output = "C:\\LODBOEtemp\\output" ;
	    
	    // patterns
	    String FILE_NAME_Patterns = "C:\\LODBOEtemp\\medicalFreepaltest.txt" ; 
	    
		Map<String,Map<String,List<String>>> trainset = null ; 
		Sentinfo sentInfo = new Sentinfo() ; 
		LinkedHashMap<String, Integer> TripleDict  = new LinkedHashMap<String, Integer>();
		Map<String,List<Integer>> Labeling= new HashMap<String,List<Integer>>() ;
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

		if (trainset == null )
		{
			trainset = new HashMap<String, Map<String,List<String>>>();
		}
		
		int count  = 0 ;
		Model candidategraph = ModelFactory.createDefaultModel(); 
		
		Map<String,List<String>> CandidatetriplePerSent = new HashMap <String, List<String>>() ;
		int foundcount  = 0 ;
		for(String title : bioLiteratures)
		{
			
			Model Sentgraph = sentInfo.graph;
			if (trainset.containsKey(title))
				continue ; 
			
			count++ ; 
			Map<String,List<String>> TripleCandidates = new HashMap<String, List<String>>();
			Map<String, List<String>> triples = null ;
			// get the goldstandard concepts for current title 
			List<String> GoldSndconcepts = null ; 
			Map<String, Integer> allconcepts = null ; 
			
			// this is optional and not needed here , it used to measure the concepts recall 
			Map<String, List<String>> temptitles = new HashMap<String, List<String>>(); 
			temptitles.put(title,GoldSndconcepts) ;
						
			// get the concepts 
			allconcepts  = ConceptsDiscovery.getconcepts(temptitles,api);
			// Ontology Lookup
		   Map<String, Dataset> lookupresources =  sentInfo.lookupresources ;
			
			
			
			for ( String cpt : allconcepts.keySet() )
			{
				 Dataset dataset = new Dataset();
				 lookupresources.put(cpt, dataset) ;
			}
			
			
				
			// Enrichment
			Enrichment.SemanticEnrich(lookupresources);
			
			// using matching RDF literals
			lookupresources = inferenc.resourcesSemanticLookup(lookupresources) ;

			// Ranking algorithm  
		    	Map<String, Dataset> ret = ranking.URIRankingLLD(allconcepts,lookupresources,api) ;
			
			// Pruning set the most top 3 URIs
             pruning.URIspruning(lookupresources) ;

             // Syntax & syntactic match 
			
			triples = inferenc.TriplesRetrieval (lookupresources) ;
			
			List<String> Candidatetriple = lodtripleextraction.LODTE(lookupresources) ;
			
			if (Candidatetriple.size() > 0)
			{
				CandidatetriplePerSent.put(title,Candidatetriple) ;
				foundcount++ ;
			}
			
		    Map<String,List<String>> Tripleresultsyn = new HashMap<String, List<String>>();	
			Tripleresultsyn  = syntactic.getExactMatch(triples,lookupresources) ;
			
			
			 Map<String,List<String>> Tripleresultsynn = new HashMap<String, List<String>>();
			Tripleresultsynn  = syntactic.getExactMatchSynonym(triples,lookupresources) ;
			
			
			
			
			
			ontologyfactory.getontosyntax(Tripleresultsyn,lookupresources) ;
			ontologyfactory.getontosyntax(Tripleresultsynn,lookupresources) ;
			ontologyfactory.getontoSynonym (lookupresources) ;
			ontologyfactory.getontoPreflabel(lookupresources);
			ontologyfactory.getontodefinition(lookupresources);
			ontologyfactory.getontoscheme(lookupresources);
			ontologyfactory.getontoSemanticType(lookupresources);
			ontologyfactory.getontoHierarchy(lookupresources);
			
			// using Syntactic patterns
			//ArrayList<String> RelInstances = SyntaticPattern.getSyntaticPattern(title,allconcepts,FILE_NAME_Patterns) ;
			//ontologyfactory.getontoSyntaticPattern(RelInstances,Sentgraph,lookupresources) ;

			//Sentgraph.write(System.out, "RDF/XML-ABBREV") ;
			ontologyfactory.ontoWriteWholetofile(lookupresources,candidategraph,FILE_NAME_Output) ;

		}

	}

}








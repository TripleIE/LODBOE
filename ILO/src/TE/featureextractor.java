package TE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import TextProcess.NLPEngine;
import TextProcess.removestopwords;

public class featureextractor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 

	}
	
	public static List<String> getFeatures(String sent,Map<String, Integer> allconcepts)
	{
		List<String> featuers = new ArrayList<String>() ;
		featuers.addAll(getWordsFeature(sent)) ;
		featuers.addAll(getPOSFeature(sent))  ;
		for (String Entity:allconcepts.keySet())
		{
			featuers.addAll(getVerbsbtw(sent, Entity,allconcepts ));
			featuers.addAll(lifedataSemanticType(Entity)) ;
			featuers.addAll(lifedataAncestor(Entity)) ;
			featuers.addAll(lifedataDescent(Entity)) ;
		}
		
		return featuers ;
		
		
	}
	public static List<String> getWordsFeature(String sent)
	{
		List<String> words = new ArrayList<String>() ;
        removestopwords rem = new removestopwords(); 
        
        sent =  rem.removestopwordfromsen(sent) ;
 		String tokens[] = sent.split(" ") ;
    	for (String token : tokens) 
    	{
    		words.add(token) ;
    	}
    	
    	return words ;
	}
	
	public static List<String> getPOSFeature(String sent)
	{
		List<String> nouns = new ArrayList<String>() ;
		List<String> verbs = new ArrayList<String>() ;
		StanfordCoreNLP pipeline = NLPEngine.getStanfordCoreNLP() ;
		Tree t = NLPEngine.gettree( pipeline,sent) ;
		NLPEngine.getNoun(t,nouns) ;
		NLPEngine.getVerbs(t,verbs) ;
		nouns.addAll(verbs) ;
    	return nouns ;
	}

	public static List<String> getVerbsbtw(String sent, String entity1,Map<String, Integer> allconcepts )
	{
		List<String> verbs = new ArrayList<String>() ;
		List<String> retverbs = new ArrayList<String>() ;
		for (String concept :allconcepts.keySet() )
		{
			
			String btw = StringUtils.substringBetween(sent ,entity1, concept ) ;
			if(btw != null)
			{
				
				StanfordCoreNLP pipeline = NLPEngine.getStanfordCoreNLP() ;
				Tree t = NLPEngine.gettree( pipeline,sent) ;
				NLPEngine.getVerbs(t,verbs) ;
				
				for (String vb : verbs)
				{
					if (btw.contains(vb))
					{
						retverbs.add(vb) ;
					}
				}
			}
		}
    	return retverbs ;
	}
	
	public static List<String> lifedataSemanticType(String entity)
	{
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?label where {?subject  ?predicate "  +  " \"" +   entity +  "\". " 
			        + "?subject ?a ?type."
			        + "?type rdfs:label ?label."
			        + " } " 
		            + "LIMIT 100"  ;
		
		// now creating query object
		try
		{
			
			List<String> types = new ArrayList<String>() ;
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
			ResultSet results ;
			results = qexec.execSelect(); 	
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         String label = soln.get("label").toString();  //get the subject
		         types.add(label);
		         System.out.println(label) ;
			}
			
			return types ;
		}
		catch(QueryParseException e)
		{
			System.out.println(e) ;
		}
		catch (Exception e)
		{
			
		}
		return null;
		
	}
	
	public static List<String> lifedataAncestor(String entity)
	{
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?label where {?subject  ?predicate "  +  " \"" +   entity +  "\". " 
			        + "?subject skos:broader ?broader."
			        + "?broader rdfs:label ?label."

			        		+ " } " +
		             "LIMIT 100"  ;
		
		// now creating query object
		try
		{
			
			List<String> types = new ArrayList<String>() ;
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
			ResultSet results ;
			results = qexec.execSelect(); 	
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         String label = soln.get("label").toString();  //get the subject
		         types.add(label);
		         System.out.println(label) ;
			}
			
			return types ;
		}
		catch(QueryParseException e)
		{
		}
		catch (Exception e)
		{
			
		}
		return null;
		
	}
	
	public static List<String> lifedataDescent (String entity)
	{
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?label where {?subject  ?predicate "  +  " \"" +   entity +  "\". " 
			        + "?subject skos:narrower ?narrower."
			        + "?narrower rdfs:label ?label."

			        		+ " } " +
		             "LIMIT 100"  ;
		
		// now creating query object
		try
		{
			
			List<String> types = new ArrayList<String>() ;
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
			ResultSet results ;
			results = qexec.execSelect(); 	
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         String label = soln.get("label").toString();  //get the subject
		         types.add(label);
		         System.out.println(label) ;
			}
			
			return types ;
		}
		catch(QueryParseException e)
		{
		}
		catch (Exception e)
		{
			
		}
		return null;
		
	}
	
}

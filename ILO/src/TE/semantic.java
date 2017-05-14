package TE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.CosineSimilarity;
import util.ReadXMLFile;
import util.predicateOntoDictionary;
import util.surfaceFormDiscovery;
import DS.QueryEngine;
import TextProcess.NLPEngine;
import TextProcess.removestopwords;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import edu.stanford.nlp.trees.Tree;

public class semantic {
	
	
	
	public static Map<String,Integer> getSemanticRelationbyDefinition(Map<String, Integer> concepts, String Sentence) throws IOException
	{
		QueryEngine queryengine = new QueryEngine() ;
		Map<String,Integer>  rels = new HashMap <String, Integer>() ;
		for (String conceptout : concepts.keySet())
		{
			for (String conceptin : concepts.keySet())
			{
				if (conceptout == conceptin )
				{
					continue ; 
				}
				// get the defention of each concept from Web of data 
				 
				List<String> defs  = new ArrayList<String>()  ;
				String Def = surfaceFormDiscovery.getdefFromMesh(conceptin);
				ResultSet results = queryengine.LLDqueryDefinition(conceptin);
				defs = getdefs(results) ;
				if(Def != null && Def.isEmpty())
				{	
					defs.add(Def) ;
				}
				
/*				for (String defterm: defs)
				{
					List<String>  rel = getPatternRelation(conceptout,conceptin,defterm) ;

				}*/
				
				NLPEngine nlpprocessor = new NLPEngine() ;
				rels = getVerbsRelation(defs,nlpprocessor);
				

			}
		
		}
		
		 List<String> uri = new ArrayList<String>() ;
		 Map<String,List<String>> PreURI =  new HashMap <String, List<String>>() ;
		 List<String> predicates  = predicateOntoDictionary.getPredicatDictionary() ;	
		 for (String verb:rels.keySet())
		 {
			 for (String predicate : predicates)
			 {
				if ( predicate.contains(verb))
				{
					uri.add(predicate) ;
				}
				 
			 }
			 // the relation and URI found in the dictionary
			 PreURI.put(verb,uri) ;
		 }
		
		return rels ;
		
	}
	public static List<String> getdefs(ResultSet results)
	{
		List<String> defs=  new ArrayList<String>() ;
		try 
		{

        	for (; results.hasNext();) 
 			{
 			    // Result processing is done here.
 		         QuerySolution soln = results.nextSolution() ;
 		         String def = soln.get("def").toString();  //def
 		         defs.add(def);
 		         System.out.println(def) ;
 			}
		}
       catch (Exception e)
       {
    	   System.out.println(e) ;
       }
		
		return defs ;
	}
	
	public static Map<String,Integer> getVerbsRelation(List<String> definitions, NLPEngine nlpprocessor)
	 {
		 List<String> relations = new ArrayList<String>() ;
		 List<String> uri = new ArrayList<String>() ;
		 Map<String,Integer> verbs =  new HashMap <String, Integer>() ;
         for (String def : definitions)
         {
			 Tree parsertree = nlpprocessor.getParseTreeSentence(def) ;
			 List<String> verb = new ArrayList<String>() ;
			 nlpprocessor.getVerbs(parsertree, verb);
			 for(String verbout :verb)
			 {
				 verbs.put(verbout, 1) ;
			 }
         }	 
	    return verbs ; 
	 }
	 public static List<String> getPatternRelation(String concept1, String Concept2, String sentence)
	 {
		 List<String> relations = new ArrayList<String>() ;
		 List<String> uri = new ArrayList<String>() ;
		 Map<String,List<String>> PreURI =  new HashMap <String, List<String>>() ;
		 sentence = Concept2+ " is " + sentence ;
		 Pattern p = Pattern.compile(Pattern.quote(concept1) + "(.*?)" + Pattern.quote(Concept2)) ;
		 Matcher m = p.matcher(sentence);
		 while (m.find()) {
		   System.out.println(m.group(1));
		   relations.add(m.group(1)) ;
		 }
		 
		 List<String> predicates  = predicateOntoDictionary.getPredicatDictionary() ;
		
		 for (String rel:relations)
		 {
			 for (String predicate : predicates)
			 {
				if ( predicate.contains(rel))
				{
					uri.add(predicate) ;
				}
				 
			 }
			 // the relation and URI found in the dictionary
			 PreURI.put(rel,uri) ;
		 }
		 
		 List<String> statements=  new ArrayList<String>() ;
		 if ( !PreURI.isEmpty())
		 {
			 for (String rel:PreURI.keySet())
			 {
				 statements.add(concept1 + "," + PreURI.get(rel).get(1) + "," + Concept2);
			 }
			 
			 
		 }
		 
		 
	    return statements ; 
	 }

	public static Map<String,List<String>> getSemanticMatch(Map<String,List<String>> triples, String Sentence) throws IOException
	{		
		Map<String,List<String>> _Triples = new HashMap<String, List<String>>();
		Map<String,List<String>> _TripleCandidates = new HashMap<String, List<String>>();
		QueryEngine queryengine = new QueryEngine() ;
		for (String concept : triples.keySet())
		{
			
			System.out.println("***********************" + concept + "**************************") ;
			// get triples for specific concepts
			List<String> _triples = triples.get(concept) ;
			List<String> _triplelist  = new ArrayList<String>()  ;

			String Prior = "empty" ;
			 for (String triple: _triples)
			 {
				 String onto[] = triple.split("\\*") ;
				 String tokens[] = onto[1].split("~") ;
				 if (!Prior.equals(tokens[0]))
				 {
					 Prior = tokens[0] ;
					 ResultSet results = queryengine.TripleDescriptive(tokens[0],onto[0]) ; 
					 _triplelist.addAll(getstatements(results,tokens[0],""));
				 }

			 }
			 _Triples.put(concept,_triplelist) ;
			 
		}
		
		ReadXMLFile.Serialized(_Triples, "F:\\eclipse64\\eclipse\\triplesSemantic");
		_TripleCandidates = getSimilairMatch(_Triples,Sentence) ; 
		
		return _TripleCandidates; 
	}
	
	public static List<String> getstatements(ResultSet results,String URI,String onto)
	{
		List<String> statements=  new ArrayList<String>() ;
		try 
		{

        	for (; results.hasNext();) 
 			{
 			    // Result processing is done here.
 		         QuerySolution soln = results.nextSolution() ;
 		         String def = soln.get("def").toString();  //def
 		         String triple = onto + "*" +  URI +"~"+ "skos:definition" + "~" + def ;
 		         statements.add(triple);
 		         System.out.println(triple) ;
 			}
		}
       catch (Exception e)
       {
    	   System.out.println(e) ;
       }
		
		return statements ;
	}
	
	public static Map<String,List<String>> getSimilairMatch(Map<String,List<String>> triples, String Sentence)
	{		
		Map<String,List<String>> exactMatchTriples = new HashMap <String, List<String>>() ;
		Map<String,List<String>> tempconcepts = new HashMap <String, List<String>>() ;
		tempconcepts.putAll(triples);
		CosineSimilarity sim = new CosineSimilarity() ;
		for (String concept : triples.keySet())
		{
			List<String> statement = new ArrayList<String>() ;
			String sent = Sentence ; //Sentence.replaceFirst(concept, "");
			List<String> _triples = triples.get(concept) ;
			 for (String triple: _triples)
			 {
				 String tokens[] =  triple.split("~") ;
				 String Object ;
				 String Predicate ; 
				 String definition  ; 
//				 if(tokens.length == 4)
//				 {
//					 String _tokens[] =  tokens[3].split("\\[|\\^+");
//					 String Objects[] = _tokens[0].split("@") ;
//					 Object = Objects[0] ;
//				 }
//				 else
				 {
					 String _tokens[] =  tokens[2].split("\\[|\\^+");
					 String Objects[] = _tokens[0].split("@") ;
					 Object = Objects[0] ;
					 definition = Object ;
					 
				 }
				 
                 if(Object.isEmpty())
                	 continue ; 
                 
                 removestopwords rem = new removestopwords(); 
                 
                 sent =  rem.removestopwordfromsen(sent) ;
                 Object =  rem.removestopwordfromsen(Object) ;
                 if(Object.isEmpty())
                	 continue ; 
                 
				 Map<String, Integer>  vectormain = getVector(sent,Object) ;
				 Map<String, Integer>  vectorobj = getVector(Object,vectormain) ;
				 Map<String, Integer>  vectorsent = getVector(sent,vectormain) ;
				 
				 if (sim.cosineSimilarity(vectorobj, vectorsent) > 0.30)
				 {
						 statement.add(triple) ;
				 }

			 }
			 
			 String definition  ;
			 
			 exactMatchTriples.put(concept, statement) ;
			
		}
		if (exactMatchTriples.size() >0)
		{
			return exactMatchTriples ;
		}
		return null; 
	}
	public static Map<String, Integer>  getVector(String text1, String text2)
	 {
		Map<String, Integer>  vector = new HashMap<String, Integer>();
		
		String tokens[] = text1.split(" ") ;
   	for (String token : tokens) 
   	{
   		vector.put(token,0) ;
   	}
   	
		String tokens1[] = text2.split(" ") ;
   	for (String token : tokens1) 
   	{
   		vector.put(token,0) ;
   	}
   	
		return vector;
	 }
	 
	 public static Map<String, Integer>  getVector(String text1, Map<String, Integer> vectorin)
	 {
		Map<String, Integer>  vector = new HashMap<String, Integer>();
		vector.putAll(vectorin);
		String tokens[] = text1.split(" ") ;
   	for (String token : tokens) 
   	{
   		vector.put(token, vector.get(token) + 1);
   	}   	
		return vector;
	 }

	 public static Map<String, Integer>  getbagofwords( Map<String, List<String>> Docs)
	 {
		 
		Map<String, Integer>  vector = new HashMap<String, Integer>();
		 removestopwords rem = new removestopwords();
		for (String text : Docs.keySet())
		{
			text =  rem.removestopwordfromsen(text) ;
			String tokens[] = text.split(" ") ;
			
		   	for (String token : tokens) 
		   	{
		   		vector.put(token,1);
		   	} 
		}    
		return vector;
	 }
	 public static Map<String, Integer>  getbagofwords( String text)
	 {
		 
		Map<String, Integer>  vector = new HashMap<String, Integer>();
		 removestopwords rem = new removestopwords();

		{
			text =  rem.removestopwordfromsen(text) ;
			String tokens[] = text.split(" ") ;
			
		   	for (String token : tokens) 
		   	{
		   		vector.put(token,1);
		   	} 
		}    
		return vector;
	 }
}

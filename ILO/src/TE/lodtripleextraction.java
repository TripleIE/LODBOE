package TE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TextProcess.removestopwords;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import DS.QueryEngine;
import util.Dataset;

public class lodtripleextraction {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static List<String> LODTE(Map<String, Dataset> resources)
	{
		
		List<String> Candidatetriple = new ArrayList<String>() ;
		Map<String,List<String>> tempconcepts = new HashMap <String, List<String>>() ;
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>() ;
		
		for (String concept : resources.keySet())
		{
			List<String> statement = new ArrayList<String>() ;
			// get triples for specific concepts
			Map<String, Double> _triples = resources.get(concept).gettopuriconfident() ;
			
			List<String> objs = new ArrayList<String>() ;
			for (String triple: _triples.keySet())
			{
				objs.add(triple);
			}

			List<QuerySolution> uris3 = new ArrayList<QuerySolution>() ;

			 try 
			 {
				 BreadthFirstSearch(3,objs,  uris3,concept) ;

				 // we do URI matching between the topconfidentURI 
				 if (URImatch (concept, uris3,resources, Candidatetriple,objs) )
					 continue ; 
				 else if (PrefLabelmatch (concept,uris3,resources, Candidatetriple,objs) )
					 continue ; 
				 else if (Synonymmatch(concept ,uris3,resources, Candidatetriple,objs))
					 continue ; 
				 else if (DefinitionRelatedness(concept ,uris3,resources, Candidatetriple,objs,0.01))
					 continue ; 
				 else if (Semantictypeoverlap(concept ,uris3,resources, Candidatetriple,objs,0.01))
					 continue ; 
				 else if (Hierarchyoverlap(concept ,uris3,resources, Candidatetriple,objs,0.01))
					 continue ; 
				 
			 }
			 catch(Exception e)
			 {
				continue ;  
			 }
		}
		
		return Candidatetriple ;
		
	}
	
	
	
	
	public static  Boolean URImatch(String concept ,List<QuerySolution> uris3,Map<String, Dataset> resources,List<String> Candidatetriple,List<String> objs)
	{
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>(resources) ;
		 for(QuerySolution sol:uris3)
		 {
			 
			 if (!sol.get("o").isResource()) 
				 continue ; 
			 System.out.println(sol.get("o").asResource().getURI());
			 
			 // match it with others concepts
			 for (String cpt : tempresources.keySet())
			 {
				 // if the match concept is the input concept then skip
				 if ( concept.toLowerCase() == cpt.toLowerCase())
					 continue ; 
				 
				 Dataset cptdataset = tempresources.get(cpt) ;
				 Map<String, Double> cpturi = cptdataset.gettopuriconfident() ;
				 
				 // do URI matching
				 for (String conpt : cpturi.keySet())
				 {
					 if (sol.get("o").asResource().getURI() == conpt )  
					 {
						 String RDFtriple = objs.get(0) + " " + sol.get("p").asResource().getURI() + " " + conpt  ;
						 if (!Candidatetriple.contains(RDFtriple))
							 Candidatetriple.add(RDFtriple ) ;
						 
						 	break ; 
					 }
				 }
						 
			 }
			 
			 
		 }
		 
		 return false ; 
	}
	
	public static  Boolean PrefLabelmatch(String concept ,List<QuerySolution> uris3,Map<String, Dataset> resources,List<String> Candidatetriple,List<String> objs)
	{
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>(resources) ;
		 for(QuerySolution sol:uris3)
		 {
			 
			 if (!sol.get("o").isResource()) 
				 continue ; 
			 
			 System.out.println(sol.get("o").asResource().getURI());
			 
			 // match it with others concepts
			 for (String cpt : tempresources.keySet())
			 {
				 
				 if ( concept.toLowerCase() == cpt.toLowerCase())
					 continue ;
				 
				 Dataset cptdataset = tempresources.get(cpt) ;
				 String cptPrefLabel = cptdataset.PrefLabel  ;
				 String token[] = cptPrefLabel.split(" ", 3) ;
				 Map<String, Double> cpturi = cptdataset.gettopuriconfident() ;
				 String URI = "" ;
				 for (String conpt : cpturi.keySet())
				 {
					 try 
					 {
						 if (!sol.get("label").isLiteral()) 
						 continue ; 
					 }
					 catch(Exception e)
					 {
						continue ;  
					 }
					 
					 if (sol.get("label").asLiteral().getLexicalForm().toLowerCase() == token[2].toLowerCase() )  
					 {
						 String RDFtriple = objs.get(0) + " " + sol.get("p").asResource().getURI() + " " + conpt  ;
						 if (!Candidatetriple.contains(RDFtriple))
							 Candidatetriple.add(RDFtriple ) ;
						 break ; 
					 }
				 }
						 
			 } 
		 }
		 
		 return false ; 
	}
	
	
	public static  Boolean Synonymmatch(String concept ,List<QuerySolution> uris3,Map<String, Dataset> resources,List<String> Candidatetriple,List<String> objs)
	{
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>(resources) ;
		 for(QuerySolution sol:uris3)
		 {
			 
			 if (!sol.get("o").isResource()) 
				 continue ; 
			 
			 
			 System.out.println(sol.get("o").asResource().getURI());
			 
			 // match it with others concepts
			 for (String cpt : tempresources.keySet())
			 {
				 if ( concept.toLowerCase() == cpt.toLowerCase())
					 continue ;
				 Dataset cptdataset = tempresources.get(cpt) ;
				 List<String> cptSynonym = cptdataset.Synonym  ;
				 Map<String, Double> cpturi = cptdataset.gettopuriconfident() ;
				 String URI = "" ;
				 for (String conpt : cpturi.keySet())
				 {
					 
					 for(String syn:cptSynonym)
					 {
						 String token[] = syn.split("!") ;
						 try 
						 {
							 if (!sol.get("label").isLiteral()) 
							 continue ; 
						 }
						 catch(Exception e)
						 {
							continue ;  
						 }
						 
						 if (sol.get("label").asLiteral().getLexicalForm().toLowerCase() == token[1].toLowerCase() )  
						 {
							 String RDFtriple = objs.get(0) + " " + sol.get("p").asResource().getURI() + " " + conpt  ;
							 if (!Candidatetriple.contains(RDFtriple))
								 Candidatetriple.add(RDFtriple ) ;
							  break ; 
						 }
					 }
				 }
						 
			 } 
		 }
		 
		 return false ; 
	}
	
	
	public static  Boolean DefinitionRelatedness(String concept , List<QuerySolution> uris3,Map<String, Dataset> resources,List<String> Candidatetriple,List<String> objs, double threshold)
	{
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>(resources) ;
		 for(QuerySolution sol:uris3)
		 {
			 
			 if (!sol.get("o").isResource()) 
				 continue ; 
			 
			 
			 System.out.println(sol.get("o").asResource().getURI());
			 
			 String definition = LLDqueryDefinition(sol.get("o").asResource().getURI()) ;
			 
			 
			 if (definition == null)
				 continue ; 
			 
			  List<String> URIdefBagofwords = removestopwords.removestopword(definition) ;
			 
			 // match it with others concepts
			 for (String cpt : tempresources.keySet())
			 {
				 if ( concept.toLowerCase() == cpt.toLowerCase())
					 continue ;
				 
				 Dataset cptdataset = tempresources.get(cpt) ;
				 List<String> cptDefinition = cptdataset.Definition ;
				 Map<String, Double> cpturi = cptdataset.gettopuriconfident() ;
				 String URI = "" ;
				 for (String conpt : cpturi.keySet())
				 {
					 
					 for(String def:cptDefinition)
					 {
						 List<String> defBagofwords = removestopwords.removestopword(def) ;
						 List<String> InterBagofwords = intersection(defBagofwords,URIdefBagofwords) ;
						 Double score =  ((double) InterBagofwords.size() / (double) (defBagofwords.size() + URIdefBagofwords.size() - InterBagofwords.size())) ;
						 if ( score > threshold)  
						 {
							 String RDFtriple = objs.get(0) + " " + sol.get("p").asResource().getURI() + " " + conpt  ;
							 if (!Candidatetriple.contains(RDFtriple))
								 Candidatetriple.add(RDFtriple ) ;
							 break ; 
						 }
					 }
				 }
						 
			 } 
		 }
		 
		 return false ; 
	}	
	
	
	public static  Boolean Semantictypeoverlap(String concept , List<QuerySolution> uris3,Map<String, Dataset> resources,List<String> Candidatetriple,List<String> objs, double threshold)
	{
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>(resources) ;
		 for(QuerySolution sol:uris3)
		 {
			 
			 if (!sol.get("o").isResource()) 
				 continue ; 
			 
			 System.out.println(sol.get("o").asResource().getURI());

			 List<String> semantictype =   Enrichment.LLDCategorybyURI(sol.get("o").asResource().getURI()) ;
			 
			 
			 if (semantictype == null || semantictype.size() >= 0 )
				 continue ; 
			 
			 // match it with others concepts
			 for (String cpt : tempresources.keySet())
			 {
				 
				 if ( concept.toLowerCase() == cpt.toLowerCase())
					 continue ;
				 Dataset cptdataset = tempresources.get(cpt) ;
				 List<String> cptSemantic = cptdataset.Category ;
				 Map<String, Double> cpturi = cptdataset.gettopuriconfident() ;
				 String URI = "" ;
				 for (String conpt : cpturi.keySet())
				 {
					    List<String> InterSemantictype = intersection(cptSemantic,semantictype) ; 
						 Double score = (double) ((double) InterSemantictype.size() / (double) (semantictype.size() + cptSemantic.size() - InterSemantictype.size())) ;
						 if ( score > threshold)  
						 {
							 String RDFtriple = objs.get(0) + " " + sol.get("p").asResource().getURI() + " " + conpt  ;
							 if (!Candidatetriple.contains(RDFtriple))
								 Candidatetriple.add(RDFtriple ) ;
							 break ;  
						 }
				 }
						 
			 } 
		 }
		 
		 return false ; 
	}
	
	
	public static  Boolean Hierarchyoverlap(String concept ,List<QuerySolution> uris3,Map<String, Dataset> resources,List<String> Candidatetriple,List<String> objs, double threshold) throws IOException
	{
		Map<String, Dataset> tempresources = new HashMap<String, Dataset>(resources) ;
		 for(QuerySolution sol:uris3)
		 {

			 if (!sol.get("o").isResource()) 
				 continue ; 
			 
			 try 
			 {
				 if (!sol.get("label").isLiteral()) 
				 continue ; 
			 }
			 catch(Exception e)
			 {
				continue ;  
			 }
			 
			 String label = sol.get("label").asLiteral().getLexicalForm() ;
			 List<String> hierarchy =   Enrichment.LLDHierarchy(label );
			 List<String> Lixecalhierarchy = new ArrayList<String>() ; 
			 for(String hier: hierarchy)
			 {
				String tokens[] =  hier.split("!") ;
				Lixecalhierarchy.add(tokens[1].toLowerCase()) ;
			 }
			 
			 
			 
			 if (hierarchy == null)
				 continue ; 
			 
			 // match it with others concepts
			 for (String cpt : tempresources.keySet())
			 {
				 if ( concept.toLowerCase() == cpt.toLowerCase())
					 continue ;
				 
				 Dataset cptdataset = tempresources.get(cpt) ;
				 List<String> cptHierarchy = cptdataset.Hierarchy ;
				 List<String> LixecalcptHierarchy = new ArrayList<String>() ;
				 for(String hier: cptHierarchy)
				 {
					String tokens[] =  hier.split("!") ;
					LixecalcptHierarchy.add(tokens[1].toLowerCase()) ;
				 }
				 Map<String, Double> cpturi = cptdataset.gettopuriconfident() ;
				 String URI = "" ;
				 for (String conpt : cpturi.keySet())
				 {
					 
					    List<String> IntercptHierarchy = intersection(Lixecalhierarchy,LixecalcptHierarchy) ; 
						 Double score = ((double) IntercptHierarchy.size() / (double)  (Lixecalhierarchy.size() + LixecalcptHierarchy.size() - IntercptHierarchy.size())) ;
						 if ( score > 0)  
						 {
							 String RDFtriple = objs.get(0) + " " + sol.get("p").asResource().getURI() + " " + conpt  ;
							 if (!Candidatetriple.contains(RDFtriple))
								 Candidatetriple.add(RDFtriple ) ;
							 
							 break ;  
						 }
				 }
						 
			 } 
		 }
		 
		 return false ; 
	}
	
	
	
	 public static  List<String> intersection(List<String> list1, List<String> list2) {
	        List<String> list = new ArrayList<String>();

	        for (String t : list1) {
	            if(list2.contains(t.toLowerCase())) {
	                list.add(t);
	            }
	        }
	        return list;
	    }
	public static String LLDqueryDefinition(String URI)
	{

		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
		        "select distinct  ?def " +
			    "where { " +
		                   "<" + URI+  ">" + " skos:note|skos:definition ?def." + 
		            " } " +
		            "LIMIT 1" ;
		
		// now creating query object
		try
		{
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
			ResultSet results ;
			qexec.setTimeout(30000);
			results = qexec.execSelect(); 
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         return soln.get("def").toString() ;
			}
		}
		catch(QueryParseException e)
		{
		}
		catch (Exception e)
		{
			
		}
		return null;
		
	}
	
	public static  List<QuerySolution>  BreadthFirstSearch(int depth,List<String> uris, List<QuerySolution> uris3, String concept) throws IOException
	{	

		if (depth == 0)
		{
			return  uris3 ;
		}
		depth = depth - 1 ;
		QueryEngine queryengine = new QueryEngine() ;
		for (String uri1 :uris)  
		{
			   List<QuerySolution> tempuris = null ;
			   tempuris =  lifedataqueryuris(uri1) ;
				 if ( tempuris != null && tempuris.size() > 0)  
				 {
                   uris3.addAll(tempuris) ;
                   
				 }
				 
				 List<String> statement = new ArrayList<String>() ;
				 for(QuerySolution sol: tempuris)
				 {
					 if(sol.get("o").isResource()) 
					 {
						
						  try 
						  {
							  String lebel = sol.get("label").asLiteral().getLexicalForm() ;
							  if(!concept.equals(lebel))
							  {
								  String URI = sol.getResource("o").getURI() ;
								  if(!statement.contains(URI) ) 
								  		statement.add(URI) ;
							  }
						  }
						 catch(Exception e)
						 {
							
						 }
					 }
				 }
				 
			    
			  List<QuerySolution> tempuris3 = BreadthFirstSearch(depth,statement,uris3,concept) ;

		}
		
		return  uris3 ;
	}
	
	public static List<QuerySolution> lifedataqueryuris(String resource)
	{
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?p ?o ?label where {<" + resource +  "> ?p  ?o." + 
			        "OPTIONAL {?o rdfs:label ?label} "
			        		+ " } " +
		             "LIMIT 100"  ;
		
		// now creating query object
		try
		{
			
			List<QuerySolution> Linkuris = new ArrayList<QuerySolution>() ;
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
			ResultSet results ;
			results = qexec.execSelect(); 	
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         String subj = soln.get("o").toString();  //get the subject
		         Linkuris.add(soln);
		         System.out.println(subj) ;
			}
			
			return Linkuris ;
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

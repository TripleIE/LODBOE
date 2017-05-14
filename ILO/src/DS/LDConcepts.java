package DS;

import java.util.ArrayList;
import java.util.List;

import util._Entity;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class LDConcepts {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Boolean EntityMentionDetection(String mention)
	{
		Boolean booleanask = false ;
		if (mention.length() > 2)
		{
			booleanask = EntityMentionDetectionLLD(mention) ; 

		}
		
		return booleanask;
		
	}
	
	public static Boolean EntityMentionDetectionLLD(String mention)
	{
		

		List<_Entity> entitymentions = new ArrayList<_Entity>() ;
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
		        "select ?entity where" +
			    "{ " +
		                   "?entity ?predicate" +  " \"" +   mention +  "\". "   +
		                  "?entity rdf:type skos:Concept" + 
		        " } LIMIT 1"  ;
		

		
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
				return true ;
		         
		    }
		}
		catch(QueryParseException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
		return false;
		
	}
}

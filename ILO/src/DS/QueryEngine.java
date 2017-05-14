package DS;

import java.io.BufferedReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//import org.apache.http.client.ClientProtocolException;






import com.hp.hpl.jena.graph.Triple;
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import util._Entity;
import util._EntityCandidate;

public class QueryEngine {
	
    
	List<String> LinkList = new ArrayList<String>() ;
    List<String> Linkuris = new ArrayList<String>() ;
	public static String callURL(String myURL) {
			System.out.println("Requeted URL:" + myURL);
			StringBuilder sb = new StringBuilder();
			URLConnection urlConn = null;
			InputStreamReader in = null;
			try {
				URL url = new URL(myURL);
				urlConn = url.openConnection();
				if (urlConn != null)
					urlConn.setReadTimeout(60 * 1000);
				if (urlConn != null && urlConn.getInputStream() != null) {
					in = new InputStreamReader(urlConn.getInputStream(),
							Charset.defaultCharset());
					BufferedReader bufferedReader = new BufferedReader(in);
					if (bufferedReader != null) {
						int cp;
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						bufferedReader.close();
					}
				}
			in.close();
			} catch (Exception e) {
				throw new RuntimeException("Exception while calling URL:"+ myURL, e);
			} 
	 
			return sb.toString();
		}

	 /*
	  *  get the result of the query in rdf/xml file format  
	  */
	 public  static void  excuteQuery(String query,String  filename) throws  IOException
	 { 

		 callURL(query) ;
		 InputStream input = null ; 
		 BufferedReader rd ;
	       try 
	       {
			   rd = new BufferedReader (new InputStreamReader(input));
	        
		       File file = new File(filename);
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				
				FileOutputStream fileStream = new FileOutputStream(file);
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
				String data ; 
				while ((data = rd.readLine()) != null)
				{  
					System.out.println(data);  
					writer.write(data);
				}
				
				writer.close() ;
	       }	  
	  finally 
	     {
	  	//   response.close() ;  
	     }
	 } 
	 
	  public  static String encode(String arg) {
	        String encodedArg;
			try {
				encodedArg = URLEncoder.encode(arg, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				throw new IllegalStateException("This should never happen.");
			}
	        return encodedArg;
		}
	 
		
/*************************************** Resources lookup Section******************************************************************/
	  public static List<String> BeeResourceLookup(String mention,Integer limit)
		{
		  List<String> Linkuris = new ArrayList<String>() ;
			
		try
			{

				String queryString=
						"PREFIX p: <http://dbpedia.org/property/>"+
						"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
						"PREFIX category: <http://dbpedia.org/resource/Category:>"+
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
						"PREFIX geo: <http://www.georss.org/georss/>"+
						"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				        "Select ?entity " +
					    "{ " +
				                   "?entity ?predicate  ?label."   +
				              /*    "?entity rdf:type skos:Concept" + */
				        " FILTER (regex ( ?label, "  + "\"" +   mention +  "\"," + "\"i\""  +" ) ) } LIMIT " + Integer.toString(5) ;  ;
				
				
				// ask did not work
				 Query query = QueryFactory.create(queryString) ;
	           
				 // http://sparql.bioontology.org/
				 // "http://sparql.bioontology.org/sparql/"
		         QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.hegroup.org/sparql/", query); 		                      
		         ResultSet results1 ;
		         qexec.setTimeout(60000);
		         results1 = qexec.execSelect() ;
		         for (; results1.hasNext();) 
		         {

		             QuerySolution soln = results1.nextSolution() ;
			         String subj = soln.get("entity").toString();  //get the subject
			         Linkuris.add(subj);
		             System.out.println(subj) ;
		         }
		         return Linkuris ;

			}
			catch(QueryParseException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				
				e.printStackTrace();
			}
			return null;
			
		}
		
	  public static List<String> BioResourceLookup(String mention,Integer limit)
		{
		  List<String> Linkuris = new ArrayList<String>() ;
			
		try
			{
				//APIkey 
				//396993d0-4ce2-4123-93de-214e9b9ebcf2
				String queryString=
						"PREFIX p: <http://dbpedia.org/property/>"+
						"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
						"PREFIX category: <http://dbpedia.org/resource/Category:>"+
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
						"PREFIX geo: <http://www.georss.org/georss/>"+
						"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				        "Select ?entity " +
					    "{ " +
				                   "?entity ?predicate  ?label."   +
				              /*    "?entity rdf:type skos:Concept" + */
				        " FILTER (CONTAINS ( UCASE(str(?label)), "  + "\"" +   mention.toUpperCase() +  "\") ) } LIMIT " + Integer.toString(limit) ;  ;
				
				
				// ask did not work
				 Query query = QueryFactory.create(queryString) ;
	           
				 // http://sparql.bioontology.org/
				 // "http://sparql.bioontology.org/sparql/"
		         QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql/", query); 		                      
		         qexec.addParam("apikey", "396993d0-4ce2-4123-93de-214e9b9ebcf2") ;
		         ResultSet results1 ;
		         qexec.setTimeout(30000);
		         results1 = qexec.execSelect() ;
		         for (; results1.hasNext();) 
		         {

		             QuerySolution soln = results1.nextSolution() ;
			         String subj = soln.get("entity").toString();  //get the subject
			         Linkuris.add(subj);
		             System.out.println(subj) ;
		         }
		         return Linkuris ;

			}
			catch(QueryParseException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				
				e.printStackTrace();
			}
			return null;
			
		}
	  
	  
	  public  List<String> lifedataResourceLookupnew(String entity,String predicate,Integer limit)
		{
			List<String> Linkuris = new ArrayList<String>() ;
			
		
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select Distinct ?concept1  " +
				    "where { " 
				        + " ?concept1 skos:altLabel|skos:prefLabel|rdfs:label "   + "\"" +   entity +  "\"."  
				        + " ?concept1 a skos:Concept."
			            + " } " +
			            "LIMIT " +  Integer.toString(limit) ;

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
			         String subj = soln.get("concept1").toString();  //get the subject
			         Linkuris.add(subj);
			         System.out.println(subj) ;
				}
				return Linkuris ;
			}
			catch(Exception  e)
			{
				System.out.println(e.getMessage()) ;
			}
			return null;
			
			
		}
	  public  List<String> lifedataResourceLookup(String entity,String predicate,Integer limit)
		{
			List<String> Linkuris = new ArrayList<String>() ;
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?s " +
				    "where { " +
			                   "?s " + predicate + "\"" +   entity +  "\"." + 
			            " } " +
			            "LIMIT " +  Integer.toString(limit) ;

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
			         String subj = soln.get("s").toString();  //get the subject
			         Linkuris.add(subj);
			         System.out.println(subj) ;
				}
				return Linkuris ;
			}
			catch(Exception  e)
			{
				System.out.println(e.getMessage()) ;
			}
			return null;
			
			
		}	
		
		public  List<String> LODResourceLookup(String entity,String predicate,Integer limit)
		{
			List<String> Linkuris = new ArrayList<String>() ;
			
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
			        "Select ?entity " +
				    "{ " +
			                   "?entity  ?predicate "  +  " \"" +   entity +  "\". "   +
			        "   FILTER (!isBlank(?entity)) } " + "LIMIT " +  Integer.toString(limit) ; 

			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 	
				for (; results.hasNext();) 
				{
				    // Result processing is done here.
			         QuerySolution soln = results.nextSolution() ;
			         String subj = soln.get("entity").toString();  //get the subject
			         Linkuris.add(subj);
			         System.out.println(subj) ;
				}
				return Linkuris ;
			}
			catch(Exception  e)
			{
				System.out.println(e.getMessage()) ;
			}
			return null;
			
			
		}
		
		
		
		public static  List<String> DBpResourceLookup(String entity,String predicate,Integer limit)
		{
			List<String> Linkuris = new ArrayList<String>() ;
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?s " +
				    "where { " +
			                   "?s  rdfs:label ?label." +
			                   
				              // "FILTER (regex(str(?label)," + "\"" +   entity +  "\"" + ",\"i\"))" +
			                  "?label <bif:contains> "   + "\"" +   entity +  "\"." + 
			            " } " +
			            "LIMIT " +  Integer.toString(limit) ;

			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
				ResultSet results ;
				qexec.setTimeout(1800000);
				results = qexec.execSelect(); 	
				for (; results.hasNext();) 
				{
				    // Result processing is done here.
			         QuerySolution soln = results.nextSolution() ;
			         String subj = soln.get("s").toString();  //get the subject
			         Linkuris.add(subj);
			         System.out.println(subj) ;
				}
				return Linkuris ;
			}
			catch(Exception  e)
			{
				System.out.println(e.getMessage()) ;
				DBpResourceLookupexact(entity,predicate,limit) ;
			}
			return null;
			
			
		}
		public static  List<String> DBpResourceLookupexact(String entity,String predicate,Integer limit)
		{
			List<String> Linkuris = new ArrayList<String>() ;
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?s " +
				    "where { " +
			                   "?s  rdfs:label " + "\"" +   entity +  "\"@en" +
			            " } " +
			            "LIMIT " +  Integer.toString(limit) ;

			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
				ResultSet results ;
				qexec.setTimeout(1800000);
				results = qexec.execSelect(); 	
				for (; results.hasNext();) 
				{
				    // Result processing is done here.
			         QuerySolution soln = results.nextSolution() ;
			         String subj = soln.get("s").toString();  //get the subject
			         Linkuris.add(subj);
			         System.out.println(subj) ;
				}
				return Linkuris ;
			}
			catch(Exception  e)
			{
				System.out.println(e.getMessage()) ;
			}
			return null;
			
			
		}
	

		public  List<String> bio2rdfResourceLookup(String entity,String predicate,Integer limit)
		{
			List<String> Linkuris = new ArrayList<String>() ;
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
					"PREFIX ov: <http://bio2rdf.org/omim_vocabulary:>"+
					"PREFIX dct: <http://purl.org/dc/terms/>"+
			        "select distinct  ?s " +
				    "where { " +
			                   "?s ?a ?type." +
			                   "?s rdfs:label ?label ." +
			                   "?label <bif:contains> "   + "\"'" +   entity +  "'\"." + 
			            " FILTER(!contains(str(?s)," + "\"" +  "clinicaltrials" + "\"" + "  ))  } " +
			            "LIMIT " +  Integer.toString(limit) ;

			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				// http://bio2rdf.org/sparql
				//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://bio2rdf.org/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 	
				for (; results.hasNext();) 
				{
				    // Result processing is done here.
			         QuerySolution soln = results.nextSolution() ;
			         String subj = soln.get("s").toString();  //get the subject
			         Linkuris.add(subj);
			         System.out.println(subj) ;
				}
				return Linkuris ;
			}
			catch(Exception  e)
			{
				System.out.println(e.getMessage()) ;
			}
			return null;
			
			
		}
		
		/*************************************** End Resources lookup Section******************************************************************/
		public static ResultSet beerdfquery(String entity)
		{
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?p ?o ?label where {<" + entity +  "> ?p  ?o. "
			        		+ "OPTIONAL {?o rdfs:label ?label }"
			        		+ "} ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://sparql.hegroup.org/sparql/", query);
				ResultSet results ;
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			return null;
		}
		
		public static ResultSet bioPortaluery(String entity)
		{
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?p ?o ?label where {<" + entity +  "> ?p  ?o. "
			        		+ "OPTIONAL {?o rdfs:label ?label }"
			        		+ "} ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql/", query); 		                      
		        qexec.addParam("apikey", "396993d0-4ce2-4123-93de-214e9b9ebcf2") ;
		        qexec.setTimeout(30000);
				ResultSet results ;
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			return null;
			
			
		}
		public static ResultSet bio2rdfquery(String entity)
		{
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?p ?o ?label where {<" + entity +  "> ?p  ?o. "
			        		+ "OPTIONAL {?o rdfs:label ?label }"
			        		+ "} ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://bio2rdf.org/sparql", query);
				ResultSet results ;
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			return null;
			
			
		}
		
		public static ResultSet dbpediaquery(String entity)
		{
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?p ?o where {<" + entity +  "> ?p  ?o. } ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			return null;
			
			
		}

		public static ResultSet lifedataquery(String resource)
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
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
				ResultSet results ;
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}		

		public static List<String> lifedataqueryuris(String resource)
		{
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				        "select distinct  ?o ?label where {<" + resource +  "> ?p  ?o." + 
				        "OPTIONAL {?o rdfs:label ?label} "
				        		+ " } " +
			             "LIMIT 100"  ;
			
			// now creating query object
			try
			{
				
				List<String> Linkuris = new ArrayList<String>() ;
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
				ResultSet results ;
				results = qexec.execSelect(); 	
				for (; results.hasNext();) 
				{
				    // Result processing is done here.
			         QuerySolution soln = results.nextSolution() ;
			         String subj = soln.get("o").toString();  //get the subject
			         Linkuris.add(subj);
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

		public static ResultSet LODquery(String entity)
		{
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				        "select distinct  ?p ?o where {<" + entity +  "> ?p  ?o.  " +
				        "FILTER (lang(?o) = 'en') }" +
			             "LIMIT 100"  ;
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", query);
				ResultSet results ;
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}
		
		public static ResultSet TriplePropertyaquery(String entity,String onto)
		{
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?p ?o where {<" + entity +  "> ?p  ?o. } ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);

				ResultSet results ;
				
				if (onto.contains("bioontology"))
				{

			        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(onto, query); 
					qexec.addParam("apikey", "396993d0-4ce2-4123-93de-214e9b9ebcf2") ;
					qexec.setTimeout(30000);
					results = qexec.execSelect(); 
				}
				else
				{
					QueryExecution qexec = QueryExecutionFactory.sparqlService(onto, query);
					qexec.setTimeout(30000);
					results = qexec.execSelect(); 
				}
				
					
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
			
		}
		public static ResultSet TripleDescriptive(String entity,String onto)
		{
			
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
					"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +
			        "select distinct  ?def where {<" + entity +  "> rdfs:comment|skos:note|skos:definition|dbpedia-owl:abstract   ?def. } ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);

				ResultSet results ;
				
				if (onto.contains("bioontology"))
				{

			        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(onto, query); 
					qexec.addParam("apikey", "396993d0-4ce2-4123-93de-214e9b9ebcf2") ;
					qexec.setTimeout(30000);
					results = qexec.execSelect(); 
				}
				else
				{
					QueryExecution qexec = QueryExecutionFactory.sparqlService(onto, query);
					qexec.setTimeout(30000);
					results = qexec.execSelect(); 
				}
				
					
				return results ;
			}
			catch(QueryParseException e)
			{
				System.out.println(e.getMessage()) ;
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage()) ;
			}
			return null;
			
			
		}
		
		
		public static ResultSet LODqueryDefinition(String entity, String predicate)
		{
	
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?s ?o " +
				    "where { " +
			                   "?s " + predicate +  "?label." +
			                   "?label <bif:contains> "   + "\"" +   entity +  "\"." +
			                   //"?s skos:note|skos:definition ?o." +
			                   "?s rdfs:comment ?o." +
			            " } " +
			            "LIMIT 100" ;
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}
		
		public static ResultSet LLDqueryDefinition(String entity)
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
			                   "?s " + "rdfs:label|skos:prefLabel|skos:altLabel" + "\"" +   entity +  "\"." + 
			                   "?s skos:note|skos:definition ?def." + 
			            " } " +
			            "LIMIT 50" ;
			
	
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}
		// get the entities and type concepts from Linked life Data
		public static List<_Entity> LLDqueryConcepts(String entity, String predicate,int limit)
		{
	
			List<_Entity> conceptlist = new ArrayList<_Entity>() ;
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			        "select distinct  ?concept ?def " +
				    "where { " +
			                   "?concept " + predicate + "\"" +   entity +  "\"." +   // concept with this name 
			                   "?concept skos:note|skos:definition ?def." +   // concept shell have a definition 
			                  
			            " } " +
			            "LIMIT 1 "  ; //+  Integer.toString(limit) ; 
			
	
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				// http://sparql.bioontology.org, "http://linkedlifedata.com/sparql"
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				
				// for bioportal 
				// qexec.addParam("apikey", this.apikey) ;
				
				results = qexec.execSelect(); 
				_Entity _entity = new _Entity() ;
				int counter = 0 ;
				boolean found = false ; 
				for (; results.hasNext();) 
				{
				    // Result processing is done here.
			         QuerySolution soln = results.nextSolution() ;
			        // String subj = soln.get("s").toString();  //get the subject
			         _entity.Labels.put(entity,soln.get("concept").toString());  //get the su  
			         _entity.definitions.put(entity, soln.get("def").toString()) ;
			         found  = true ; 
				}
				
				if (found) 
				{
					// get the concept 
					String queryString1 =
							"PREFIX p: <http://dbpedia.org/property/>"+
							"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
							"PREFIX category: <http://dbpedia.org/resource/Category:>"+
							"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
							"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
							"PREFIX geo: <http://www.georss.org/georss/>"+
							"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
							"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					        "select distinct  ?altlab ?cat ?typelabel " +
						    "where { "  + 
					                   "<" + _entity.Labels.get(entity) + ">  rdf:type ?cat." +  // concept type
					                   "?cat skos:prefLabel  ?typelabel." +  // concept type label 
					                   "<" + _entity.Labels.get(entity) + "> skos:altLabel ?altlab" +  
					            " } " +
					            "LIMIT 50"  ; //+  Integer.toString(limit) ; 
					
					Query query1 = QueryFactory.create(queryString1);
					QueryExecution qexec1 = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query1);
					ResultSet results1 ;
					qexec1.setTimeout(30000);
					results1 = qexec1.execSelect(); 
					
					for (; results1.hasNext();) 
					{
					    // Result processing is done here.
				         QuerySolution soln = results1.nextSolution() ;
				        // String subj = soln.get("s").toString();  //get the subject
				         _entity.types.put(entity + " + " + soln.get("cat").toString(),soln.get("typelabel").toString());  //get the su  
				         _entity.altLabels.put(entity, soln.get("altlab").toString()) ;
					}
					conceptlist.add(_entity) ;
					
					return conceptlist ;
				}

			}
			catch(QueryParseException e)
			{
				System.out.println(e.getMessage()); 
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}
		// get the entities and type concepts from Linked life Data
				public static List<_Entity> LODqueryConcepts(String entity, String predicate,int limit)
				{
			
					List<_Entity> conceptlist = new ArrayList<_Entity>() ;
					String queryString=
							"PREFIX p: <http://dbpedia.org/property/>"+
							"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
							"PREFIX category: <http://dbpedia.org/resource/Category:>"+
							"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
							"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
							"PREFIX geo: <http://www.georss.org/georss/>"+
							"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
							"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"+

					        "select distinct  ?concept ?def " +
						    "where { " +
					                   "?concept " + predicate + "\"" +   entity +  "\"@en." +   // concept with this name 
					                   "?concept rdfs:comment|dbpedia-owl:abstract ?def." +   // concept shell have a definition 
					                  
					            " } " +
					            "LIMIT 20 "  ; //+  Integer.toString(limit) ; 
					
			
					
					// now creating query object
					try
					{
						Query query = QueryFactory.create(queryString);
						QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
						ResultSet results ;
						//qexec.setTimeout(30000);
						results = qexec.execSelect(); 
						_Entity _entity = new _Entity() ;
						int counter = 0 ; 
						boolean found = false ;
						for (; results.hasNext();) 
						{
						    // Result processing is done here.
					         QuerySolution soln = results.nextSolution() ;
					         // take only the english resource 
					         if (soln.get("def").toString().contains("@en") )
					         {
						        // String subj = soln.get("s").toString();  //get the subject
						         _entity.Labels.put(entity,soln.get("concept").toString());  //get the su  
						         _entity.definitions.put(entity, soln.get("def").toString()) ;
						         found = true ;
						          break ; 
					         }
					         found = true ;
						}
						
						
						if (!found) 
						{
							// get the concept 
							String queryString1 =
									"PREFIX p: <http://dbpedia.org/property/>"+
									"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
									"PREFIX category: <http://dbpedia.org/resource/Category:>"+
									"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
									"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
									"PREFIX geo: <http://www.georss.org/georss/>"+
									"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
									"PREFIX dcterms: <http://purl.org/dc/terms/>"+
							        "select distinct  ?cat ?typelabel " +
								    "where { "  + 
							                   "<" + _entity.Labels.get(entity) + ">  dcterms:subject ?cat." +  // concept type
							                   "?cat rdfs:label  ?typelabel." +  // concept type lab  
							                   "FILTER (lang(?typelabel) = \"en\")" + 
							            " } " +
							            "LIMIT 50"  ; //+  Integer.toString(limit) ; 
							
							Query query1 = QueryFactory.create(queryString1);
							QueryExecution qexec1 = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query1);
							ResultSet results1 ;
							qexec1.setTimeout(30000);
							results1 = qexec1.execSelect(); 
							
							for (; results1.hasNext();) 
							{
							    // Result processing is done here.
						         QuerySolution soln = results1.nextSolution() ;
						        // String subj = soln.get("s").toString();  //get the subject
						         _entity.types.put(entity + " + " + soln.get("cat").toString(),soln.get("typelabel").toString());  //get the su  
						         
							}
							conceptlist.add(_entity) ;
							
							return conceptlist ;
	
						}
					}
					catch(QueryParseException e)
					{
						System.out.println(e.getMessage()); 
					}
					catch (Exception e)
					{
						
					}
					return null;
					
				}
		public static ResultSet fullquery(String entity1,String entity2)
		{
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				        "select distinct  ?p ?o where {<" + entity1 +  "> ?p  <" + entity2 +  ">. } " +
			             "LIMIT 100"  ;
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}
		public static ResultSet DBpquery(String entity)
		{
			String queryString=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				        "select distinct  ?p ?o where {<" + entity +  "> ?p  ?o. } ";
			
			// now creating query object
			try
			{
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 	
				return results ;
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
		}
		public  Map<String,Integer> Getlabelvalue(String entity) throws IOException
		{
			Map<String,Integer> statMap = new HashMap<String, Integer>();
			
			String queryStringlabel=
					"PREFIX p: <http://dbpedia.org/property/>"+
					"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
					"PREFIX category: <http://dbpedia.org/resource/Category:>"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
					"PREFIX geo: <http://www.georss.org/georss/>"+
					"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				        "select distinct   ?o where {<" + entity +  "> rdfs:label|skos:prefLabel  ?o. }  LIMIT 2";

			// now creating query object
			try
			{
				System.out.println("Enter label");
				Query query = QueryFactory.create(queryStringlabel);
				System.out.println(query);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
				ResultSet results ;
				qexec.setTimeout(30000);
				results = qexec.execSelect(); 	
				statMap.putAll(Getliteral(results)) ;
				return statMap ;
				
			}
			catch(QueryParseException e)
			{
			}
			catch (Exception e)
			{
				
			}
			return null;
			
			
		}	
		public Map<String,Integer> Getliteral(ResultSet results) throws IOException
		{
			
			
			Map<String,Integer> statMap = new HashMap<String, Integer>();
			for (; results.hasNext();) 
			{


			    
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         RDFNode   object    = soln.get("o");      // get the object
		         if (object instanceof Literal )
		         {
				    	statMap.put(object.toString(),1) ; 
		         } 

			}
			    
			return statMap;

			
		}
		
		
		

public static  ResultSet JenaSparqlExample(String entity) {

//Querying remote SPARQL services	
	ResultSet results = null ; 
	try 
	{

	     String ontology_service = "http://linkedlifedata.com/sparql";
	
	String sparqlQuery=
			"PREFIX p: <http://dbpedia.org/property/>"+
			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"PREFIX geo: <http://www.georss.org/georss/>"+
			"PREFIX w3: <http://www.w3.org/2002/07/owl#>" +
	        "select distinct  ?p ?o where {<" + entity +  "> ?p  ?o. } ";

	QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service, sparqlQuery);
	 results = x.execSelect();
	}
	catch(QueryParseException e)
	{
		System.out.println(e.getMessage()); 
		return null ; 
	}
	catch (Exception e)
	{
		System.out.println(e.getMessage()); 
		 return null ; 
	}
	 return results ;
}


public static Map<String,Integer> getconcepts(_EntityCandidate resource, String predicate)
{


	String queryString=
			"PREFIX p: <http://dbpedia.org/property/>"+
			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"PREFIX geo: <http://www.georss.org/georss/>"+
			"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
	        "select distinct  ?label  where" +
		    "{ " +
		    	" <" +   resource.Resource +  ">" + "skos:broaderTransitive|skos:narrowerTransitive ?concept." + 
	                    "?concept rdfs:label|skos:prefLabel|skos:altLabel ?label" + 
	            " } LIMIT 100"  ;
	
	Map<String,Integer> conceptlist = new HashMap<String, Integer>();
	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
		qexec.setTimeout(30000);
		ResultSet results = qexec.execSelect(); 
		for (; results.hasNext();) 
		{
			// Result processing is done here.
	         QuerySolution soln = results.nextSolution() ;
			 conceptlist.put(soln.get("label").toString(),1);
		}
		return conceptlist ;
	}
	catch(QueryParseException e)
	{
		e.printStackTrace();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
	
}

public static List<_EntityCandidate> generatingCandidateEntities(String entity, String predicate)
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
	        "select distinct  ?entity  where" +
		    "{ " +
	                   "?entity ?predicate" + " \"" +   entity +  "\". " + 
	                   //"?entity rdf:type skos:Concept" + 
	            " } LIMIT 100"  ;
	

	List< _EntityCandidate> conceptlist = new ArrayList< _EntityCandidate>() ;
	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
		qexec.setTimeout(30000);
		ResultSet results = qexec.execSelect(); 
		for (; results.hasNext();) 
		{
			// Result processing is done here.
	         QuerySolution soln = results.nextSolution() ;
	         _EntityCandidate Resource = new _EntityCandidate() ;
	         Resource.Resource  = soln.get("entity").toString() ;
			 conceptlist.add(Resource);
		}
		return conceptlist ;
	}
	catch(QueryParseException e)
	{
		e.printStackTrace();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
	
}


public static Boolean EntityMentionDetection(String mention, String predicate)
{
	
	//acquiring all triples with object match the mention

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
	        "ASK " +
		    "{ " +
	                   "?entity ?predicate" +  " \"" +   mention +  "\". "   +
	                  "?entity rdf:type skos:Concept" + 
	        " } "  ;
	

	
	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
		Boolean results ;
		qexec.setTimeout(10000);
		results = qexec.execAsk(); 
		return results ;
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

public static Boolean EntityMentionDetectionBio(String concept)
{
	
try
	{
		//APIkey 
		//396993d0-4ce2-4123-93de-214e9b9ebcf2
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
		        "Select * " +
			    "{ " +
		                   "?entity ?predicate  ?label."   +
		                  "?entity rdf:type skos:Concept" + 
		        " FILTER (CONTAINS ( UCASE(str(?label)), "  + "\"" +   concept.toUpperCase() +  "\") ) } LIMIT 100"  ;
		
		// ask did not work
		 Query query = QueryFactory.create(queryString) ;
       
		 // http://sparql.bioontology.org/
		 // "http://sparql.bioontology.org/sparql/"
         QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql/", query); 		                      
         qexec.addParam("apikey", "396993d0-4ce2-4123-93de-214e9b9ebcf2") ;
         ResultSet results1 ;
         qexec.setTimeout(300000);
         results1 = qexec.execSelect() ;
         for (; results1.hasNext();) 
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


public static Model describe(String resource,String endpoint)
{

	String queryString=
			"PREFIX p: <http://dbpedia.org/property/>"+
			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"PREFIX geo: <http://www.georss.org/georss/>"+
			"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
	        "DESCRIBE  " + " <" +   resource +  ">"   ; 
	

	
	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		Model results ;
		qexec.setTimeout(30000);
		results = qexec.execDescribe(); 
		return results ;
	}
	catch(QueryParseException e)
	{
		e.printStackTrace();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
	
}

public static ResultSet UMLSsemantictype(String resource,String endpoint)
{

	String queryString=
			"PREFIX p: <http://dbpedia.org/property/>"+
			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"PREFIX geo: <http://www.georss.org/georss/>"+
			"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
	        "Select distinct ?semantictype where {  " + " <" +   resource +  ">"  
	        + " a ?semantictype }" ; 

	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		qexec.setTimeout(300000);
		ResultSet results = qexec.execSelect(); 
		return results ;
	}
	catch(QueryParseException e)
	{
		e.printStackTrace();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
	
}
public static ResultSet construct(String resource,String endpoint)
{

	String queryString=
			"PREFIX p: <http://dbpedia.org/property/>"+
			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"PREFIX geo: <http://www.georss.org/georss/>"+
			"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
	        "Select ?s ?p ?o   " + 
	         "WHERE { " + "<" +    resource + ">" + " !rdfs:labe* ?s. "   +
	         "?s ?p ?o} Group by ?s ?p ?o " ;
	

	
	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results ;
		qexec.setTimeout(30000);
		results = qexec.execSelect(); 
		return results ;
	}
	catch(QueryParseException e)
	{
		e.printStackTrace();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
	
}


public static  List<String> lifedataterms(String entity)
{
	List<String> terms = new ArrayList<String>() ;
	
	String queryString=
			"PREFIX p: <http://dbpedia.org/property/>"+
			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"PREFIX geo: <http://www.georss.org/georss/>"+
			"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
			"PREFIX lld: <http://linkedlifedata.com/resource/>" + 
			"PREFIX skos-xl: <http://www.w3.org/2008/05/skos-xl#>" +
	        "select Distinct ?s ?term ?label ?note  " +
		    "where { " 
		        + " ?s ?p  "   + "\"" +   entity +  "\"."  
		        + " ?s skos:inScheme  lld:umls."
		        + " ?s skos-xl:altLabel ?term."
		        + " ?term skos-xl:literalForm ?label."
		        + "?term skos:note ?note."
	            + " } " ; 

	// now creating query object
	try
	{
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql?inference=false", query);
		ResultSet results ;
		qexec.setTimeout(30000);
		results = qexec.execSelect(); 	
		for (; results.hasNext();) 
		{
		    // Result processing is done here.
	         QuerySolution soln = results.nextSolution() ;
	         String subj = soln.get("s").toString();  //get the subject
	         String term = soln.get("term").toString();  //get the subject
	         String label = soln.get("label").toString();  //get the subject
	         terms.add(subj + "|" + term + "|" + label );
	         System.out.println(subj + "|" + term + "|" + label ) ;
		}
		return terms ;
	}
	catch(Exception  e)
	{
		System.out.println(e.getMessage()) ;
	}
	return null;
	
	
}



} // end of the class


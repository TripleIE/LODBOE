package util;

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DS.ConceptsDiscovery;
import DS.MetamapConcepts;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.xerces.parsers.DOMParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException ;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class surfaceFormDiscovery {
	
	public static Map<String, Integer> getsurfaceFormLLD(String concept) throws IOException
	{
		Map<String, Integer> surfaceForm = new HashMap<String, Integer>() ;
		surfaceForm.put(concept,1) ;  

		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX geo: <http://www.georss.org/georss/>"+
				"PREFIX w3: <http://www.w3.org/2002/07/owl#>"+
				"select distinct ?label  where {?riskfactor skos:altLabel|skos:prefLabel|rdfs:label " + "\"" + concept.trim() + "\" ."  
						+ "?riskfactor skos:altLabel|skos:prefLabel|rdfs:label  ?label" +  "} ";
		
		// now creating query object
		try
		{
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedlifedata.com/sparql", query);
			//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", query);
			ResultSet results ;
			qexec.setTimeout(30000);
			results = qexec.execSelect(); 
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         surfaceForm.put((soln.get("label").toString()),1) ;
		         
		    }

			return surfaceForm ;
		}
		catch(QueryParseException e)
		{
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		return surfaceForm;
		
		
	}
	
	public static Map<String, Integer> getsurfaceFormMesh(String concept) throws IOException
	{
		Map<String, Integer> surfaceForm = new HashMap<String, Integer>() ;
		surfaceForm.put(concept,1) ;  
		List<String> conceptslist = new ArrayList<String>();
		conceptslist.add(concept) ;

		String queryString =
				
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" + 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + 
				"PREFIX meshv: <http://id.nlm.nih.gov/mesh/vocab#>" + 
				"PREFIX mesh: <http://id.nlm.nih.gov/mesh/>" + 
				"PREFIX mesh2015: <http://id.nlm.nih.gov/mesh/2015/>" + 
				"PREFIX mesh2016: <http://id.nlm.nih.gov/mesh/2016/>" + 
				"PREFIX mesh2017: <http://id.nlm.nih.gov/mesh/2017/>" + 
				"select distinct ?label  where { " +
				"?d a meshv:Descriptor. " +   
				"?d meshv:concept ?c ." +
				"?d rdfs:label ?dName ." +
				"?c rdfs:label ?cName." +
				"?c meshv:term ?t ." +
				"?t rdfs:label ?label  " + 
				"FILTER(REGEX(?dName," + "'^" + concept + "$'" + " ,'i')" + "|| REGEX(?cName," + "'^" + concept + "$'" + " ,'i')"+ ")}"  ;
		
				// "FILTER(REGEX(?dName," + "'" + concept.trim() + "'" + ",'i' )" + ")}" /* + "  || REGEX(?cName," + concept  + ",'i')) }"*/ ;
		
		// now creating query object
		try
		{
			
			//Any MeSH term ('D' or 'M') that has entity  as part of its name. (inference required)
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://id.nlm.nih.gov/mesh/sparql?inference=true", query);
			//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lod.openlinksw.com/sparql", query);
			ResultSet results ;
			qexec.setTimeout(30000);
			results = qexec.execSelect(); 			
			for (; results.hasNext();) 
			{
			    // Result processing is done here.
		         QuerySolution soln = results.nextSolution() ;
		         surfaceForm.put((soln.get("label").asLiteral().getLexicalForm().toLowerCase()),1) ;
		         
		    }
			
/*			MetaMapApi api = new MetaMapApiImpl();
			List<String> conceptsIdentified = MetamapConcepts.getconceptsforms(concept, api);
			for (String altconcept:conceptsIdentified)
			{
				conceptslist.add(altconcept);
				surfaceForm.put(altconcept,1);
			}*/
			
			
			
			// babelnet
			
/*			for (String cpt :conceptslist)
			{
					String key= "d079f78a-7936-449d-aa03-2286178981ae" ;
					String term = cpt.replace(" ", "+") ; 
					
					String urlStr = "http://babelnet.io//v4//getSenses?word=" + term+ "&lang=EN&key=" + key ;
					String senses = httpGet(urlStr); 
					if (senses != null)
						surfaceForm.putAll(getSenses(senses)) ;
					
			}*/
			

			


			return surfaceForm ;
		}
		catch(QueryParseException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		return surfaceForm;
		
		
	}
	
	public static String getdefFromMesh(String resource) throws IOException
	{
		Map<String, Integer> surfaceForm = new HashMap<String, Integer>() ; 

		String queryString =
				
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" + 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + 
				"PREFIX meshv: <http://id.nlm.nih.gov/mesh/vocab#>" + 
				"PREFIX mesh: <http://id.nlm.nih.gov/mesh/>" + 
				"PREFIX mesh2015: <http://id.nlm.nih.gov/mesh/2015/>" + 
				"PREFIX mesh2016: <http://id.nlm.nih.gov/mesh/2016/>" + 
				"PREFIX mesh2017: <http://id.nlm.nih.gov/mesh/2017/>" + 
				"select distinct ?def where {  ?d a meshv:Descriptor ." +
				 " ?d meshv:concept ?c . " + 
				 " ?c rdfs:label ?cName. " + 
				 " ?c meshv:scopeNote ?def. " +
		
				"FILTER(REGEX(?cName," + "'" + resource.trim() + "'" + ",'i' )" + ")}" ;  /* + "  || REGEX(?cName," + concept  + ",'i')) }"*/ ;
		
		// now creating query object
		try
		{
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://id.nlm.nih.gov/mesh/sparql?inference=true", query);
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
			e.printStackTrace();
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		return null ;
		
	}
	
    public static String httpGet(String urlStr) throws IOException {
  	  URL url = new URL(urlStr);
  	  HttpURLConnection conn =
  	      (HttpURLConnection) url.openConnection();

  	  if (conn.getResponseCode() != 200) {
           throw new IOException(conn.getResponseMessage());
  	  }

  	  // Buffer the result into a string
  	  BufferedReader rd = new BufferedReader(
  	      new InputStreamReader(conn.getInputStream()));
  	  StringBuilder sb = new StringBuilder();
  	  String line;
  	  while ((line = rd.readLine()) != null) {
  	    sb.append(line);
  	  }
  	  rd.close();

  	  conn.disconnect();
  	  return sb.toString();
  	}

    public static Map<String, Integer> getSenses(String Jsonstring) 
	{
			
			// read the json file
    	   Map<String, Integer> Senses = new HashMap<String, Integer>() ;

			JSONParser parser = new JSONParser();
	        try {     
	            Reader targetReader = new StringReader(Jsonstring);
	            
	            Object obj = parser.parse(targetReader);
                 
	            JSONArray jsonarray =  (JSONArray) obj;
	            Iterator<JSONObject> iterator = jsonarray.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject jsonObject =  iterator.next() ;
	            	 ;
	            	System.out.println(jsonObject.toString());
	            	String lemma =  (String) jsonObject.get("lemma");
	            	String simpleLemma =  (String) jsonObject.get("simpleLemma");
	            	System.out.println(lemma);
	            	System.out.println(simpleLemma);
	            	Senses.put(lemma, 1);
		            }

	           // JSONArray name =  (JSONArray) jsonObject.get("lemma");
	           // System.out.println(name);

/*	            String city = (String) jsonObject.get("city");
	            System.out.println(city);

	            String job = (String) jsonObject.get("job");
	            System.out.println(job);

	            // loop array
	            JSONArray cars = (JSONArray) jsonObject.get("cars");
	            Iterator<String> iterator = cars.iterator();
	            while (iterator.hasNext()) {
	             System.out.println(iterator.next());
	            }*/
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        
	        return Senses ;
	    }
    
	public static boolean findInXfordDictionary(String term) throws IOException 
	{
		URL url = new URL("http://www.oed.com/srupage?operation=searchRetrieve&query=cql.serverChoice+=+" + term + "test&maximumRecords=100&startRecord=1");
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  
		  rd.close();

		  conn.disconnect();
		  
		  if (parsexmlstring(sb.toString()) != 0) 
		  {
			  return true ;
		  }
		  
		return false ; 
	}
	  public static int parsexmlstring(String xmlRecords) throws IOException 
	  {

			  DOMParser parser = new DOMParser();
			  try {
			      parser.parse(xmlRecords);   
			      Document doc = parser.getDocument();
			      String message = doc.getDocumentElement().getTextContent();
			      
			      NodeList nodes = doc.getElementsByTagName("srw:numberOfRecords");
		           Element line = (Element) nodes.item(0);
		           System.out.println("Name: " + line.getNodeValue());

			      System.out.println(message);
			  } catch (SAXException e) {
			      // handle SAXException 

			  }
			  
			return 0;    
		    
	}
}

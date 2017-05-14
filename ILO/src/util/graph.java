package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.hp.hpl.jena.ontology.OntTools.Path;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class graph {

	static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
	static String LD = "http://www.linkeddatatools.com/colors#" ;
	static String   LOD = "http://www.linkeddatatools.com/clothing-features# " ; 
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

	   Model candidategraphA = ModelFactory.createDefaultModel(); 

   	   Resource white = candidategraphA.createResource("http://www.linkeddatatools.com/colors#white");
   	   final Property pwhite = ResourceFactory.createProperty("http://www.linkeddatatools.com/clothing-features#color") ;
   	   white.addProperty(pwhite,"white");

	   Resource recA = candidategraphA.createResource("http://www.linkeddatatools.com/clothes#t-shirt");
   	   final Property pA1 = ResourceFactory.createProperty("http://www.linkeddatatools.com/clothing-features#size") ;
   	   recA.addProperty(pA1,"12");
   	   final Property pA2 = ResourceFactory.createProperty("http://www.linkeddatatools.com/clothing-features#color") ;
   	   recA.addProperty(pA2,white);
   	   //candidategraphA.write(System.out, "RDF/XML-ABBREV") ;     
   	   
   	   
	   Model candidategraphB = ModelFactory.createDefaultModel(); 

   	   Resource whiteB = candidategraphB.createResource(rdf + "white");
   	   final Property pwhiteB = ResourceFactory.createProperty(rdf + "color") ;
   	   whiteB.addProperty(pwhiteB,"white");
	   
	   
	   Resource recB = candidategraphB.createResource(rdf + "shirt");
   	   final Property pB1 = ResourceFactory.createProperty(LOD + "size") ;
   	   recB.addProperty(pB1,"13");
   	   candidategraphB.setNsPrefix( "rdf", rdf ) ;
   	   candidategraphB.setNsPrefix( "LOD", LOD ) ;
   	   
   	   final Property pB2 = ResourceFactory.createProperty(LOD + "color") ;
   	   recB.addProperty(pB2,whiteB);

   	    candidategraphB.write(System.out, "RDF/XML-ABBREV") ;  
  	   
   	   
		Model Intersection = candidategraphB.union(candidategraphA);
		Intersection.write(System.out, "RDF/XML-ABBREV"); 
   	   
	}

	
	public static void readowl(String filename) throws IOException
	{
		java.nio.file.Path input = Paths.get("F:\\eclipse64\\data", "doid.owl");
		Model owlgraph = ModelFactory.createDefaultModel();
		owlgraph.read(input.toUri().toString(), "RDF/XML-ABBREV") ;
		Map<String, List<String>> diseaselist =  ReadXMLFile.ReadCDR_TestSet_BioCDisease() ; 
		Map<String, List<String>> diseasegoldstandard =  new HashMap<String, List<String>>(); 
		for(String title :diseaselist.keySet())
		{
			List<String> diseases = diseaselist.get(title) ; 
			for (String diease : diseases  )
			{
				List<String> hier = gethierarchy(diease,owlgraph) ;
				if ( hier.size() > 0 )
					diseasegoldstandard.put(diease, hier) ;
				
			}
		}
		Random rand = new Random();
		
		File file = new File("F:\\eclipse64\\data\\doid.owl");
		
		List<String> dislist = readowlLines(file.toURL()) ;

		for (int i = 1 ; i < 500 ; i++)
		{
			int  n = rand.nextInt(1000) + 1;
			 String disea = dislist.get(n) ;
			 List<String> hier1 = gethierarchy(disea,owlgraph) ;
			 if ( hier1.size() > 0 )
					diseasegoldstandard.put(disea, hier1) ;
		}

		//gethierarchy("common cold",owlgraph) ; 
		//owlgraph.write(System.out, "RDF/XML-ABBREV"); 
		
		ReadXMLFile.Serialized(diseasegoldstandard,"F:\\eclipse64\\eclipse\\DiseaseGoldstandard") ;
		
	}
    public static List<String> readowlLines(URL url) throws IOException
    {

    	
        Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
        List<String> diseases;
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        try 
        {
        	diseases = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
  
                String labeltag = "<rdfs:label rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">" ;
                String labelend = "</rdfs:label>" ;
                if ( line.contains(labeltag) && line.contains(labelend )  )
                {
                	String Diseaselabel  = line.replaceAll(labeltag, "")  ;
                	Diseaselabel = Diseaselabel.replaceAll(labelend, "") ;
                	diseases.add(Diseaselabel.trim());
                }
                
            }
        }
        finally
        {
        }
        

        return diseases ;

    }
	
	public static List<String> gethierarchy(String concept,Model Diseasegraph)
	{
		List<String> Hier = new ArrayList<String>() ;
		// Create a new query
		String queryString = 
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
		    "SELECT ?label " +
		    "WHERE {" +
		    "      ?concept rdfs:label " + "\"" + concept + "\"." + 
		    "      ?concept  rdfs:subClassOf+ ?parent . " +
		    "      ?parent  rdfs:label ?label . " +
		    "      }";
		 
		Query query = QueryFactory.create(queryString);
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, Diseasegraph);
		ResultSet results = qe.execSelect();
		for (; results.hasNext();) 
		{
		    // Result processing is done here.
	         QuerySolution soln = results.nextSolution() ;
	         String label = soln.get("label").asLiteral().getLexicalForm();  //get the subject
	         Hier.add(label);
	         System.out.println(label) ;
		}
		// Important - free up resources used running the query
		qe.close();
		
		return Hier ;
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		 

	}
}

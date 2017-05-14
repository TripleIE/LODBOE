package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable; 

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
public class Dataset implements Serializable
{
	
	Map<String, List<String>> NSURIs = new HashMap <String, List<String>>() ;
	Model graph = null ; 
	List<String> lexicalaltlabel ;
	OntModel candidategraph = ModelFactory.createOntologyModel(); 
	Map<String, Double> uriconfident = null ; 
	
	public List<String> Synonym= null ; 
	public List<String> Definition = null ; 
	public List<String>  Category = null ; 
	public List<String>  ontology = null ; 
	public List<String>  Hierarchy = null ; 
	public List<String>  NHierarchy = null ; 
	public String PrefLabel = null  ;
	Map<String, Double> Topuriconfident = null  ;
	public void Setonto(String onto,List<String> uris )
	{
		NSURIs.put(onto, uris) ;
	}
	
	public List<String>  getontoURIs(String onto)
	{
		return NSURIs.get(onto);
	}

	public Map<String, List<String>>  getonto()
	{
		return NSURIs;
	}
	
	public void removeURI(String onto,String uri)
	{
		NSURIs.get(onto).remove(uri);
	}
	
	public void SetGraph(Model model)
	{
		graph = model ; 
	}
	public Model getGraph()
	{
		if(graph == null)
		{
			graph = ModelFactory.createDefaultModel();
		}
		return graph  ; 
	}
	
	public Model getcandidateGraph()
	{
		return candidategraph  ; 
	}
	public OntModel getontocandidateGraph()
	{
		return candidategraph  ; 
	}
	public void Setaltlebel(List<String> lexical)
	{
		lexicalaltlabel = lexical ; 
	}
	
	public List<String> getaltlebel()
	{
		return lexicalaltlabel  ; 
	}
	
	public void Seturiconfident(Map<String, Double> luriconfidentin )
	{
		uriconfident = luriconfidentin ;
	}
	
	public void SetTopuriconfident(Map<String, Double> luriconfidentin )
	{
		Topuriconfident = luriconfidentin ;
	}
	public List<String>  Sorturiconfident( int top)
	{
		if (top > uriconfident.size())
		{
			top = uriconfident.size() ;
		}
		
		Map<String, Double> confident = new HashMap <String, Double>(uriconfident) ;
		List<String> list  = new ArrayList<String>()  ;
		for ( int i = 0 ; i < top; i++)
		{
			Double max = 0.0 ; 
			String URI = null ; 
			for ( String key :  confident.keySet())
			{
			    Double value = confident.get(key) ;
			    if (value > max)
			    {
			    	max = value ;
			    	URI = key ; 
			    	
			    }
			}
			list.add(URI +"!" + max) ; 
			confident.put(URI, -100.0) ;
			
		}
		return list; 		
	}
	
	
	public List<String>  getTopBesturiconfident( int top,double threashold)
	{
		if (top > uriconfident.size())
		{
			top = uriconfident.size() ;
		}
		
		Map<String, Double> confident = new HashMap <String, Double>(uriconfident) ;
		List<String> list  = new ArrayList<String>()  ;

		Double max = 0.0 ; 
		String URI = null ; 
		int count = 1; 
		for ( String key :  confident.keySet())
		{
		    Double value = confident.get(key) ;
		    if (value > threashold && count <= top)
		    {
		    	max = value ;
		    	URI = key ; 
		    	
		    	list.add(URI +"!" + value) ; 
		    	count++ ; 
		    	
		    }
		}
		return list; 		
	}
	public Map<String, Double> geturiconfident()
	{
		return uriconfident  ; 
	}
	
	public Map<String, Double> gettopuriconfident()
	{
		return Topuriconfident  ; 
	}
}

package DS;

import edu.stanford.nlp.trees.Tree;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TextProcess.NLPEngine;
import TextProcess.removestopwords;
import util.NGramAnalyzer;
import util.ReadXMLFile;
import util.readfiles;
import util.surfaceFormDiscovery;
public class ConceptsDiscovery {


	
	public static HashMap<String, Map<String, List<String>>> getcachconcepts(List<String> titles, MetaMapApi api) throws IOException
	{
		
		double avgRecall = 0  ; 
		double avgPrecision = 0 ;
		double avgFmeasure = 0 ; 
		int counter = 0 ;
		HashMap<String, Map<String, List<String>>> diectwithtitle = new HashMap<String, Map<String, List<String>>>();
		for(String title : titles)
		{
			title = title.toLowerCase() ;
			String orgTitle = title ;
			Map<String, Integer> allconcepts = new HashMap<String, Integer>();
			counter++ ; 
			try {
				if (!title.contains("@en"))
					continue ; 
				 System.out.println(title);
				 
				// find all concepts that exist in the UMLS
				Map<String, Integer> metmapconcepts = MetamapConcepts.getconcepts(title,api) ;
				String[] arr = new String[metmapconcepts.size()] ;
				int i= 0 ; 
				for( String concept : metmapconcepts.keySet())
				{
					arr[i] = concept ;
					i++ ; 
				}
				
				
			// sort them Descending  
				arr = insertionSort(arr); 
				// pruning the concepts 
				for( String concept : arr)
				{
					if ( title.contains(concept.toLowerCase()) )
					{
						title = title.replace(concept.toLowerCase(), "") ;
					}
					else
					{
						metmapconcepts.remove(concept) ;
					}
				}
				
				allconcepts.putAll(metmapconcepts);
				// get the surface form & synonym of each concepts 
				   Map<String, List<String>> diect = new HashMap<String, List<String>>();

				   for (String concept:allconcepts.keySet())
				   {
					   Map<String, Integer> surfaceFormmesh = null ;
					   Map<String, Integer> surfaceFormlld = null ;
					   List<String> forms = new ArrayList<String>();
					   forms.add(concept) ;
					   diect.put(concept, forms) ;
				   }
				   
				   diectwithtitle.put(orgTitle, diect) ;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		
	
		return diectwithtitle ;
	}
	public static Map<String, Integer> getconcepts(Map<String, List<String>> titles,MetaMapApi api)
	{


	    
		int counter = 0 ;
		Map<String, Integer> allconcepts = new HashMap<String, Integer>();
		NLPEngine nlpprocessor = new NLPEngine() ;

		for(String title : titles.keySet())
		{
			if ( title == null || title.isEmpty())
				continue ; 
			
			title = title.toLowerCase() ;
			Tree parsertree = nlpprocessor.getParseTreeSentence(title) ;
			List<String> verbs = new ArrayList<String>() ;
			nlpprocessor.getVerbs(parsertree, verbs);
			
			counter++ ;		  
			try {
				
				// find all concepts that exist in the UMLS
				Map<String, Integer> metmapconcepts = MetamapConcepts.getconcepts(title,api) ;
				String[] arr = new String[metmapconcepts.size()] ;
				int i= 0 ; 
				for( String concept : metmapconcepts.keySet())
				{
					arr[i] = concept ;
					i++ ; 
				}
				
								
			// sort them Descending  
				arr = insertionSort(arr); 
				// pruning the concepts 
				for( String concept : arr)
				{
					if ( title.contains(concept.toLowerCase()) )
					{
						title = title.replace(concept.toLowerCase(), "") ;
					}
					else
					{
						metmapconcepts.remove(concept) ;
					}
				}
				

				
				for (String verb :verbs )
				{
					metmapconcepts.remove(verb) ;
				}

				allconcepts.putAll(metmapconcepts);

				
				Map<String, Integer> lodconcepts = new HashMap<String, Integer>();
				Map<String, Integer> mentions = new HashMap<String, Integer>();
				
				mentions = NGramAnalyzer.entities(1,3, title) ;
				
				for(String mention : mentions.keySet())
				{
					// no need to examine the stopwords
					if (!mention.isEmpty() && !removestopwords.removestopwordsingle(mention.trim()) && LDConcepts.EntityMentionDetection(mention) ) 
						lodconcepts.put(mention, 1) ;
				}
				
				
				for (String verb :verbs )
				{
					lodconcepts.remove(verb) ;
				}
				allconcepts.putAll(lodconcepts);
				

				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		

		return allconcepts ;
	}

	
	public static String[] insertionSort(String[] arr) {
	    for(int i=1;i<arr.length;i++) {
	        int j = 0;
	        for(;j<i;j++) {
	            if(arr[j].length() < arr[j+1].length()) {
	                String temp = arr[j];
	                arr[j] = arr[j+1];
	                arr[j+1] = temp;
	            }
	        }
	    }
	    return arr;
	}

}

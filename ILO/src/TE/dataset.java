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

import util.ReadXMLFile;
import util.Sentinfo;
import util.readfiles;
import DS.ConceptsDiscovery;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class dataset {

	 static String FILE_NAME_Patterns = "C:\\LODBOEtemp\\medicalFreepaltest.txt" ; 
	
	public static void relprecision() throws IOException 
	{
		// TODO Auto-generated method stub
	    // patterns
	   
		
		Map<String,Map<String,List<String>>> trainset = null ; 
		//Map<String, List<String>> titles =  ReadXMLFile.ReadCDR_TestSet_BioC()  ;
	   File fFile = new File("F:\\eclipse64\\data\\labeled_titles.txt");
	   // File fFile = new File("F:\\eclipse64\\eclipse\\TreatRelation");
	   List<String> sents = readfiles.readLinesbylines(fFile.toURL());
		
		Sentinfo sentInfo = new Sentinfo() ; 
		
		//trainset  = ReadXMLFile.DeserializeT("F:\\eclipse64\\eclipse\\TrainsetTest") ;
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
	    
		
		
		
		int count  = 0 ;
		int count1  = 0 ;
		Model candidategraph = ModelFactory.createDefaultModel(); 
		Map<String,List<String>> TripleCandidates = new HashMap<String, List<String>>();
		List<String> statements=  new ArrayList<String>() ;
		List<String> notstatements=  new ArrayList<String>() ;
		Double TPcount = 0.0 ; 
		Double FPcount = 0.0 ;
		Double NonTPcount = 0.0 ;
		Double TPcountTot = 0.0 ; 
		Double NonTPcountTot = 0.0 ;
		for(String title : sents)
		{
			
			if (title.contains("<YES>") || title.contains("<TREAT>") || title.contains("<DIS>") || title.contains("</"))
			{
			
				Boolean TP = false ; 
				Boolean NonTP = false ;
	           if (title.contains("<YES>") && title.contains("</YES>"))
	           {
	        	   TP = true ; 
	        	   TPcountTot++ ; 
	        	   
	           }
	           else
	           {
	        	   NonTP = true ; 
	        	   NonTPcountTot++ ; 
	           }
						
				
				title = title.replaceAll("<YES>", " ") ;
				title = title.replaceAll("</YES>", " ") ;
				title = title.replaceAll("<TREAT>", " ") ;
				title = title.replaceAll("</TREAT>", " ") ;
				title = title.replaceAll("<DIS>", " ") ;
				title = title.replaceAll("</DIS>", " ") ;
				title = title.toLowerCase() ;
	
				count++ ; 
	
				// get the goldstandard concepts for current title 
				List<String> GoldSndconcepts = new ArrayList<String> () ;
				Map<String, Integer> allconcepts = null ; 
				
				// this is optional and not needed here , it used to measure the concepts recall 
				Map<String, List<String>> temptitles = new HashMap<String, List<String>>(); 
				temptitles.put(title,GoldSndconcepts) ;
							
				// get the concepts 
				allconcepts  = ConceptsDiscovery.getconcepts(temptitles,api);
				
				ArrayList<String> RelInstances1 = SyntaticPattern.getSyntaticPattern(title,allconcepts,dataset.FILE_NAME_Patterns) ;
				//Methylated-CpG island recovery assay: a new technique for the rapid detection of methylated-CpG islands in cancer
				if (RelInstances1 != null && RelInstances1.size() > 0 )
				{
					TripleCandidates.put(title, RelInstances1) ;
					ReadXMLFile.Serialized(TripleCandidates,"F:\\eclipse64\\eclipse\\TreatRelationdisc") ;
					
			           if (TP )
			           {
			        	   TPcount++ ; 
			        	   
			           }
			           else
			           {
			        	   FPcount++ ; 
			           }
	
				}
				else
				{
					 notstatements.add(title) ;
				}
			}
 
		}
		int i = 0 ;
		i++ ; 
	}
	public static void Pubmed() throws IOException 
	{
		// TODO Auto-generated method stub
		
		Map<String,Map<String,List<String>>> trainset = null ; 
		//Map<String, List<String>> titles =  ReadXMLFile.ReadCDR_TestSet_BioC()  ;
	    File fFile = new File("F:\\TempDB\\PMCxxxx\\articals.txt");
	    List<String> sents = readfiles.readLinesbylines(fFile.toURL()); 
		
		Sentinfo sentInfo = new Sentinfo() ; 
		
		trainset  = ReadXMLFile.DeserializeT("F:\\eclipse64\\eclipse\\TrainsetTest") ;
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
		
		
		/************************************************************************************************/
		//Map<String, Integer> bagofwords = semantic.getbagofwords(titles) ; 
		//trainxmllabeling(trainset,bagofwords); 
		/************************************************************************************************/
		
		
		int count  = 0 ;
		int count1  = 0 ;
		Model candidategraph = ModelFactory.createDefaultModel(); 
		Map<String,List<String>> TripleCandidates = new HashMap<String, List<String>>();
		for(String title : sents)
		{
			
			Model Sentgraph = sentInfo.graph;
			if (trainset.containsKey(title))
				continue ; 
			//8538
			count++ ; 

			Map<String, List<String>> triples = null ;
			// get the goldstandard concepts for current title 
			List<String> GoldSndconcepts = new ArrayList<String> () ;
			Map<String, Integer> allconcepts = null ; 
			
			// this is optional and not needed here , it used to measure the concepts recall 
			Map<String, List<String>> temptitles = new HashMap<String, List<String>>(); 
			temptitles.put(title,GoldSndconcepts) ;
						
			// get the concepts 
			allconcepts  = ConceptsDiscovery.getconcepts(temptitles,api);
			
			ArrayList<String> RelInstances1 = SyntaticPattern.getSyntaticPattern(title,allconcepts,FILE_NAME_Patterns) ;
			//Methylated-CpG island recovery assay: a new technique for the rapid detection of methylated-CpG islands in cancer
			if (RelInstances1 != null && RelInstances1.size() > 0 )
			{
				count1++ ;
				TripleCandidates.put(title, RelInstances1) ;
				
				if (count1 == 30)
				{
					ReadXMLFile.Serialized(TripleCandidates,"F:\\eclipse64\\eclipse\\Relationdisc1") ;
					count1 = 0 ;
				}
			}
 
		}
		
		int i = 0 ;
		i++ ; 
	}

}

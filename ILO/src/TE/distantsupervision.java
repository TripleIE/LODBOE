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

import DS.ConceptsDiscovery;
import util.ReadXMLFile;
import util.readfiles;
import util.surfaceFormDiscovery;

public class distantsupervision {


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Map<String, Map<String,String>> related = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\related.dat") ;
		Map<String, Map<String,String>> skosmappingRelation = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\skosmappingRelation.dat") ;
		Map<String, Map<String,String>> Semanticrelation = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\Semanticrelation.dat") ;
		Map<String, Map<String,String>> narrowerTransitive = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\narrowerTransitive.dat") ;
		Map<String, Map<String,String>> exactMatch = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\exactMatch.dat") ;
		Map<String, Map<String,String>> narrower = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\narrower.dat") ;
		Map<String, Map<String,String>> closeMatch = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\closeMatch.dat") ;
		Map<String, Map<String,String>> broaderTransitive = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\broaderTransitive.dat") ;
		Map<String, Map<String,String>> broader = ReadXMLFile.DeserializeLLD("F:\\eclipse64\\data\\broader.dat") ;
		
		Map<String,Map<String,List<String>>> trainset = null ; 
		//Map<String, List<String>> titles =  ReadXMLFile.ReadCDR_TestSet_BioC()  ;
		LinkedHashMap<String, Integer> TripleDict  = new LinkedHashMap<String, Integer>();
		Map<String,List<Integer>> Labeling= new HashMap<String,List<Integer>>() ;
		
		MetaMapApi api = new MetaMapApiImpl();
		List<String> theOptions = new ArrayList<String>();
	    //theOptions.add("-y");  // turn on Word Sense Disambiguation
	    theOptions.add("-u");  //  unique abrevation 
	    theOptions.add("--negex");  
	    theOptions.add("-v");
	    theOptions.add("-c");   // use relaxed model that  containing internal syntactic structure, such as conjunction.
	    if (theOptions.size() > 0) {
	      api.setOptions(theOptions);
	    }
	    File fFile = new File("F:\\TempDB\\PMCxxxx\\articals.txt");
	    List<String> sents = readfiles.readLinesbylines(fFile.toURL()); 
		
/*		if (trainset == null )
		{
			trainset = new HashMap<String, Map<String,List<String>>>();
		}*/
		
		Map<String,List<String>> canidateSen  = new HashMap<String, List<String>>();
/*		for(String tit:titles.keySet())
		{
			sents.add(tit) ;
		}*/
		
		
        
		
		for(String tit:sents)
		{
			

			List<String> reltriple = new ArrayList<String>();
			
			// this is optional and not needed here , it used to measure the concepts recall 
			Map<String, List<String>> temptitles = new HashMap<String, List<String>>(); 
			temptitles.put(tit,null) ;
			// get the concepts 
			Map<String, Integer> allconcepts = null ; 
			allconcepts  = ConceptsDiscovery.getconcepts(temptitles,api);
			Map<String, Integer> allconceptstemp =    new HashMap<String, Integer>(allconcepts);
			
			
			// Enrichment  
			Map<String,List<String>> ConcpForms = new HashMap<String, List<String>>();
			Map<String,Integer> wordforms = new HashMap<String, Integer>();
			for (String concp:allconcepts.keySet())
			{
				List<String> form1 = new ArrayList<String>()  ;
				wordforms  = surfaceFormDiscovery.getsurfaceFormLLD(concp);
				for (String form:wordforms.keySet())
				{
					form1.add(form) ;
				}
				ConcpForms.put(concp, form1)  ;
			}
		
			// check if 2 concepts exist in the one relation 
			
			
			String rel = null ; 
			if ( (rel = getrel(ConcpForms, related,allconceptstemp, "Related")) != null)
				reltriple.add(rel)	; 
			
			if ( (rel = getrel(ConcpForms, skosmappingRelation,allconceptstemp, "skosmappingRelation")) != null)
				reltriple.add(rel)	; 
			
			if ( (rel = getrel(ConcpForms, Semanticrelation,allconceptstemp, "Semanticrelation")) != null)
				reltriple.add(rel)	;
			
			if ( (rel = getrel(ConcpForms, narrowerTransitive,allconceptstemp, "narrowerTransitive")) != null)
				reltriple.add(rel)	;
			
			if ( (rel = getrel(ConcpForms, exactMatch,allconceptstemp, "exactMatch")) != null)
				reltriple.add(rel)	;
			
			if ( (rel = getrel(ConcpForms, narrower,allconceptstemp, "narrower")) != null)
				reltriple.add(rel)	;
			
			if ( (rel = getrel(ConcpForms, closeMatch,allconceptstemp,  "closeMatch")) != null)
				reltriple.add(rel)	;
			
			if ( (rel = getrel(ConcpForms, broaderTransitive,allconceptstemp, "broaderTransitive")) != null)
				reltriple.add(rel)	;
			
			if ( (rel = getrel(ConcpForms, broader,allconceptstemp, "broader")) != null)
				reltriple.add(rel)	;
			
			/*getrel(ConcpForms, skosmappingRelation,allconceptstemp, "skosmappingRelation") ;
			getrel(ConcpForms, Semanticrelation,allconceptstemp, "Semanticrelation") ;
			getrel(ConcpForms, narrowerTransitive,allconceptstemp, "narrowerTransitive") ;
			getrel(ConcpForms, exactMatch,allconceptstemp, "exactMatch") ;
			getrel(ConcpForms, narrower,allconceptstemp, "narrower") ;
			
			getrel(ConcpForms, closeMatch,allconceptstemp, "closeMatch") ;
			getrel(ConcpForms, broaderTransitive,allconceptstemp, "broaderTransitive") ;
			getrel(ConcpForms, broader,allconceptstemp, "broader") ;*/
			
			if (reltriple.size() > 0 ) 
			{
				canidateSen.put(tit, reltriple) ;
				ReadXMLFile.Serialized(canidateSen, "F:\\eclipse64\\data\\canidateSen.xml");
			}
			
		}
		
		ReadXMLFile.Serialized(canidateSen, "F:\\eclipse64\\data\\canidateSen.xml");

	}
	
	
	public static String getrel(Map<String,List<String>> ConcpForms,Map<String, Map<String,String>> related,Map<String, Integer> allconcepts, String rellabel)
	{
		for (String concp1: ConcpForms.keySet())
		{

		  // get all semantic concepts of the concept
		  List<String> lixecals  = ConcpForms.get(concp1) ;
			
		  
			for (String lix:lixecals)
			{
				// check if the semantic concept is in one of the relation instance 
				
				Map<String,String> relsenity = related.get(lix) ;
				if (relsenity == null)
					continue ; 
				
				
			//	for (String entity1 :relsenity.keySet())
			//	{
					for (String concp2:allconcepts.keySet())
					{
						if (concp2.equals(concp1))
							continue ; 
						
						List<String> lixe  = ConcpForms.get(concp2) ;
						for (String lixin:lixe)
						{
							
							String str = relsenity.get(lixin) ;
							if (str == null )
								continue  ;
							
							System.out.println(concp1 + ", " + rellabel + ", "  +  concp2   ) ;
							System.out.println(relsenity.get(lix) + ", " + rellabel + ", "  +  relsenity.get(lixin) ) ;
							String relTriple = relsenity.get(lix) + ", " + "Skos:" + rellabel + ", "  +  relsenity.get(lixin) ;
							relTriple  = concp1 + ":" + concp2 + "[" + relTriple ; 
							return relTriple ;
							
						}

					}
					
				//}

			}

		}
		return  null ;
	}

}

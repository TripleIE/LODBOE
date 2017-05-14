package DS;

import gov.nih.nlm.nls.metamap.ConceptPair;
import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Negation;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Position;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetamapConcepts {
	
	public static Map<String, Integer> getconcepts(String sentence, MetaMapApi api) throws Exception
	{
		    sentence = sentence.toLowerCase() ;
			

			Map<String, Integer>  concepttypes = new HashMap<String, Integer>(); 
			
		 	List<Result> resultList = api.processCitationsFromString(sentence);
		    Result result = resultList.get(0);
		    System.out.println(result);
		    List<String> conceptsIdentified = new ArrayList<String>();
		    List<String> actionsIdentified = new ArrayList<String>();
		    List<Negation> negList = result.getNegations();
		    
		    if (negList.size() > 0)
		    {
		      System.out.println("Negations:");
		      for (Negation e: negList) {
		        System.out.println("type: " + e.getType());
		        System.out.print("Trigger: " + e.getTrigger() + ": [");
		        for (Position pos: e.getTriggerPositionList()) {
		          System.out.print(pos  + ",");
		        }
		        System.out.println("]");
		        System.out.print("ConceptPairs: [");
		        for (ConceptPair pair: e.getConceptPairList()) {
		          System.out.print(pair + ",");
		        }
		        System.out.println("]");
		        System.out.print("ConceptPositionList: [");
		        for (Position pos: e.getConceptPositionList()) {
		          System.out.print(pos + ",");
		        }
		        System.out.println("]");
		      }
		    } else {
		    	System.out.println(" None.");
		    }
		    try {
		    	for (Utterance utterance: result.getUtteranceList()) {
		    		System.out.println("Utterance:");
		    		System.out.println(" Id: " + utterance.getId());
		    		System.out.println(" Utterance text: " + utterance.getString());
		    		System.out.println(" Position: " + utterance.getPosition());
		    		for (PCM pcm: utterance.getPCMList()) {
		    			System.out.println("Phrase:");
		    			  System.out.println(" text: " + pcm.getPhrase().getPhraseText());
		    			  System.out.println("Mappings:");
		    	          for (Mapping map: pcm.getMappingList()) {
		    	            System.out.println(" Map Score: " + map.getScore());
		    	            for (Ev mapEv: map.getEvList()) {
		    	              System.out.println("   Score: " + mapEv.getScore());
		    	              System.out.println("   Concept Id: " + mapEv.getConceptId());
		    	              System.out.println("   Concept Name: " + mapEv.getConceptName());
		    	              System.out.println("   Preferred Name: " + mapEv.getPreferredName());
		    	              System.out.println("   Matched Words: " + mapEv.getMatchedWords());
		    	              System.out.println("   Semantic Types: " + mapEv.getSemanticTypes());
		    	              System.out.println("   MatchMap: " + mapEv.getMatchMap());
		    	              System.out.println("   MatchMap alt. repr.: " + mapEv.getMatchMapList());
		    	              System.out.println("   is Head?: " + mapEv.isHead());
		    	              System.out.println("   is Overmatch?: " + mapEv.isOvermatch());
		    	              System.out.println("   Sources: " + mapEv.getSources());
		    	              System.out.println("   Positional Info: " + mapEv.getPositionalInfo());
		    	              System.out.println("   Negation Info: " + mapEv.getNegationStatus());

		    	              if(mapEv.getNegationStatus() != 1 && mapEv.getScore() <= -700 )
		    	              {
		    	            	  List<String> s = mapEv.getMatchedWords() ; //mapEv.getConceptName(); 
		    	            	  Position pos =  mapEv.getPositionalInfo().get(0) ;
		    	            	  String entity = sentence.substring(pos.getX(), pos.getY()+pos.getX()) ;
         	            	      conceptsIdentified.add(entity .toLowerCase());
		    	              }
		    	              
		    	              
		    	            }
		    		}
		    	}
				
			}
		    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    for(int i = 0; i< conceptsIdentified.size(); i++)
	        {
	      	  System.out.println(conceptsIdentified.get(i));
	      	  concepttypes.put(conceptsIdentified.get(i).toLowerCase(), 1) ;
	        }
		    
		    return concepttypes ;
	}
	
	
}

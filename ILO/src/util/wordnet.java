package util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class wordnet {
	
	
	 private static ILexicalDatabase db = new NictWordNet();
//     private static RelatednessCalculator[] rcs = {
//                     new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
//                     new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
//                     };

	 public static void main(String[] args)
	 {
		 lesk( "Antiandrogenic therapy can cause coronary arterial disease", "Antiandrogenic therapy can cause coronary arterial disease" ) ;
	 }
	 private static RelatednessCalculator[] rcs = {
		 new WuPalmer(db)
      };
    public static double lesk( String word1, String word2 ) {
             WS4JConfiguration.getInstance().setMFS(true);
             WS4JConfiguration.getInstance().setStem(true);
             Map<String,Integer> scornor = new HashMap<String, Integer>();
             //System.out.println( word1 + "\t" + word2);
             
             //WS4JConfiguration.getInstance().setStem(true);
             double s ; 
             String tokens1[] = word1.split(" ") ;
             String tokens2[] = word2.split(" ") ;
             double raowtot = 0 ; 
             double score  = 0 ; 
             for ( RelatednessCalculator rc : rcs )
             {
	             for (String token1 : tokens1 )
	             {
	            	 raowtot = 0 ; 
	            	 for (String token2 : tokens2 )
	            	 {
	                     s = rc.calcRelatednessOfWords(token1,token2);
	                     if (s > 1)
	                     {
	                    	 s = 1 ; 
	                     }
	                     
	                     raowtot += s ;
	                     System.out.println(s );
	                     scornor.put(token2, 1) ;
	            	 }	 
	            	 score += raowtot ;
	            	 scornor.put(token1, 1) ;
	             }
             } 
             
            score =  score/scornor.size() ;
            
            return score ; 

     }
}

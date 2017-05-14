package util;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Lin;
//import edu.cmu.lti.ws4j.impl.HirstStOnge;
//import edu.cmu.lti.ws4j.impl.JiangConrath;
//import edu.cmu.lti.ws4j.impl.LeacockChodorow;
//import edu.cmu.lti.ws4j.impl.Lesk;
//import edu.cmu.lti.ws4j.impl.Lin;
//import edu.cmu.lti.ws4j.impl.Path;
//import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class SimilarityCalculationDemo {
	
	 private static ILexicalDatabase db = new NictWordNet();
//     private static RelatednessCalculator[] rcs = {
//                     new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
//                     new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
//                     };
    
//	 private static RelatednessCalculator[] rcs = {
//		 new WuPalmer(db)
//      };
	 private static RelatednessCalculator[] rcs = {
		 new Lin(db)
      };
    public double run( String word1, String word2 ) {
             WS4JConfiguration.getInstance().setMFS(true);
             WS4JConfiguration.getInstance().setStem(true);
             //System.out.println( word1 + "\t" + word2);
             
             //WS4JConfiguration.getInstance().setStem(true);
             double s = 0 ; 
             for ( RelatednessCalculator rc : rcs ) {
                     s = rc.calcRelatednessOfWords(word1, word2);
                     if ( s > 1 )
                     {
                    	 s = 1 ;
                     }
                //     System.out.print( rc.getClass().getName()+"\t"+s );
             }
             
             return s ; 
     }
}

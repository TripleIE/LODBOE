package TextProcess;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import TextProcess.removestopwords;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefAnnotation;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class NLPEngine {
	
    // segmentation function 
	// return list of 
    // using Stanford APIs 
	public static StanfordCoreNLP pipeline ;
	public List<String> getSentences(String text)
	{
		
	    Reader reader = new StringReader(text);
	    DocumentPreprocessor docprocess = new DocumentPreprocessor(reader);
	    List<String> sentenceList = new ArrayList<String>();

	    for (List<HasWord> sentence : docprocess) {
	       String sentenceString = Sentence.listToString(sentence);
	       sentenceList.add(sentenceString.toString());
	    }
        
	    return sentenceList ;
	    
	}

	public Tree  getParseTreeSentence(String sentance)
	{
		
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	     props.put("pos.model", "E:\\Ptriple\\Vendor\\parser\\english-left3words-distsim.tagger");
	  //  props.put("pos.model", "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Everything2III\\english-left3words-distsim.tagger");
	    props.put("ner.model", "E:\\Ptriple\\Vendor\\parser\\english.all.3class.caseless.distsim.crf.ser.gz");
	    props.put("parse.model", "E:\\Ptriple\\Vendor\\parser\\englishPCFG.caseless.ser.gz");
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
	   
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(sentance);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types

	 // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree tree = null ; 
	    for(CoreMap sentence: sentences)
	    {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) 
	      {
	    	 
	        // this is the text of the token
	        String word = token.get(LemmaAnnotation.class);
	        
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);  
	        
	        String coref = token.get(CorefAnnotation.class);
	        
	        // this is the parse tree of the current sentence
	      }
	      
	      tree = sentence.get(TreeAnnotation.class);
	      break ;
	    }
		return tree;

    
	}
	
	public static  String  getLemma(String sentance)
	{
		  
		String lemma = " " ;
	    StanfordCoreNLP pipeline = getStanfordCoreNLP() ; 
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(sentance);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types

	 // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree tree = null ; 
	    for(CoreMap sentence: sentences)
	    {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) 
	      {
	    	 
	        // this is the text of the token
	    	  lemma  += " " + token.get(LemmaAnnotation.class);    
	      }
	      break ;
	    }
		return lemma;

    
	}
	
	
	public static StanfordCoreNLP  getStanfordCoreNLP()
	{
		
		if (pipeline == null)
		{
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	     props.put("pos.model", "E:\\Ptriple\\Vendor\\parser\\english-left3words-distsim.tagger");
	  //  props.put("pos.model", "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Everything2III\\english-left3words-distsim.tagger");
	    props.put("ner.model", "E:\\Ptriple\\Vendor\\parser\\english.all.3class.caseless.distsim.crf.ser.gz");
	    props.put("parse.model", "E:\\Ptriple\\Vendor\\parser\\englishPCFG.caseless.ser.gz");
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
	   
	    	pipeline = new StanfordCoreNLP(props);
		}
	    
	    return pipeline ;

    
	}
	
	public static Tree  gettree(StanfordCoreNLP pipeline,String sentance)
	{
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(sentance);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types

	 // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree tree = null ; 

		    for(CoreMap sentence: sentences)
		    {
		      tree = sentence.get(TreeAnnotation.class);
		      break ;
		    }
		return tree;

    
	}
	
	
	// returns all nouns in tree
	
	public static void  getNoun(Tree t, List<String> nouns) {
         
	    // NN Noun, singular or mass 
	    // NNS Noun, plural 
	    //  NNP Proper noun, singular 
	    //  NNPS Proper noun, plural
	    
	    if (t.label().value().equals("NN") || t.label().value().equals("NNS") ||
	    		t.label().value().equals("NNP") || t.label().value().equals("NNPS"))
	        nouns.add(t.firstChild().value());
	    else
	        for (Tree child : t.children())
	            getNoun(child,nouns);    
	}
	
	// return all verbs in tree
	public static void  getVerbs(Tree t, List<String> verbs) {
        

		removestopwords stopwords = new removestopwords() ;
	    if (t.label().value().equals("VB") || t.label().value().equals("VBD") ||
	    		t.label().value().equals("VBG") || t.label().value().equals("VBN") || t.label().value().equals("VBP") || t.label().value().equals("VBZ"))
	    {
	    	if (! stopwords.removestopwordsingle(t.firstChild().value()))
	    		verbs.add(t.firstChild().value());
	    }
	    else
	        for (Tree child : t.children())
	        	getVerbs(child,verbs);    
	}

	
	public void  findConsecutiveNouns(Tree t, List<String> nouns) {
        
	    // NN Noun, singular or mass 
	    // NNS Noun, plural 
	    //  NNP Proper noun, singular 
	    //  NNPS Proper noun, plural
		if (t == null)
		{
			return  ; 
		}
		int count = 0 ; 
		String entity = "" ;
		for (Tree child : t.children())
		{
			   if (child.label().value().equals("NN") || child.label().value().equals("NNS") ||
					   child.label().value().equals("NNP") || child.label().value().equals("NNPS")|| child.label().value().equals("JJ"))
			   {
				   entity  += (child.firstChild().value() + " ") ;
				   count++ ;
			   }
			       
		}
		 if (count > 1 )
			 nouns.add(entity);
		
		{
			for (Tree child : t.children())
			  findConsecutiveNouns(child,nouns);
		}
	}
    




	
	
}
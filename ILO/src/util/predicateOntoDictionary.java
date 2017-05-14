package util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class predicateOntoDictionary {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File file = new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\TripleExtraction\\linked lifedata predicate.txt");

			String[] pre = readfiles.readLines(file.toURL());
			
		    
			Map<String, Integer> intoDic = new HashMap<String, Integer>() ;
			
			for (String predicate : pre)
			{
				intoDic.put(predicate, 1) ;
			}
			
			
			List<String> properties  = new ArrayList<String>()  ;
			for (String termr : intoDic.keySet())
			{
				properties.add(termr);
			}
			
			Collections.sort(properties);

			ReadXMLFile.Serializeddir(properties, "F:\\eclipse64\\eclipse\\intoDictionary");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public static List<String> getPredicatDictionary() {
		
		List<String> dir = ReadXMLFile.Deserializedirlis("F:\\eclipse64\\eclipse\\intoDictionary") ;
		
		
		// TODO Auto-generated method stub
		return dir;
	}
	
	
	

}

package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException ;





import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;


public class parseJson {
	
	
	public static void getSenses(String Jsonstring) throws MalformedURLException, IOException, ParseException 
	{
			

		
		File filename  = new File("E:\\Freepal\\freepal-dataset.json") ;
        Reader fileReader = new InputStreamReader(filename.toURL().openStream(), Charset.forName("UTF-8"));
        List<String> lines;
        BufferedReader bufferedReader = new BufferedReader(fileReader);
		JSONParser parser = new JSONParser();
        try 
        {
            lines = new ArrayList<String>();
            String line;
            System.out.println("Start");
            while ((line = bufferedReader.readLine()) != null) {
              //  lines.add(line);
	            Reader targetReader = new StringReader(line);
	            Object obj = parser.parse(targetReader);
	            JSONObject jsonObject =  (JSONObject) obj;
	            
	            String name = (String) jsonObject.get("entropy").toString();
/*	            if(Double.parseDouble(name) > 3)
	            	System.out.println(Double.parseDouble(name));*/
	            String rel = (String) jsonObject.get("toprelation").toString();
	            if(rel.toLowerCase().contains("medicine") && Double.parseDouble(name) > 2.0)
	            {
	            	String feature = (String) jsonObject.get("feature").toString();
	            	System.out.print(feature);
	            	System.out.print("!");
	            	System.out.println(rel);
	            	//System.out.println(Double.parseDouble(name));
	            }
	            
	            
            }
            System.out.println("done");
        }
        finally
        {
        }
		
	    }
	


	  @SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		  
		  getSenses(null) ; 
	  }

	  
	}


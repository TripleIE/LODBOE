package util;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadXMLFile { 
    
    

    
    
    public static void ReadLLD(String filename) {

        try {

    	File fXmlFile = new File(filename);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(fXmlFile);

    	doc.getDocumentElement().normalize();
    	Map<String, Map<String,String>> list = new HashMap<String, Map<String,String>>();
    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList resultnodes =  doc.getElementsByTagName("result") ; 
        for (int i = 0; i < resultnodes.getLength()  ; i++)  
        {
        	System.out.println( resultnodes.item(i).getNodeName()); 
        	Node nNode = resultnodes.item(i) ;
        	if (nNode.getNodeType() == Node.ELEMENT_NODE)
        	{
        		 Element eElement = (Element) nNode;
        		 NodeList Bnodes =  eElement.getElementsByTagName("binding") ;
     			 String Concept1 = null ; 
    			 String Concept2 = null ; 
    			 String labelconcept1  = null;
    			 String labelconcept2 = null; 
        		 for (int ii = 0; ii < Bnodes.getLength()  ; ii++)
        		 {
   
        			 
        			 Node nBNode = Bnodes.item(ii) ; // binding
        			 Element eBElement = (Element) nBNode;
        			 String Label = eBElement.getAttribute("name"); 
        			 
        			 if(Label.equals("concept1"))
        			 {
        				 Concept1 =  eBElement.getTextContent().trim(); 
        			 }
        			 
        			 if(Label.equals("concept2"))
        			 {
        				 Concept2 =  eBElement.getTextContent().trim(); 
        			 }
        			 
           			 if(Label.equals("labelconcept1"))
        			 {
           				labelconcept1 =  eBElement.getTextContent().trim(); 
        			 }
           			 
           			 if(Label.equals("labelconcept2"))
        			 {
           				labelconcept2 =  eBElement.getTextContent().trim(); 
        			 }	 
        		 }
    			 
        		 Map<String,String> nlist = list.get(labelconcept1) ;
    			 if (nlist == null )
    			 {
    				  nlist = new HashMap<String,String>();  
    				  nlist.put(labelconcept2,Concept2) ; 
    				  nlist.put(labelconcept1,Concept1) ;
    				  list.put(labelconcept1, nlist) ;
    			 }
    			 else
    			 {
    				 nlist.put(labelconcept2,Concept2) ;
    				 list.put(labelconcept1, nlist) ;
    			 }
    			 
    			 Map<String,String> nnlist = list.get(labelconcept2) ;
    			 if (nnlist == null )
    			 {
    				  nnlist = new HashMap<String,String>();  
    				  nnlist.put(labelconcept1,Concept1) ;
    				  nlist.put(labelconcept2,Concept2) ;
    				  list.put(labelconcept2, nlist) ;
    			 }
    			 else
    			 {
    				 nnlist.put(labelconcept1,Concept1) ; 
    				 list.put(labelconcept2, nlist) ;
    			 }
        		 
        	}
        	
            
        }
        	SerializedLLD(list,"F:\\eclipse64\\data\\skosmappingRelation.dat") ;
        } catch (Exception e) {
    	e.printStackTrace();
 
        }

        
        
      }  
  public static String Read(String filename) {

    try {

	File fXmlFile = new File(filename);
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);

	//optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
    String text =  doc.getElementsByTagName("TEXT").item(0).getTextContent() ; 
	System.out.println( doc.getElementsByTagName("TEXT").item(0).getTextContent());
	text = text.replaceAll("\n", " ") ;
	System.out.println("----------------------------");
	 return text ;
	 
    } catch (Exception e) {
	e.printStackTrace();
	 return null ; 
    }
    
  }

  public static Map<String, List<String>> ReadCDR_TestSet_BioC() {

	    Map<String, List<String>> goldstandard = null ;
	    goldstandard =  null ; //Deserialize("F:\\eclipse64\\eclipse\\goldstandardtest6") ;
	    if (goldstandard== null )
	    try {

       String filename = "F:\\eclipse64\\eclipse\\CDR_TestSet.BioC.xml" ;
       // String filename = "F:\\eclipse64\\eclipse\\CDR_TestSet.BioCtest.xml" ;

	    goldstandard  = new HashMap<String, List<String>>();
		File fXmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		NodeList passageList = doc.getElementsByTagName("passage");

		for (int i = 0; i < passageList.getLength()  ; i++)
		{
			List<String> conclist = new ArrayList<String>() ;
			NodeList childList = passageList.item(i).getChildNodes();
			for (int j = 0; j < childList.getLength(); j++)
			{
				if ("infon".equals(childList.item(j).getNodeName()) && "title".equals(childList.item(j).getTextContent()))
				{
					String title = null ; 
					NodeList childList1 = passageList.item(i).getChildNodes();
					for (int kk = 0; kk < childList1.getLength(); kk++)
					{
						 System.out.println(childList1.item(kk).getNodeName());
						if ("text".equals(childList1.item(kk).getNodeName()))
						{
							 System.out.println(childList1.item(kk).getTextContent()
					                    .trim());
							title = childList1.item(kk).getTextContent()
				                    .trim().toLowerCase() ;
						}
						
						if ("annotation".equals(childList1.item(kk).getNodeName()))
						{
							NodeList childList2 = childList.item(kk).getChildNodes();
							for (int kkk = 0; kkk < childList2.getLength(); kkk++)
							{
								if ("text".equals(childList2.item(kkk).getNodeName()))
								{
									 conclist.add(childList2.item(kkk).getTextContent().trim().toLowerCase()) ;
									 System.out.println(childList2.item(kkk).getTextContent()
							                    .trim());

								}
							}

						}
					}
					
					goldstandard.put(title,conclist) ;
					
				}
			}
           
		}
			Serialized(goldstandard,"F:\\eclipse64\\eclipse\\goldstandardtest6") ;
	    } 
	    catch (Exception e) {
		e.printStackTrace();
	    }
	    return goldstandard;
	  } ;
  
	  
	  public static Map<String, List<String>> ReadCDR_TestSet_BioCDisease() {

		    Map<String, List<String>> goldstandard = null ;
		    goldstandard =  Deserialize("F:\\eclipse64\\eclipse\\Diseaselist") ;
		    if (goldstandard== null )
		    try {

	        String filename = "F:\\eclipse64\\eclipse\\CDR_TestSet.BioC.xml" ;
	       // String filename = "F:\\eclipse64\\eclipse\\CDR_TestSet.BioCtest.xml" ;
		    goldstandard  = new HashMap<String, List<String>>();
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	        
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList passageList = doc.getElementsByTagName("passage");
			int count  = 0 ; 
			for (int i = 0; i < passageList.getLength()  ; i++)
			{
				List<String> conclist = new ArrayList<String>() ;
				NodeList childList = passageList.item(i).getChildNodes();
				for (int j = 0; j < childList.getLength(); j++)
				{
					if ("infon".equals(childList.item(j).getNodeName()) && "title".equals(childList.item(j).getTextContent()))
					{
						String title = null ; 
						NodeList childList1 = passageList.item(i).getChildNodes();
						for (int kk = 0; kk < childList1.getLength(); kk++)
						{
							 System.out.println(childList1.item(kk).getNodeName());
							if ("text".equals(childList1.item(kk).getNodeName()))
							{
								 System.out.println(childList1.item(kk).getTextContent()
						                    .trim());
								title = childList1.item(kk).getTextContent()
					                    .trim().toLowerCase() ;
							}
							
							if ("annotation".equals(childList1.item(kk).getNodeName()))
							{
								NodeList childList2 = childList.item(kk).getChildNodes();
								Boolean found = false ; 
								for (int kkk = 0; kkk < childList2.getLength(); kkk++)
								{
									if ("infon".equals(childList2.item(kkk).getNodeName()))
									{
										// conclist.add(childList2.item(kkk).getTextContent().trim().toLowerCase()) ;
									//	 System.out.println(childList2.item(kkk).getTextContent()
								     //               .trim());
										 if ("Disease".equals(childList2.item(kkk).getTextContent().trim()))
										 {
											 found = true ; 

												System.out.println(childList2.item(kkk).getTextContent()
											                  .trim());
										 }
										 
									}
									if ("text".equals(childList2.item(kkk).getNodeName()) && found)
									{
										 conclist.add(childList2.item(kkk).getTextContent().trim().toLowerCase()) ;
										 System.out.println(childList2.item(kkk).getTextContent().toLowerCase()
								                    .trim());
										 count++ ;
										 found = false ;
									}
								}

							}
						}
						
						goldstandard.put(title,conclist) ;
						
					}
				}
	           
			}
				Serialized(goldstandard,"F:\\eclipse64\\eclipse\\Diseaselist") ;
		    } 
		    catch (Exception e) {
			e.printStackTrace();
		    }
		    return goldstandard;
		  } ;
  
		public static  void Serializedsrc(Map<String, Dataset> dictionary,String fileout) throws IOException
		 {

		     try
		     {
		    	 // Create output stream.
		         FileOutputStream fileOut =
		         new FileOutputStream(fileout);
		         ObjectOutputStream out = new ObjectOutputStream(fileOut);
		         out.writeObject(dictionary);
		         out.close();
		         fileOut.close();
		         System.out.printf("Serialized data is saved in" + fileout);
		     }catch(IOException i)
		     {
		          i.printStackTrace();
		     }
		 }  
		
		 @SuppressWarnings("resource")
		public static  Map<String, Dataset> Deserializersrc(String fileout) 
		   {
			       Map<String, Dataset> dictionary ;
				   FileInputStream fileIn;
				try {
					fileIn = new FileInputStream(fileout);
					
	
			        ObjectInputStream in = new ObjectInputStream(fileIn);
			        dictionary = (Map<String, Dataset>)in.readObject();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			      return dictionary;
		   }
	  
			public static  void Serializeddir(List<String> dictionary,String fileout) throws IOException
			 {

			     try
			     {
			    	 // Create output stream.
			         FileOutputStream fileOut =
			         new FileOutputStream(fileout);
				     // Create XML encoder.
				     XMLEncoder xenc = new XMLEncoder(fileOut);
			
				     // Write object.
				     xenc.writeObject(dictionary);
				     xenc.close();
			         fileOut.close();
			         System.out.printf("Serialized data is saved in" + fileout);
			     }catch(IOException i)
			     {
			          i.printStackTrace();
			     }
			 }  
		public static  void Serializeddir(Map<String, Integer> dictionary,String fileout) throws IOException
		 {

		     try
		     {
		    	 // Create output stream.
		         FileOutputStream fileOut =
		         new FileOutputStream(fileout);
			     // Create XML encoder.
			     XMLEncoder xenc = new XMLEncoder(fileOut);
		
			     // Write object.
			     xenc.writeObject(dictionary);
			     xenc.close();
		         fileOut.close();
		         System.out.printf("Serialized data is saved in" + fileout);
		     }catch(IOException i)
		     {
		          i.printStackTrace();
		     }
		 }  
		 public static  List<String> Deserializedirlis(String fileout)
		   {
			 	   List<String> dictionary ;
				   FileInputStream fileIn;
				try {
					fileIn = new FileInputStream(fileout);
				
				   XMLDecoder decoder =  new XMLDecoder(fileIn);
				   dictionary  = (List<String>)decoder.readObject();
				   decoder.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			      return dictionary;
		   }
		 public static  Map<String, Integer> Deserializedir(String fileout)
		   {
			       Map<String, Integer> dictionary ;
				   FileInputStream fileIn;
				try {
					fileIn = new FileInputStream(fileout);
				
				   XMLDecoder decoder =  new XMLDecoder(fileIn);
				   dictionary  = (Map<String, Integer>)decoder.readObject();
				   decoder.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			      return dictionary;
		   }
		 
			public static  void Serializedlabel(Map<String, List<Integer>> dictionary,String fileout) throws IOException
			 {

			     try
			     {
			    	 // Create output stream.
			         FileOutputStream fileOut =
			         new FileOutputStream(fileout);
				     // Create XML encoder.
				     XMLEncoder xenc = new XMLEncoder(fileOut);
			
				     // Write object.
				     xenc.writeObject(dictionary);
				     xenc.close();
			         fileOut.close();
			         System.out.printf("Serializedlabel data is saved in" + fileout);
			     }catch(IOException i)
			     {
			          i.printStackTrace();
			     }
			 }	
			
			 public static  Map<String, List<Integer>> Deserializedirlabel(String fileout)
			   {
				 Map<String, List<Integer>> dictionary ;
					   FileInputStream fileIn;
					try {
						fileIn = new FileInputStream(fileout);
					
					   XMLDecoder decoder =  new XMLDecoder(fileIn);
					   dictionary  = (Map<String, List<Integer>>)decoder.readObject();
					   decoder.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				      return dictionary;
			   }
	public static  void Serialized(Map<String, List<String>> dictionary,String fileout) throws IOException
	 {

	     try
	     {
	    	 // Create output stream.
	         FileOutputStream fileOut =
	         new FileOutputStream(fileout);
		     // Create XML encoder.
		     XMLEncoder xenc = new XMLEncoder(fileOut);
	
		     // Write object.
		     xenc.writeObject(dictionary);
		     xenc.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in" + fileout);
	     }catch(IOException i)
	     {
	          i.printStackTrace();
	     }
	 }
	
	
	
	public static  void SerializedLLD(Map<String, Map<String, String>> list,String fileout) throws IOException
	 {

	     try
	     {
	    	 // Create output stream.
	         FileOutputStream fileOut =
	         new FileOutputStream(fileout);
		     // Create XML encoder.
		     XMLEncoder xenc = new XMLEncoder(fileOut);
	
		     // Write object.
		     xenc.writeObject(list);
		     xenc.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in" + fileout);
	     }catch(IOException i)
	     {
	          i.printStackTrace();
	     }
	 }
	
	 public static  Map<String, Map<String, String>> DeserializeLLD(String fileout)
	   {
		     HashMap<String, Map<String, String>> dictionary ;
			   FileInputStream fileIn;
			try {
				fileIn = new FileInputStream(fileout);
			
			   XMLDecoder decoder =  new XMLDecoder(fileIn);
			   dictionary  = (HashMap<String, Map<String, String>>) decoder.readObject();
			   decoder.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		      return dictionary;
	   }
	
	public static  void Serialized(HashMap<String, Map<String, List<String>>> dictionary,String fileout) throws IOException
	 {

	     try
	     {
	    	 // Create output stream.
	         FileOutputStream fileOut =
	         new FileOutputStream(fileout);
		     // Create XML encoder.
		     XMLEncoder xenc = new XMLEncoder(fileOut);
	
		     // Write object.
		     xenc.writeObject(dictionary);
		     xenc.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in" + fileout);
	     }catch(IOException i)
	     {
	          i.printStackTrace();
	     }
	 }
	
	public static  void SerializedT(Map<String, Map<String, List<String>>> dictionary,String fileout) throws IOException
	 {

	     try
	     {
	    	 // Create output stream.
	         FileOutputStream fileOut =
	         new FileOutputStream(fileout);
		     // Create XML encoder.
		     XMLEncoder xenc = new XMLEncoder(fileOut);
	
		     // Write object.
		     xenc.writeObject(dictionary);
		     xenc.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in" + fileout);
	     }catch(IOException i)
	     {
	          i.printStackTrace();
	     }
	 }
	 public static  Map<String, Map<String, List<String>>> DeserializeT(String fileout)
	   {
		       Map<String, Map<String, List<String>>> dictionary ;
			   FileInputStream fileIn;
			try {
				fileIn = new FileInputStream(fileout);
			
			   XMLDecoder decoder =  new XMLDecoder(fileIn);
			   dictionary  = (Map<String, Map<String, List<String>>>) decoder.readObject();
			   decoder.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		      return dictionary;
	   }
	 public static  Map<String, List<String>> Deserialize(String fileout)
	   {
			   Map<String, List<String>> dictionary ;
			   FileInputStream fileIn;
			try {
				fileIn = new FileInputStream(fileout);
			
			   XMLDecoder decoder =  new XMLDecoder(fileIn);
			   dictionary  = (Map<String, List<String>>)decoder.readObject();
			   decoder.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		      return dictionary;
	   }
	 

	}
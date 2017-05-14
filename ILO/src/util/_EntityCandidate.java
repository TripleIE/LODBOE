package util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class _EntityCandidate implements java.io.Serializable {
	public String Resource ; 
	public Map<String, String>  Labels ; 
	public Map<String, String>  altLabels ;
	public Map<String, String> definitions ;
	public Map<String, String> types ;
	public Map<String, Integer>  relation ;
	public Map<String, Integer>  endentity ;
	public Map<String,Integer>  concepts ;
	public double sim  ;
	public int count  ;
	public double mean  ;
 	
	
	public _EntityCandidate()
	{
		 Labels = new HashMap<String, String>();
		 altLabels = new HashMap<String, String>();
		 definitions = new HashMap<String, String>();
		 types  = new HashMap<String, String>();
		 relation = new HashMap<String, Integer>();
		 endentity = new HashMap<String, Integer>();
		 concepts = new HashMap<String, Integer>();
		 sim = 0 ; 
		 count = 0 ;
		 
	}


}
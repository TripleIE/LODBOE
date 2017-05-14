package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class _Entity implements java.io.Serializable {
	public Map<String, String>  Labels ; 
	public Map<String, String>  altLabels ;
	public Map<String, String> definitions ;
	public Map<String, String> types ;
	public Map<String, Integer>  relation ;
	public Map<String, Integer>  endentity ;
 	
	
	public _Entity()
	{
		 Labels = new HashMap<String, String>();
		 altLabels = new HashMap<String, String>();
		 definitions = new HashMap<String, String>();
		 types  = new HashMap<String, String>();
		 relation = new HashMap<String, Integer>();
		 endentity = new HashMap<String, Integer>();
	}


}
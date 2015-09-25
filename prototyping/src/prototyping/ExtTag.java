package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.*;
import java.util.*;

import prototyping.PlayList.MSG;



public class ExtTag {
	protected PlayList containingList;
	protected String containingListName;
	protected String myLine;
	protected int myLineNumber;
	public String myTagName;
	protected PlayListScanner playListScanner;
	public int errParanoid = 0;
	public int traceParanoid = 0;
	protected boolean validated;
	private static	Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { {Tokens.EXTM3U, "EXTM3U"}, 
												{Tokens.EXT_X_VERSION, "EXT_X_VERSION"} 
											  };	
	
	ExtTag(PlayList playList, PlayListScanner scanner, String tagName) {
		// parent reference
		containingList = playList;
		playListScanner = scanner;
		myLineNumber = playListScanner.currLineNum;
		myLine = playListScanner.currLine;
		containingListName = containingList.myURL;
		myTagName = tagName;
		validated = false;
	}
	
	public boolean IsValid() { return validated;}
	
	public String toString() {
		return myTagName;
	}
	
	public String Location(){
		return (containingList.toString() + "::LINE::" + Integer.toString(myLineNumber) +
				"::Tag::" + myTagName);
	}
	
	public String Context(){
		String context = new String("Context:");
		context += context + Thread.currentThread().getStackTrace()[2].getFileName();
		context += "::" + Thread.currentThread().getStackTrace()[2].getClassName();
		context += "::" + Thread.currentThread().getStackTrace()[2].getMethodName();
		context += "::Line:" + Thread.currentThread().getStackTrace()[2].getLineNumber();
		return context;
	}
	
	String GetTimeStamp(){
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}
	
	public static ExtTag Clone(PlayList playList, PlayListScanner scanner, String tagName){
		ExtTag clone = new ExtTag(playList, scanner, tagName);
		return clone;
	}
	
	public static void Initialize() throws NoSuchMethodException, SecurityException{
		// load my map  validator.class.getMethod()
		for (String validator[] : validatorList)
		     //validatorMap.put(validator[0], ExtTag.class.getMethod(validator[1]));
			validatorMap.put(validator[0], ExtTag.class.getDeclaredMethod(validator[1]));
		//validatorMap.put(validator[0], ExtTag.class.getMethod("EXTM3U",Class<ExtTag>,ExtTag.class));
		// load leaf classes maps
		MasterListExtTag.Initialize();
		MediaListExtTag.Initialize();
	}
	
	// utility methods
	public static String GetCandidateTag(String line){
		int beginIndex = line.indexOf(Tokens.tagBegin);
		int endIndex = line.indexOf(Tokens.tagEnd);
		if (beginIndex==0 && endIndex > 0){
			// normal case, tag with endTag delimiter, no tagBegin
			return line.substring(beginIndex+1 ,endIndex);
		}
		if (beginIndex < 0){
			// something is wrong, probably shouldn't be here, return empty string
			return "";
		}
		// possibly a tag with no endTag delimiter, just return (trimmed, no tagBegin) line
		return line.substring(beginIndex+1).trim();
	}
	
	public static boolean HasValidator(String tagName){
		return (validatorMap.containsKey(tagName));
	}
	

	//public static void Validate(ExtTag This, String tagName) throws Exception, IllegalArgumentException, InvocationTargetException{
	public void Validate(String tagName) throws Exception, IllegalArgumentException, InvocationTargetException{	
	//validatorMap.get(tagName).invoke(This);
		if (HasValidator(tagName))
			 validatorMap.get(tagName).invoke(this, (Object[])null);
	}
		
	//private static void EXTM3U(ExtTag This)
	private void EXTM3U(){
		validated = true;
	}
	
	//private static void EXT_X_VERSION(ExtTag This)
	private void EXT_X_VERSION(){
		
		validated = true;
	}
	
	// logging utils at ExtTag level
	// More logging stuff/utilities

	// some wrappers to make code reading easier, would be simpler
    // if java let you overload operators
//    public class MSG{
//    	private String[] fields;
//    	//private String[] fixedFields;
//    	
//    	public MSG(String... infields){
//    		fields[0] =  Integer.toString(myLineNumber);
//    		fields[1] = myTagName;
//    		int i = 1;
//    		for (String field : infields){
//    			fields[i++] = new String(field);
//    		}
//    	}
//    }
    
    public class MSG{
    	private ArrayList<String> fields;
    	
    	public MSG(String... infields){
    		fields = new ArrayList<String>();
    		//fields.add(Integer.toString(myLineNumber));
    		//fields.add(myTagName);
    		for (String field : infields){
    			fields.add(field);
    		}
    	}
    	
	   	// for contained
    	public MSG(ArrayList<String> infields){
	       		fields = new ArrayList<String>();
	    		//fields.add(Integer.toString(myLineNumber));
	    		//fields.add(myTagName);
	    		for (String field : infields){
	    			fields.add(field);
	    		}
    	}
    }
    
 	// for use at this level	
	public void LogStreamError(MSG msg){
		containingList.LogStreamError(msg.fields);
	}

	public void LogTrace(MSG msg){
		containingList.LogTrace(msg.fields);
	}
	
	public void LogStreamError(MSG msg, int paranoid){
		containingList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(MSG msg, int paranoid){
		containingList.LogTrace(msg.fields, paranoid);
	}
	
	// for contained levels
	public void LogStreamError(ArrayList<String> fields){
		MSG msg = new MSG(fields);
		containingList.LogStreamError(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields){
		MSG msg = new MSG(fields);
		containingList.LogTrace(msg.fields);
	}
	
	public void LogStreamError(ArrayList<String> fields, int paranoid){
		MSG msg = new MSG(fields);
		containingList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(ArrayList<String> fields, int paranoid){
		MSG msg = new MSG(fields);
		containingList.LogTrace(msg.fields, paranoid);
	}
	
	
}



package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;
import java.util.*;



public class ExtTag {
	protected PlayList containingList;
	protected String containingListName;
	protected String myLine;
	protected int myLineNumber;
	public String myTagName;
	protected PlayListScanner playListScanner;
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
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		containingList.mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		containingList.mediaStream.LogRunError(fields, paranoidLevel);
	}
	
	public static boolean IsExtTag(String candidate){
		// check if in map of tag strings x tag validator interfaces
		return false;
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
			// normal case, tag with endTag delimiter
			return line.substring(beginIndex ,endIndex);
		}
		if (beginIndex < 0){
			// something is wrong, probably shouldn't be here, return empty string
			return "";
		}
		// possibly a tag with no endTag delimiter, just return (trimmed) line
		return line.substring(beginIndex).trim();
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
		String[] msg = {"Error Number", "Error Type", "File Name", "Line Number", "Details"};
		LogRunError(msg, 20);
		validated = false;
	}
	
	//private static void EXT_X_VERSION(ExtTag This)
	private void EXT_X_VERSION(){
		
		validated = false;
	}
}

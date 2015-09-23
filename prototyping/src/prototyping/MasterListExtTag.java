package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class MasterListExtTag extends ExtTagStream {

	private static	Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { {Tokens.EXT_X_STREAM_INF, "EXT_X_STREAM_INF"}, 
												{Tokens.EXT_X_MEDIA, "EXT_X_MEDIA"},
											   };
	
	MasterListExtTag(PlayList playList, PlayListScanner scanner, String tagName, String url) throws MalformedURLException{
		super(playList, scanner, tagName, url);
	}
	
	// use this one to wait for validate to download
	MasterListExtTag(PlayList playList, PlayListScanner scanner, String tagName) throws MalformedURLException{
		super(playList, scanner, tagName);
	}
	
	
	//public static MasterListExtTag Clone(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
	//	MasterListExtTag clone = new MasterListExtTag(playList, scanner, url, tagName);
	//	return clone;
	//}
	
	public static void Initialize() throws NoSuchMethodException, SecurityException{
		// load my map  validator.class.getMethod()
		for (String validator[] : validatorList)
		     //validatorMap.put(validator[0], ExtTag.class.getMethod(validator[1]));
			validatorMap.put(validator[0], MasterListExtTag.class.getDeclaredMethod(validator[1]));
		//validatorMap.put(validator[0], ExtTag.class.getMethod("EXTM3U",Class<ExtTag>,ExtTag.class));
	}
	
	public static boolean HasValidator(String tagName){
		return (validatorMap.containsKey(tagName));
	}
	
	public static boolean HasStream(String tagName){
		return (HasValidator(tagName) && (tagName == Tokens.EXT_X_STREAM_INF));
	}
	
	//public static void Validate(ExtTag This, String tagName) throws Exception, IllegalArgumentException, InvocationTargetException{
	public void Validate(String tagName) throws Exception, IllegalArgumentException, InvocationTargetException{	
	//validatorMap.get(tagName).invoke(This);
		if (HasValidator(tagName))
			 validatorMap.get(tagName).invoke(this, (Object[])null);
	}
	
	//private static void EXT_X_STREAM_INF(ExtTag This)
	private void EXT_X_STREAM_INF(){
		if (!containingList.IsMaster()){
			
			validated = false;
			return;
		}
		// need to download
		//boolean success = false;
		if (playListScanner.scanner.hasNext()){
			String urlLine = playListScanner.GetNextLine();
			if (true) // is not a tag, i.e. a missing line
			GetStream(urlLine);
			else;
				// log missing url (vs malformed)
		}
		else {
			// log error end of file
			// leave validated = false;
		}
		
		validated = true;
	}
	
	private void EXT_X_MEDIA(){
		validated = true;
	}
	
}

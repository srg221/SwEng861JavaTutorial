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
	
//	MasterListExtTag(PlayList playList, PlayListScanner scanner, String tagName, String url) throws MalformedURLException{
//		super(playList, scanner, tagName, url);
//	}
	
	// use this one to wait for validate to download
	MasterListExtTag(PlayList playList, PlayListScanner scanner, String tagName) throws MalformedURLException{
		super(playList, scanner, tagName);
	}
	
	
	//public static MasterListExtTag Clone(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
	//	MasterListExtTag clone = new MasterListExtTag(playList, scanner, url, tagName);
	//	return clone;
	//}
	
	public static void Initialize() {
		// load my map  validator.class.getMethod()
		for (String validator[] : validatorList)
			try {
				validatorMap.put(validator[0], MasterListExtTag.class.getDeclaredMethod(validator[1], PlayListScanner.class ));
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static boolean HasValidator(String tagName){
		return (validatorMap.containsKey(tagName));
	}
	
	public static boolean HasStream(String tagName){
		return (HasValidator(tagName) && (tagName == Tokens.EXT_X_STREAM_INF));
	}
	
	//public static void Validate(ExtTag This, String tagName) throws Exception, IllegalArgumentException, InvocationTargetException{
	public void Validate(String tagName, PlayListScanner scanner) {	
	//validatorMap.get(tagName).invoke(This);
		if (HasValidator(tagName))
			try {
				validatorMap.get(tagName).invoke(this, scanner);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	//private static void EXT_X_STREAM_INF(ExtTag This)
	private void EXT_X_STREAM_INF(PlayListScanner scanner){
		if (!containingList.IsMaster()){
			validated = false;
			return;
		}
		// need to download
		while (scanner.scanner.hasNext()){
			String urlLine = scanner.GetNextLine();
			// skip comments and blank lines
			if (scanner.IsBlanksOrComment(urlLine)) continue;
			if (!urlLine.startsWith(Tokens.tagBegin)){ 
				// is not a tag, comment, take it as the URL
				GetStream(urlLine);  // will mark bad if any problem getting stream
				break; // done in this loop, need to validate remaining line
			}
			else {
				// next line is a tag - log missing url (vs malformed)
				MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "missing URL line for MediaPlayList");
				LogStreamError(msg);
				// rewind to line just before tag
				int curLine = scanner.currLineNum;
				scanner.GoTo(curLine-1);
				// set bad tag and quit
				validated = false;
				return;
			}
		} 
		// did we hit EOF before found URL?
		if (!scanner.scanner.hasNext())
		{
			MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "EOF hit before URL found");
			LogStreamError(msg);
			validated = false;
			return;
		}
		// if still validated check rest of the tag line (myLine)
		if (validated){
			// 
		}
	}
	
	private void EXT_X_MEDIA(PlayListScanner scanner){
		validated = true;
	}
	
}

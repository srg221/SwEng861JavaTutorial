package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import prototyping.ExtTag.MSG;

public class MasterListExtTag extends ExtTagStream {

	private static	Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { {Tokens.EXT_X_STREAM_INF, "EXT_X_STREAM_INF"}, 
												{Tokens.EXT_X_MEDIA, "EXT_X_MEDIA"},
												{Tokens.EXT_X_I_FRAME_STREAM_INF, "EXT_X_I_FRAME_STREAM_INF"},
												{Tokens.EXT_X_SESSION_DATA, "EXT_X_SESSION_DATA"},
												};
	
//	MasterListExtTag(PlayList playList, PlayListScanner scanner, String tagName, String url) throws MalformedURLException{
//		super(playList, scanner, tagName, url);
//	}
	
	// use this one to wait for validate to download
	MasterListExtTag(PlayList playList, PlayListScanner scanner, String tagName) {
		super(playList, scanner, tagName);
	}
	
	
	//public static MasterListExtTag Clone(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
	//	MasterListExtTag clone = new MasterListExtTag(playList, scanner, url, tagName);
	//	return clone;
	//}
	
	public static boolean Initialize() {
		// load my map  validator.class.getMethod()
		boolean status = true;
		for (String validator[] : validatorList)
			try {
				validatorMap.put(validator[0], MasterListExtTag.class.getDeclaredMethod(validator[1], PlayListScanner.class ));
			} catch (NoSuchMethodException | SecurityException e) {
				// curent logging not conducive to posting from static methods, this is a fatal coding error,
				// will return bool to indicate success, and exit on failure
				e.printStackTrace();
				status = false;
			}
		return status;
	}
	
	public static boolean HasValidator(String tagName){
		return (validatorMap.containsKey(tagName));
	}
	
	public static boolean HasStream(String tagName){
		return (HasValidator(tagName) && (tagName == Tokens.EXT_X_STREAM_INF));
	}
	
	
	public boolean Validate(String tagName, PlayListScanner scanner) {			
		boolean status = true;
		if (HasValidator(tagName))
			try {
				validatorMap.get(tagName).invoke(this, scanner);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// curent logging not conducive to posting from static methods, this is a fatal coding error,
				// will return bool to indicate successful call - not necessarily a valid tag
				e.printStackTrace();
				status = false;
			}
		IsValidforRelease(tagName, containingList.version, this);
		return status;
	}
	
	//private static void EXT_X_STREAM_INF(ExtTag This)

	@SuppressWarnings("rawtypes")
	private void EXT_X_STREAM_INF(PlayListScanner scanner){
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (!containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() since attributes follow
		//Matcher matcher = Tokens.EXT_X_STREAM_INFpattern.matcher(myLine);
		if (!Tokens.EXT_X_STREAM_INFpattern.matcher(myLine).find()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
			LogTrace(msg, 20);
			validated = false;
		}
		// need to download - find URL
		while (scanner.scanner.hasNext()){
			String urlLine = scanner.GetNextLine();
			// skip comments and blank lines
			if (scanner.IsBlanksOrComment(urlLine)) continue;
			if (!urlLine.startsWith(Tokens.tagBegin)){ 
				// is not a tag, comment, take it as the URL
				GetStream(urlLine);  // will mark bad if any problem getting stream
				break; // done in this loop, need to validate remaining line
			}
			else if (!urlLine.startsWith(Tokens.tagBegin)){
				// next line is a tag - log missing url (vs malformed)
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "missing URL line for MediaPlayList");
				LogStreamError(msg);
				// rewind to line just before tag
				int curLine = scanner.currLineNum;
				scanner.GoTo(curLine-1);
				// set bad tag and quit
				validated = false;
				return;
			}
			// did we hit EOF before found URL?
			if (!scanner.scanner.hasNext())
			{
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "EOF hit before URL found");
				LogStreamError(msg);
				validated = false;
				return;
			}
		} 

		// still valid, try to get attr, load attr set 
		attrSet.add(new Attr<Attr.AvInt>(Attr.AvInt.class, Tokens.BANDWIDTH, this));
		attrSet.add(new Attr<Attr.AvInt>(Attr.AvInt.class, Tokens.AVERAGE_BANDWIDTH, this));
		attrSet.add(new Attr<Attr.AvQuotedString>(Attr.AvQuotedString.class, Tokens.CODECS, this));
		attrSet.add(new Attr<Attr.AvResolution>(Attr.AvResolution.class ,Tokens.RESOLUTION, this));
		attrSet.add(new Attr<Attr.AvQuotedString>(Attr.AvQuotedString.class, Tokens.AUDIO, this));
		attrSet.add(new Attr<Attr.AvQuotedString>(Attr.AvQuotedString.class, Tokens.VIDEO, this));
		attrSet.add(new Attr<Attr.AvQuotedString>(Attr.AvQuotedString.class, Tokens.SUBTITLES, this));
		// tbd - special handling here since NONE changes type to AvString...
		attrSet.add(new Attr<Attr.AvQuotedString>(Attr.AvQuotedString.class, Tokens.CLOSED_CAPTIONS, this));
		// deprecated in ver > 5
		attrSet.add(new Attr<Attr.AvInt>(Attr.AvInt.class, Tokens.PROGRAM_ID, this));
		// test of external class
		//attrSet.add(new Attr<IntAttr>(IntAttr.class,Tokens.BANDWIDTH, this));
		// find the above in the Tag line, only tise valid will remain in set
		Attr.GetAttr(this);
		// check that they are supported in this ver
		for ( Attr a : attrSet){
			String name = a.name;
			// the call logs invalid ver msg 
			IsValidforRelease(name, containingList.version, this);
		}

	}
	
	private void EXT_X_MEDIA(PlayListScanner scanner){
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (!containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() since attributes follow
//		if (!Tokens.EXT_X_MEDIApattern.matcher(myLine).find()){
//			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//			LogStreamError(msg);
//			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//			LogTrace(msg, 20);
//			validated = false;
//		}
		
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
		
	}
	
	private void EXT_X_I_FRAME_STREAM_INF(PlayListScanner scanner){
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (!containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() since attributes follow
//		if (!Tokens.EXT_X_I_FRAME_STREAM_INFpattern.matcher(myLine).find()){
//			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//			LogStreamError(msg);
//			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//			LogTrace(msg, 20);
//			validated = false;
//		}
		
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
		
	}
	
	
	private void EXT_X_SESSION_DATA(PlayListScanner scanner){
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (!containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() since attributes follow
//		if (!Tokens.EXT_X_SESSION_DATApattern.matcher(myLine).find()){
//			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//			LogStreamError(msg);
//			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//			LogTrace(msg, 20);
//			validated = false;
//		}
		
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
		
	}
}

package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import prototyping.ExtTag.MSG;

//import prototyping.ExtTag.MSG;

public class MediaListExtTag extends ExtTagStream {

	private static Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { { Tokens.EXTINF, "EXTINF" },
			{ Tokens.EXT_X_MEDIA_SEQUENCE, "EXT_X_MEDIA_SEQUENCE" },
			{ Tokens.EXT_X_ENDLIST, "EXT_X_ENDLIST" },
			{ Tokens.EXT_X_TARGETDURATION, "EXT_X_TARGETDURATION" },
			{ Tokens.EXT_X_KEY, "EXT_X_KEY" },
			{ Tokens.EXT_X_DISCONTINUITY_SEQUENCE, "EXT_X_DISCONTINUITY_SEQUENCE" },
			{ Tokens.EXT_X_BYTERANGE, "EXT_X_BYTERANGE" },
			{ Tokens.EXT_X_I_FRAMES_ONLY, "EXT_X_I_FRAMES_ONLY" },
			{ Tokens.EXT_X_MAP, "EXT_X_MAP" },
			{ Tokens.EXT_X_PROGRAM_DATE_TIME, "EXT_X_PROGRAM_DATE_TIME" },
			{ Tokens.EXT_X_PLAYLIST_TYPE, "EXT_X_PLAYLIST_TYPE" }, 
			{ Tokens.EXT_X_ALLOW_CACHE, "EXT_X_ALLOW_CACHE" } };

	// MediaListExtTag(PlayList playList, PlayListScanner scanner, String
	// tagName,
	// String url) throws MalformedURLException {
	// super(playList, scanner, tagName, url);
	// }

	// this one waits for validate to download
	MediaListExtTag(PlayList playList, PlayListScanner scanner, String tagName){
		super(playList, scanner, tagName);
	}

	// public static MediaListExtTag Clone(PlayList playList, PlayListScanner
	// scanner, String tagName, String url) throws MalformedURLException{
	// MediaListExtTag clone = new MediaListExtTag(playList, scanner, tagName,
	// url);
	// return clone;
	// }

	public static boolean Initialize() {
		// load my map validator.class.getMethod()
		
		boolean status = true;
		for (String validator[] : validatorList)
			try {
				validatorMap.put(validator[0], MediaListExtTag.class.getDeclaredMethod(validator[1], PlayListScanner.class));
			} catch (NoSuchMethodException | SecurityException e) {
				// curent logging not conducive to posting from static methods, this is a fatal coding error,
				// will return bool to indicate success, and exit on failure
				e.printStackTrace();
				status =  false;
			}
		return status;
	}

	public static boolean HasValidator(String tagName) {
		return (validatorMap.containsKey(tagName));
	}

	public static boolean HasStream(String tagName) {
		return (HasValidator(tagName) && (tagName == Tokens.EXTINF));
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
		return status;
	}

	// private static void EXTINF(ExtTag This)
	@SuppressWarnings("unchecked")
	private void EXTINF(PlayListScanner scanner) {
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern 
		if (!Tokens.EXTINFpattern.matcher(myLine).find()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
			LogTrace(msg, 20);
			validated = false;
		}
		
		// check if endlist was already found
		if (((MediaPlayList)containingList).endListFound){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "No segments allowed after EXT-X-ENDLIST");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "No segments allowed after EXT-X-ENDLIST");
			LogTrace(msg, 20);
			validated = false;
		}
		
		// need to download - find next URL
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

		// if still validated check rest of the tag line (myLine), and .ts file characteristics
		// get segment duration (tag value)
		boolean floatOk = false; boolean intOk = false;
		// try float first
		try {
			float v = 0;
			value = FindTagValue(v);
			floatOk = true;
		} catch (TokenNotFoundException e) {
			floatOk = false;
		}
		
		if (!floatOk) {
			try {
				int v = 0;
				value = FindTagValue(v);
				intOk = true;
			} catch (TokenNotFoundException e) {
				intOk = false;
			}
		}
		if (Math.abs(containingList.version) < 3 && floatOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Duration for ver "+containingList.version+ "should be an int");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Duration for ver "+containingList.version+ "should be an int");
			LogTrace(msg, 20);
		}
		if (Math.abs(containingList.version) >= 3 && intOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Duration for ver "+containingList.version+ "should be a float");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Duration for ver "+containingList.version+ "should be a float");
			LogTrace(msg, 20);
		}
		if (!floatOk && !intOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad Duration");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad Duration");
			LogTrace(msg, 20);
			validated = false;
		}

		if (validated && ((MediaPlayList)containingList).targetDuration.floatValue() < value.floatValue()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Duration "+value+" > TargetDuration "+ ((MediaPlayList)containingList).targetDuration);
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Duration "+value+" > TargetDuration "+ ((MediaPlayList)containingList).targetDuration);
			LogTrace(msg, 20);
			validated = false;
		}
		
		// save <title> in pseudoAttr
		// consider everything past first comma potential descriptor
		int commaLoc = myLine.indexOf(',');
		// since optional, only do if comma found
		if (commaLoc > 0){
			String desc = new String(myLine.substring(commaLoc+1 ,myLine.length()));
			// pattern is overkill here since title is any string "expressed as raw UTF-8 text"
			// but doing it to make sure it exists before creating Attr to hold it
			Matcher matcher = Pattern.compile(Tokens.UnQuotededStrRegExp).matcher(desc);
			if (matcher.find() ){
				// debug...
				String grp0 = matcher.group(0);
				pseudoAttr = new Attr<Attr.AvString>(Attr.AvString.class, "TITLE", this);
				((Attr.AvString)pseudoAttr.valueContainer).value = desc;
			}
		}
	}
	
	private void EXT_X_ENDLIST(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, matches ok here
		if (!Tokens.EXT_X_ENDLISTpattern.matcher(myLine).matches()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
			LogTrace(msg, 20);
			validated = false;
			return;
		}
		// set playlist endlist found
		((MediaPlayList)containingList).endListFound = true;
	}


	private void EXT_X_TARGETDURATION(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
		if (!Tokens.EXT_X_TARGETDURATIONpattern.matcher(myLine).find()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
			LogTrace(msg, 20);
			validated = false;
		}
		
		// if still validated check rest of the tag line (myLine), and .ts file characteristics
		// get segment duration (tag value), should be an int
		boolean floatOk = false; boolean intOk = false;
		// try int first
		try {
			int v = 0;
			((MediaPlayList)containingList).targetDuration = value = FindTagValue(v);
			intOk = true;
		} catch (TokenNotFoundException e) {
			intOk = false;
		}
		
		if (!intOk) {
			try {
				float v = 0;
				((MediaPlayList)containingList).targetDuration = value = FindTagValue(v);
				floatOk = true;
			} catch (TokenNotFoundException e) {
				floatOk = false;
			}
		}
		if (floatOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Duration should be an int");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Duration should be an int");
			LogTrace(msg, 20);
		}
		if (!floatOk && !intOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad Duration");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad Duration");
			LogTrace(msg, 20);
			validated = false;
		}

		
		
		// TODO msg
//		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
//		LogStreamError(msg);
//		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
//		LogTrace(msg, 20);
	}

	private void EXT_X_MEDIA_SEQUENCE(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern 
		if (!Tokens.EXT_X_MEDIA_SEQUENCEpattern.matcher(myLine).find()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
			LogTrace(msg, 20);
			validated = false;
		}
		// get media sequence, should be an int
		boolean floatOk = false; boolean intOk = false;
		// try int first
		try {
			int v = 0;
			value = FindTagValue(v);
			intOk = true;
		} catch (TokenNotFoundException e) {
			intOk = false;
		}
		
		if (!intOk) {
			try {
				float v = 0;
				value = FindTagValue(v);
				floatOk = true;
			} catch (TokenNotFoundException e) {
				floatOk = false;
			}
		}
		if (floatOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Media Sequence Number should be an int");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Media Sequence Number should be an int");
			LogTrace(msg, 20);
		}
		if (!floatOk && !intOk){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad Media Sequence Number");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad Media Sequence Number");
			LogTrace(msg, 20);
			validated = false;
		}

		
		// TODO msg
//		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
//		LogStreamError(msg);
//		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
//		LogTrace(msg, 20);
	}

	private void EXT_X_PLAYLIST_TYPE(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//		if (!Tokens.EXT_X_PLAYLIST_TYPEpattern.matcher(myLine).find()){
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
		
	private void EXT_X_DISCONTINUITY_SEQUENCE(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_DISCONTINUITY_SEQUENCEpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}	
		
	private void EXT_X_DISCONTINUITY(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_DISCONTINUITYpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}

	private void EXT_X_I_FRAMES_ONLY(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_I_FRAMES_ONLYpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}
	
	private void EXT_X_BYTERANGE(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_BYTERANGEpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}

	private void EXT_X_PROGRAM_DATE_TIME(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_PROGRAM_DATE_TIMEpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}

	private void EXT_X_KEY(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_KEYpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}
	
	private void EXT_X_MAP(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_MAPpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}
	
	@SuppressWarnings("unused")
	private void EXT_X_ALLOW_CACHE(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		if (containingList.IsMaster()){
			// error for this is logged in validator since if we got here,
			// It has to be a coding error, just need to mark false.
			validated = false;
			return;
		}
		// check tag pattern, find() tbd
//			if (!Tokens.EXT_X_ALLOW_CACHEpattern.matcher(myLine).find()){
//				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
//				LogStreamError(msg);
//				msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
//				LogTrace(msg, 20);
//				validated = false;
//			}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}
	
}

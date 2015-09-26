package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//import prototyping.ExtTag.MSG;

public class MediaListExtTag extends ExtTagStream {

	private static Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { { Tokens.EXTINF, "EXTINF" },
			{ Tokens.EXT_X_MEDIA_SEQUENCE, "EXT_X_MEDIA_SEQUENCE" },
			{ Tokens.EXT_X_ENDLIST, "EXT_X_ENDLIST" },
			{ Tokens.EXT_X_TARGETDURATION, "EXT_X_TARGETDURATION" },
			{ Tokens.EXT_X_START, "EXT_X_START" },
			{ Tokens.EXT_X_PLAYLIST_TYPE, "EXT_X_PLAYLIST_TYPE" } };

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
	private void EXTINF(PlayListScanner scanner) {
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
				MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "missing URL line for media (.ts)");
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
		// if still validated check rest of the tag line (myLine), and .ts file characteristics
		if (validated){
			// 
		}

	}

	private void EXT_X_MEDIA_SEQUENCE(PlayListScanner scanner) {

		validated = true;
	}

	private void EXT_X_ENDLIST(PlayListScanner scanner) {

		validated = true;
	}

	private void EXT_X_TARGETDURATION(PlayListScanner scanner) {

		validated = true;
	}

	private void EXT_X_START(PlayListScanner scanner) {

		validated = true;
	}

	private void EXT_X_PLAYLIST_TYPE(PlayListScanner scanner) {

		validated = true;
	}
}

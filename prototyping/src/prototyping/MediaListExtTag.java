package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class MediaListExtTag extends ExtTagStream {

	private static Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { { Tokens.EXTINF, "EXTINF" },
			{ Tokens.EXT_X_MEDIA_SEQUENCE, "EXT_X_MEDIA_SEQUENCE" },
			{ Tokens.EXT_X_ENDLIST, "EXT_X_ENDLIST" },
			{ Tokens.EXT_X_TARGETDURATION, "EXT_X_TARGETDURATION" },
			{ Tokens.EXT_X_START, "EXT_X_START" },
			{ Tokens.EXT_X_PLAYLIST_TYPE, "EXT_X_PLAYLIST_TYPE" } };

	MediaListExtTag(PlayList playList, PlayListScanner scanner, String tagName,
			String url) throws MalformedURLException {
		super(playList, scanner, tagName, url);
	}

	// this one waits for validate to download
	MediaListExtTag(PlayList playList, PlayListScanner scanner, String tagName)
			throws MalformedURLException {
		super(playList, scanner, tagName);
	}

	// public static MediaListExtTag Clone(PlayList playList, PlayListScanner
	// scanner, String tagName, String url) throws MalformedURLException{
	// MediaListExtTag clone = new MediaListExtTag(playList, scanner, tagName,
	// url);
	// return clone;
	// }

	public static void Initialize() throws NoSuchMethodException,
			SecurityException {
		// load my map validator.class.getMethod()
		for (String validator[] : validatorList)
			// validatorMap.put(validator[0],
			// ExtTag.class.getMethod(validator[1]));
			validatorMap.put(validator[0],
					MediaListExtTag.class.getDeclaredMethod(validator[1]));
		// validatorMap.put(validator[0],
		// ExtTag.class.getMethod("EXTM3U",Class<ExtTag>,ExtTag.class));
	}

	public static boolean HasValidator(String tagName) {
		return (validatorMap.containsKey(tagName));
	}

	public static boolean HasStream(String tagName) {
		return (HasValidator(tagName) && (tagName == Tokens.EXTINF));
	}

	// public static void Validate(ExtTag This, String tagName) throws
	// Exception, IllegalArgumentException, InvocationTargetException{
	public void Validate(String tagName) throws Exception,
			IllegalArgumentException, InvocationTargetException {
		// validatorMap.get(tagName).invoke(This);
		if (HasValidator(tagName))
			validatorMap.get(tagName).invoke(this, (Object[]) null);
	}

	// private static void EXTINF(ExtTag This)
	private void EXTINF() {
		// need to download
		boolean success = false;
		if (playListScanner.scanner.hasNext()) {
			String urlLine = playListScanner.GetNextLine();
			if (true) // is not a tag, i.e. a missing line
				GetStream(urlLine);
			else
				;
			// log missing url (vs malformed)
		} else {
			// log error end of file
			String[] msg = { "Error Number", "Error Type", "File Name",
					"Line Number", "Details" };
			LogRunError(msg, 20);
			// leave validated = false;
		}
		// String[] msg = {"Error Number", "Error Type", "File Name",
		// "Line Number", "Details"};
		// LogRunError(msg, 20);
		validated = true;
		;
	}

	private void EXT_X_MEDIA_SEQUENCE() {
		// String[] msg = {"Error Number", "Error Type", "File Name",
		// "Line Number", "Details"};
		// LogRunError(msg, 20);
		validated = true;
	}

	private void EXT_X_ENDLIST() {
		// String[] msg = {"Error Number", "Error Type", "File Name",
		// "Line Number", "Details"};
		// LogRunError(msg, 20);
		validated = true;
	}

	private void EXT_X_TARGETDURATION() {
		// String[] msg = {"Error Number", "Error Type", "File Name",
		// "Line Number", "Details"};
		// LogRunError(msg, 20);
		validated = true;
	}

	private void EXT_X_START() {
		// String[] msg = {"Error Number", "Error Type", "File Name",
		// "Line Number", "Details"};
		// LogRunError(msg, 20);
		validated = true;
	}

	private void EXT_X_PLAYLIST_TYPE() {
		// String[] msg = {"Error Number", "Error Type", "File Name",
		// "Line Number", "Details"};
		// LogRunError(msg, 20);
		validated = true;
	}
}

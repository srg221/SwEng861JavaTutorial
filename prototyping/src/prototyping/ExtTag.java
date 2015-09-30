package prototyping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;


//import prototyping.PlayList.MSG;

public class ExtTag {
	protected PlayList containingList;
	protected String containingListName;
	protected String myLine;
	protected int myLineNumber;
	public String myTagName;
	//protected PlayListScanner playListScanner;  // big mistake, which scanner is in use depends on context/lifecycle
	protected boolean validated = true; // assume success
	public Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();
	private static Map<String, Method> validatorMap = new HashMap<String, Method>();
	private static String[][] validatorList = { { Tokens.EXTM3U, "EXTM3U" },
												{ Tokens.EXT_X_INDEPENDENT_SEGMENTS, "EXT_X_INDEPENDENT_SEGMENTS" }, 
												{ Tokens.EXT_X_VERSION, "EXT_X_VERSION" },
												{ Tokens.EXT_X_START, "EXT_X_START" } };

	ExtTag(PlayList playList, PlayListScanner scanner, String tagName) {
		// parent reference
		containingList = playList;
		//playListScanner = scanner;
		myLineNumber = scanner.currLineNum;
		myLine = scanner.currLine;
		containingListName = containingList.myURL;
		myTagName = tagName;
	}

	
	// anyone can mark bad
	public void MarkBad() { validated = false; }
	public boolean IsValid() { return validated; }

	public String toString() {
		return myTagName;
	}

	public String Location() {
//		if (myLineNumber == 0){
//			try {
//				throw new DebugException(Thread.currentThread().getStackTrace()[1].getClassName() + "lineNum == 0");
//			} catch (DebugException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return (containingList.toString() + "::LINE::"
				+ Integer.toString(myLineNumber) + "::Tag::" + myTagName);
	}

	public static String Context() {
		String context = new String("Context:");
		context += context
				+ Thread.currentThread().getStackTrace()[2].getFileName();
		context += "::"
				+ Thread.currentThread().getStackTrace()[2].getClassName();
		context += "::"
				+ Thread.currentThread().getStackTrace()[2].getMethodName();
		context += "::Line:"
				+ Thread.currentThread().getStackTrace()[2].getLineNumber();
		return context;
	}

	static String GetTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}

	public static ExtTag Clone(PlayList playList, PlayListScanner scanner,
			String tagName) {
		ExtTag clone = new ExtTag(playList, scanner, tagName);
		return clone;
	}

	public static boolean Initialize() {
		// load my map validator.class.getMethod()
		boolean status = true;
		for (String validator[] : validatorList)
			try {
				validatorMap.put(validator[0],
						ExtTag.class.getDeclaredMethod(validator[1], PlayListScanner.class));
			} catch (NoSuchMethodException | SecurityException e) {
				// curent logging not conducive to posting from static methods, this is a fatal coding error,
				// will return bool to indicate success, and exit on failure
				e.printStackTrace();
				status = false;
			}
		// validatorMap.put(validator[0],
		// ExtTag.class.getMethod("EXTM3U",Class<ExtTag>,ExtTag.class));
		// load leaf classes maps
		if (status)
		 status = MasterListExtTag.Initialize();
		if (status)
			status = MediaListExtTag.Initialize();
		return status;
	}

	// utility methods
	public static String GetCandidateTag(String line) {
		int beginIndex = line.indexOf(Tokens.tagBegin);
		int endIndex = line.indexOf(Tokens.tagEnd);
		if (beginIndex == 0 && endIndex > 0) {
			// normal case, tag with endTag delimiter, no tagBegin
			return line.substring(beginIndex + 1, endIndex);
		}
		if (beginIndex < 0) {
			// something is wrong, probably shouldn't be here, return empty
			// string
			return "";
		}
		// possibly a tag with no endTag delimiter, just return (trimmed, no
		// tagBegin) line
		return line.substring(beginIndex + 1).trim();
	}

	public static boolean HasValidator(String tagName) {
		return (validatorMap.containsKey(tagName));
	}

	// public static void Validate(ExtTag This, String tagName) throws
	// Exception, IllegalArgumentException, InvocationTargetException{
	public boolean Validate(String tagName, PlayListScanner scanner) {
		// validatorMap.get(tagName).invoke(This);
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
	
	// private static void EXTM3U(ExtTag This)
	private void EXTM3U(PlayListScanner scanner) {
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		// check tag pattern. matches() since exact
		if (!Tokens.EXTM3Upattern.matcher(myLine).matches()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Extra characters in line");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Extra characters in line");
			LogTrace(msg, 20);
			validated = false;
		}
		// needs to be first line
		if (myLineNumber != 1){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Not first line in list");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Not first line in list");
			LogTrace(msg, 20);
			validated = false;
		}
	}

	// private static void EXT_X_VERSION(ExtTag This)
	private void EXT_X_VERSION(PlayListScanner scanner) {
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		// check tag format pattern,
		Matcher matcher = Tokens.EXT_X_VERSIONpattern.matcher(myLine);
		if (!matcher.find()){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Bad format");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Bad format");
			LogTrace(msg, 20);
			validated = false;
		}
		if (containingList.version != -1){
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.TAG.toString(), "Version previously set to "+Integer.toString(containingList.version));
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "Duplicate Version Tag");
			LogTrace(msg, 20);
			validated = false;
			return;
		}
		// get version
		// advance past tag
		String remaining = myLine.substring(myLine.indexOf(Tokens.tagEnd)+1);
		int v;
		try {
			v = Tokens.GetNextInt(remaining);
			if (v>0){
				containingList.version = v;
				return;
			}
		} catch (TokenNotFoundException e) {
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Specified version is not an integer");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context() , "version not an int. Exception:" + e.getMessage()+":"+e.getCause());
			LogTrace(msg, 20);
			validated = false;
		}
	}
	
	private void EXT_X_INDEPENDENT_SEGMENTS(PlayListScanner scanner) {
		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);
		// check tag pattern. matches() since exact
//		if (!Tokens.EXT_X_INDEPENDENT_SEGMENTSpattern.matcher(myLine).matches()){
//			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Extra characters in line");
//			LogStreamError(msg);
//			msg = new MSG(GetTimeStamp(), Location(), Context() , "Extra characters in line");
//			LogTrace(msg, 20);
//			validated = false;
//		}
		// TODO msg
		msg = new MSG(GetTimeStamp(), Location(), Err.Sev.WARN.toString(), Err.Type.TAG.toString(), "Validator implementation not complete");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), Location(), Context() , "Validator implementation not complete");
		LogTrace(msg, 20);
	}

	private void EXT_X_START(PlayListScanner scanner) {

		MSG msg = new MSG(GetTimeStamp(), Location(), Context() , "Starting tag validation");
		LogTrace(msg, 40);

		// check tag pattern, matches() tbd
//		if (!Tokens.EXT_X_STARTpattern.matcher(myLine).matches()){
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

	
	// logging utils at ExtTag level
	// More logging stuff/utilities

	// some wrappers to make code reading easier, would be simpler
	// if java let you overload operators

	public class MSG {
		private ArrayList<String> fields;

		public MSG(String... infields) {
			fields = new ArrayList<String>();
			// fields.add(Integer.toString(myLineNumber));
			// fields.add(myTagName);
			for (String field : infields) {
				fields.add(field);
			}
		}

		// for contained
		public MSG(ArrayList<String> infields) {
			fields = new ArrayList<String>();
			// fields.add(Integer.toString(myLineNumber));
			// fields.add(myTagName);
			for (String field : infields) {
				fields.add(field);
			}
		}
	}

	// for use at this ("the ExtTag") level
	public void LogStreamError(MSG msg) {
		containingList.LogStreamError(msg.fields);
	}

	public void LogTrace(MSG msg) {
		containingList.LogTrace(msg.fields);
	}

	public void LogStreamError(MSG msg, int paranoid) {
		containingList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(MSG msg, int paranoid) {
		containingList.LogTrace(msg.fields, paranoid);
	}

	// for contained (M3u8InputStream) levels
	public void LogStreamError(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		containingList.LogStreamError(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		containingList.LogTrace(msg.fields);
	}

	public void LogStreamError(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		containingList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		containingList.LogTrace(msg.fields, paranoid);
	}


}

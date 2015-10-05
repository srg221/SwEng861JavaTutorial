package prototyping;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tokens {
	
	// common playlist tokens
	// Line and tag delimiters
	public static final String beginLine = "#";
	public static final String endLine = "\\r?\\n";
	public static final String tagBegin = "#EXT"; // primarily to distinguish tag from comment
	public static final String tagEnd = ":";
    // Common RegExpressions, most copied from the internet or def of attr in RFC
    public static final String urlRegExp = "^(?:https?|ftp)://[^\\s/$.?#]*\\.[^\\s]*$";
    public static final String urlRegExp2 = "\\b(https?|ftp|file)://[-A-Z0-9+&@#/%?=~_|!:,.;]*[-A-Z0-9+&@#/%=~_|]";
    public static final String integerRegExp = "\\d+";
    public static final String floatRegExp = "\\d+\\.?\\d*";
    public static final String hexRegExp = "^0[x|X]([0-9A-F]+)";
    public static final String quotedStrRegExp = "[^\"][^\n\"\r]+\"";  // needs work - match anything but LF, ", or CR
    public static final String resolutionRegExp = "^("+integerRegExp+")x("+integerRegExp+")";
    public static final String beginLineExp = "^#";
    // Common and Attribute Value patterns 
    public static final Pattern urlPattern = Pattern.compile(urlRegExp);
    public static final Pattern urlPattern2 = Pattern.compile(urlRegExp2);
    public static final Pattern integerPattern = Pattern.compile(integerRegExp);
    public static final Pattern floatPattern = Pattern.compile(floatRegExp);
    public static final Pattern hexPattern = Pattern.compile(hexRegExp);
    
	// maybe this is not req
    public static Pattern resolutionPattern = Pattern.compile("^("+integerRegExp+")x("+integerRegExp+")$");
	
    // Basic Tags  4.3.1
	public static final String EXTM3U = "EXTM3U"; //4.3.1.1
	public static final String EXT_X_VERSION = "EXT-X-VERSION"; //4.3.1.2
	// Media or Master Playlist tags 4.3.5
	public static final String EXT_X_INDEPENDENT_SEGMENTS  = "EXT-X-INDEPENDENT-SEGMENTS"; // 4.3.5.1
	public static final String EXT_X_START  = "EXT-X-START"; // 4.3.5.2
	// Tag Match Patterns
	public static Pattern EXTM3Upattern = Pattern.compile(beginLineExp+EXTM3U+"$");
	//public static Pattern EXT_X_VERSIONpattern = Pattern.compile("^#"+EXT_X_VERSION+tagEnd+"("+integerRegExp+")$");
	public static Pattern EXT_X_VERSIONpattern = Pattern.compile(beginLineExp+EXT_X_VERSION+tagEnd+"("+integerRegExp+")$");
    // Attributes & Other
    public static final String TIME_OFFSET = "TIME-OFFSET";  //EXT-X-START attr
    public static final String PRECISE = "PRECISE";  //EXT-X-START attr
 
    // master playlist tokens   
    // Tags 4.3.4
    public static final String EXT_X_MEDIA = "EXT-X-MEDIA";  //4.3.4.1
	public static final String EXT_X_STREAM_INF = "EXT-X-STREAM-INF"; //4.3.4.2
	public static final String EXT_X_I_FRAME_STREAM_INF = "EXT-X-I-FRAME_STREAM-INF"; //4.3.4.3
	public static final String EXT_X_SESSION_DATA = "EXT-X-SESSION-DATA"; //4.3.4.4
    // Tag Match Patterns
	public static Pattern EXT_X_STREAM_INFpattern = Pattern.compile(beginLineExp+EXT_X_STREAM_INF+tagEnd);
	public static Pattern EXT_X_MEDIApattern = Pattern.compile(beginLineExp+EXT_X_MEDIA+tagEnd);
    // Attributes & Other
	public static final String TYPE = "TYPE";  //EXT_X_MEDIA attr
	public static final String NAME = "NAME";  //EXT_X_MEDIA attr
	public static final String ASSOC_LANGUAGE = "ASSOC-LANGUAGE";  //EXT_X_MEDIA attr
	public static final String GROUP_ID = "GROUP-ID";  //EXT_X_MEDIA attr
	public static final String DEFAULT = "DEFAULT";  //EXT_X_MEDIA attr
	public static final String AUTOSELECT = "AUTOSELECT";  //EXT_X_MEDIA attr
	public static final String FORCED = "FORCED";  //EXT_X_MEDIA attr
	public static final String IN_STREAM_ID = "INSTREAM-ID"; //EXT_X_MEDIA attr
	public static final String CHARACTERISTICS = "CHARACTERISTICS";  //EXT_X_MEDIA attr
	public static final String BANDWIDTH = "BANDWIDTH"; //EXT_X_STREAM_INF attr
	public static final String AVERAGE_BANDWIDTH = "AVERAGE-BANDWIDTH"; //EXT_X_STREAM_INF attr
	public static final String CODECS = "CODECS"; //EXT_X_STREAM_INF attr
	public static final String RESOLUTION = "RESOLUTION";  //EXT_X_STREAM_INF attr
	public static final String AUDIO = "AUDIO"; //EXT_X_STREAM_INF attr
	public static final String VIDEO = "VIDEO"; //EXT_X_STREAM_INF attr
	public static final String SUBTITLES = "SUBTITLES"; //EXT_X_STREAM_INF attr
	public static final String CLOSED_CAPTIONS = "CLOSED-CAPTIONS"; //EXT_X_STREAM_INF attr
	public static final String PROGRAM_ID = "PROGRAM-ID"; //EXT_X_STREAM_INF attr, removed in ver >= 6
	public static final String URI = "URI";  //EXT-X-I-FRAME-STREAM-INF attr et al
	public static final String DATA_ID = "DATA-ID"; // EXT-X-SESSION-DATA attr
	public static final String VALUE = "VALUE";  // EXT-X-SESSION-DATA attr
	public static final String LANGUAGE = "LANGUAGE"; // EXT-X-SESSION-DATA attr et al
	
		
    
    // media playlist tokens
	// Media Segment Tags 4.3.2
	public static final String EXTINF = "EXTINF"; //4.3.2.1
	public static final String EXT_X_BYTERANGE = "EXT_X_BYTERANGE"; //4.3.2.2
	public static final String EXT_X_DISCONTINUITY = "EXT-X-DISCONTINUITY"; //4.3.2.3
	public static final String EXT_X_KEY = "EXT-X-KEY"; //4.3.2.4
	public static final String EXT_X_MAP = "EXT-X-MAP"; //4.3.2.5
	public static final String EXT_X_PROGRAM_DATE_TIME = "EXT-X-PROGRAM-DATE-TIME"; //4.3.2.6
	// Media PlayList Tags 4.3.3
	public static final String EXT_X_TARGETDURATION  = "EXT-X-TARGETDURATION";  //4.3.3.1
	public static final String EXT_X_MEDIA_SEQUENCE = "EXT-X-MEDIA-SEQUENCE";  //4.3.3.2
	public static final String EXT_X_DISCONTINUITY_SEQUENCE = "EXT-X-DISCONTINUITY-SEQUENCE"; //4.3.3.3
	public static final String EXT_X_ENDLIST = "EXT-X-ENDLIST"; // 4.3.3.4
	public static final String EXT_X_PLAYLIST_TYPE = "EXT-X-PLAYLIST-TYPE"; //4.3.3.5
	public static final String EXT_X_I_FRAMES_ONLY = "EXT-X-I-FRAMES-ONLY"; // 4.3.3.6
	public static final String EXT_X_ALLOW_CACHE = "EXT-X-ALLOW-CACHE"; // not valid after ver 7
    //  attributes & Other
    public static final String METHOD = "METHOD";
    public static final String IV = "IV";
    public static final String KEY_FORMAT = "KEYFORMAT";
    public static final String KEY_FORMAT_VERSIONS = "KEYFORMATVERSIONS";
    // Tag Match Patterns
    public static final Pattern EXTINFpattern = Pattern.compile(beginLineExp+EXTINF+tagEnd+"("+floatRegExp+")(?:,(.+)?)?$");
    public static final Pattern EXT_X_ENDLISTpattern = Pattern.compile(beginLineExp+EXT_X_ENDLIST+"$");
    public static final Pattern EXT_X_MEDIA_SEQUENCEpattern = Pattern.compile(beginLineExp+EXT_X_MEDIA_SEQUENCE+tagEnd+"("+integerRegExp+")$");
	public static final Pattern EXT_X_TARGETDURATIONpattern = Pattern.compile(beginLineExp+EXT_X_TARGETDURATION+tagEnd+ "("+integerRegExp+")$");
    
	
	// version notes
	// ver>=2 needed for IV attr of EXT-X-KEY tag req ver>=2
	// ver>=3 Floating Point duration values of EXTINF 
	// ver>=4 EXT-X-BYTERANGE and  EXT-X-I-FRAMES-ONLY  
	// ver>=5 EXT-X-MAP, KEYFORMAT and KEYFORMATVERSIONS attributes of the EXT-X-KEY tag
	// ver>=6 EXT-X-MAP tag in a Media Playlist that does not contain EXTX-	I-FRAMES-ONLY
	// PROGRAM-ID attr removed from EXT-X-STREAM-INF & EXT-X-IFRAME-STREAM-INF after ver 6
	// ver>=7  needed for SERVICE values of INSTREAM-ID attr of EXT-X-MEDIA 
	
    
    // Get token methods - algos copied from a number of sources
    public static int GetNextInt(String line) throws TokenNotFoundException {
        Matcher matcher = integerPattern.matcher(line);
        if (matcher.find()){   	
        	String temp = matcher.group();
        	return Integer.parseInt(matcher.group());}
        else{
        	throw new TokenNotFoundException("Integer value not found");
        }
    }

    public static <T extends Enum<T>> T GetEnum(String line, Class<T> enumType){
            return Enum.valueOf(enumType, line);
    }

    public static float GetNextFloat(String line) throws TokenNotFoundException {
        Matcher matcher = floatPattern.matcher(line);
        if (matcher.find()){   	
        	return Float.parseFloat(matcher.group());
        }
        else{
        	throw new TokenNotFoundException("Float value not found");
        }
    }
    
    public static List<Byte> GetNextHex(String line) throws TokenNotFoundException {
        List<Byte> bytes = new ArrayList<Byte>();
        Matcher matcher = hexPattern.matcher(line.toUpperCase());

        if (matcher.find()) {
            String value = matcher.group();

            for (char c : value.toCharArray()) {
                bytes.add(ConverHextoByte(c));
            }
            return bytes;
        } else {
        	throw new TokenNotFoundException("Hex value not found");
        }
    }
    
    public static byte ConverHextoByte(char hex) {
        if (hex >= 'A') {
            return (byte) ((hex & 0xF) + 9);
        } else {
            return (byte) (hex & 0xF);
        }
    }

/*
    public static Attribute.Resolution GetNextResolution(String line) throws TokenNotFoundException {
        Matcher matcher = resolutionPattern.matcher(line);
        if (matcher.find()) {
            return new Attribute().Resolution(GetNextInt(matcher.group(1)), GetNextInt(matcher.group(2)));
        } else {
        	throw new TokenNotFoundException("Resolution not found");
        }
    }
    */
}

 
    	


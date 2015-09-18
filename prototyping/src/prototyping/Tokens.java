package prototyping;
import java.util.regex.Pattern;


public class Tokens {
    // master playlist tokens   
    public static String EXT_X_STREAM_INF = "EXT-X-STREAM-INF";

    // media playlist tokens
    public static  String EXTINF = "EXTINF";
    
    // patterns
    public static Pattern URL_PATTERN2 = Pattern.compile("^(?:https?|ftp)://[^\\s/$.?#]*\\.[^\\s]*$");
    public static Pattern URL_PATTERN = Pattern.compile("\\b(https?|ftp|file)://[-A-Z0-9+&@#/%?=~_|!:,.;]*[-A-Z0-9+&@#/%=~_|]");
}

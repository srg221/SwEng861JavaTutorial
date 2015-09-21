package prototyping;

import java.net.MalformedURLException;

public class ExtTagStream extends ExtTag {

	protected M3u8InputStream inStream;
	
	ExtTagStream(PlayList playList, PlayListScanner scanner, String url) throws MalformedURLException{
		super(playList, scanner);
		String myUrl = url;
		if (!Tokens.urlPattern.matcher(url).matches()){
			// relative url, build complete 
			myUrl = playList.inStream.GetUrlNoFN() + '/' + url;
		}
		inStream = new M3u8InputStream(myUrl, containingList);
		inStream.Download();
	}	
	
	public static ExtTagStream Clone(PlayList playList, PlayListScanner scanner, String url) throws MalformedURLException{
		ExtTagStream clone = new ExtTagStream(playList, scanner, url);
		return clone;
	}
}

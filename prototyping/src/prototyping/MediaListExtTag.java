package prototyping;

import java.net.MalformedURLException;

public class MediaListExtTag extends ExtTagStream {

	MediaListExtTag(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
		super(playList, scanner, url, tagName);
	}
	
	//public static MediaListExtTag Clone(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
	//	MediaListExtTag clone = new MediaListExtTag(playList, scanner, url, tagName);
	//	return clone;
	//}
	
	public static void Initialize(){
		
	}
}

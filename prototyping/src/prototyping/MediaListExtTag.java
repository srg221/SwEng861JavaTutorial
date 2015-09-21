package prototyping;

import java.net.MalformedURLException;

public class MediaListExtTag extends ExtTagStream {

	MediaListExtTag(PlayList playList, PlayListScanner scanner, String url) throws MalformedURLException{
		super(playList, scanner, url);
	}
	
	public static MediaListExtTag Clone(PlayList playList, PlayListScanner scanner, String url) throws MalformedURLException{
		MediaListExtTag clone = new MediaListExtTag(playList, scanner, url);
		return clone;
	}
	
	public static void Initialize(){
		
	}
}

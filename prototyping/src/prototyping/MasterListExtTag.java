package prototyping;

import java.net.MalformedURLException;

public class MasterListExtTag extends ExtTagStream {

	MasterListExtTag(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
		super(playList, scanner, url, tagName);
	}
	
	//public static MasterListExtTag Clone(PlayList playList, PlayListScanner scanner, String url, String tagName) throws MalformedURLException{
	//	MasterListExtTag clone = new MasterListExtTag(playList, scanner, url, tagName);
	//	return clone;
	//}
	
	public static void Initialize(){
		
	}
}

package prototyping;

import java.net.MalformedURLException;

public class MasterListExtTag extends ExtTagStream {

	MasterListExtTag(PlayList playList, PlayListScanner scanner, String url) throws MalformedURLException{
		super(playList, scanner, url);
	}
	
	public static MasterListExtTag Clone(PlayList playList, PlayListScanner scanner, String url) throws MalformedURLException{
		MasterListExtTag clone = new MasterListExtTag(playList, scanner, url);
		return clone;
	}
}

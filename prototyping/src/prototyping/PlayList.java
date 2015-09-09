package prototyping;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

public class PlayList 
{
	private boolean isMaster;
	private M3u8InputStream inStream;
	private List<extTag> validTags;
	private List<String> invalidTags;
	
	public PlayList(String url) {
		try {
			inStream = new M3u8InputStream(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inStream.Download();
	}
}


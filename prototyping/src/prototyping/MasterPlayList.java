package prototyping;

import java.net.MalformedURLException;

public class MasterPlayList extends PlayList {
	
	public MasterPlayList(String url) {
		super(url);
		isMaster = true;
	}
	
	public MasterPlayList(M3u8InputStream m3u8In){
		super();
		inStream = m3u8In;
		isMaster = true;
	}
	
	public void Validate(MediaStream mediaStream){
		// pick out EXT-X-STREAM-INF tags, create (tags?) mediaplaylists. add to mediastream list (& download urls)
	}
}

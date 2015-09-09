package prototyping;

public class MediaPlayList extends PlayList {

	public MediaPlayList(String url) {
		super(url);
		isMaster = false;
	}
	
	public MediaPlayList(M3u8InputStream m3u8In){
		super();
		inStream = m3u8In;
		isMaster = true;
	}
	
	public void Validate(MediaStream mediaStream){
		// pick out #EXTINF tags, create MediaListExtTags (& download urls)
	}
}

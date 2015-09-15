package prototyping;

import java.io.IOException;

public class MediaPlayList extends PlayList {

	public MediaPlayList(String url, MediaStream inMediaStream) {
		super(url, inMediaStream);
		isMaster = false;
	}
	
	public MediaPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream){
		super();
		inStream = m3u8In;
		mediaStream = inMediaStream;
		//isMaster = false;
	}
	
	public void Validate(MediaStream mediaStream) throws IOException {
		// pick out #EXTINF tags, create MediaListExtTags (& download urls)
		MediaPlayListValidator validator = new MediaPlayListValidator(this);
	    validator.Validate();
	}

}
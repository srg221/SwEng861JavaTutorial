package prototyping;

import java.io.IOException;

public class MediaPlayList extends PlayList {

	public MediaPlayList(String url) {
		super(url);
		isMaster = false;
	}
	
	public MediaPlayList(M3u8InputStream m3u8In){
		super();
		inStream = m3u8In;
		//isMaster = false;
	}
	
	public void Validate(MediaStream mediaStream) throws IOException {
		// pick out #EXTINF tags, create MediaListExtTags (& download urls)
		MediaPlayListValidator validator = new MediaPlayListValidator(this);
	    validator.Validate();
	}

}
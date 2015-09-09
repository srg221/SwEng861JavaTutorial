package prototyping;

import java.io.IOException;

public class MediaPlayListValidator {
	private PlayListScanner listScanner;
	
	MediaPlayListValidator(MediaPlayList playList) throws IOException{
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
}

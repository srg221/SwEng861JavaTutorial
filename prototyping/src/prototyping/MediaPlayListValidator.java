package prototyping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class MediaPlayListValidator {
	private PlayListScanner listScanner;
	private MediaPlayList mediaPlayList;
	
	MediaPlayListValidator(MediaPlayList playList) throws IOException{
		mediaPlayList = playList;
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
	
	public void Validate() throws IllegalArgumentException, InvocationTargetException, Exception{
		
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.scanner.next();
            if ( line.startsWith("#" + Tokens.EXTINF)){
            	line = listScanner.scanner.next();
            	// line here is URL, so may need not to pass
            	MediaListExtTag tag = new MediaListExtTag(mediaPlayList,listScanner, line);
            	//tag.inStream.Download();  //already done as part of inStream creation
            	// testing
            	if (ExtTag.HasValidator(Tokens.EXTM3U)){
            		ExtTag TestTag =  new ExtTag(mediaPlayList,listScanner);
            		TestTag.Validate(Tokens.EXTM3U);
            	}
            	mediaPlayList.validTags.add(tag);
            }
		}
	}
}


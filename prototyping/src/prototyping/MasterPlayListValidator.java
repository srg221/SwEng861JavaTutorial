package prototyping;

import java.io.IOException;
import java.net.MalformedURLException;

public class MasterPlayListValidator {

	private PlayListScanner listScanner;
	private MasterPlayList masterPlayList;
	
	MasterPlayListValidator(MasterPlayList playList) throws IOException{
		masterPlayList = playList;
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
	
	public void Validate() throws MalformedURLException{
		
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.scanner.next();
            if ( line.startsWith("#" + Tokens.EXT_X_STREAM_INF)){
            	line = listScanner.scanner.next();
            	MasterListExtTag tag = new MasterListExtTag(masterPlayList,line);
            	// tag.inStream.Download();  //already done as part of inStream creation
            	masterPlayList.validTags.add((extTag)tag);
            }
		}
	}
}

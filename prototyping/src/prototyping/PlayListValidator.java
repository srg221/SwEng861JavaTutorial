package prototyping;

import java.io.IOException;

public class PlayListValidator {
	private PlayListScanner listScanner;
	
	PlayListValidator(PlayList playList) throws IOException{
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
	
	public boolean IsMaster(){
		boolean ret = false;
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.scanner.next();
            if ( line.startsWith("#"+Tokens.EXT_X_STREAM_INF)){
            	ret = true;
            	break;
            }
		}
            return ret;
    }

}

package prototyping;

import java.io.IOException;

public class PlayListValidator {
	private PlayListScanner listScanner;
	private PlayList mPlayList;
	
	PlayListValidator(PlayList playList) throws IOException{
		mPlayList = playList;
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
	
	public void Log(String[] fields){
		mPlayList.mediaStream.mErrorLogger.Log(fields);
	}
}

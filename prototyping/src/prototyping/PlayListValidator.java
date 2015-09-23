package prototyping;

import java.io.IOException;
// todo - delete this class
public class PlayListValidator {
	private PlayListScanner listScanner;
	private PlayList myPlayList;
	
	PlayListValidator(PlayList inPlayList) throws IOException{
		myPlayList = inPlayList;
		listScanner = new PlayListScanner(myPlayList.inStream.GetInputStream());
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
	
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		myPlayList.mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		myPlayList.mediaStream.LogTrace(fields, paranoidLevel);
	}
}

package prototyping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;

public class PlayList 
{
	protected boolean isMaster;
	protected    M3u8InputStream inStream = null;
	protected String myURL;
	//protected String myFileName;
	public MediaStream mediaStream;
	public ArrayList<ExtTagStream> validStreamTags = new ArrayList<ExtTagStream>(); //temp
	protected ArrayList<ExtTag> inValidExtTags = new ArrayList<ExtTag>();
	
	protected PlayList(){};

	// this constructor only called for root when we don't know if it is a master
	// or media yet
	public PlayList(String url, MediaStream inMediaStream) {
		try {
			myURL = url;
			mediaStream = inMediaStream;
			inStream = new M3u8InputStream(url, this);
		} catch (MalformedURLException e) {
			// this exception unlikely since url was validiated in media stream
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// need to catch I/O exception here, cannot connect to root playlist URL
		inStream.Download();
	}

	// can remove this method and entire PlayListValidator Class
		public void Validate(MediaStream mediaStream) throws IOException{
		PlayListValidator validator = new PlayListValidator(this);
		if (validator.IsMaster()){
			mediaStream.rootPlaylist = new MasterPlayList(inStream, mediaStream);
		}
		else{
			mediaStream.rootPlaylist = new MediaPlayList(inStream, mediaStream);
		}
		
		mediaStream.rootPlaylist.Validate(mediaStream);
	}
	
	public void Validate() throws IOException{
		PlayListScanner listScanner =  new PlayListScanner(inStream.GetInputStream());
		//private PlayList myPlayList;
	    boolean isMaster = false;
			while (listScanner.scanner.hasNext()) {
				String line = listScanner.scanner.next();
	            //if ( line.startsWith("#"+Tokens.EXT_X_STREAM_INF)){
            	String candidateTag = ExtTag.GetCandidateTag(line);
				if ( Tokens.EXT_X_STREAM_INFpattern.matcher(candidateTag).matches()){
	            	isMaster = true;
	            	break;
	            }
			}
			if (isMaster){
				mediaStream.rootPlaylist = new MasterPlayList(inStream, mediaStream);
			}
			else{
				mediaStream.rootPlaylist = new MediaPlayList(inStream, mediaStream);
			}
			
			mediaStream.rootPlaylist.Validate(mediaStream);
	    }
		
	
	public boolean IsMaster(){ return isMaster; }
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		mediaStream.LogRunError(fields, paranoidLevel);
	}
}


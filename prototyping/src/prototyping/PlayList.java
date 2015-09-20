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
	public ArrayList<ExtTagStream> validTags = new ArrayList<ExtTagStream>();
	protected ArrayList<String> invalidTags = new ArrayList<String>();
	
	protected PlayList(){};

	public PlayList(String url, MediaStream inMediaStream) {
		try {
			myURL = url;
			mediaStream = inMediaStream;
			inStream = new M3u8InputStream(url, this);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inStream.Download();
	}
	
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
	
	public boolean IsMaster(){ return isMaster; }
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		mediaStream.LogRunError(fields, paranoidLevel);
	}
}


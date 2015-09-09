package prototyping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;

public class PlayList 
{
	protected boolean isMaster;
	protected    M3u8InputStream inStream;
	public ArrayList<extTag> validTags = new ArrayList<extTag>();
	protected ArrayList<String> invalidTags = new ArrayList<String>();
	
	public PlayList(){};
	public PlayList(String url) {
		try {
			inStream = new M3u8InputStream(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inStream.Download();
	}
	
	public void Validate(MediaStream mediaStream) throws IOException{
		PlayListValidator validator = new PlayListValidator(this);
		if (validator.IsMaster()){
			mediaStream.rootPlaylist = new MasterPlayList(inStream);
		}
		else{
			mediaStream.rootPlaylist = new MediaPlayList(inStream);
		}
		
		mediaStream.rootPlaylist.Validate(mediaStream);
	}
	
	public boolean IsMaster(){ return isMaster; }
}


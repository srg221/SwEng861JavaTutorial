package prototyping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class MediaPlayList extends PlayList {

	public MasterPlayList masterPlayList = null; //referring list, or if this is root remains null
	public ArrayList<ExtTagStream> validMediaStreams = new ArrayList<ExtTagStream>();
	public MediaPlayList(String url, MediaStream inMediaStream, PlayList master) {
		super(url, inMediaStream);
		masterPlayList = (MasterPlayList) master;
		isMaster = false;
	}

	public MediaPlayList(String url, MediaStream inMediaStream) {
		super(url, inMediaStream);
		// masterPlayList = null; // this media list is the root, stays null since no master
		isMaster = false;
	}
	
	// media stream already resolved - 
	public MediaPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream, PlayList master){
		super();
		inStream = m3u8In;
		mediaStream = inMediaStream;
		myURL = m3u8In.mUrl.toString();
		masterPlayList = (MasterPlayList) master;
		isMaster = false;
	}
	

	public MediaPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream){
		super();
		inStream = m3u8In;
		mediaStream = inMediaStream;
		myURL = m3u8In.mUrl.toString();
		// masterPlayList = null; // this media list is the root, stays null since no master
		isMaster = false;
	}
	
	public void Validate(MediaStream mediaStream) throws IOException {
		// pick out #EXTINF tags, create MediaListExtTags (& download urls)
		MediaPlayListValidator validator = new MediaPlayListValidator(this);
	    try {
			validator.ValidateEx();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
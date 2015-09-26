package prototyping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
//import java.net.MalformedURLException;
import java.util.ArrayList;

public class MasterPlayList extends PlayList {
	
	//public ArrayList<ExtTagStream> validMediaPlayLists = new ArrayList<ExtTagStream>();
	
	public MasterPlayList(String url, MediaStream mediaStream) {
		super(url, mediaStream);
		isMaster = true;
	}
	
	// inStream already resolved/created
	public MasterPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream){
		super(m3u8In.myUrl.toString(),inMediaStream);
		mediaStream = inMediaStream;
		inStream = m3u8In;
		isMaster = true;
	}
	
	public void Validate(MediaStream mediaStream) throws IOException{
		// pick out EXT-X-STREAM-INF tags, create (tags?) mediaplaylists. add to mediastream list (& download urls)
		MasterPlayListValidator validator = new MasterPlayListValidator(this);
	    //validator.Validate();
	    try {
			validator.ValidateEx();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

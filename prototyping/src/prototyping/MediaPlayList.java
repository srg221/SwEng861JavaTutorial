package prototyping;


import java.util.ArrayList;

public class MediaPlayList extends PlayList {

	public MasterPlayList masterPlayList = null; // referring list, or if this
													// is root remains null
	public ArrayList<ExtTagStream> validMediaStreams = new ArrayList<ExtTagStream>();

	public MediaPlayList(String url, MediaStream inMediaStream,
			PlayList master, int inLineNo) {
		super(url, inMediaStream);
		lineNum = inLineNo;
		masterPlayList = (MasterPlayList) master;
		isMaster = false;
	}

	public MediaPlayList(String url, MediaStream inMediaStream, int inLineNo) {
		super(url, inMediaStream);
		lineNum = inLineNo;
		// (String url, MediaStream inMediaStream)
		// masterPlayList = null; // this media list is the root, stays null
		// since no master
		isMaster = false;
	}

	// media InputStream already resolved/created for these
	public MediaPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream,
			PlayList master) {
		// super();
		inStream = m3u8In;
		mediaStream = inMediaStream;
		myURL = m3u8In.myUrl.toString();
		masterPlayList = (MasterPlayList) master;
		isMaster = false;
	}

	// when it is top level
	public MediaPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream) {
		// super();
		inStream = m3u8In;
		mediaStream = inMediaStream;
		myURL = m3u8In.myUrl.toString();
		// masterPlayList = null; // this media list is the root, stays null
		// since no master
		isMaster = false;
	}

	public void Validate(MediaStream mediaStream) {
		// pick out media list tags & create MediaListExtTags (& download urls)
		MediaPlayListValidator validator;
		validator = new MediaPlayListValidator(this);
		validator.ValidateEx();
	}

}
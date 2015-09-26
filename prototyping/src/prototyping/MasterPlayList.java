package prototyping;


public class MasterPlayList extends PlayList {
	
	//public ArrayList<ExtTagStream> validMediaPlayLists = new ArrayList<ExtTagStream>();
	
	//public MasterPlayList(String url, MediaStream mediaStream ) {
		//super(url, mediaStream, inLineNo);
		//isMaster = true;
	//}
	
	// inStream already resolved/created
	public MasterPlayList(M3u8InputStream m3u8In, MediaStream inMediaStream){
		//super(m3u8In.myUrl.toString(),inMediaStream);
		mediaStream = inMediaStream;
		inStream = m3u8In;
		isMaster = true;
	}
	
	public void Validate(MediaStream mediaStream){
		// pick out Master playlist tags & create MediaListExtTags (& download urls)
		MasterPlayListValidator validator;
		validator = new MasterPlayListValidator(this);
		validator.ValidateEx();
	}
}

package prototyping;

import java.net.MalformedURLException;

public class extTag {

	protected M3u8InputStream inStream;
	protected PlayList containingList;
	
	extTag(PlayList playList, String url) throws MalformedURLException{
		// parent reference
		containingList = playList;
		String myUrl = url;
		if (!Tokens.URL_PATTERN.matcher(url).matches()){
			// relative url, build complete 
			myUrl = playList.inStream.GetUrlNoFN() + '/' + url;
		}
		inStream = new M3u8InputStream(myUrl, containingList);
		inStream.Download();
	}
	

	public void LogStreamError(String[] fields, int paranoidLevel){
		containingList.mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		containingList.mediaStream.LogRunError(fields, paranoidLevel);
	}
	
}

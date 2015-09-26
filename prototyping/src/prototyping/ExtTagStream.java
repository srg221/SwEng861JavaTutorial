package prototyping;

import java.net.MalformedURLException;

public class ExtTagStream extends ExtTag {

	protected M3u8InputStream inStream = null;

	ExtTagStream(PlayList playList, PlayListScanner scanner, String tagName,
			String url) throws MalformedURLException {
		super(playList, scanner, tagName);
		// does this tag have a stream associated with it?
		if (url != null) {
			String myUrl = url;
			if (!Tokens.urlPattern.matcher(url).matches()) {
				// relative url, build complete
				myUrl = playList.inStream.GetUrlNoFN() + '/' + url;
			}
			inStream = new M3u8InputStream(myUrl, containingList);
			// only attempt to download if inStream is valid after creation
			if (inStream.IsValid())
				inStream.Download();
			// set this tag's validation status equal to result of download
			validated = inStream.IsValid();
		}
	}

	ExtTagStream(PlayList playList, PlayListScanner scanner, String tagName)
			throws MalformedURLException {
		super(playList, scanner, tagName);
	}

	// protected void Validate(){
	// if (HasStream(myTagName)){
	// String myUrl;
	// inStream = new M3u8InputStream(myUrl, containingList);
	// inStream.Download();
	// }
	// }

	protected void GetStream(String urlLine) {
		String myUrl = urlLine;
		// construct path relative to my Playlist's stream location
		if (!Tokens.urlPattern.matcher(urlLine).matches()) {
			// relative url, build complete
			myUrl = containingList.inStream.GetUrlNoFN() + '/' + myUrl;
		}
		inStream = new M3u8InputStream(myUrl, containingList);
		// only attempt to download if inStream is valid after creation
		if (inStream.IsValid())
			inStream.Download();
		// set this tag's validation status equal to result of download
		validated = inStream.IsValid();
	}

	public static ExtTagStream Clone(PlayList playList,
			PlayListScanner scanner, String tagName, String url)
			throws MalformedURLException {
		ExtTagStream clone = new ExtTagStream(playList, scanner, tagName, url);
		return clone;
	}

	// need methods to pattern match url ending with playlist or .ts, and
	// relative path for same
}

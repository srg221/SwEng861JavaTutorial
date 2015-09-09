package prototyping;
import java.io.IOException;
import java.util.List;


public class MediaStream {
	public PlayList rootPlaylist;
	public List<MediaPlayList> mediaPlayLists;
	
	MediaStream(String rootUrl) {
		rootPlaylist = new PlayList(rootUrl);
	}
	
	public void Validate() throws IOException {
		rootPlaylist.Validate(this);
		// if rootPlayList is master iterate through mediaPlayLists and validate - downloading media for now
	}

}

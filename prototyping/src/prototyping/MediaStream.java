package prototyping;
import java.util.List;


public class MediaStream {
	private PlayList rootPlaylist;
	private List<MediaPlayList> mediaPlayLists;
	
	MediaStream(String rootUrl) {
		rootPlaylist = new PlayList(rootUrl);
	}

}

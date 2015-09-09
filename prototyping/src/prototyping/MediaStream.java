package prototyping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MediaStream {
	public PlayList rootPlaylist;
	public ArrayList<MediaPlayList> mediaPlayLists = new ArrayList<MediaPlayList>();
	
	MediaStream(String rootUrl) {
		rootPlaylist = new PlayList(rootUrl);
	}
	
	public void Validate() throws IOException {
		rootPlaylist.Validate(this);
		// if rootPlayList is master iterate through mediaPlayLists 
		// and validate (downloading media for now)
		if (rootPlaylist.IsMaster()){
			for (extTag tag : rootPlaylist.validTags){
				// build media playlists
				MediaPlayList mediaPlayList = new MediaPlayList(tag.inStream);
				mediaPlayLists.add(mediaPlayList);
			}
			for (MediaPlayList mediaPlayList : mediaPlayLists){
				mediaPlayList.Validate(this);
			}
			
		}
	}

}

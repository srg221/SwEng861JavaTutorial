package prototyping;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;


public class MediaStream {
	public PlayList rootPlaylist;
	public ArrayList<MediaPlayList> mediaPlayLists = new ArrayList<MediaPlayList>();
	// TODO setup logger and path in constructor
	public SimpleLogger mErrorLogger = null;
	//public Handler mErrorLogHandler = null;
	
	MediaStream(String rootUrl) {
		mErrorLogger = new SimpleLogger();
		//mErrorLogHandler = new FileHandler("test.log", LOG_SIZE, LOG_ROTATION_COUNT);
		//Logger.getLogger("").addHandler(handler);
		rootPlaylist = new PlayList(rootUrl, this);
	}
	
	public void Validate() throws IOException {
		rootPlaylist.Validate(this);
		// if rootPlayList is master iterate through mediaPlayLists 
		// and validate (downloading media for now)
		if (rootPlaylist.IsMaster()){
			for (extTag tag : rootPlaylist.validTags){
				// build media playlists
				MediaPlayList mediaPlayList = new MediaPlayList(tag.inStream, this);
				mediaPlayLists.add(mediaPlayList);
			}
			for (MediaPlayList mediaPlayList : mediaPlayLists){
				mediaPlayList.Validate(this);
			}
			
		}
	}

}

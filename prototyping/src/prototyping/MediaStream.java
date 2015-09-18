package prototyping;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;


public class MediaStream {
	public PlayList rootPlaylist;
	public ArrayList<MediaPlayList> mediaPlayLists = new ArrayList<MediaPlayList>();
	// TODO setup logger and path in constructor
	private SimpleLogger streamLogger = null;
	private SimpleLogger runLogger = null;
	//public Handler mErrorLogHandler = null;
	
	MediaStream(String rootUrl) {

		if (!CreateLoggers(rootUrl)){
			System.out.println("Non-recoverable runtime context error - Exiting...");
			System.exit(-1);
		}
		// rin at max paranoids unless someone sets lower
		streamLogger.SetParanoidLevel(100);
		runLogger.SetParanoidLevel(100);
		// headers
		String[] msg = {"Error Number", "Error Type", "File Name", "Line Number", "Details"};
		streamLogger.Log(msg);
		// timestamp and startup info
		//msg = { System.
		
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

	private boolean CreateLoggers(String rootUrl){

		// set up logs - always using user directories
		String localLogPath = System.getProperty("user.home");
		// temp for parsing convenience only
		URL tmpURL;
		try {
			tmpURL = new URL(rootUrl);
		} catch (MalformedURLException e) {
			// Bad URL
			System.out.println("Bad root URL, cannot parse:\n" + rootUrl);
			return false;
		} 
		// create a local directory for each distinct root url
		String localPath = localLogPath + tmpURL.getPath();
		File relativePath = new File( (String) localPath.subSequence(0, localPath.lastIndexOf('/')) );
		relativePath.mkdirs();
		// find root file name to build log filenames
		String rootFileName = localPath.substring(localPath.lastIndexOf('/'));
		// paranoid
		//String LRootFileName = rootFileName.toLowerCase();
		rootFileName = (String)rootFileName.subSequence(0, rootFileName.lastIndexOf('.'));
		// make complete paths, delete files if they already exist
		String runLogPath = relativePath + rootFileName + "RunLog.csv";
		String streamLogPath = relativePath + rootFileName + "StreamLog.csv";
		File runLogFile = new File(runLogPath);
		File streamLogFile = new File(streamLogPath);
		if (runLogFile.exists()) 
		{
			runLogFile.delete();
		}
		if (streamLogFile.exists()) 
		{
			streamLogFile.delete();
		}
		// non-recoverable runtime context errors
		try {
			streamLogger = new SimpleLogger(streamLogPath);
		} catch (IOException e) {
			// File Open error
			System.out.println("Cannot create output file for stream logger:\n" + streamLogPath);
			return false;
		}
		try {
			runLogger = new SimpleLogger(runLogPath);
		} catch (IOException e) {
			// File Open error
			System.out.println("Cannot create output file for run logger:\n:\n" + runLogPath);
			return false;
		}
		//mErrorLogHandler = new FileHandler("test.log", LOG_SIZE, LOG_ROTATION_COUNT);
		//Logger.getLogger("").addHandler(handler);
		return true;
	}
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		streamLogger.Log(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		runLogger.Log(fields, paranoidLevel);
	}

}

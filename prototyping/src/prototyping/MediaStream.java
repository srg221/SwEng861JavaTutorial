package prototyping;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import prototyping.M3u8InputStream.MSG;


public class MediaStream {
	public PlayList rootPlaylist;
	public ArrayList<MediaPlayList> mediaPlayLists = new ArrayList<MediaPlayList>();
	// TODO setup logger and path in constructor
	private SimpleLogger streamLogger = null;
	private SimpleLogger traceLogger = null;
	//public Handler mErrorLogHandler = null;
	
	MediaStream(String rootUrl) throws NoSuchMethodException, SecurityException {

		// create logs
		if (!CreateLoggers(rootUrl)){
			System.out.println("Non-recoverable runtime context error - Exiting...");
			System.exit(-1);
		}
		// using max paranoids unless someone sets lower
		streamLogger.SetParanoidLevel(100);
		traceLogger.SetParanoidLevel(100);
		
		// headers 
		MSG msg = new MSG("Error No.","Time", "Location", "Severity", "Type", "Details");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), "", Err.Sev.INFO.toString(),"", "Analysis Started: Root URL = "+rootUrl);
		LogStreamError(msg);
		msg = new MSG("Trace No.","Time","Location", "Context","Details");
		LogTrace(msg);
		// trace timestamp creation time 
		msg = new MSG(GetTimeStamp(),"","","Media Stream Created: Root URL = "+rootUrl );
		LogTrace(msg, 20);
		
		//Initialize the validators
		ExtTag.Initialize();  // this also calls down to leafs
		
		// create the root playlist
		rootPlaylist = new PlayList(rootUrl, this);	
	}
	
	public void Validate() throws IOException {
		//validate the root
		rootPlaylist.Validate();
		// if rootPlayList is a master iterate through mediaPlayLists 
		// and validate (downloading media playlists as required)
		if (rootPlaylist.IsMaster()){
			for (ExtTag tag : rootPlaylist.validTags){
				// build media playlists
				if (tag.myTagName.equals(Tokens.EXT_X_STREAM_INF)){
					ExtTagStream extTagStream = (ExtTagStream) tag;
					MediaPlayList mediaPlayList = new MediaPlayList(extTagStream.inStream, this, rootPlaylist);
					mediaPlayLists.add(mediaPlayList);
				}
			}
			// validate the children media play lists downloading .TS 
			// files as you go
			for (MediaPlayList mediaPlayList : mediaPlayLists){
				mediaPlayList.Validate(this);
			}
			// todo - cross list validate since we have all the info
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
			// Bad URL - need to log to console since can't make logs
			System.out.println("Bad root URL, cannot parse:\n" + rootUrl);
			return false;
		} 
		// create a local directory for each distinct root url
		String localPath = localLogPath + tmpURL.getPath();
		File relativePath = new File( (String) localPath.subSequence(0, localPath.lastIndexOf('/')) );
		relativePath.mkdirs();
		// find root file name to build log filenames
		String rootFileName = localPath.substring(localPath.lastIndexOf('/'));
		// paranoid here
		//String LRootFileName = rootFileName.toLowerCase();
		rootFileName = (String)rootFileName.subSequence(0, rootFileName.lastIndexOf('.'));
		// make complete paths, delete files if they already exist
		String traceLogPath = relativePath + rootFileName + "TraceLog.csv";
		String streamLogPath = relativePath + rootFileName + "StreamLog.csv";
		File runLogFile = new File(traceLogPath);
		File streamLogFile = new File(streamLogPath);
		if (runLogFile.exists()) 
		{
			runLogFile.delete();
		}
		if (streamLogFile.exists()) 
		{
			streamLogFile.delete();
		}
		// non-recoverable runtime errors, can't create log files,
		// let console know and exit
		try {
			streamLogger = new SimpleLogger(streamLogPath, ',');
		} catch (IOException e) {
			// File Open error
			System.out.println("Cannot create output file for stream logger:\n" + streamLogPath);
			return false;
		}
		try {
			traceLogger = new SimpleLogger(traceLogPath, ',');
		} catch (IOException e) {
			// File Open error
			System.out.println("Cannot create output file for run logger:\n" + traceLogPath);
			return false;
		}

		System.out.println("Stream error log created:\n" + streamLogPath);
		System.out.println("Run error log created:\n" + traceLogPath);
		// success if get this far
		return true;
	}
	
	// logging utils
	
	String GetTimeStamp(){
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		streamLogger.Log(fields, paranoidLevel);
	}

	public void LogTrace(String[] fields, int paranoidLevel){
		traceLogger.Log(fields, paranoidLevel);
	}

	public void LogStreamError(String[] fields){
		streamLogger.Log(fields);
	}

	public void LogTrace(String[] fields){
		traceLogger.Log(fields);
	}

	// some wrappers to make code reading easier, would be simpler
    // if java let you overload operators
    public class MSG{
    	private ArrayList<String> fields;
    	
    	public MSG(String... infields){
    		fields = new ArrayList<String>();
    		for (String field : infields){
    			fields.add(field);
    		}
    	}
    	
    	public MSG(ArrayList<String> infields){
       		fields = new ArrayList<String>();
    		for (String field : infields){
    			fields.add(field);
    		}
    	}
    }
    
    // for local
 	public void LogStreamError(MSG msg, int paranoidLevel){
		streamLogger.Log(msg.fields, paranoidLevel);
	}

	public void LogTrace(MSG msg, int paranoidLevel){
		traceLogger.Log(msg.fields, paranoidLevel);
	}
	
	public void LogStreamError(MSG msg){
		streamLogger.Log(msg.fields);
	}

	public void LogTrace(MSG msg){
		traceLogger.Log(msg.fields);
	}
	
	// for contained
 	public void LogStreamError(ArrayList<String> fields, int paranoidLevel){
		MSG msg = new MSG(fields);
 		streamLogger.Log(msg.fields, paranoidLevel);
	}

	public void LogTrace(ArrayList<String> fields, int paranoidLevel){
		MSG msg = new MSG(fields);
		traceLogger.Log(msg.fields, paranoidLevel);
	}
	
	public void LogStreamError(ArrayList<String> fields){
		MSG msg = new MSG(fields);
 		streamLogger.Log(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields){
		MSG msg = new MSG(fields);
		traceLogger.Log(msg.fields);
	}
	
}

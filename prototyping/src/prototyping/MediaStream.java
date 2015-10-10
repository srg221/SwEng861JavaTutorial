package prototyping;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MediaStream {
	public PlayList rootPlaylist;
	public ArrayList<MediaPlayList> mediaPlayLists = new ArrayList<MediaPlayList>();
	// TODO setup logger and path in constructor
	private SimpleLogger streamLogger = null;
	private SimpleLogger traceLogger = null;
	public String rootDirectory = null;
	
	MediaStream(String rootUrl, String rootDirectory, int streamLogLevel, int traceLogLevel) {

		this.rootDirectory = new String(rootDirectory);
		// temps - these ones are to build path for logs and 
		// validate root URL and local file system access check
		String localPath = rootDirectory;
		URL tmpURL;
		try {
			tmpURL = new URL(rootUrl);
			// create a local directory for each distinct root url
			localPath = localPath + tmpURL.getPath();
		} catch (MalformedURLException e) {
			// Bad URL - need to log to console since can't make logs
			System.out.println("Bad root URL, cannot parse:\n" + rootUrl);
			System.exit(-1);
		} 
		File relativePath = new File( (String) localPath.subSequence(0, localPath.lastIndexOf('/')) );
		try{
			relativePath.mkdirs();
		}catch (SecurityException e){
			// still no logs yet, so go to console
			System.out.println("Can not create local directory path:" + relativePath.getPath());
			System.exit(-1);
		}

		// find root file name to build log filenames
		String rootFileName = localPath.substring(0, localPath.lastIndexOf('.'));

		// create logs
		CreateLoggers(rootFileName);
		// using max paranoids unless someone sets lower, production code should at least 
		// pass levels in constructor, provide api, or read from shared mem so can change on the fly
		streamLogger.SetParanoidLevel(streamLogLevel);
		traceLogger.SetParanoidLevel(traceLogLevel);
		
		// Stream log header & start informational message
		MSG msg = new MSG("Error No.","Time", "Location", "Severity", "Type", "Details");
		LogStreamError(msg);
		msg = new MSG(GetTimeStamp(), "", Err.Sev.INFO.toString(),"", "Analysis Started: Root URL = "+rootUrl);
		LogStreamError(msg);
		// Trace (debug) log header and start msg
		msg = new MSG("Trace No.","Time","Current Stream Item", "Context","Details");
		LogTrace(msg);
		msg = new MSG(GetTimeStamp(),"","","Media Stream Created: Root URL = "+rootUrl );
		LogTrace(msg, 20);
				
		//Initialize the ExtTag validators - this also calls down to leafs
		if (!ExtTag.Initialize()) {
			// this is a coding error, stacks are dumped in ExtTag and its leaf classes
			// post fatal internal error and exit here
			msg = new MSG(GetTimeStamp(), "Startup" , Err.Sev.ERROR.toString(), Err.Type.INTERNAL.toString(), "Cannot continue - Exiting...");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), "Media Stream Creation", Context() , "ExtTag Validators Initialzation Fail");
			LogTrace(msg);
			System.exit(-1);
		}
		
		// create the root playlist
		rootPlaylist = new PlayList(rootUrl, this);	
	}
	
	// constructor with log levels, but no local root path
	MediaStream(String rootUrl, int streamLogLevel, int traceLogLevel) {
		this(rootUrl,System.getProperty("user.home"), streamLogLevel, streamLogLevel);
	}
	
	// constructor with no log levels
	MediaStream(String rootUrl, String rootDirectory) {
		this (rootUrl,  rootDirectory, 100, 100);
	}
	
	// constructor with no local root path, use user home directory
	MediaStream(String rootUrl) {
		this(rootUrl, System.getProperty("user.home"), 100,100);
	}
	

	public void Validate() {
		//validate the root
		rootPlaylist.Validate();
		// if rootPlayList is a master iterate through mediaPlayLists 
		// and validate (downloading media playlists as required)
		if (rootPlaylist.IsMaster()){
			for (ExtTag tag : rootPlaylist.validTags){
				// build media playlists
				if (tag.myTagName.equals(Tokens.EXT_X_STREAM_INF)){
					ExtTagStream extTagStream = (ExtTagStream) tag;
					MediaPlayList mediaPlayList = new MediaPlayList(extTagStream.inStream, this, rootPlaylist );
					if (mediaPlayList.IsValid())
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

	private void CreateLoggers(String rootFileName){

		// make complete paths to log files, delete files if they already exist
		String traceLogPath = rootFileName + "TraceLog.csv";
		String streamLogPath = rootFileName + "StreamLog.csv";
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
		}
		try {
			traceLogger = new SimpleLogger(traceLogPath, ',');
		} catch (IOException e) {
			// File Open error
			System.out.println("Cannot create output file for run logger:\n" + traceLogPath);
		}
		// output to console since would need to find logs to see the log created messages
		System.out.println("Stream error log created:\n" + streamLogPath);
		System.out.println("Run trace log created:\n" + traceLogPath);	
		// success if get this far
	}
	
	// logging utils for trace and stream validation logs
	// Pay no attention to the man behind the curtain, i.e. has
	// some neat ideas, but not production quality...
	
	static String Context() {
		String context = new String("Context:");
		context += context
				+ Thread.currentThread().getStackTrace()[2].getFileName();
		context += "::"
				+ Thread.currentThread().getStackTrace()[2].getClassName();
		context += "::"
				+ Thread.currentThread().getStackTrace()[2].getMethodName();
		context += "::Line:"
				+ Thread.currentThread().getStackTrace()[2].getLineNumber();
		return context;
	}
	
	static String GetTimeStamp(){
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
    
    // for local logging at "Media Stream Level"
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
	
	// for contained (Playlist down to tags)
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

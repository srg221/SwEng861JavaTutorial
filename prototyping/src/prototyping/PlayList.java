package prototyping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import prototyping.MediaStream.MSG;

public class PlayList {
	protected boolean isMaster;
	protected M3u8InputStream inStream = null;
	protected String myURL;
	// protected String myFileName;
	public MediaStream mediaStream;  // containng stream
	public ArrayList<ExtTag> validTags = new ArrayList<ExtTag>();
	private boolean validated = true; // assume success
	protected ArrayList<ExtTag> inValidExtTags = new ArrayList<ExtTag>();
	protected int lineNum = 0; 	// containing list line number, remains zero if
								// top level
	public int version = -1; 	// sentinel value to indicate never set, equiv to ver 1 
								// in any compatiblity check (use abs value)

	protected PlayList() {
	};

	// this constructor only called for root when we don't know if it is a
	// master
	// or media yet
	public PlayList(String url, MediaStream inMediaStream) {
		myURL = url;
		mediaStream = inMediaStream;
		inStream = new M3u8InputStream(url, this);
		// only attempt to download if inStream is valid after creation
		if (inStream.IsValid())
			inStream.Download();
		// set this tag's validation status equal to result of download
		validated = inStream.IsValid();
	}

	// anyone can mark bad
	public void MarkBad() {
		validated = false;
	}

	public boolean IsValid() {
		return validated;
	}

	public String toString() {
		if (inStream != null && inStream.GetFile() != null) {
			return inStream.GetFile().getPath();
		} else {
			return myURL;
		}
	}

	public String Location() {
		// if (lineNum == 0){
		// try {
		// throw new
		// DebugException(Thread.currentThread().getStackTrace()[1].getClassName()
		// + " lineNum == 0");
		// } catch (DebugException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		return toString() + "::LINE::" + Integer.toString(lineNum);
	}

	public String Context() {
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

	String GetTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}

	// // can remove this method and entire PlayListValidator Class
	// public void Validate(MediaStream mediaStream) throws IOException{
	// PlayListValidator validator = new PlayListValidator(this);
	// if (validator.IsMaster()){
	// mediaStream.rootPlaylist = new MasterPlayList(inStream, mediaStream);
	// }
	// else{
	// mediaStream.rootPlaylist = new MediaPlayList(inStream, mediaStream);
	// }
	//
	// mediaStream.rootPlaylist.Validate(mediaStream);
	// }

	// this signature is meant to be overridden, not an abstract class so can't
	// make abstract...
	public void Validate(MediaStream mediaStream) {
	};

	public void Validate() {
		PlayListScanner listScanner = new PlayListScanner(
				inStream.GetInputStream());
		// private PlayList myPlayList;
		boolean isMaster = false;
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.GetNextLine();
			// if ( line.startsWith("#"+Tokens.EXT_X_STREAM_INF)){
			String candidateTag = ExtTag.GetCandidateTag(line);
			if (MasterListExtTag.HasValidator(candidateTag)) {
				isMaster = true;
				break;
			}
			if (MediaListExtTag.HasValidator(candidateTag)) {
				isMaster = false;
				break;
			}
		}
		if (isMaster) {
			mediaStream.rootPlaylist = new MasterPlayList(inStream, mediaStream);
		} else {
			mediaStream.rootPlaylist = new MediaPlayList(inStream, mediaStream);
		}

		mediaStream.rootPlaylist.Validate(mediaStream);
	}

	public boolean IsMaster() {
		return isMaster;
	}

	// More logging stuff/utilities
	// some wrappers to make code reading easier, would be simpler
	// if java let you overload operators

	public class MSG {
		private ArrayList<String> fields;

		public MSG(String... infields) {
			fields = new ArrayList<String>();
			// fields.add(myURL);
			for (String field : infields) {
				fields.add(field);
			}
		}

		// for contained
		public MSG(ArrayList<String> infields) {
			fields = new ArrayList<String>();
			// fields.add(myURL);
			for (String field : infields) {
				fields.add(field);
			}
		}
	}

	// for use at this level ("PlayList")
	public void LogStreamError(MSG msg) {
		mediaStream.LogStreamError(msg.fields);
	}

	public void LogTrace(MSG msg) {
		mediaStream.LogTrace(msg.fields);
	}

	public void LogStreamError(MSG msg, int paranoid) {
		mediaStream.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(MSG msg, int paranoid) {
		mediaStream.LogTrace(msg.fields, paranoid);
	}

	// for contained levels ("ExtTag level")
	public void LogStreamError(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		mediaStream.LogStreamError(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		mediaStream.LogTrace(msg.fields);
	}

	public void LogStreamError(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		mediaStream.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		mediaStream.LogTrace(msg.fields, paranoid);
	}

}

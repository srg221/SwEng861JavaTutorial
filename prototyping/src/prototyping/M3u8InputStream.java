package prototyping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Base64;
//import java.util.logging.Handler;
//import java.util.logging.Logger;

import java.util.Date;

public class M3u8InputStream {

	public URL myUrl = null;
	public String myStrUrl = null;
	private boolean validated = true; //assume success
	private InputStream myInputStream = null;
	private File myLocalFile = null;
	private String myLocalRootPath = "";
	private PlayList myPlayList;
	private ExtTag myTag = null;


	// this one belongs to a playlist
	public M3u8InputStream(String url, PlayList containingList){
		myPlayList = containingList;
		myStrUrl = new String(url);
		//myLocalRootPath = System.getProperty("user.home");
		myLocalRootPath = new String(myPlayList.mediaStream.rootDirectory);
		try {
			myUrl = new URL(url);
		} catch (MalformedURLException e) {
			validated = false;
			MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "Malformed Playlist URL "+myStrUrl);
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context(), "Malformed Playlist URL "+myStrUrl);
			LogTrace(msg, 20);
		}
	}


	// this one belongs to an ExtTagStream
	public M3u8InputStream(String url, ExtTag containingTag) {
		myPlayList = containingTag.containingList;
		myTag = containingTag;
		myStrUrl = new String(url);
		//myLocalRootPath = System.getProperty("user.home");
		myLocalRootPath = new String(myPlayList.mediaStream.rootDirectory);	
		try {
			myUrl = new URL(url);
		} catch (MalformedURLException e) {
			validated = false;
			MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "Malformed URL "+myStrUrl+ " Exception:"+e.getMessage());
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
			LogTrace(msg, 20);
		}
	}

	// anyone can mark bad
	public void MarkBad() { validated = false; }
	public boolean IsValid() { return validated; }
	
	public void Download() {
		// if need to set connection attributes, say pswd
		java.net.URLConnection conn;
			try {
				conn = myUrl.openConnection();
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Open conn to "+myStrUrl+" success");
				LogTrace(msg, 40);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);				
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.URL.toString(), "Cannot open connection to " +myStrUrl+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				validated = false;
				return;
			}

			// hard coded authentication test (for class only, if not in url)
			// String username = "tomcat";
			// String pswd = "Apac-Hee";
			// String userpass = username + ":" + pswd;
			// check if in url
			// Simple user & password authentication
			if (myUrl.getUserInfo() != null) {
				// need to import org.apache.commons.codec.binary.Base64 to use
				// String basicAuth = "Basic " + new String(new
				// Base64().encode(url.getUserInfo().getBytes()));
				// So do something more explicit
				String basicAuth = "Basic "
						+ javax.xml.bind.DatatypeConverter
								.printBase64Binary(myUrl.getUserInfo()
										.getBytes());
				conn.setRequestProperty("Authorization", basicAuth);
			}

			InputStream connInStream;
			try {
				connInStream = conn.getInputStream();
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Get input stream " +myStrUrl+ " success.");
				LogTrace(msg, 40);
			} catch (IOException e) {
				MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.URL.toString(), "Opening connection to " +myStrUrl+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);
				//e.printStackTrace();
				validated = false;
				return;
			}
			ReadableByteChannel rbc = Channels.newChannel(connInStream);
			// if don't need to set/add request properties can skip all of
			// the above and just do
			// ReadableByteChannel rbc = Channels.newChannel(mUrl.openStream());

			// set up local storage and create file
			String localPath = myLocalRootPath + myUrl.getPath();
			File relativePath = new File((String) localPath.subSequence(0,
					localPath.lastIndexOf('/')));
			relativePath.mkdirs();
			myLocalFile = new File(localPath);
			if (myLocalFile.exists()) {
				myLocalFile.delete();
			}
			try {
				myLocalFile.createNewFile();
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Creating file "+myLocalFile.getPath()+" success");
				LogTrace(msg, 40);				
			} catch (IOException e) {
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);				
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.FILE.toString(), "Create file " +myLocalFile.getPath()+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				validated = false;
				//e.printStackTrace();
				return;
			}

			// make out stream and stream out
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(myLocalFile);
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Open file out stream "+myLocalFile.getPath()+" success");
				LogTrace(msg, 40);		
			} catch (FileNotFoundException e) {
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);				
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.FILE.toString(), "Opening file " +myLocalFile.getPath()+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				// e.printStackTrace();
				validated = false;
				return;
			}
			try {
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Transfer from " +myStrUrl+ " success.");
				LogTrace(msg, 40);
				
			} catch (IOException e) {
				//e.printStackTrace();
				MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.URL.toString(), "Download " +myStrUrl+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);
				validated = false;
				return;
				} finally {
					try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
				}

			try {
				fos.close();
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Closing file out stream "+myLocalFile.getPath()+" success");
				LogTrace(msg, 40);		
			} catch (IOException e) {
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);				
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.FILE.toString(), "Closing file " +myLocalFile.getPath()+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				//e.printStackTrace();
				validated = false;
				return;
			}
		
	}

	public File GetFile() {
		return myLocalFile;
	}

	public InputStream GetInputStream() {
		if (myUrl == null || myLocalFile == null) {
			return null;
		}
		if (myInputStream != null) {
			try {
				myInputStream.close();
			} catch (IOException e) {
				MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
				LogTrace(msg, 20);				
				msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.FILE.toString(), "Closing file " +myLocalFile.getPath()+ " failed. Exception:"+e.getMessage());
				LogStreamError(msg);
				//e.printStackTrace();
				validated = false;
			}
		}
		try {
			myInputStream = (InputStream) new FileInputStream(myLocalFile);
		} catch (FileNotFoundException e) {
			MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "Exception:" + e.getMessage()+":"+e.getCause());
			LogTrace(msg, 20);				
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.ERROR.toString(), Err.Type.FILE.toString(), "Opening file " +myLocalFile.getPath()+ " failed. Exception:"+e.getMessage());
			LogStreamError(msg);
			validated = false;
			//e.printStackTrace();
		}
		return myInputStream;
	}

	public String GetUrlNoFN() {
		String sUrl = myUrl.toString();
		// return sUrl.substring(0, sUrl.indexOf(fName));
		return ((String) sUrl.subSequence(0, sUrl.lastIndexOf('/')));
	}

	public String Location() {
		if (myTag != null)
			return (myTag.Location());
		else {
			return (myPlayList.Location());
		}
	}

	public String Context() {
		String context = new String();
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

	// logging utils at ExtTag level
	// More logging stuff/utilities

	// some wrappers to make code reading easier, would be simpler
	// if java let you overload operators
	public class MSG {
		private ArrayList<String> fields;

		public MSG(String... infields) {
			fields = new ArrayList<String>();
			for (String field : infields) {
				fields.add(field);
			}
		}

		// for contained 
		public MSG(ArrayList<String> infields) {
			fields = new ArrayList<String>();
			for (String field : infields) {
				fields.add(field);
			}
		}
		// was needed at one point in development
		// works, but not used now
		public MSG Prefix(MSG prefix) {
			String[] tmp = new String[fields.size()];
			int i = 0;
			for (String field : fields) {
				tmp[i++] = field;
			}
			fields.clear();
			for (String field : prefix.fields) {
				fields.add(field);
			}
			for (; i >= 0; i--) {
				fields.add(tmp[i]);
			}
			return this;
		}

		public MSG Suffix(MSG suffix) {
			for (String field : suffix.fields) {
				fields.add(field);
			}
			return this;
		}

	}

	// for use at this level -  the "M3u8InputStream level"
	public void LogStreamError(MSG msg) {
		if (myTag != null)
			myTag.LogStreamError(msg.fields);
		else {
			myPlayList.LogStreamError(msg.fields);
		}
	}

	public void LogTrace(MSG msg) {
		if (myTag != null)
			myTag.LogTrace(msg.fields);
		else {
			myPlayList.LogTrace(msg.fields);
		}
	}

	public void LogStreamError(MSG msg, int paranoid) {
		if (myTag != null)
			myTag.LogStreamError(msg.fields, paranoid);
		else {
			myPlayList.LogStreamError(msg.fields, paranoid);
		}
	}

	public void LogTrace(MSG msg, int paranoid) {
		if (myTag != null)
			myTag.LogTrace(msg.fields, paranoid);
		else {
			myPlayList.LogTrace(msg.fields, paranoid);
		}
	}

	// for contained levels - don't exist now
	// maybe never will, showing for completeness
	public void LogStreamError(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		if (myTag != null) {
			myTag.LogStreamError(msg.fields);
		} else {
			myPlayList.LogStreamError(msg.fields);
		}
	}

	public void LogTrace(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		if (myTag != null) {
			myTag.LogTrace(msg.fields);
		} else {
			myPlayList.LogTrace(msg.fields);
		}
	}

	public void LogStreamError(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		if (myTag != null) {
			myTag.LogStreamError(msg.fields, paranoid);
		} else {
			myPlayList.LogStreamError(msg.fields);
		}
	}

	public void LogTrace(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		if (myTag != null) {
			myTag.LogTrace(msg.fields, paranoid);
		} else {
			myPlayList.LogTrace(msg.fields);
		}
	}
}

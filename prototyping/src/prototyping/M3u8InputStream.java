package prototyping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
//import java.util.logging.Handler;
//import java.util.logging.Logger;

import java.util.Date;

import prototyping.ExtTag.MSG;

public class M3u8InputStream {

	public URL myUrl = null;
	public String myStrUrl = null;
	private InputStream myInputStream = null;
	private File myLocalFile = null;
	private String myLocalRootPath = "";
	private PlayList myPlayList;
	private ExtTag myTag = null;

	// private boolean isRoot = false;

	public M3u8InputStream(String url, String localRootPath,
			PlayList containingList) throws MalformedURLException {
		myPlayList = containingList;
		myStrUrl = new String(url);
		myUrl = new URL(url);
		myLocalRootPath = localRootPath;
		// isRoot = true;
	}

	public M3u8InputStream(String url, PlayList containingList)
			throws MalformedURLException {
		myPlayList = containingList;
		myStrUrl = new String(url);
		myUrl = new URL(url);
		myLocalRootPath = System.getProperty("user.home");
	}

	public M3u8InputStream(String url, String localRootPath,
			PlayList containingList, ExtTag containingTag)
			throws MalformedURLException {
		myPlayList = containingList;
		myTag = containingTag;
		myStrUrl = new String(url);
		myUrl = new URL(url);
		myLocalRootPath = localRootPath;
		// isRoot = true;
	}

	public M3u8InputStream(String url, PlayList containingList,
			ExtTag containingTag) throws MalformedURLException {
		myPlayList = containingList;
		myTag = containingTag;
		myStrUrl = new String(url);
		myUrl = new URL(url);
		myLocalRootPath = System.getProperty("user.home");
	}

	public void Download() {
		// if need to set connection attribites, say pswd
		java.net.URLConnection conn;
		try {
			conn = myUrl.openConnection();

			// hardcoded (for class only, if not in url)
			// String username = "tomcat";
			// String pswd = "Apac-Hee";
			// String userpass = username + ":" + pswd;
			// check if in url
			if (myUrl.getUserInfo() != null) {
				// need to import org.apache.commons.codec.binary.Base64 to use
				// String basicAuth = "Basic " + new String(new
				// Base64().encode(url.getUserInfo().getBytes()));
				String basicAuth = "Basic "
						+ javax.xml.bind.DatatypeConverter
								.printBase64Binary(myUrl.getUserInfo()
										.getBytes());
				conn.setRequestProperty("Authorization", basicAuth);
			}
			// this gives a Base64 constructor error
			// String basicAuth = "Basic " + new
			// String(Base64.encodeBase64(userpass.getBytes()));
			// alternate Base64 encode
			// String basicAuth = "Basic " +
			// javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
			// conn.setRequestProperty ("Authorization", basicAuth);
			// conn.addRequestProperty("User-Agent",
			// "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0a2) Gecko/20110613  Firefox/6.0a2");
			InputStream connInStream = conn.getInputStream();
			ReadableByteChannel rbc = Channels.newChannel(connInStream);

			// else just this if don't need to set/add request properties
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
			myLocalFile.createNewFile();

			// stream out
			FileOutputStream fos = new FileOutputStream(myLocalFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			// testing
			MSG msg = new MSG(GetTimeStamp(), Location(), Context(), "download from " +myStrUrl+ " success");
			LogTrace(msg, 20);
			msg = new MSG(GetTimeStamp(), Location(), Err.Sev.INFO.toString(), Err.Type.URL.toString(), "download from " +myStrUrl+ " success");
			LogStreamError(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			MSG msg = new MSG(GetTimeStamp(), Location(), Err.Sev.SEVERE.toString(), Err.Type.URL.toString(), "download from " +myStrUrl+ " failed");
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), Location(), Context(), "download from " +myStrUrl+ " failed");
			LogTrace(msg, 20);
		}
	}

	public File GetFile() {
		return myLocalFile;
	}

	public InputStream GetInputStream() throws IOException {
		if (myUrl == null || myLocalFile == null) {
			return null;
		}
		if (myInputStream != null) {
			myInputStream.close();
		}
		myInputStream = (InputStream) new FileInputStream(myLocalFile);
		return myInputStream;
	}

	public String GetUrlNoFN() {
		// String fName = mUrl.getFile();
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

	// for use at this level
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

	// for contained levels - probably don't exist
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

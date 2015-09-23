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
import java.util.ArrayList;
import java.util.Base64;
//import java.util.logging.Handler;
//import java.util.logging.Logger;


import prototyping.ExtTag.MSG;

public class M3u8InputStream {

	public URL mUrl = null;
	private InputStream mInputStream = null;
	private File mLocalFile = null;
	private String mLocalRootPath = "";
	private PlayList mPlayList;
	private ExtTag mTag = null;
	//private boolean isRoot = false;
		
	public M3u8InputStream(String url, String localRootPath, PlayList containingList) throws MalformedURLException 
	{
		mPlayList = containingList;
		mUrl = new URL(url);
		mLocalRootPath = localRootPath;
		//isRoot = true;
	}
	
	public M3u8InputStream(String url, PlayList containingList) throws MalformedURLException 
	{
		mPlayList = containingList;
		mUrl = new URL(url);
		mLocalRootPath = System.getProperty("user.home");
	}
	
	public M3u8InputStream(String url, String localRootPath, PlayList containingList, ExtTag containingTag) throws MalformedURLException 
	{
		mPlayList = containingList;
		mTag = containingTag;
		mUrl = new URL(url);
		mLocalRootPath = localRootPath;
		//isRoot = true;
	}
	
	public M3u8InputStream(String url, PlayList containingList, ExtTag containingTag) throws MalformedURLException 
	{
		mPlayList = containingList;
		mTag = containingTag;
		mUrl = new URL(url);
		mLocalRootPath = System.getProperty("user.home");
	}
	
	public void Download() 
	{
			// if need to set connection attribites, say pswd
			java.net.URLConnection conn;
			try {
				conn = mUrl.openConnection();

			// hardcoded (for class only, if not in url)
			// String username = "tomcat";
			// String pswd = "Apac-Hee";
			// String userpass = username + ":" + pswd;
			// check if in url
			if (mUrl.getUserInfo() != null) {
			    // need to import org.apache.commons.codec.binary.Base64 to use
				// String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
				String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(mUrl.getUserInfo().getBytes());
				conn.setRequestProperty("Authorization", basicAuth);
			}
			// this gives a Base64 constructor error
			//String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
			// alternate Base64 encode
			//String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
			//conn.setRequestProperty ("Authorization", basicAuth);
	        //conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0a2) Gecko/20110613  Firefox/6.0a2");
	        InputStream connInStream = conn.getInputStream();
	        ReadableByteChannel rbc = Channels.newChannel(connInStream);
			
			// else just this if don't need to set/add request properties
			//ReadableByteChannel rbc = Channels.newChannel(mUrl.openStream());
			
			// set up local storage and create file
			String localPath = mLocalRootPath + mUrl.getPath();
			File relativePath = new File( (String) localPath.subSequence(0, localPath.lastIndexOf('/')) );
			relativePath.mkdirs();
			mLocalFile = new File(localPath);
			if (mLocalFile.exists()) 
			{
				mLocalFile.delete();
			}
			mLocalFile.createNewFile();
			
			// stream out 
			FileOutputStream fos = new FileOutputStream(mLocalFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();	
			// testing
			MSG msg = new MSG("Connected to URL");
			LogStreamError(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				MSG msg = new MSG("Could not download this URL");
				LogStreamError(msg);
			}	
	}
	
	public File GetFile() 
	{
		return mLocalFile;
	}
	
	
	public InputStream GetInputStream() throws IOException 
	{
		if ( mUrl ==  null || mLocalFile == null ) 
		{
			return null;
		}
		if ( mInputStream != null ) 
		{
			 mInputStream.close();
		}
		mInputStream = (InputStream) new FileInputStream(mLocalFile);
		return mInputStream;
	}
	
	public String GetUrlNoFN(){
		//String fName = mUrl.getFile();
		String sUrl = mUrl.toString();
		//return sUrl.substring(0, sUrl.indexOf(fName));
		return ((String)sUrl.subSequence(0, sUrl.lastIndexOf('/')));
	}
	

	// logging utils at ExtTag level
	// More logging stuff/utilities

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
	    	
		   	// for contained
	    	public MSG(ArrayList<String> infields){
		       		fields = new ArrayList<String>();
		    		for (String field : infields){
		    			fields.add(field);
		    		}
	    	}
	    }
	    
	 	// for use at this level	
		public void LogStreamError(MSG msg){
			if (mTag != null)
				mTag.LogStreamError(msg.fields);
			else{
				// todo add 2 blanks MSG newMsg = new MSG("","",msg);
				mPlayList.LogStreamError(msg.fields);
			}
		}

		public void LogTrace(MSG msg){
			if (mTag != null)
				mTag.LogTrace(msg.fields);
		}
		
		public void LogStreamError(MSG msg, int paranoid){
			if (mTag != null)
				mTag.LogStreamError(msg.fields, paranoid);
		}

		public void LogTrace(MSG msg, int paranoid){
			if (mTag != null)
				mTag.LogTrace(msg.fields, paranoid);
		}
		
		// for contained levels - probably don't exist
		public void LogStreamError(ArrayList<String> fields){
			MSG msg = new MSG(fields);
			mTag.LogStreamError(msg.fields);
		}

		public void LogTrace(ArrayList<String> fields){
			MSG msg = new MSG(fields);
			mTag.LogTrace(msg.fields);
		}
		
		public void LogStreamError(ArrayList<String> fields, int paranoid){
			MSG msg = new MSG(fields);
			mTag.LogStreamError(msg.fields, paranoid);
		}

		public void LogTrace(ArrayList<String> fields, int paranoid){
			MSG msg = new MSG(fields);
			mTag.LogTrace(msg.fields, paranoid);
		}
		
}

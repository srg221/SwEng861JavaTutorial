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
import java.util.Base64;
//import java.util.logging.Handler;
//import java.util.logging.Logger;

public class M3u8InputStream {

	private URL mUrl = null;
	private InputStream mInputStream = null;
	private File mLocalFile = null;
	private String mLocalRootPath = "";
	private PlayList mPlayList;
	private boolean isRoot = false;
	
	// not used yet
	void CreatePathAndLog(String localRootPath) {
		mLocalRootPath = localRootPath;
	}
	
	public M3u8InputStream(String url, String localRootPath, PlayList containingList) throws MalformedURLException 
	{
		mPlayList = containingList;
		mUrl = new URL(url);
		mLocalRootPath = localRootPath;
		isRoot = true;
	}
	
	public M3u8InputStream(String url, PlayList containingList) throws MalformedURLException 
	{
		mPlayList = containingList;
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	public void LogStreamError(String[] fields, int paranoidLevel){
		mPlayList.mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		mPlayList.mediaStream.LogRunError(fields, paranoidLevel);
	}
	
}

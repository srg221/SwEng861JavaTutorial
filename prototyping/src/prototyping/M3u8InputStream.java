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
//import java.util.logging.Handler;
//import java.util.logging.Logger;

public class M3u8InputStream {

	private URL mUrl = null;
	private InputStream mInputStream = null;
	private File mLocalFile = null;
	// TODO setup logger and path in constructor
	//private Logger mErrorLogger = null;
	//private Handler mErrorLogHandler = null;
	private String mLocalRootPath = "";
	
	void CreatePathAndLog(String localRootPath) {
		mLocalRootPath = localRootPath;
	}
	
	public M3u8InputStream(String url, String localRootPath) throws MalformedURLException 
	{
		mUrl = new URL(url);
		mLocalRootPath = localRootPath;
		//mErrorLogger = new Logger(localRootPath);
		// mErrorLogHandler = new FileHandler("test.log", LOG_SIZE, LOG_ROTATION_COUNT);
		// Logger.getLogger("").addHandler(handler);
	}
	
	public M3u8InputStream(String url) throws MalformedURLException 
	{
		mUrl = new URL(url);
		mLocalRootPath = System.getProperty("user.home");
	}
	
	public void Download() 
	{
		try 
		{
			// if need to set connection attribites
			//java.net.URLConnection conn = mUrl.openConnection();
	        //conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0a2) Gecko/20110613  Firefox/6.0a2");
	        //InputStream connInStream = conn.getInputStream();
	        //ReadableByteChannel rbc1 = Channels.newChannel(connInStream);
			ReadableByteChannel rbc = Channels.newChannel(mUrl.openStream());
			
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
		} 
		catch (IOException e) 
		{
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
}

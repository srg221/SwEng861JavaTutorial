package prototyping;
import java.io.*;

// simple console test program for MediaStream.java
public class Main {

	public static void main(String[] args) {

		try {
				System.out.println("url?:\n");
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				String url = bufferRead.readLine();
				// for quick testing w/o typing @ console
				if (url.isEmpty()) 
				{
					//url = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
					//url = "ftp://tomcat:Apac-Hee@146.186.90.203/Arris/ipad.m3u8";
					//url = "http://tomcat:Apac-Hee@146.186.90.203:8080/Arris/ipad.m3u8";
					//url = "http://146.186.90.203:8080/Arris/ipad.m3u8";
					//url = "http://localhost/Arris/ipad.m3u8";	
					//url = "http://127.0.0.1/Arris/ipad.m3u8";
				}
				// create an instance of the MediaStream validator
				MediaStream testMediaStream = new MediaStream(url);
				// validate it
				testMediaStream.Validate();
				
			} 
		catch (Exception e) 
		{
			// for debug only, all exceptions should be caught in app
			e.printStackTrace();
		}
	}

}

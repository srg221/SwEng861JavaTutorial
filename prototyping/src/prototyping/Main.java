package prototyping;
import java.io.*;
import java.net.MalformedURLException;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		   M3u8InputStream test;
		try {
				System.out.println("url?:");
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				String url = bufferRead.readLine();
				if (url.isEmpty()) 
				{
					url = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
				}
				
				//test = new M3u8InputStream(url, "C:\\temp");
				test = new M3u8InputStream(url);
				test.Download();
				InputStream iStream = test.GetInputStream(); 
			} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
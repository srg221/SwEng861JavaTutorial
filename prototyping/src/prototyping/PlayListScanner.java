package prototyping;

import java.io.InputStream;
import java.util.Scanner;

public class PlayListScanner {

	public Scanner scanner;
	
	PlayListScanner( InputStream inStream) 
	{
		scanner = new Scanner(inStream).useDelimiter("\\r?\\n");  //return followed by newline
	}
}

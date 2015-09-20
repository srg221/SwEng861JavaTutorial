package prototyping;

import java.io.InputStream;
import java.util.Scanner;

public class PlayListScanner {

	public Scanner scanner;
	public int currLineNum = 1;
	public String currLine = "";
	
	PlayListScanner( InputStream inStream ) 
	{
		scanner = new Scanner(inStream).useDelimiter("\\r?\\n");  //return followed by newline
	}
	
	public String GetNextLine(){
		if (scanner.hasNext()) {
			currLineNum++;
			currLine = scanner.next();
			return currLine;
		}
		else return "";
	}
	
	//public boolean HasNextLine() {
	//	return scanner.hasNext();
	//}
}

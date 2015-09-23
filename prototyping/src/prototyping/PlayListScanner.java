package prototyping;

import java.io.InputStream;
import java.util.Scanner;

public class PlayListScanner {

	public Scanner scanner;
	public int currLineNum;
	public String currLine;
	
	PlayListScanner( InputStream inStream ) 
	{
		scanner = new Scanner(inStream).useDelimiter(Tokens.endLine);  //return followed by newline
		currLine = new String("");
		currLineNum = 0;
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

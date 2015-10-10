package prototyping;

import java.io.InputStream;
import java.util.Scanner;

public class PlayListScanner {

	public Scanner scanner;
	public int currLineNum;
	public String currLine;
	
	@SuppressWarnings("resource")
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
	
	public void GoTo(int line){
		if (line < currLineNum){
			scanner.reset();
			currLineNum = 0;
			currLine = "";
		}
	while (currLineNum < line)
		GetNextLine();
	}
	
	public boolean IsBlanksOrComment(String line){
		// line formatting checks
		// check if comment
		if (line.startsWith(Tokens.beginLine) && !line.startsWith(Tokens.tagBegin))
			return true; // well formed comment
		// completely blank lines allowed
		if (line.length() == 0)
			return true;
		
		return false;
	}

	//public boolean HasNextLine() {
	//	return scanner.hasNext();
	//}
}

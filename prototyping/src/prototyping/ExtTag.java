package prototyping;


public class ExtTag {
	protected PlayList containingList;
	protected String containingListName;
	protected String myLine;
	protected int myLineNumber;
	protected PlayListScanner playListScanner;
	
	ExtTag(PlayList playList, PlayListScanner scanner) {
		// parent reference
		containingList = playList;
		playListScanner = scanner;
		myLineNumber = playListScanner.currLineNum;
		myLine = playListScanner.currLine;
		containingListName = containingList.myURL;
	}
	

	public void LogStreamError(String[] fields, int paranoidLevel){
		containingList.mediaStream.LogStreamError(fields, paranoidLevel);
	}

	public void LogRunError(String[] fields, int paranoidLevel){
		containingList.mediaStream.LogRunError(fields, paranoidLevel);
	}
	
	public static boolean IsExtTag(String candidate){
		// check if in map of tag strings x tag validator interfaces
		return false;
	}
	
	public static ExtTag Clone(PlayList playList, PlayListScanner scanner){
		ExtTag clone = new ExtTag(playList, scanner);
		return clone;
	}
	
}

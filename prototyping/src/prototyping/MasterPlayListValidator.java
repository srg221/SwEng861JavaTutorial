package prototyping;

import java.io.IOException;
import java.net.MalformedURLException;

public class MasterPlayListValidator {

	private PlayListScanner listScanner;
	private MasterPlayList masterPlayList;
	
	MasterPlayListValidator(MasterPlayList playList) throws IOException{
		masterPlayList = playList;
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
	
	public void Validate() throws MalformedURLException{
		
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.scanner.next();
            if ( line.startsWith("#" + Tokens.EXT_X_STREAM_INF)){
            	line = listScanner.GetNextLine();
            	MasterListExtTag tag = new MasterListExtTag(masterPlayList, listScanner, line, Tokens.EXT_X_STREAM_INF);
            	// tag.inStream.Download();  //already done as part of inStream creation
            	masterPlayList.validTags.add((ExtTagStream)tag);
            }
		}
	}
	
	public void ValidateEx(){
		
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.scanner.next();
			// line formatting checks
			// check if comment
			if (line.startsWith(Tokens.beginLine) && !line.startsWith(Tokens.tagBegin))
				continue; // well formed comment
			// completely blank lines allowed
			if (line.length() == 0)
				continue;
			// At this point needs to be a tag, will never be in this context if on a URL
			if (!line.startsWith(Tokens.tagBegin)){
				//Todo - log error non-comment, non-tag line
				continue;
			}
			// Check for zero extra whitespace at end
			if (line.trim().length() != line.length()){
				// To do - log error tag line with extra whitespace
			}
			
			// Set candidateTag = to EXTM3U or everything up to end tag
			//    need to have error it no endtag found
			// try candidate on each tag type, when find, create tag and validate
			
			
		}
		
	}
}

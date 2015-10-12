package prototyping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class MediaPlayListValidator {
	private PlayListScanner listScanner;
	private MediaPlayList mediaPlayList;
	
	MediaPlayListValidator(MediaPlayList playList){
		mediaPlayList = playList;
		if (playList.inStream.GetInputStream() != null)
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
	
//	public void Validate() throws IllegalArgumentException, InvocationTargetException, Exception{
//		
//		while (listScanner.scanner.hasNext()) {
//			String line = listScanner.scanner.next();
//            if ( line.startsWith("#" + Tokens.EXTINF)){
//            	line = listScanner.scanner.next();
//            	// line here is URL, so may need not to pass
//            	MediaListExtTag tag = new MediaListExtTag(mediaPlayList,listScanner, Tokens.EXTINF, line);
//            	//tag.inStream.Download();  //already done as part of inStream creation
//            	// testing
//            	if (ExtTag.HasValidator(Tokens.EXTM3U)){
//            		ExtTag TestTag =  new ExtTag( mediaPlayList, listScanner, Tokens.EXTM3U);
//            		TestTag.Validate(Tokens.EXTM3U, listScanner);
//            	}
//            	mediaPlayList.validTags.add(tag);
//            }
//		}
//	}
	
	public void ValidateEx(){
		
		MSG msg = new MSG(GetTimeStamp(), mediaPlayList.Location(), Context() , "Starting MPL validation");
		LogTrace(msg, 40);
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.GetNextLine();
			
			// special handling for line 1 since it needs to be a EXTM3U tag
			// can't do further down since only want to complain once...
			if (listScanner.currLineNum == 1 && !Tokens.EXTM3Upattern.matcher(listScanner.currLine).matches()  ){
				msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Err.Sev.SEVERE.toString(), Err.Type.TAG.toString(), "List does not start with EXTM3U Tag");
				LogStreamError(msg);
				msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "List does not start with EXTM3U Tag");
				LogTrace(msg, 20);
			}
			// skip well formed comments and completely blank lines
			if (listScanner.IsBlanksOrComment(line))
				continue;
			
			// At this point needs to be a tag, will never be in this context if on a URL
			if (!line.startsWith(Tokens.tagBegin)){
				//log error non-comment, non-tag line
				msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum) , Err.Sev.WARN.toString(), Err.Type.FORMAT.toString(), "Expected TAG or Comment");
				LogStreamError(msg, 20);
				msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "Non-comment, non-tag, non-blank line, and do not expect URL");
				LogTrace(msg, 20);
				continue;
			}
			
			// Check for zero extra whitespace at begin/end
			if (line.trim().length() != line.length()){
				// log error tag line with extra whitespace
				msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum) , Err.Sev.WARN.toString(), Err.Type.FORMAT.toString(), "Leading or Trailing whitespace");
				LogStreamError(msg, 20);
				msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "Leading or Trailing whitespace");
				LogTrace(msg, 20);
			}
			
			// Set candidateTag = to EXTM3U or everything up to end tag
			//    need to have error it no endtag found
			// try candidate on each tag type, when find, create tag and validate
			String candidateTag = ExtTag.GetCandidateTag(line);
			if (ExtTag.HasValidator(candidateTag)){
				ExtTag extTag =  new ExtTag(mediaPlayList, listScanner, candidateTag);
				// this check is only for successful invoke using reflection, not necessarily a valid tag
				// a failure to invoke indicates a coding error, so only thing we can do is exit 
				// actually for production code should throw an exception that makes it back to the 
				// client
				if (!extTag.Validate(candidateTag, listScanner)){
					msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum) , Err.Sev.ERROR.toString(), Err.Type.INTERNAL.toString(), "Cannot continue - Exiting...");
					LogStreamError(msg);
					msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "ExtTag Validators Invoke fail");
					LogTrace(msg);
					System.exit(-1);
				}
				if (extTag.IsValid()){
					mediaPlayList.validTags.add(extTag);
				}
				else{
					mediaPlayList.inValidExtTags.add(extTag);
				}
				continue;
			}
			if (MediaListExtTag.HasValidator(candidateTag)){
				MediaListExtTag extTag =  new MediaListExtTag(mediaPlayList, listScanner, candidateTag);
				extTag.Validate(candidateTag, listScanner);
				if (extTag.IsValid()){
					mediaPlayList.validTags.add(extTag);
				}
				else{
					mediaPlayList.inValidExtTags.add(extTag);
				}
				continue;
			}
			// check for Master tags in Media - each media tag validator should log error
			// anything with a stream should not download it, should it advance scanner?
			if (MasterListExtTag.HasValidator(candidateTag)){
				MasterListExtTag extTag =  new MasterListExtTag(mediaPlayList, listScanner, candidateTag);
				extTag.Validate(candidateTag, listScanner);
				if (extTag.IsValid()){
					msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum) , Err.Sev.ERROR.toString(), Err.Type.INTERNAL.toString(), "Validation logic fail");
					LogStreamError(msg);
					msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "Logic validated MPL tag in media play list");
					LogTrace(msg);
					extTag.MarkBad();
				}
				mediaPlayList.inValidExtTags.add(extTag);
				continue;
			}
			// line started with tagBegin, but was not recognized - could be no validator or a bad TAG, well say TAG
			msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum) , Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Unrecognized Tag:"+candidateTag);
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "Unrecognized Tag:"+candidateTag);
			LogTrace(msg);			
	}

		// 2nd pass to see if mandatory valid tags found
		
	    // pick out streams, note they were already downloaded as part of tag validation
		for (ExtTag tag : mediaPlayList.validTags){
			if (tag.myTagName.equals(Tokens.EXTINF)){
				if (tag.IsValid())
					mediaPlayList.validMediaStreams.add((ExtTagStream) tag);
				}
			}
		// check format errors (mostly tbd)
		// check if mandatory valid tags exist
		// tbd  - all but begin and end tag
		if (!mediaPlayList.validTags.get(0).myTagName.contentEquals(Tokens.EXTM3U)){
			msg = new MSG(GetTimeStamp(), mediaPlayList.Location(0) , Err.Sev.SEVERE.toString(), Err.Type.FORMAT.toString(), "Missing "+Tokens.EXTM3U);
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), mediaPlayList.Location(0), Context() , "Missing "+Tokens.EXTM3U);
			LogTrace(msg);		
		}
		if (!mediaPlayList.validTags.get((mediaPlayList.validTags.size()-1)).myTagName.contentEquals(Tokens.EXT_X_ENDLIST)){
			msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum) , Err.Sev.ERROR.toString(), Err.Type.FORMAT.toString(), "Missing "+Tokens.EXT_X_ENDLIST);
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), mediaPlayList.Location(listScanner.currLineNum), Context() , "Missing "+Tokens.EXT_X_ENDLIST);
			LogTrace(msg);		
		}
		
		// validate .ts files, like est duration or repeated .ts
		ValidateStreams();
	}
	
	public void ValidateStreams(){
		// go through mediaPlayList.validMediaStreams, 
		// simple case as an example - look for repeating files
		// tbd, validate .ts files
		ArrayList<ExtTagStream> validStreams = new ArrayList<ExtTagStream>(mediaPlayList.validMediaStreams);
		for (ExtTagStream testTag : validStreams){
			String testTagStrUrl = testTag.inStream.myStrUrl;
			int testTagLN = testTag.myLineNumber;
			for (ExtTagStream tag : mediaPlayList.validMediaStreams){
				if (tag.inStream.myStrUrl.contentEquals(testTagStrUrl) &&
						tag.myLineNumber > testTagLN) {
					MSG msg = new MSG(GetTimeStamp(), tag.Location(), Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Url Line "+(tag.myLineNumber+1)+" is duplicate of "+(testTagLN+1) );
					LogStreamError(msg);
					msg = new MSG(GetTimeStamp(), tag.Location(), Context() , "Duplicate Version Tag");
					LogTrace(msg, 20);
				}
			}
			
		}
	}
	
	// logging helpers
	static String GetTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}
	
	public String Context(){
		String context = new String("Context:");
		context += context + Thread.currentThread().getStackTrace()[2].getFileName();
		context += "::" + Thread.currentThread().getStackTrace()[2].getClassName();
		context += "::" + Thread.currentThread().getStackTrace()[2].getMethodName();
		context += "::Line:" + Thread.currentThread().getStackTrace()[2].getLineNumber();
		return context;
	}
	
	public class MSG {
		private ArrayList<String> fields;

		public MSG(String... infields) {
			fields = new ArrayList<String>();
			// fields.add(Integer.toString(myLineNumber));
			// fields.add(myTagName);
			for (String field : infields) {
				fields.add(field);
			}
		}

		// for contained
		public MSG(ArrayList<String> infields) {
			fields = new ArrayList<String>();
			// fields.add(Integer.toString(myLineNumber));
			// fields.add(myTagName);
			for (String field : infields) {
				fields.add(field);
			}
		}
	}

	// for use at this level
	public void LogStreamError(MSG msg) {
		mediaPlayList.LogStreamError(msg.fields);
	}

	public void LogTrace(MSG msg) {
		mediaPlayList.LogTrace(msg.fields);
	}

	public void LogStreamError(MSG msg, int paranoid) {
		mediaPlayList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(MSG msg, int paranoid) {
		mediaPlayList.LogTrace(msg.fields, paranoid);
	}

	// for contained levels
	public void LogStreamError(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		mediaPlayList.LogStreamError(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		mediaPlayList.LogTrace(msg.fields);
	}

	public void LogStreamError(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		mediaPlayList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		mediaPlayList.LogTrace(msg.fields, paranoid);
	}

	
} // end class



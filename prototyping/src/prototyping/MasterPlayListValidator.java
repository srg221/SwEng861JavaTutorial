package prototyping;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MasterPlayListValidator {

	private PlayListScanner listScanner;
	private MasterPlayList masterPlayList;
	
	MasterPlayListValidator(MasterPlayList playList){
		masterPlayList = playList;
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
	
//	public void Validate() throws MalformedURLException{
//		
//		while (listScanner.scanner.hasNext()) {
//			String line = listScanner.scanner.next();
//            if ( line.startsWith("#" + Tokens.EXT_X_STREAM_INF)){
//            	line = listScanner.GetNextLine();
//            	MasterListExtTag tag = new MasterListExtTag(masterPlayList, listScanner, Tokens.EXT_X_STREAM_INF, line);
//            	// tag.inStream.Download();  //already done as part of inStream creation
//            	masterPlayList.validTags.add((ExtTagStream)tag);
//            }
//		}
//	}
	
	public void ValidateEx() {
		
		MSG msg = new MSG(GetTimeStamp(), masterPlayList.Location(), Context() , "Starting MPL validation");
		LogTrace(msg, 40);
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.GetNextLine();
			// skip well formed comments and completely blank lines
			if (listScanner.IsBlanksOrComment(line))
				continue;
			// At this point needs to be a tag, will never be in this context if on a URL
			if (!line.startsWith(Tokens.tagBegin)){
				//log error non-comment, non-tag line
				msg = new MSG(GetTimeStamp(), masterPlayList.Location() , Err.Sev.WARN.toString(), Err.Type.FORMAT.toString(), "Expected TAG or Comment");
				LogStreamError(msg, 20);
				msg = new MSG(GetTimeStamp(), masterPlayList.Location(), Context() , "Non-comment, non-tag, non-blank line, and do not expect URL");
				LogTrace(msg, 20);
				continue;
			}
			// Check for zero extra whitespace at begin/end
			if (line.trim().length() != line.length()){
				// log error tag line with extra whitespace
				msg = new MSG(GetTimeStamp(), masterPlayList.Location() , Err.Sev.WARN.toString(), Err.Type.FORMAT.toString(), "Leading or Trailing whitespace");
				LogStreamError(msg, 20);
				msg = new MSG(GetTimeStamp(), masterPlayList.Location(), Context() , "Leading or Trailing whitespace");
				LogTrace(msg, 20);
			}
			
			// Set candidateTag = to EXTM3U or everything up to end tag
			//    need to have error it no endtag found
			// try candidate on each tag type, when find, create tag and validate
			String candidateTag = ExtTag.GetCandidateTag(line);
			if (ExtTag.HasValidator(candidateTag)){
				ExtTag extTag =  new ExtTag(masterPlayList, listScanner, candidateTag);
				// this check is only for successful invoke using reflection, not necessarily a valid tag
				// a failure to invoke indicates a coding error, so only thing we can do is exit 
				// actually for production code should throw an exception that makes it back to the 
				// client
				if (!extTag.Validate(candidateTag, listScanner)){
					msg = new MSG(GetTimeStamp(), masterPlayList.Location() , Err.Sev.ERROR.toString(), Err.Type.INTERNAL.toString(), "Cannot continue - Exiting...");
					LogStreamError(msg);
					msg = new MSG(GetTimeStamp(), masterPlayList.Location(), Context() , "ExtTag Validators Invoke fail");
					LogTrace(msg);
					System.exit(-1);
				}
				if (extTag.IsValid()){
					masterPlayList.validTags.add(extTag);
				}
				else{
					masterPlayList.inValidExtTags.add(extTag);
				}
				continue;
			}
			if (MasterListExtTag.HasValidator(candidateTag)){
				MasterListExtTag extTag =  new MasterListExtTag(masterPlayList, listScanner, candidateTag);
				extTag.Validate(candidateTag, listScanner);
				if (extTag.IsValid()){
					masterPlayList.validTags.add(extTag);
				}
				else{
					masterPlayList.inValidExtTags.add(extTag);
				}
				continue;
			}
			// check for Media tags in Master - each media tag validator should log error
			// anything with a stream should not download it, should it advance scanner?
			if (MediaListExtTag.HasValidator(candidateTag)){
				MediaListExtTag extTag =  new MediaListExtTag(masterPlayList, listScanner, candidateTag);
				extTag.Validate(candidateTag, listScanner);
				if (extTag.IsValid()){
					msg = new MSG(GetTimeStamp(), masterPlayList.Location() , Err.Sev.ERROR.toString(), Err.Type.INTERNAL.toString(), "Validation logic fail");
					LogStreamError(msg);
					msg = new MSG(GetTimeStamp(), masterPlayList.Location(), Context() , "Logic validated media tag in Master Playlist");
					LogTrace(msg);
					extTag.MarkBad();
				}
				// log MediaList tag in MasterList
				masterPlayList.inValidExtTags.add(extTag);
				continue;
			}
			// line started with tagBegin, but was not recognized - could be no validator or a bad TAG, well say TAG
			msg = new MSG(GetTimeStamp(), masterPlayList.Location() , Err.Sev.ERROR.toString(), Err.Type.TAG.toString(), "Unrecognized Tag:"+candidateTag);
			LogStreamError(msg);
			msg = new MSG(GetTimeStamp(), masterPlayList.Location(), Context() , "Unrecognized Tag:"+candidateTag);
			LogTrace(msg);		
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
		masterPlayList.LogStreamError(msg.fields);
	}

	public void LogTrace(MSG msg) {
		masterPlayList.LogTrace(msg.fields);
	}

	public void LogStreamError(MSG msg, int paranoid) {
		masterPlayList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(MSG msg, int paranoid) {
		masterPlayList.LogTrace(msg.fields, paranoid);
	}

	// for contained levels
	public void LogStreamError(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		masterPlayList.LogStreamError(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		masterPlayList.LogTrace(msg.fields);
	}

	public void LogStreamError(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		masterPlayList.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		masterPlayList.LogTrace(msg.fields, paranoid);
	}


}

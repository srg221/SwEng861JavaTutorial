package prototyping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class MasterPlayListValidator {

	private PlayListScanner listScanner;
	private MasterPlayList masterPlayList;
	
	MasterPlayListValidator(MasterPlayList playList) throws IOException{
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
	
	public void ValidateEx() throws IllegalArgumentException, InvocationTargetException, Exception{
		
		while (listScanner.scanner.hasNext()) {
			String line = listScanner.GetNextLine();
			// skip well formed comments and completely blank lines
			if (listScanner.IsBlanksOrComment(line))
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
			String candidateTag = ExtTag.GetCandidateTag(line);
			if (ExtTag.HasValidator(candidateTag)){
				ExtTag extTag =  new ExtTag(masterPlayList, listScanner, candidateTag);
				extTag.Validate(candidateTag, listScanner);
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
					assert(false);
					// log runtime error
				}
				else{
					// log MediaList tag in MasterList
					masterPlayList.inValidExtTags.add(extTag);
				}
				continue;
			}
			// log message runtime error	
		}
		
	}
}

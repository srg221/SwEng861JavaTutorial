package prototyping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class MediaPlayListValidator {
	private PlayListScanner listScanner;
	private MediaPlayList mediaPlayList;
	
	MediaPlayListValidator(MediaPlayList playList) throws IOException{
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
				ExtTag extTag =  new ExtTag(mediaPlayList, listScanner, candidateTag);
				extTag.Validate(candidateTag, listScanner);
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
					assert(false);
					// log runtime error
				}
				else{
					// log MediaList tag in MasterList
					mediaPlayList.inValidExtTags.add(extTag);
				}
				continue;
			}
			// log message runtime error	
	}

	// pick out streams
	for (ExtTag tag : mediaPlayList.validTags){
	if (tag.myTagName.equals(Tokens.EXTINF)){
		if (tag.IsValid())
			mediaPlayList.validMediaStreams.add((ExtTagStream) tag);
		}
	}
	// validate .ts files, like est duration
	ValidateStreams();
	}
	
	public void ValidateStreams(){
		// go through mediaPlayList.validMediaStreams
		
	}
	
	
} // end class



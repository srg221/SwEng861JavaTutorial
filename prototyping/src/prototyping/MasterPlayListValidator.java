package prototyping;

import java.io.IOException;

public class MasterPlayListValidator {

	private PlayListScanner listScanner;
	
	MasterPlayListValidator(MasterPlayList playList) throws IOException{
		listScanner = new PlayListScanner(playList.inStream.GetInputStream());
	}
}

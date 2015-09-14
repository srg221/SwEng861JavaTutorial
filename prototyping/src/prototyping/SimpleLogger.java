package prototyping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SimpleLogger {
	
    FileWriter fileWriter;
    PrintWriter printWriter;
    int lineNumber;
    
    // use compiler supplied default constructor
    SimpleLogger() { lineNumber = 0; }
    
    public void OpenLog(String path){
    	try {
			fileWriter = new FileWriter(path);
			printWriter = new PrintWriter(fileWriter, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void CloseLog(){
    	printWriter.flush();
    	try {
			fileWriter.flush();
	    	printWriter.close();
	    	fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public void Log(String[] fields){
    	// print line number for fields > 0
    	if ( lineNumber > 0){
    		printWriter.print(lineNumber);
    	} else {
    		printWriter.print("");
    	}
 	
    	for (String field : fields){
    		printWriter.print(field);
    	}
     	printWriter.println();
     	lineNumber++;
    	printWriter.flush();
    	try {
			fileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


}

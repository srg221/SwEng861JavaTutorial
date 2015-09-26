package prototyping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SimpleLogger {
	
    FileWriter fileWriter;
    PrintWriter printWriter;
    int lineNumber;
    private char delimiter = ',';
    private int paranoidLevel = 20; 
    
    // use compiler supplied default constructor
    SimpleLogger() { lineNumber = 0; }
    
    SimpleLogger(String path, char indelimiter) throws IOException{
    	lineNumber = 0;
    	OpenLog(path);
    	delimiter = indelimiter;
     }
    
    public void OpenLog(String path) throws IOException{
    		fileWriter = new FileWriter(path);
			printWriter = new PrintWriter(fileWriter, true);
    }
    
    public void CloseLog() throws IOException{
    	printWriter.flush();
			fileWriter.flush();
	    	printWriter.close();
	    	fileWriter.close();
	}
    	    
    public void SetParanoidLevel(int level){
    	paranoidLevel = level;
    }
    
    public int GetParanoidLevel(){
    	return paranoidLevel;
    }
    
    public void Log(String[] fields, int paranoid){
    	if (paranoid <= paranoidLevel){
    		Log(fields);
    	}
    }
    
    
    public void Log(String[] fields){
    	// print line number for fields > 0
    	if ( lineNumber > 0){
    		printWriter.print(lineNumber);
        	printWriter.print(delimiter);
    	} else {
    		printWriter.print("");
    	}
 	
    	for (String field : fields){
    		printWriter.print(field);
    		printWriter.print(delimiter);  		
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
    
    public void Log(ArrayList<String> fields){
    	String[] strFields = new String[fields.size()];
    	int i = 0;
    	for (String str : fields){
    		strFields[i++] = str;
    	}
    	Log(strFields);
    }
    
    public void Log(ArrayList<String> fields, int paranoid){
    	if (paranoid <= paranoidLevel){
    		Log(fields);
    	}
    }
 
}

package prototyping;

import java.util.HashMap;
import java.util.Map;

public class Err {

	public enum Sev {
	    INFO("Information"),
	    WARN("Warning"),
	    ERROR("Error"),
	    SEVERE("Severe Error"),
	    FATAL("Fatal Error");
	    
	    private static  Map<String, Sev> sevMap = new HashMap<String, Sev>();
	    private String value;

	    static {
	        for (Sev severity : Sev.values()) {
	            sevMap.put(severity.value, severity);
	        }
	    }

	    private Sev(String value) {
	        this.value = value;
	    }

	    public static Sev fromString(String value) {
	        return sevMap.get(value);
	    }
	    
	    public String toString() {
	        return value;
	    }
	}

	
	public enum Type {
	    URL("URL"),
	    FILE("FILE"),
	    TAG("TAG"),
	    FORMAT("FORMAT");

	    
	    private static  Map<String, Type> typeMap = new HashMap<String, Type>();
	    private String value;

	    static {
	        for (Type type : Type.values()) {
	            typeMap.put(type.value, type);
	        }
	    }

	    private Type(String value) {
	        this.value = value;
	    }

	    public static Type fromString(String value) {
	        return typeMap.get(value);
	    }
	    
	    public String toString() {
	    	return value;
	    }
	    
	    
	}

}

package prototyping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.lang.reflect.Constructor;

interface IAttr <U> {
	public String GetRegExp();
	public U Get();
	public void Set(String strValue, ExtTag tag);
	//public GetNew();
}

public class Attr<T extends IAttr> {
	// Attribute name
	String name;
	public T valueContainer;
	public ExtTag myTag;
	// valueContainerType
	//public final  Class<T> valueClassType; 
	
	public Attr (Class<T> valueType, String name, ExtTag tag){
		this.name = name;
		this.myTag = tag;
		this.valueContainer = (T)ValueFactory(valueType, this);
		//this.valueClassType = valueType;
		//this.valueContainer = (T)ValueFactory(valueClassType, this);
	}
	
	public static <U> U ValueFactory(Class<U> classType, Attr<?> outer) {
    	try {
				// hack to make the factory work for embedded or 
    			// external value classes
    			String valueClassName = classType.getName();
				String outername = outer.getClass().getName();
				if (!valueClassName.contains(outername)){
					// simple case, not an embedded class - just return instance
					return classType.newInstance();
				}
				Constructor<U> ctor = classType.getDeclaredConstructor(outer.getClass());
				U ret = ctor.newInstance(outer);
				return ret;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        return null;
		}
    }

	// given a list of attributes and a line, find the the values and remove those 
	// that don't exist
	public static void GetAttr(ExtTag tag){
	
		for (Attr a : tag.attrSet){
			String t = a.name;
			String v = a.valueContainer.GetRegExp();
		}
	}
	
	// ATTRIBUTE Types
	
	// special case
	//public class AvResolution extends IAttrVal<AvResolution>{
	public class AvResolution implements IAttr<AvResolution>{
		public int width = 0;
		public int height = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.resolutionRegExp);
		
		public AvResolution() {};
		
		public AvResolution(int w, int h){
			width = w; height = h;
		}

		public String GetRegExp(){
			return Tokens.resolutionRegExp;
		}
		
		public AvResolution Get(){ return this;}
		
		public void Set(String strValue, ExtTag tag){
			
		}
		
		//public  static AvResolution GetNew(){ return new AvResolution();}
		
//		public static AvResolution cloneThis(){
//			AvResolution temp = new AvResolution();
//			return temp;
//		}
	}
	
	//public class AvHex extends IAttrVal<AvHex> {
	public class AvHex implements IAttr<AvHex> {	
		public int value = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.hexRegExp);

		public AvHex(String i){
            value = 0;
			for (char c : i.toCharArray()) {
                value += Tokens.ConverHextoByte(c);
            }
		}
		
		public AvHex(){};
		
		public String GetRegExp(){
			return Tokens.hexRegExp;
		}
		
		public AvHex Get(){ return this;}
		public AvHex GetNew(){ return new AvHex();}
		
		public void Set(String strValue, ExtTag tag){
			
		}
	}
	
	//public class AvFloat extends IAttrVal<AvFloat>{
	public class AvFloat implements IAttr<AvFloat>{
		public float value = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.floatRegExp);


		public AvFloat(){};
		
		public AvFloat(int i){
			value = i;
		}
		
		public AvFloat(float i){
			value = i;
		}
		
		public String GetRegExp(){
			return Tokens.floatRegExp;
		}
		
		public AvFloat Get(){ return this;}
		public AvFloat GetNew(){ return new AvFloat();}
		
		public void Set(String strValue, ExtTag tag){
			
		}
	}
	
	//public class AvString extends IAttrVal<AvString>{
	public class AvString implements IAttr<AvString>{
		public String value = null;
		//public static final Pattern pattern = Pattern.compile(Tokens.quotedStrRegExp);
		AvString(){
			value = new String();
		}

		AvString(String i){
			value = new String(i);
		}
		
		public String GetRegExp(){
			return Tokens.quotedStrRegExp;
		}
		
		public AvString Get(){ return this;}
		public AvString GetNew(){ return new AvString();}
		
		public void Set(String strValue, ExtTag tag){
			
		}
	}
	// valid attr types
	
	
	/*
	public class Program_Id{
		public int id = 0;
		
		Program_Id(int inId){
			id = inId;
		}
	}
	
	public class Bandwidth{
		public int bandwidth = 0;
		
		Bandwidth(int inBandwidth){
			bandwidth = inBandwidth;
		}
	}
	
	public class Duration{
		// can be a float or int, internally keeping as float
		public double duration = 0;

		Duration(double inDuration){
			duration = inDuration;
		}
		Duration(int inDuration){
			duration = (double)inDuration;
		}
	}
	*/

	
	//////////////////////////////////////////////
	public class AvInt implements IAttr<AvInt> {
		public int value = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.integerRegExp);

		
		public AvInt(){};
		
		public AvInt(int i){
			value = i;
		}
		
		public AvInt(float i){
			value = (int) i;
		}
		
		public AvInt(Number i){
			value = (int) i;
		}
		
		public String GetRegExp(){
			return Tokens.integerRegExp;
		}
		
		public AvInt Get(){ return this;}
		
		public void Set(String strValue, ExtTag tag){
			
		}

	}
	
	
	
	
	
	
	
	public String Location() {
		if (myTag != null)
			return (myTag.Location());
		else
			return "";
	}

	public String Context() {
		String context = new String();
		context += context
				+ Thread.currentThread().getStackTrace()[2].getFileName();
		context += "::"
				+ Thread.currentThread().getStackTrace()[2].getClassName();
		context += "::"
				+ Thread.currentThread().getStackTrace()[2].getMethodName();
		context += "::Line:"
				+ Thread.currentThread().getStackTrace()[2].getLineNumber();
		return context;
	}

	String GetTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}

	// logging utils at ExtTag level
	// More logging stuff/utilities

	// some wrappers to make code reading easier, would be simpler
	// if java let you overload operators
	public class MSG {
		private ArrayList<String> fields;

		public MSG(String... infields) {
			fields = new ArrayList<String>();
			for (String field : infields) {
				fields.add(field);
			}
		}

		// for contained 
		public MSG(ArrayList<String> infields) {
			fields = new ArrayList<String>();
			for (String field : infields) {
				fields.add(field);
			}
		}
		// was needed at one point in development
		// works, but not used now
		public MSG Prefix(MSG prefix) {
			String[] tmp = new String[fields.size()];
			int i = 0;
			for (String field : fields) {
				tmp[i++] = field;
			}
			fields.clear();
			for (String field : prefix.fields) {
				fields.add(field);
			}
			for (; i >= 0; i--) {
				fields.add(tmp[i]);
			}
			return this;
		}

		public MSG Suffix(MSG suffix) {
			for (String field : suffix.fields) {
				fields.add(field);
			}
			return this;
		}

	}

	// for use at this level -  the "M3u8InputStream level"
	public void LogStreamError(MSG msg) {
		if (myTag != null)
			myTag.LogStreamError(msg.fields);
	}

	public void LogTrace(MSG msg) {
		if (myTag != null)
			myTag.LogTrace(msg.fields);
	}

	public void LogStreamError(MSG msg, int paranoid) {
		if (myTag != null)
			myTag.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(MSG msg, int paranoid) {
		if (myTag != null)
			myTag.LogTrace(msg.fields, paranoid);
	}

	// for contained levels - don't exist now
	// maybe never will, showing for completeness
	public void LogStreamError(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		if (myTag != null) 
			myTag.LogStreamError(msg.fields);
	}

	public void LogTrace(ArrayList<String> fields) {
		MSG msg = new MSG(fields);
		if (myTag != null) 
			myTag.LogTrace(msg.fields);
	}

	public void LogStreamError(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		if (myTag != null) 
			myTag.LogStreamError(msg.fields, paranoid);
	}

	public void LogTrace(ArrayList<String> fields, int paranoid) {
		MSG msg = new MSG(fields);
		if (myTag != null) 
			myTag.LogTrace(msg.fields, paranoid);
	}


}



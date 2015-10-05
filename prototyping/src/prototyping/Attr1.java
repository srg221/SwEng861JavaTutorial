package prototyping;

import java.util.Set;



public class Attr1<T> {
	
	public String name;
	//public T value;
	public Class<T> valueClass;
	public T value;
	
	public interface IAttrVal<T>{
		public String GetRegExp();
		public T Get();
		public  T GetNew();
		// no setters needed
		}
	
//	public Attr1 (Class<T> type){
//		this.value =  type.GetNew();
//	}
//	
//	public Attr1 (Class<T> type, String name){
//		this.name = name;
//	}
	// "Erasure" avoidance
//	public static <T> Attr1<T> create(Class<T> value){
//
//	    return new Attr1<T>(value);
//	}
//	private Class<T> value;
//	
//	 public Attr1(Class<T> value) {
//	    	super();
//	    	this.value = value;
//	    	//this.name = name;
//	    }
//
//	
//	public T createInstance() throws InstantiationException, IllegalAccessException{
//		return value.newInstance();		
//	}
	
//	public Attr1(Class<T> value, String name) {
//		this.name =  name;
//		Attr1(value);
//	}
	
	// given a list of attributes and a line, find the the values and remove those 
	// that don't exist
	public static void GetAttr(Set<Attr1> attrSet, String line){
		
	}
	
	// ATTRIBUTE Types
	// special case
	public class AvResolution implements IAttrVal<AvResolution>{
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
		
		public AvResolution GetNew(){ return new AvResolution();}
		
//		public static AvResolution cloneThis(){
//			AvResolution temp = new AvResolution();
//			return temp;
//		}
	}
		
	public class AvInteger implements IAttrVal<AvInteger>{
		public int value = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.integerRegExp);

		public AvInteger(){};
		
		public AvInteger(int i){
			value = i;
		}
		
		public AvInteger(float i){
			value = (int) i;
		}
		
		public AvInteger(Number i){
			value = (int) i;
		}
		
		public String GetRegExp(){
			return Tokens.integerRegExp;
		}
		
		public AvInteger Get(){ return this;}
		public AvInteger GetNew(){ return new AvInteger();}
	}


	public class AvHex implements IAttrVal<AvHex> {
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
	}
	
	public class AvFloat implements IAttrVal<AvFloat>{
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
	}
	
	public class AvString implements IAttrVal<AvString>{
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


}

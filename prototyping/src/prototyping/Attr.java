package prototyping;

import java.util.Set;
import java.util.regex.Pattern;


public class Attr<T> {

	public String name;
	public Class<T> clazz;
	public T value;
	
	public interface IAttrVal<T>{
		public String GetRegExp();
		public T Get();
		// no setters needed
		}
	
	// "Erasure" avoidance
	private static <U> Attr<U> Clone(Class<U> clazz){

	    return new Attr<U>(clazz);
	}

	public Attr(String name) {
		this.name =  name;
		try {
			this.value = clazz.newInstance();
			//this.value = clazz<T>.Clone();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected Attr(Class<T> clazz){
		this.clazz = clazz;
	}
	
	// given a list of attributes and a line, find the the values and remove those 
	// that don't exist
	public static void GetAttr(Set<Attr> attrSet, String line){
		
	}
	
	// ATTRIBUTE Types
	// special case
	public class AvResolution implements IAttrVal<AvResolution>{
		public int width = 0;
		public int height = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.resolutionRegExp);
		
		public AvResolution(int w, int h){
			width = w; height = h;
		}

		public String GetRegExp(){
			return Tokens.resolutionRegExp;
		}
		
		public AvResolution Get(){ return this;}
	}
		
	public class AvInteger implements IAttrVal<AvInteger>{
		public int value = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.integerRegExp);

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
		
		public String GetRegExp(){
			return Tokens.hexRegExp;
		}
		
		public AvHex Get(){ return this;}
	}
	
	public class AvFloat implements IAttrVal<AvFloat>{
		public float value = 0;
		//public static final Pattern pattern = Pattern.compile(Tokens.floatRegExp);

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
	}
	
	public class AvString implements IAttrVal<AvString>{
		public String value = null;
		//public static final Pattern pattern = Pattern.compile(Tokens.quotedStrRegExp);

		AvString(String i){
			value = new String(i);
		}
		
		public String GetRegExp(){
			return Tokens.quotedStrRegExp;
		}
		
		public AvString Get(){ return this;}
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

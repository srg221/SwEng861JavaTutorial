package prototyping;

import java.lang.reflect.Type;


public class IntAttr implements IAttr<IntAttr> {
	public int value = 0;
	//public static final Pattern pattern = Pattern.compile(Tokens.integerRegExp);

	public IntAttr(){};
	
	public IntAttr(int i){
		value = i;
	}
	
	public IntAttr(float i){
		value = (int) i;
	}
	
	public IntAttr(Number i){
		value = (int) i;
	}
	
	public String GetRegExp(){
		return Tokens.integerRegExp;
	}
	
	public IntAttr Get(){ return this;}
	public static IntAttr GetNew(){ return new IntAttr();}
	
	public void Set(String strValue, ExtTag tag){
		
	}
	
	public Type GetType(){
		return IntAttr.class;
	}

}

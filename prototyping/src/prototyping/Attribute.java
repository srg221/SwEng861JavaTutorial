package prototyping;

import prototyping.Attribute.Resolution;

public class Attribute {
	
	
	public class Resolution{
		public int width = 0;
		public int height = 0;
		
		Resolution(int w, int h){
			width = w; height = h;
		}
	}

	public Resolution Resolution(int w, int h) {
		// TODO Auto-generated method stub
		return new Resolution(w,h);
	}

}

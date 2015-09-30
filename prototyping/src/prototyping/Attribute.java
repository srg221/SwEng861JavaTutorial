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

}

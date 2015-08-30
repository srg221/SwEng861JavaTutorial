
public class Shapes {
 public double area ()
    {
        return 0;     // Since this is just a generic "Shape" we will assume the area as zero.
                    // The actual area of a shape must be overridden by a subclass, as we see below.
                    // You will learn later that there is a way to force a subclass to override a method,
                    // but for this simple example we will ignore that.
    }
 	
 	Shapes(){
 		name = "";
 	}
 	
 	protected String name;
}

class Circle extends Shapes {                    // class declaration
    Circle (double diameter) {                  // constructor
        this.diameter = diameter;
        this.name = "cirle";
    }
    private static final double PI = Math.PI;   // constant
    private double diameter;                    // instance variable
    
    public double area () {                     // dynamic method
        double radius = diameter / 2.0;
        return PI * radius * radius;
    }

}

class Rectangle extends Shapes {
    Rectangle(double length, double width){
    	this.length = length;
    	this.width = width;
    	this.name = "rectangle";
    	}

    public double area(){
    	return length * width;
    }
    
    private double length, width;
}


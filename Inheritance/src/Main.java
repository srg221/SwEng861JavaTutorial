
public class Main {
	   
	public static void main(String[] args) {
	        Shapes s1 = new Circle (2.5);
	        Shapes s2 = new Rectangle (5.0, 4.0);
	        
	        System.out.println ("The " + s1.name + " area = " + s1.area());
	        System.out.println ("The " + s2.name + " area = " + s2.area());
	    }
}

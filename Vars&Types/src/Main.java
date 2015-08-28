
public class Main {

	// use different types to make string H3110 w0r1d 2.0 true
	public static void main(String[] args) {
		byte zero = 0;
		short a = 3;
		int b = 1;
		char d = ' ';
		float e = (float)2.0; // or = 2.0f where f is shrtcut for cast
		boolean f = true;
		String output = new String("H"+a+b+b+zero+d+'w'+zero+'r'+b+'d'+d+e+d+f);
		System.out.println(output);
	}
}


public class Main {

	public static void main(String[] args) {
        int[] arr = new int[10];
        try {
        System.out.println(arr[8001]);
        } catch( ArrayIndexOutOfBoundsException e ) {
        
        	System.out.println(e.toString() +  " exception caught");
        }
	}
}


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        Student[] students = new Student[] {
                new Student("Morgan", "Freeman"),
                new Student("Brad", "Pitt"),
                new Student("Kevin", "Spacey"),
            };
        for (Student s : students) {
            s.printFullName();
        }
    }
	
}

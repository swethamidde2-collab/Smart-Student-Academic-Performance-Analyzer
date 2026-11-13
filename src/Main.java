public class Main {

    public static void main(String[] args) {

        StudentManager manager = new StudentManager();

        Student s1 = new Student("101", "Ravi", "CSE", "A");
        Student s2 = new Student("102", "Swetha", "IT", "B");
        Student s3 = new Student("103", "Anusha", "ECE", "A");

        // Add students
        manager.addStudent(s1);
        manager.addStudent(s2);
        manager.addStudent(s3);

        // Save records
        manager.saveRecords();

        System.out.println("\n--- Loading Records ---");

        // Retrieve records
        StudentManager manager2 = new StudentManager();
        manager2.loadRecords();

        // Display retrieved records
        manager2.displayStudents();
    }
}
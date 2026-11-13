import java.util.ArrayList;

public class StudentManager {

    private ArrayList<Student> students = new ArrayList<>();

    // Add student
    public void addStudent(Student student) {
        students.add(student);
        System.out.println("Student added successfully!");
    }

    // Display all students
    public void displayStudents() {

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        for (Student s : students) {
            System.out.println("ID: " + s.getStudentId());
            System.out.println("Name: " + s.getName());
            System.out.println("Department: " + s.getDepartment());
            System.out.println("Section: " + s.getSection());
            System.out.println("----------------------");
        }
    }

    // Search student
    public Student searchStudent(String id) {

        for (Student s : students) {
            if (s.getStudentId().equals(id)) {
                return s;
            }
        }

        return null;
    }

    // Delete student
    public boolean deleteStudent(String id) {

        Student student = searchStudent(id);

        if (student != null) {
            students.remove(student);
            return true;
        }

        return false;
    }
    // Save student records
    public void saveRecords() {
        FileHandler.saveStudents(students);
    }

    // Load student records
    public void loadRecords() {
        students = FileHandler.loadStudents();
    }
    // Get all students for JavaFX TableView
public ArrayList<Student> getStudents() {
    return students;
}
}
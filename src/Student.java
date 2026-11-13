public class Student {

    private String studentId;
    private String name;
    private String department;
    private String section;

    // Constructor
    public Student(String studentId, String name,
                   String department, String section) {

        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.section = section;
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getSection() {
        return section;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
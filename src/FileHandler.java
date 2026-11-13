import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    private static final String FILE_NAME = "students.csv";

    // Save student records
    public static void saveStudents(ArrayList<Student> students) {

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write("ID,Name,Department,Section");
            writer.newLine();

            for (Student s : students) {

                writer.write(
                    s.getStudentId() + "," +
                    s.getName() + "," +
                    s.getDepartment() + "," +
                    s.getSection()
                );

                writer.newLine();
            }

            System.out.println("Records saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
        }
    }

    // Retrieve student records
    public static ArrayList<Student> loadStudents() {

        ArrayList<Student> students = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return students;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 4) {

                    Student s = new Student(
                        data[0],
                        data[1],
                        data[2],
                        data[3]
                    );

                    students.add(s);
                }
            }

            System.out.println("Records loaded successfully!");

        } catch (IOException e) {
            System.out.println("Error loading records: " + e.getMessage());
        }

        return students;
    }
}
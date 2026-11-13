import java.io.*;
import java.util.ArrayList;

public class MarksFileHandler {

    private static final String FILE_NAME = "marks.csv";
    static{
        System.out.println("Marks file location: "+new File(FILE_NAME).getAbsolutePath());
    }

    public static void saveMarks(ArrayList<MarksRecord> records) {

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write("StudentID,Subject,Internal,Exam");
            writer.newLine();

            for (MarksRecord r : records) {

                writer.write(
                    r.getStudentId() + "," +
                    r.getSubjectName() + "," +
                    r.getInternalMarks() + "," +
                    r.getExamMarks()
                );

                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving marks: " + e.getMessage());
        }
    }

    public static ArrayList<MarksRecord> loadMarks() {

        ArrayList<MarksRecord> records = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) return records;

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 4) {

                    try {
                        records.add(new MarksRecord(
                            data[0],
                            data[1],
                            Integer.parseInt(data[2]),
                            Integer.parseInt(data[3])
                        ));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid marks record: " + line);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading marks: " + e.getMessage());
        }

        return records;
    }
}
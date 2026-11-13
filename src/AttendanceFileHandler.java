import java.io.*;
import java.util.ArrayList;

public class AttendanceFileHandler {

    private static final String FILE_NAME = "attendance.csv";

    public static void saveAttendance(
            ArrayList<AttendanceRecord> records) {

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write(
                "StudentID,Subject,TotalClasses,AttendedClasses"
            );
            writer.newLine();

            for (AttendanceRecord r : records) {

                writer.write(
                    r.getStudentId() + ","
                    + r.getSubjectName() + ","
                    + r.getTotalClasses() + ","
                    + r.getAttendedClasses()
                );

                writer.newLine();
            }

        } catch (IOException e) {

            System.out.println(
                "Error saving attendance: " + e.getMessage()
            );
        }
    }

    public static ArrayList<AttendanceRecord> loadAttendance() {

        ArrayList<AttendanceRecord> records =
                new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 4) {

                    try {

                        records.add(new AttendanceRecord(
                            data[0],
                            data[1],
                            Integer.parseInt(data[2]),
                            Integer.parseInt(data[3])
                        ));

                    } catch (NumberFormatException e) {

                        System.out.println(
                            "Invalid attendance record: " + line
                        );
                    }
                }
            }

        } catch (IOException e) {

            System.out.println(
                "Error loading attendance: " + e.getMessage()
            );
        }

        return records;
    }
}
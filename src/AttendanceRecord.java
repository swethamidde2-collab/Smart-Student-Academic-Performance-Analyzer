public class AttendanceRecord {

    private String studentId;
    private String subjectName;
    private int totalClasses;
    private int attendedClasses;

    public AttendanceRecord(String studentId,
                            String subjectName,
                            int totalClasses,
                            int attendedClasses) {

        this.studentId = studentId;
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public double getAttendancePercentage() {

        if (totalClasses == 0) {
            return 0;
        }

        return (attendedClasses * 100.0) / totalClasses;
    }

    public String getAttendanceStatus() {

        if (getAttendancePercentage() >= 75) {
            return "Eligible";
        } else {
            return "Below 75%";
        }
    }
}
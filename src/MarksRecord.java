public class MarksRecord {

    private String studentId;
    private String subjectName;
    private int internalMarks;
    private int examMarks;

    public MarksRecord(String studentId, String subjectName,
                       int internalMarks, int examMarks) {

        this.studentId = studentId;
        this.subjectName = subjectName;
        this.internalMarks = internalMarks;
        this.examMarks = examMarks;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getInternalMarks() {
        return internalMarks;
    }

    public int getExamMarks() {
        return examMarks;
    }

    public int getTotalMarks() {
        return internalMarks + examMarks;
    }

    public double getPercentage() {
        return getTotalMarks();
    }

    public String getGrade() {

        double percentage = getPercentage();

        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }

    public String getStatus() {

        if (getPercentage() >= 75)
            return "Excellent";
        else if (getPercentage() >= 50)
            return "Good";
        else
            return "Needs Improvement";
    }
}
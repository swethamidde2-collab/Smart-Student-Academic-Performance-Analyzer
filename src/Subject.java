public class Subject {

    private String subjectName;
    private int internalMarks;
    private int examMarks;

    public Subject(String subjectName, int internalMarks, int examMarks) {
        this.subjectName = subjectName;
        this.internalMarks = internalMarks;
        this.examMarks = examMarks;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getTotalMarks() {
        return internalMarks + examMarks;
    }

    public double getPercentage() {
        return (getTotalMarks()/100.0)*100;
    }

    public String getGrade() {
        double percentage = getPercentage();

        if (percentage >= 90)
            return "A+";
        else if (percentage >= 80)
            return "A";
        else if (percentage >= 70)
            return "B";
        else if (percentage >= 60)
            return "C";
        else if (percentage >= 50)
            return "D";
        else
            return "F";
    }
}
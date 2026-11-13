public class Performance {

    private int internalMarks;
    private int examMarks;

    public Performance(int internalMarks, int examMarks) {
        this.internalMarks = internalMarks;
        this.examMarks = examMarks;
    }

    public int getTotalMarks() {
        return internalMarks + examMarks;
    }

    public double getPercentage() {
        return (getTotalMarks() / 100.0) * 100;
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

    public String getStatus() {

        if (getPercentage() >= 75)
            return "Excellent";
        else if (getPercentage() >= 50)
            return "Good";
        else
            return "Needs Improvement";
    }
}
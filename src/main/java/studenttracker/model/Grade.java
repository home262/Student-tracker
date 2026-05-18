package studenttracker.model;

public class Grade {

    //---------------------------------------FIELDS------------------------------------------
    private int gradeId;
    private Student student;
    private Course course;
    private double score;
    private String semester;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Grade() {}

    public Grade(int gradeId, Student student, Course course, double score, String semester) {
        this.gradeId = gradeId;
        this.student = student;
        this.course = course;
        this.score = score;
        this.semester = semester;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setGradeId(int gradeId) {this.gradeId = gradeId;}
    public void setStudent(Student student) {this.student = student;}
    public void setCourse(Course course) {this.course = course;}
    public void setScore(double score) {this.score = score;}
    public void setSemester(String semester) {this.semester = semester;}

    //-----------------------------------GETTERS----------------------------------------------
    public int getGradeId() {return gradeId;}
    public Student getStudent() {return student;}
    public Course getCourse() {return course;}
    public double getScore() {return score;}
    public String getSemester() {return semester;}

    //--------------------------------METHODS-------------------------------------------------
    public String calculateLetter() {
        if(score >= 90) {return "A";}
        else if(score >= 80) {return "B";}
        else if(score >= 70) {return "C";}
        else if(score >= 60) {return "D";}
        else {return "F";}
    }

    public boolean isPassing() {
        return score >= 50;
    }

    public double getGradePoints() {
        if (score >= 90) return 4.0;
        else if (score >= 80) return 3.0;
        else if (score >= 70) return 2.0;
        else if (score >= 60) return 1.0;
        else return 0.0;
    }
}
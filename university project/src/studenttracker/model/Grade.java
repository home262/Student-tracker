package studenttracker.model;

public class Grade {

    //---------------------------------------FIELDS------------------------------------------
    private int gradeId;
    private Student student;
    private Course course;
    private double score;
    private String letterGrade;
    private String semester;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Grade() {}

    public Grade(int gradeId, Student student, Course course, double score, String letterGrade, String semester) {
        this.gradeId = gradeId;
        this.student = student;
        this.course = course;
        this.score = score;
        this.letterGrade = letterGrade;
        this.semester = semester;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setGradeId(int gradeId) {this.gradeId = gradeId;}
    public void setStudent(Student student) {this.student = student;}
    public void setCourse(Course course) {this.course = course;}
    public void setScore(double score) {this.score = score;}
    public void setLetterGrade(String letterGrade) {this.letterGrade = letterGrade;}
    public void setSemester(String semester) {this.semester = semester;}

    //-----------------------------------GETTERS----------------------------------------------
    public int getGradeId() {return gradeId;}
    public Student getStudent() {return student;}
    public Course getCourse() {return course;}
    public double getScore() {return score;}
    public String getLetterGrade() {return letterGrade;}
    public String getSemester() {return semester;}

    //--------------------------------METHODS-------------------------------------------------
    public String calculateLetter() {
        return "";
    }

    public boolean isPassing() {
        return false;
    }

    public double getGradePoints() {
        return 0;
    }
}
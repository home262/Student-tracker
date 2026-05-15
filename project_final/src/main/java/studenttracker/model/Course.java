package studenttracker.model;

import java.util.List;
import java.util.ArrayList;


public class Course {

    //---------------------------------------FIELDS------------------------------------------
    private String courseId;
    private String courseName;
    private String instructorName;
    private int creditHours;
    private String semester;
    private List<Student> students;
    private List<Grade> grades;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Course() {
        students = new ArrayList<>();
        grades = new ArrayList<>();
    }

    public Course(String courseId, String courseName, String instructorName, int creditHours, String semester ) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.creditHours = creditHours;
        this.semester = semester;
        this.students = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setCourseId(String courseId) {this.courseId = courseId;}
    public void setCourseName(String courseName) {this.courseName = courseName;}
    public void setInstructorName(String instructorName) {this.instructorName = instructorName;}
    public void setCreditHours(int creditHours) {this.creditHours = creditHours;}
    public void setSemester(String semester) {this.semester = semester;}

    //-----------------------------------GETTERS----------------------------------------------
    public String getCourseId() {return courseId;}
    public String getCourseName() {return courseName;}
    public String getInstructorName() {return instructorName;}
    public int getCreditHours() {return creditHours;}
    public String getSemester() {return semester;}

    //--------------------------------METHODS-------------------------------------------------
    public void enroll(Student student) {
        students.add(student);
        student.addCourse(this);
    }

    public List<Student> getStudents(){
        return students;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
        grade.getStudent().addGrade(grade);
    }

    public List<Grade> getGrades() {return grades;}

    public double getAverageGrade() {
        return 0;
    }
}
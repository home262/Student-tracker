package studenttracker.model;

import java.util.List;
import java.util.ArrayList;

public class Student extends User {
    //---------------------------------------FIELDS------------------------------------------
    private String studentId;
    private String department;
    private int enrollmentYear;
    private List<Course> courses;
    private List<Grade> grades;
    private List<Attendance> attendanceRecords;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Student() {
        super();

        courses = new ArrayList<>();
        grades = new ArrayList<>();
        attendanceRecords = new ArrayList<>();
    }

    public Student(int userId,
                   String name,
                   String email,
                   String password,
                   String phone,
                   String studentId,
                   String department,
                   int enrollmentYear) {

        super(userId, name, email, password, phone);

        this.studentId = studentId;
        this.department = department;
        this.enrollmentYear = enrollmentYear;

        this.courses = new ArrayList<>();
        this.grades = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
    }

    //-----------------------------------SETTERS----------------------------------------------

    public void setStudentId(String studentId) {this.studentId = studentId;}
    public void setDepartment(String department) {this.department = department;}
    public void setEnrollmentYear(int enrollmentYear) {this.enrollmentYear = enrollmentYear;}

    //-----------------------------------GETTERS----------------------------------------------

    public String getStudentId() {return studentId;}
    public String getDepartment() {return department;}
    public int getEnrollmentYear() {return enrollmentYear;}
    public List<Course> getCourses() {return courses;}
    public List<Grade> getGrades() {return grades;}
    public List<Attendance> getAttendanceRecords() {return attendanceRecords;}

    //-----------------------------------METHODS----------------------------------------------

    public double calculateGPA(){
        return 0.0;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void addAttendance(Attendance attendance) {
        attendanceRecords.add(attendance);
    }

    public double getAttendanceRate() {
        return 0.0;
    }


    //----------------------OVERRIDDEN METHOD FROM ABSTRACT CLASS <<USER>>---------------------
    @Override
    public boolean login() {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean validateEmail() {
        return false;
    }
}
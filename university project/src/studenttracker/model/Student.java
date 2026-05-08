package studenttracker.model;

import java.util.List;

public class Student extends User {
    //---------------------------------------FIELDS------------------------------------------
    private String studentId;
    private String department;
    private double gpa;
    private int enrollmentYear;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Student() {super();}

    public Student(int userId, String name, String email, String password, String phone, String role, String studentId, String department, double gpa, int enrollmentYear) {
        super(userId, name, email, password, phone, role);
        this.studentId = studentId;
        this.department = department;
        this.gpa = gpa;
        this.enrollmentYear = enrollmentYear;
    }

    //-----------------------------------SETTERS----------------------------------------------

    public void setStudentId(String studentId) {this.studentId = studentId;}
    public void setDepartment(String department) {this.department = department;}
    public void setEnrollmentYear(int enrollmentYear) {this.enrollmentYear = enrollmentYear;}

    //-----------------------------------GETTERS----------------------------------------------

    public String getStudentId() {return studentId;}
    public String getDepartment() {return department;}
    public double getGpa() {return gpa;}
    public int getEnrollmentYear() {return enrollmentYear;}


    //-----------------------------------METHODS----------------------------------------------

    public double calculateGPA(){
        return 0.0;
    }

    public double getAttendanceRate() {
        return 0.0;
    }

    public List<Course> getCourses(){
        return null;
    }

    public List<Attendance> getAttendance(){
        return null ;
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

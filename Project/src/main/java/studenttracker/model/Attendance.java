package studenttracker.model;

import java.time.LocalDate;

public class Attendance {

    //---------------------------------------FIELDS------------------------------------------
    private int attendId;
    private Student student;
    private Course course;
    private LocalDate date;
    private String status;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Attendance() {}

    public Attendance(int attendId, Student student, Course course, LocalDate date, String status) {
        this.attendId = attendId;
        this.student = student;
        this.course = course;
        this.date = date;
        this.status = status;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setAttendId(int attendId) {this.attendId = attendId;}
    public void setStudent(Student student) {this.student = student;}
    public void setCourse(Course course) {this.course = course;}
    public void setDate(LocalDate date) {this.date = date;}
    public void setStatus(String status) {this.status = status;}

    //-----------------------------------GETTERS----------------------------------------------
    public int getAttendId() {return attendId;}
    public Student getStudent() {return student;}
    public Course getCourse() {return course;}
    public LocalDate getDate() {return date;}
    public String getStatus() {return status;}

    //--------------------------------METHODS-------------------------------------------------
    public void markPresent() {

    }

    public void markAbsent() {

    }

    public String getStatusLabel() {
        return "";
    }
}
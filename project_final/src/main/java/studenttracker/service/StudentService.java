package studenttracker.service;

import studenttracker.database.DatabaseManager;
import studenttracker.model.Attendance;
import studenttracker.model.Grade;
import studenttracker.model.Student;

import java.util.List;

public class StudentService {

    //----------------------------- FIELD -----------------------------
    private DatabaseManager databaseManager;

    //----------------------------- CONSTRUCTOR -----------------------------
    public StudentService(DatabaseManager databaseManager) {

        this.databaseManager = databaseManager;
    }

    //----------------------------- CREATE STUDENT -----------------------------
    public void addStudent(Student student) {

        databaseManager.addStudent(student);
    }

    //----------------------------- GET ALL STUDENTS -----------------------------
    public List<Student> getAllStudents() {

        return databaseManager.getAllStudents();
    }

    //----------------------------- GET STUDENT BY ID -----------------------------
    public Student getStudentById(String studentId) {

        return databaseManager.getStudentById(studentId);
    }

    //----------------------------- UPDATE STUDENT -----------------------------
    public void updateStudent(Student student) {

        databaseManager.updateStudent(student);
    }

    //----------------------------- DELETE STUDENT -----------------------------
    public void deleteStudent(String studentId) {

        databaseManager.deleteStudent(studentId);
    }

    //----------------------------- CALCULATE GPA -----------------------------
    public double calculateGPA(Student student) {

        double totalPoints = 0;

        int totalSubjects = student.getGrades().size();

        if(totalSubjects == 0) {

            return 0.0;
        }

        for(Grade grade : student.getGrades()) {

            totalPoints += grade.getGradePoints();
        }

        return totalPoints / totalSubjects;
    }

    //----------------------------- ATTENDANCE RATE -----------------------------
    public double calculateAttendanceRate(Student student) {

        int presentCount = 0;

        int totalRecords = student.getAttendanceRecords().size();

        if(totalRecords == 0) {

            return 0.0;
        }

        for(Attendance attendance : student.getAttendanceRecords()) {

            if(attendance.getStatus().equalsIgnoreCase("Present")) {

                presentCount++;
            }
        }

        return (double) presentCount / totalRecords * 100;
    }

    //----------------------------- LOGIN VALIDATION -----------------------------
    public boolean login(Student student,
                         String email,
                         String password) {

        return student.getEmail().equals(email)
                &&
                student.getPassword().equals(password);
    }

    //----------------------------- WARNING CHECK -----------------------------
    public boolean isStudentOnWarning(Student student) {

        return calculateAttendanceRate(student) < 75;
    }

    //----------------------------- SEARCH STUDENT -----------------------------
    public boolean studentExists(String studentId) {

        Student student =
                databaseManager.getStudentById(studentId);

        return student != null;
    }
}
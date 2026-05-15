package studenttracker;

import studenttracker.database.DatabaseManager;
import studenttracker.model.Student;
import studenttracker.service.StudentService;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        //---------------- DATABASE ----------------
        DatabaseManager databaseManager =
                new DatabaseManager();

        databaseManager.connect();

        databaseManager.createStudentTable();

        //---------------- SERVICE ----------------
        StudentService studentService =
                new StudentService(databaseManager);

        //---------------- CREATE STUDENT ----------------
        Student student1 = new Student(
                1,
                "Yousef",
                "yousef@gmail.com",
                "1234",
                "01000000000",
                "S101",
                "AI",
                2025
        );

        //---------------- SAVE STUDENT ----------------
        studentService.addStudent(student1);

        //---------------- GET ALL STUDENTS ----------------
        List<Student> students =
                studentService.getAllStudents();

        //---------------- PRINT STUDENTS ----------------
        for(Student student : students) {

            System.out.println("------------");
            System.out.println("Name: "
                    + student.getName());

            System.out.println("Department: "
                    + student.getDepartment());

            System.out.println("Student ID: "
                    + student.getStudentId());
        }

        //---------------- SEARCH STUDENT ----------------
        Student foundStudent =
                studentService.getStudentById("S101");

        if(foundStudent != null) {

            System.out.println("------------");
            System.out.println("Student Found!");
            System.out.println(foundStudent.getName());
        }

        //---------------- DELETE STUDENT ----------------
        // studentService.deleteStudent("S101");

        //---------------- DISCONNECT ----------------
        databaseManager.disconnect();
    }
}
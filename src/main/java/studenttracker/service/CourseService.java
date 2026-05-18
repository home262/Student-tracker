package studenttracker.service;

import studenttracker.model.Course;
import studenttracker.model.Grade;
import studenttracker.model.Student;

public class CourseService {

    //-------------------------------- ENROLL STUDENT --------------------------------

    public void enrollStudent(Course course, Student student) {

        course.enroll(student);
    }

    //-------------------------------- CALCULATE COURSE AVERAGE --------------------------------

    public double calculateCourseAverage(Course course) {

        double totalScores = 0;

        int totalGrades = course.getGrades().size();

        // Prevent division by zero
        if(totalGrades == 0) {

            return 0.0;
        }

        // Loop through all grades
        for(Grade grade : course.getGrades()) {

            totalScores += grade.getScore();
        }

        return totalScores / totalGrades;
    }

    //-------------------------------- COUNT STUDENTS --------------------------------

    public int countStudents(Course course) {

        return course.getStudents().size();
    }

    //-------------------------------- CHECK ENROLLMENT --------------------------------

    public boolean isStudentEnrolled(Course course, Student student) {

        return course.getStudents().contains(student);
    }

}
package studenttracker.service;

import studenttracker.model.Grade;
import studenttracker.model.Student;

public class GradeService {

    //-------------------------------- CALCULATE LETTER GRADE --------------------------------

    public String calculateLetterGrade(double score) {

        if(score >= 90) {

            return "A";
        }

        else if(score >= 80) {

            return "B";
        }

        else if(score >= 70) {

            return "C";
        }

        else if(score >= 60) {

            return "D";
        }

        else {

            return "F";
        }
    }

    //-------------------------------- CHECK PASSING --------------------------------

    public boolean isPassing(double score) {

        return score >= 50;
    }

    //-------------------------------- CONVERT SCORE TO GPA POINTS --------------------------------

    public double calculateGradePoints(double score) {

        if(score >= 90) {

            return 4.0;
        }

        else if(score >= 80) {

            return 3.0;
        }

        else if(score >= 70) {

            return 2.0;
        }

        else if(score >= 60) {

            return 1.0;
        }

        else {

            return 0.0;
        }
    }

    //-------------------------------- CALCULATE STUDENT AVERAGE --------------------------------

    public double calculateAverage(Student student) {

        double totalScores = 0;

        int totalGrades = student.getGrades().size();

        // Prevent division by zero
        if(totalGrades == 0) {

            return 0.0;
        }

        for(Grade grade : student.getGrades()) {

            totalScores += grade.getScore();
        }

        return totalScores / totalGrades;
    }

}
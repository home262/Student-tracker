package studenttracker.report;

import studenttracker.interfaces.Analyzable;
import studenttracker.model.Grade;
import studenttracker.model.Student;

import java.util.List;

public class GradeReport extends Report implements Analyzable {

    //---------------------------------------FIELDS------------------------------------------
    private List<Grade> grades;
    private double gpa;
    private int passingCount;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public GradeReport() {
        super();
    }

    public GradeReport(int reportId, Student student,
                       String generatedAt, String title,
                       List<Grade> grades,
                       double gpa, int passingCount) {

        super(reportId, student, generatedAt, title);

        this.grades = grades;
        this.gpa = gpa;
        this.passingCount = passingCount;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public void setPassingCount(int passingCount) {
        this.passingCount = passingCount;
    }

    //-----------------------------------GETTERS----------------------------------------------
    public List<Grade> getGrades() {
        return grades;
    }

    public double getGpa() {
        return gpa;
    }

    public int getPassingCount() {
        return passingCount;
    }

    //--------------------OVERRIDDEN METHOD FROM <<REPORT>> and <<ANALYZABLE>>------------------
    @Override
    public String generate() {
        return "";
    }

    @Override
    public String analyze() {
        return "";
    }

    //--------------------------------METHODS-------------------------------------------------
    public String getSummary() {
        return "";
    }

    public List<Grade> getFailingCourses() {
        return null;
    }
}
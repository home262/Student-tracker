package studenttracker.report;

import studenttracker.model.Student;

public abstract class Report {

    //---------------------------------------FIELDS------------------------------------------
    protected int reportId;
    protected Student student;
    protected String generatedAt;
    protected String title;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Report() {
    }

    public Report(int reportId, Student student,
                  String generatedAt, String title) {

        this.reportId = reportId;
        this.student = student;
        this.generatedAt = generatedAt;
        this.title = title;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //-----------------------------------GETTERS----------------------------------------------
    public int getReportId() {
        return reportId;
    }

    public Student getStudent() {
        return student;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public String getTitle() {
        return title;
    }

    //--------------------------------ABSTRACT METHODS----------------------------------------
    public abstract String generate();

    //--------------------------------METHODS-------------------------------------------------
    public void export() {

    }

    public void display() {

    }
}
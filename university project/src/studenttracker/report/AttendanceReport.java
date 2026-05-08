package studenttracker.report;

import studenttracker.interfaces.Analyzable;
import studenttracker.model.Attendance;
import studenttracker.model.Student;

import java.util.List;

public class AttendanceReport extends Report implements Analyzable {

    //---------------------------------------FIELDS------------------------------------------
    private List<Attendance> records;
    private double attendanceRate;
    private boolean isAtRisk;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public AttendanceReport() {
        super();
    }

    public AttendanceReport(int reportId, Student student,
                            String generatedAt, String title,
                            List<Attendance> records,
                            double attendanceRate,
                            boolean isAtRisk) {

        super(reportId, student, generatedAt, title);

        this.records = records;
        this.attendanceRate = attendanceRate;
        this.isAtRisk = isAtRisk;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setRecords(List<Attendance> records) {
        this.records = records;
    }

    public void setAttendanceRate(double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public void setAtRisk(boolean atRisk) {
        isAtRisk = atRisk;
    }

    //-----------------------------------GETTERS----------------------------------------------
    public List<Attendance> getRecords() {
        return records;
    }

    public double getAttendanceRate() {
        return attendanceRate;
    }

    public boolean isAtRisk() {
        return isAtRisk;
    }

    //----------------------------------OVERRIDDEN METHODS-------------------------------------
    @Override
    public String generate() {
        return "";
    }

    @Override
    public String analyze() {
        return "";
    }

    //--------------------------------METHODS-------------------------------------------------
    public double getRate() {
        return 0;
    }

    public boolean checkWarning() {
        return false;
    }
}
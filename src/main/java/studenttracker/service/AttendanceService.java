package studenttracker.service;

import studenttracker.model.Attendance;
import studenttracker.model.Student;

public class AttendanceService {

    //-------------------------------- MARK PRESENT --------------------------------

    public void markPresent(Attendance attendance) {

        attendance.markPresent();
    }

    //-------------------------------- MARK ABSENT --------------------------------

    public void markAbsent(Attendance attendance) {

        attendance.markAbsent();
    }

    //-------------------------------- CALCULATE ATTENDANCE RATE --------------------------------

    public double calculateAttendanceRate(Student student) {

        int presentCount = 0;

        int totalRecords = student.getAttendanceRecords().size();

        // Prevent division by zero
        if(totalRecords == 0) {

            return 0.0;
        }

        // Loop through attendance records
        for(Attendance attendance : student.getAttendanceRecords()) {

            if(attendance.getStatus().equalsIgnoreCase("Present")) {

                presentCount++;
            }
        }

        return (double) presentCount / totalRecords * 100;
    }

    //-------------------------------- COUNT ABSENCES --------------------------------

    public int countAbsences(Student student) {

        int absenceCount = 0;

        for(Attendance attendance : student.getAttendanceRecords()) {

            if(attendance.getStatus().equalsIgnoreCase("Absent")) {

                absenceCount++;
            }
        }

        return absenceCount;
    }

    //-------------------------------- CHECK WARNING STATUS --------------------------------

    public boolean hasAttendanceWarning(Student student) {

        return countAbsences(student) >= 3;
    }

}
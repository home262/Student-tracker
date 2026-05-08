package studenttracker.exception;

public class InvalidAttendanceException extends Exception {

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public InvalidAttendanceException() {
    }

    public InvalidAttendanceException(String message) {
        super(message);
    }
}
package studenttracker.controller;

import studenttracker.model.Student;

/**
 * Interface for controllers that need the current student data.
 */
public interface StudentAware {
    void setStudent(Student student);
}

package studenttracker.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import studenttracker.App;
import studenttracker.model.*;

import java.util.*;

public class AdminDashboardController {

    // ── Sidebar nav ──────────────────────────────────────────────────────────
    @FXML private Label  adminNameLabel;
    @FXML private Button navAll;
    @FXML private Button navAdd;
    @FXML private Button navCourses;
    @FXML private Button navGrades;
    @FXML private Button navAttendance;
    @FXML private Button navAdminReports;
    @FXML private Button navEnrollments;
    @FXML private javafx.scene.image.ImageView iconLogoutAdmin;

    // ── Views ────────────────────────────────────────────────────────────────
    @FXML private VBox allStudentsView;
    @FXML private VBox addStudentView;
    @FXML private VBox coursesView;
    @FXML private VBox gradesView;
    @FXML private VBox attendanceView;
    @FXML private VBox adminReportsView;
    @FXML private VBox enrollmentsView;

    // ── Enrollments ──────────────────────────────────────────────────────────
    @FXML private ComboBox<Student> enrollStudentCombo;
    @FXML private TableView<Course> enrollTable;
    @FXML private TableColumn<Course, String> eColId, eColName, eColInstructor, eColSemester;
    @FXML private TableColumn<Course, Integer> eColCredits;
    @FXML private ComboBox<Course> enrollCourseCombo;
    @FXML private Label enrollStatusLabel;

    // ── All Students ─────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String>  colId, colName, colEmail, colDept, colPhone;
    @FXML private TableColumn<Student, Integer> colYear;
    @FXML private Label totalCountLabel, deptCountLabel, yearCountLabel, statusLabel;

    // ── Add Student ──────────────────────────────────────────────────────────
    @FXML private TextField     addIdField, addNameField, addEmailField, addPhoneField, addYearField;
    @FXML private PasswordField addPasswordField;
    @FXML private ComboBox<String> addDeptField;
    @FXML private Label         formErrorLabel;

    // ── Courses ──────────────────────────────────────────────────────────────
    @FXML private TableView<Course>             coursesTable;
    @FXML private TableColumn<Course, String>   cColId, cColName, cColInstructor, cColSemester;
    @FXML private TableColumn<Course, Integer>  cColCredits;
    @FXML private TextField   newCourseIdField, newCourseNameField, newInstructorField, newSemesterField;
    @FXML private ComboBox<Integer> newCreditsCombo;
    @FXML private Label       coursesStatusLabel;

    // ── Grades ───────────────────────────────────────────────────────────────
    @FXML private ComboBox<Student>  gradeStudentCombo;
    @FXML private TableView<Grade>   gradesAdminTable;
    @FXML private TableColumn<Grade, String>  gColCourse, gColLetter, gColSemester;
    @FXML private TableColumn<Grade, Double>  gColScore;
    @FXML private ComboBox<Course>   gradeCourseCombo;
    @FXML private TextField          gradeScoreField, gradeSemesterField;
    @FXML private Label              gradesStatusLabel;

    // ── Attendance ───────────────────────────────────────────────────────────
    @FXML private ComboBox<Student>     attStudentCombo;
    @FXML private TableView<Attendance> attAdminTable;
    @FXML private TableColumn<Attendance, String> aColCourse, aColDate, aColStatus;
    @FXML private ComboBox<Course>      attCourseCombo;
    @FXML private DatePicker            attDatePicker;
    @FXML private ComboBox<String>      attStatusCombo;
    @FXML private Label                 attStatusLabel;

    // ── Admin Reports ────────────────────────────────────────────────────────
    @FXML private TableView<String[]>             summaryTable;
    @FXML private TableColumn<String[], String>   srColId, srColName, srColDept, srColCourses, srColGpa, srColAtt;

    // ── State ─────────────────────────────────────────────────────────────────
    private Admin admin;
    private ObservableList<Student> allStudents;
    private FilteredList<Student> filteredStudents;

    // ═════════════════════════════════════════════════════════════════════════
    //  INIT
    // ═════════════════════════════════════════════════════════════════════════

    public void initData(Admin admin) {
        this.admin = admin;
        adminNameLabel.setText(admin.getName());
        // Load logout icon
        try {
            var stream = getClass().getResourceAsStream("/assets/icons/sword2.png");
            if (stream != null && iconLogoutAdmin != null)
                iconLogoutAdmin.setImage(new javafx.scene.image.Image(stream));
        } catch (Exception ignored) {}
        setupStudentsTable();
        loadStudents();
        setupDeptCombo();
        setupCoursesSection();
        setupGradesSection();
        setupAttendanceSection();
        setupReportsSection();
        setupEnrollmentsSection();
        showAllStudents();
    }

    // ── Students Table Setup ──────────────────────────────────────────────────

    private void setupStudentsTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        studentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadStudents() {
        List<Student> list = App.getDatabase().getAllStudents();
        allStudents = FXCollections.observableArrayList(list);
        filteredStudents = new FilteredList<>(allStudents, s -> true);
        studentsTable.setItems(filteredStudents);
        updateStudentStats();
    }

    private void updateStudentStats() {
        Set<String> depts = new HashSet<>();
        Set<Integer> years = new HashSet<>();
        for (Student s : allStudents) {
            if (s.getDepartment() != null) depts.add(s.getDepartment());
            years.add(s.getEnrollmentYear());
        }
        totalCountLabel.setText(String.valueOf(allStudents.size()));
        deptCountLabel.setText(String.valueOf(depts.size()));
        yearCountLabel.setText(String.valueOf(years.size()));
    }

    private void setupDeptCombo() {
        addDeptField.setItems(FXCollections.observableArrayList(
            "Computer Science","Information Technology","Software Engineering",
            "Artificial Intelligence","Cyber Security","Data Science","Networking"));
        addDeptField.setPromptText("Select department");
    }

    // ── Courses Section ───────────────────────────────────────────────────────

    private void setupCoursesSection() {
        cColId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        cColName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        cColInstructor.setCellValueFactory(new PropertyValueFactory<>("instructorName"));
        cColCredits.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        cColSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        coursesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        newCreditsCombo.setItems(FXCollections.observableArrayList(0, 1, 2, 3, 4));
        newCreditsCombo.setValue(3);
        newSemesterField.setText("Spring 2026");
    }

    private void loadCourses() {
        List<Course> list = App.getDatabase().getAllCourses();
        coursesTable.setItems(FXCollections.observableArrayList(list));
    }

    // ── Grades Section ────────────────────────────────────────────────────────

    private void setupGradesSection() {
        gColCourse.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getCourse() != null ?
                cd.getValue().getCourse().getCourseName() : "Unknown"));
        gColScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        gColLetter.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().calculateLetter()));
        gColSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        gradesAdminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        gradeSemesterField.setText("Spring 2026");

        List<Student> students = App.getDatabase().getAllStudents();
        gradeStudentCombo.setItems(FXCollections.observableArrayList(students));
        gradeStudentCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Student s) { return s == null ? "" : s.getName() + " (" + s.getStudentId() + ")"; }
            public Student fromString(String str) { return null; }
        });
        gradeStudentCombo.setOnAction(e -> loadGradesForSelectedStudent());

        List<Course> courses = App.getDatabase().getAllCourses();
        gradeCourseCombo.setItems(FXCollections.observableArrayList(courses));
        gradeCourseCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Course c) { return c == null ? "" : c.getCourseId() + " — " + c.getCourseName(); }
            public Course fromString(String str) { return null; }
        });
    }

    private void loadGradesForSelectedStudent() {
        Student sel = gradeStudentCombo.getValue();
        if (sel == null) return;
        List<Grade> grades = App.getDatabase().getGradesForStudent(sel.getStudentId());
        gradesAdminTable.setItems(FXCollections.observableArrayList(grades));
    }

    // ── Attendance Section ────────────────────────────────────────────────────

    private void setupAttendanceSection() {
        aColCourse.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getCourse() != null ?
                cd.getValue().getCourse().getCourseName() : "Unknown"));
        aColDate.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getDate() != null ?
                cd.getValue().getDate().toString() : ""));
        aColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        attAdminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        attStatusCombo.setItems(FXCollections.observableArrayList("Present", "Absent"));
        attStatusCombo.setValue("Present");
        attDatePicker.setValue(java.time.LocalDate.now());

        List<Student> students = App.getDatabase().getAllStudents();
        attStudentCombo.setItems(FXCollections.observableArrayList(students));
        attStudentCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Student s) { return s == null ? "" : s.getName() + " (" + s.getStudentId() + ")"; }
            public Student fromString(String str) { return null; }
        });
        attStudentCombo.setOnAction(e -> loadAttForSelectedStudent());

        List<Course> courses = App.getDatabase().getAllCourses();
        attCourseCombo.setItems(FXCollections.observableArrayList(courses));
        attCourseCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Course c) { return c == null ? "" : c.getCourseId() + " — " + c.getCourseName(); }
            public Course fromString(String str) { return null; }
        });
    }

    private void loadAttForSelectedStudent() {
        Student sel = attStudentCombo.getValue();
        if (sel == null) return;
        List<Attendance> att = App.getDatabase().getAttendanceForStudent(sel.getStudentId());
        attAdminTable.setItems(FXCollections.observableArrayList(att));
    }

    // ── Reports Section ───────────────────────────────────────────────────────

    private void setupReportsSection() {
        srColId.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[0]));
        srColName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[1]));
        srColDept.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[2]));
        srColCourses.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[3]));
        srColGpa.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[4]));
        srColAtt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[5]));
        summaryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadReports() {
        List<String[]> rows = App.getDatabase().getStudentSummaries();
        summaryTable.setItems(FXCollections.observableArrayList(rows));
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  NAVIGATION
    // ═════════════════════════════════════════════════════════════════════════

    private void hideAll() {
        for (VBox v : new VBox[]{allStudentsView, addStudentView, coursesView, gradesView, attendanceView, adminReportsView, enrollmentsView}) {
            v.setVisible(false); v.setManaged(false);
        }
        for (Button b : new Button[]{navAll, navAdd, navCourses, navGrades, navAttendance, navAdminReports, navEnrollments})
            b.getStyleClass().setAll("nav-button");
    }

    @FXML private void showAllStudents() {
        hideAll(); allStudentsView.setVisible(true); allStudentsView.setManaged(true);
        navAll.getStyleClass().setAll("nav-button", "nav-button-active");
        loadStudents();
    }

    @FXML private void showAddForm() {
        hideAll(); addStudentView.setVisible(true); addStudentView.setManaged(true);
        navAdd.getStyleClass().setAll("nav-button", "nav-button-active");
        clearForm();
    }

    @FXML private void showCourses() {
        hideAll(); coursesView.setVisible(true); coursesView.setManaged(true);
        navCourses.getStyleClass().setAll("nav-button", "nav-button-active");
        loadCourses();
    }

    @FXML private void showGrades() {
        hideAll(); gradesView.setVisible(true); gradesView.setManaged(true);
        navGrades.getStyleClass().setAll("nav-button", "nav-button-active");
        // Refresh combos
        List<Student> students = App.getDatabase().getAllStudents();
        gradeStudentCombo.setItems(FXCollections.observableArrayList(students));
        List<Course> courses = App.getDatabase().getAllCourses();
        gradeCourseCombo.setItems(FXCollections.observableArrayList(courses));
    }

    @FXML private void showAttendance() {
        hideAll(); attendanceView.setVisible(true); attendanceView.setManaged(true);
        navAttendance.getStyleClass().setAll("nav-button", "nav-button-active");
        List<Student> students = App.getDatabase().getAllStudents();
        attStudentCombo.setItems(FXCollections.observableArrayList(students));
        List<Course> courses = App.getDatabase().getAllCourses();
        attCourseCombo.setItems(FXCollections.observableArrayList(courses));
    }

    @FXML private void showAdminReports() {
        hideAll(); adminReportsView.setVisible(true); adminReportsView.setManaged(true);
        navAdminReports.getStyleClass().setAll("nav-button", "nav-button-active");
        loadReports();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  STUDENT HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML private void handleSearch() {
        String q = searchField.getText().trim().toLowerCase();
        filteredStudents.setPredicate(s -> q.isEmpty() ||
            s.getStudentId().toLowerCase().contains(q) ||
            s.getName().toLowerCase().contains(q) ||
            (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(q)));
    }

    @FXML private void handleDelete() {
        Student sel = studentsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a scholar to banish from the registry.", true); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Banish Scholar");
        confirm.setHeaderText("Remove " + sel.getName() + " from the Sanctum?");
        confirm.setContentText("This will permanently delete the scholar's record.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                App.getDatabase().deleteStudent(sel.getStudentId());
                allStudents.remove(sel);
                updateStudentStats();
                setStatus("Scholar \"" + sel.getName() + "\" has been removed.", false);
            }
        });
    }

    @FXML private void handleAddStudent() {
        String sid = addIdField.getText().trim(), name = addNameField.getText().trim();
        String email = addEmailField.getText().trim(), pass = addPasswordField.getText().trim();
        String phone = addPhoneField.getText().trim(), yearS = addYearField.getText().trim();
        String dept = addDeptField.getValue();
        if (sid.isEmpty() || name.isEmpty() || email.isEmpty() || pass.isEmpty() || yearS.isEmpty() || dept == null) {
            formErrorLabel.setText("All fields except phone are required."); return;
        }
        int year;
        try { year = Integer.parseInt(yearS); } catch (NumberFormatException e) {
            formErrorLabel.setText("Enrollment year must be a number."); return;
        }
        if (App.getDatabase().getStudentById(sid) != null) {
            formErrorLabel.setText("A scholar with this ID already exists."); return;
        }
        App.getDatabase().addStudent(new Student(0, name, email, pass, phone, sid, dept, year));
        clearForm();
        formErrorLabel.setStyle("-fx-text-fill: #6dbf8a; -fx-font-size: 12px;");
        formErrorLabel.setText("Scholar \"" + name + "\" enrolled successfully!");
        showAllStudents();
    }

    private void clearForm() {
        addIdField.clear(); addNameField.clear(); addEmailField.clear();
        addPasswordField.clear(); addPhoneField.clear(); addYearField.clear();
        addDeptField.setValue(null); formErrorLabel.setText("");
        formErrorLabel.setStyle("-fx-text-fill: #e66777; -fx-font-size: 12px;");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  COURSE HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML private void handleAddCourse() {
        String id = newCourseIdField.getText().trim(), name = newCourseNameField.getText().trim();
        String instr = newInstructorField.getText().trim(), sem = newSemesterField.getText().trim();
        Integer credits = newCreditsCombo.getValue();
        if (id.isEmpty() || name.isEmpty() || instr.isEmpty() || credits == null) {
            setCourseStatus("All course fields are required.", true); return;
        }
        App.getDatabase().adminAddCourse(id, name, instr, credits, sem.isEmpty() ? "Spring 2026" : sem);
        newCourseIdField.clear(); newCourseNameField.clear(); newInstructorField.clear();
        loadCourses();
        setCourseStatus("Course \"" + name + "\" added successfully.", false);
    }

    @FXML private void handleDeleteCourse() {
        Course sel = coursesTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setCourseStatus("Select a course to delete.", true); return; }
        App.getDatabase().adminDeleteCourse(sel.getCourseId());
        loadCourses();
        setCourseStatus("Course \"" + sel.getCourseName() + "\" deleted.", false);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  GRADE HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML private void handleAddGrade() {
        Student s = gradeStudentCombo.getValue();
        Course  c = gradeCourseCombo.getValue();
        String  scoreStr = gradeScoreField.getText().trim();
        String  sem = gradeSemesterField.getText().trim();
        if (s == null || c == null || scoreStr.isEmpty()) {
            setGradeStatus("Select student, course, and enter a score.", true); return;
        }
        double score;
        try { score = Double.parseDouble(scoreStr); } catch (NumberFormatException e) {
            setGradeStatus("Score must be a number (e.g. 85.5).", true); return;
        }
        if (score < 0 || score > 100) { setGradeStatus("Score must be between 0 and 100.", true); return; }
        App.getDatabase().adminAddGrade(s.getStudentId(), c.getCourseId(), score, sem.isEmpty() ? "Spring 2026" : sem);
        gradeScoreField.clear();
        loadGradesForSelectedStudent();
        setGradeStatus("Grade added for " + s.getName() + " in " + c.getCourseName() + ".", false);
    }

    @FXML private void handleDeleteGrade() {
        Grade sel = gradesAdminTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setGradeStatus("Select a grade to delete.", true); return; }
        App.getDatabase().adminDeleteGrade(sel.getGradeId());
        loadGradesForSelectedStudent();
        setGradeStatus("Grade deleted.", false);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ATTENDANCE HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML private void handleAddAttendance() {
        Student s = attStudentCombo.getValue();
        Course  c = attCourseCombo.getValue();
        java.time.LocalDate date = attDatePicker.getValue();
        String  status = attStatusCombo.getValue();
        if (s == null || c == null || date == null || status == null) {
            setAttStatus("Select student, course, date, and status.", true); return;
        }
        App.getDatabase().adminAddAttendance(s.getStudentId(), c.getCourseId(), date.toString(), status);
        loadAttForSelectedStudent();
        setAttStatus("Attendance recorded for " + s.getName() + " on " + date + ".", false);
    }

    @FXML private void handleDeleteAttendance() {
        Attendance sel = attAdminTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setAttStatus("Select an attendance record to delete.", true); return; }
        App.getDatabase().adminDeleteAttendance(sel.getAttendId());
        loadAttForSelectedStudent();
        setAttStatus("Attendance record deleted.", false);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  EXPORT
    // ═════════════════════════════════════════════════════════════════════════

    @FXML private void handleExportAll() {
        try {
            String filename = "sanctum_report_all_" +
                java.time.LocalDate.now().toString().replace("-", "") + ".csv";
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
                pw.println("Student ID,Name,Department,Courses,GPA,Attendance %");
                for (String[] row : App.getDatabase().getStudentSummaries())
                    pw.println(String.join(",", row));
            }
            setAdminReportStatus("✓  Exported to: " + filename, false);
        } catch (Exception e) {
            setAdminReportStatus("Export failed: " + e.getMessage(), true);
        }
    }

    @FXML private void handleRefreshReports() { loadReports(); }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGOUT
    // ═════════════════════════════════════════════════════════════════════════

    @FXML private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                var icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/app_icon.png"));
                stage.getIcons().setAll(icon);
            } catch (Exception ignored) {}
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setScene(new Scene(root, 500, 600));
            stage.setTitle("Scholar's Sanctum");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  STATUS HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    private void setStatus(String msg, boolean err) {
        statusLabel.setStyle(err ? "-fx-text-fill: #e66777;" : "-fx-text-fill: #6dbf8a;");
        statusLabel.setText(msg);
    }
    private void setCourseStatus(String msg, boolean err) {
        coursesStatusLabel.setStyle(err ? "-fx-text-fill: #e66777;" : "-fx-text-fill: #6dbf8a;");
        coursesStatusLabel.setText(msg);
    }
    private void setGradeStatus(String msg, boolean err) {
        gradesStatusLabel.setStyle(err ? "-fx-text-fill: #e66777;" : "-fx-text-fill: #6dbf8a;");
        gradesStatusLabel.setText(msg);
    }
    private void setAttStatus(String msg, boolean err) {
        attStatusLabel.setStyle(err ? "-fx-text-fill: #e66777;" : "-fx-text-fill: #6dbf8a;");
        attStatusLabel.setText(msg);
    }
    private void setAdminReportStatus(String msg, boolean err) {
        // reuse statusLabel in reports view area if needed
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ENROLLMENT HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    private void setupEnrollmentsSection() {
        eColId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        eColName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        eColInstructor.setCellValueFactory(new PropertyValueFactory<>("instructorName"));
        eColCredits.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        eColSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        enrollTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        List<Student> students = App.getDatabase().getAllStudents();
        enrollStudentCombo.setItems(FXCollections.observableArrayList(students));
        enrollStudentCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Student s) { return s == null ? "" : s.getName() + " (" + s.getStudentId() + ")"; }
            public Student fromString(String str) { return null; }
        });
        enrollStudentCombo.setOnAction(e -> loadEnrollmentsForSelectedStudent());

        List<Course> courses = App.getDatabase().getAllCourses();
        enrollCourseCombo.setItems(FXCollections.observableArrayList(courses));
        enrollCourseCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Course c) { return c == null ? "" : c.getCourseId() + " — " + c.getCourseName(); }
            public Course fromString(String str) { return null; }
        });
    }

    private void loadEnrollmentsForSelectedStudent() {
        Student sel = enrollStudentCombo.getValue();
        if (sel == null) {
            enrollTable.setPlaceholder(new Label("Select a scholar to view enrolled courses."));
            enrollTable.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<Course> courses = App.getDatabase().getEnrollmentsForStudent(sel.getStudentId());
        enrollTable.setPlaceholder(new Label("No course enrollments found for " + sel.getName() + "."));
        enrollTable.setItems(FXCollections.observableArrayList(courses));
    }

    @FXML private void showEnrollments() {
        hideAll(); enrollmentsView.setVisible(true); enrollmentsView.setManaged(true);
        navEnrollments.getStyleClass().setAll("nav-button", "nav-button-active");
        // Refresh combos
        List<Student> students = App.getDatabase().getAllStudents();
        enrollStudentCombo.setItems(FXCollections.observableArrayList(students));
        List<Course> courses = App.getDatabase().getAllCourses();
        enrollCourseCombo.setItems(FXCollections.observableArrayList(courses));
        loadEnrollmentsForSelectedStudent();
    }

    @FXML private void handleEnrollStudent() {
        Student s = enrollStudentCombo.getValue();
        Course c = enrollCourseCombo.getValue();
        if (s == null || c == null) {
            setEnrollStatus("Select both a scholar and a course to enroll.", true);
            return;
        }
        App.getDatabase().enrollStudentInCourse(s.getStudentId(), c.getCourseId());
        loadEnrollmentsForSelectedStudent();
        setEnrollStatus("Successfully enrolled " + s.getName() + " in " + c.getCourseName() + ".", false);
    }

    @FXML private void handleDeleteEnrollment() {
        Student s = enrollStudentCombo.getValue();
        Course selCourse = enrollTable.getSelectionModel().getSelectedItem();
        if (s == null || selCourse == null) {
            setEnrollStatus("Select a course enrollment from the table to remove.", true);
            return;
        }
        App.getDatabase().unenrollStudentFromCourse(s.getStudentId(), selCourse.getCourseId());
        loadEnrollmentsForSelectedStudent();
        setEnrollStatus("Removed enrollment in " + selCourse.getCourseName() + " for " + s.getName() + ".", false);
    }

    private void setEnrollStatus(String msg, boolean err) {
        enrollStatusLabel.setStyle(err ? "-fx-text-fill: #e66777;" : "-fx-text-fill: #6dbf8a;");
        enrollStatusLabel.setText(msg);
    }
}

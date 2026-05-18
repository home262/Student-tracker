package studenttracker.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import studenttracker.App;
import studenttracker.model.*;
import studenttracker.service.StudentService;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController implements StudentAware {

    @FXML private Label nameLabel;
    @FXML private Label departmentLabel;
    @FXML private Label semesterLabel;
    @FXML private Label gpaLabel;
    @FXML private Label attendanceLabel;
    @FXML private Label creditsLabel;
    @FXML private Label standingLabel;
    @FXML private VBox  courseBreakdownBox;
    @FXML private VBox  warningsBox;
    @FXML private Label exportStatusLabel;

    private Student student;
    private StudentService service;

    @Override
    public void setStudent(Student student) {
        this.student = student;
        this.service = new StudentService(App.getDatabase());
        buildReport();
    }

    private void buildReport() {
        if (student == null) return;

        double gpa   = service.calculateGPA(student);
        double attRate = service.calculateAttendanceRate(student);
        List<Course> courses  = student.getCourses();
        List<Grade>  grades   = student.getGrades();
        List<Attendance> att  = student.getAttendanceRecords();
        int totalCredits = courses.stream().mapToInt(Course::getCreditHours).sum();

        // Header labels
        nameLabel.setText(student.getName());
        departmentLabel.setText(student.getDepartment() != null ? student.getDepartment() : "—");
        semesterLabel.setText("Spring 2026");
        gpaLabel.setText(String.format("%.2f", gpa));
        attendanceLabel.setText(String.format("%.0f%%", attRate));
        creditsLabel.setText(String.valueOf(totalCredits));

        String standing = gpa >= 3.7 ? "Honour Roll" : gpa >= 3.0 ? "Good Standing" :
                          gpa >= 2.0 ? "At Risk"    : "Critical";
        standingLabel.setText(standing);
        standingLabel.setStyle(gpa >= 3.0
            ? "-fx-text-fill: #7dd887; -fx-font-weight: bold; -fx-font-size: 13px;"
            : gpa >= 2.0
            ? "-fx-text-fill: #d8c28f; -fx-font-weight: bold; -fx-font-size: 13px;"
            : "-fx-text-fill: #e66777; -fx-font-weight: bold; -fx-font-size: 13px;");

        // Build grade map & attendance map per course
        Map<String, Grade>  gradeMap = new LinkedHashMap<>();
        for (Grade g : grades) {
            if (g.getCourse() != null) gradeMap.put(g.getCourse().getCourseId(), g);
        }
        Map<String, long[]> attMap = new LinkedHashMap<>();
        for (Attendance a : att) {
            if (a.getCourse() == null) continue;
            String cid = a.getCourse().getCourseId();
            attMap.computeIfAbsent(cid, k -> new long[]{0, 0});
            attMap.get(cid)[0]++;
            if (a.getStatus().equalsIgnoreCase("Present")) attMap.get(cid)[1]++;
        }

        // Per-course breakdown
        courseBreakdownBox.getChildren().clear();
        for (Course c : courses) {
            courseBreakdownBox.getChildren().add(buildCourseRow(c, gradeMap, attMap));
        }
        if (courses.isEmpty()) {
            Label l = new Label("No courses enrolled yet.");
            l.setStyle("-fx-text-fill: #8b847a;");
            courseBreakdownBox.getChildren().add(l);
        }

        // Warnings
        warningsBox.getChildren().clear();
        boolean hasWarning = false;
        for (Map.Entry<String, long[]> e : attMap.entrySet()) {
            long total = e.getValue()[0];
            long present = e.getValue()[1];
            long absent  = total - present;
            if (absent >= 3) {
                hasWarning = true;
                String cName = courses.stream().filter(c -> c.getCourseId().equals(e.getKey()))
                    .map(Course::getCourseName).findFirst().orElse(e.getKey());
                double pct = total > 0 ? (double) present / total * 100 : 0;
                warningsBox.getChildren().add(buildWarningRow(cName, absent, pct));
            }
        }
        if (attRate < 75) hasWarning = true;
        if (!hasWarning) {
            Label ok = new Label("✓  No warnings — your path through the Sanctum is clear.");
            ok.setStyle("-fx-text-fill: #7dd887; -fx-font-size: 13px;");
            warningsBox.getChildren().add(ok);
        }
    }

    private HBox buildCourseRow(Course course, Map<String, Grade> gradeMap,
                                 Map<String, long[]> attMap) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #1a1614; -fx-border-color: #2d251f; " +
                     "-fx-border-radius: 6; -fx-background-radius: 6;");

        // Course name
        VBox left = new VBox(3);
        Label nameL = new Label(course.getCourseName());
        nameL.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label instrL = new Label(course.getInstructorName() + "  ·  " + course.getCreditHours() + " cr");
        instrL.setStyle("-fx-text-fill: #6b645c; -fx-font-size: 11px;");
        left.getChildren().addAll(nameL, instrL);
        HBox.setHgrow(left, Priority.ALWAYS);

        // Grade badge
        Grade g = gradeMap.get(course.getCourseId());
        Label gradeBadge;
        if (g != null) {
            String letter = g.calculateLetter();
            gradeBadge = new Label(letter + "  " + String.format("%.0f", g.getScore()));
            gradeBadge.setStyle(getGradeBadgeStyle(letter));
        } else {
            gradeBadge = new Label("N/A");
            gradeBadge.setStyle("-fx-background-color: #2a2826; -fx-text-fill: #6b645c; " +
                                "-fx-font-size: 12px; -fx-background-radius: 4; -fx-padding: 3 8;");
        }

        // Attendance badge
        long[] a = attMap.get(course.getCourseId());
        Label attBadge;
        if (a != null && a[0] > 0) {
            double pct = (double) a[1] / a[0] * 100;
            long absent = a[0] - a[1];
            String pctStr = String.format("%.0f%%", pct) + " presence";
            attBadge = new Label(pctStr + (absent >= 3 ? "  ⚠" : ""));
            boolean warn = pct < 75 || absent >= 3;
            attBadge.setStyle("-fx-background-color: " + (warn ? "#3a1a1a" : "#1a2e1a") +
                "; -fx-text-fill: " + (warn ? "#e66777" : "#7dd887") +
                "; -fx-font-size: 11px; -fx-background-radius: 4; -fx-padding: 3 8;");
        } else {
            attBadge = new Label("No records");
            attBadge.setStyle("-fx-text-fill: #6b645c; -fx-font-size: 11px;");
        }

        row.getChildren().addAll(left, gradeBadge, attBadge);
        return row;
    }

    private VBox buildWarningRow(String courseName, long absences, double attPct) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setStyle("-fx-background-color: #1e0e10; -fx-border-color: #8b2020; " +
                     "-fx-border-radius: 6; -fx-background-radius: 6;");
        Label title = new Label("⚠  " + courseName);
        title.setStyle("-fx-text-fill: #e66777; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label detail = new Label(absences + " absences — attendance at " +
            String.format("%.0f%%", attPct) + ". Risk of exam exclusion.");
        detail.setStyle("-fx-text-fill: #a8a29e; -fx-font-size: 11px;");
        box.getChildren().addAll(title, detail);
        return box;
    }

    @FXML
    private void handleExportCSV() {
        if (student == null) return;
        try {
            String filename = "report_" + student.getStudentId() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

            try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
                pw.println("Scholar's Sanctum — Semester Report");
                pw.println("Student: " + student.getName());
                pw.println("ID: " + student.getStudentId());
                pw.println("Department: " + student.getDepartment());
                pw.println("GPA: " + String.format("%.2f", service.calculateGPA(student)));
                pw.println("Attendance Rate: " + String.format("%.0f%%",
                        service.calculateAttendanceRate(student)));
                pw.println();
                pw.println("Course,Instructor,Credits,Score,Grade,Attendance %");

                Map<String, Grade> gradeMap = new HashMap<>();
                for (Grade g : student.getGrades())
                    if (g.getCourse() != null) gradeMap.put(g.getCourse().getCourseId(), g);

                Map<String, long[]> attMap = new HashMap<>();
                for (Attendance a : student.getAttendanceRecords()) {
                    if (a.getCourse() == null) continue;
                    String cid = a.getCourse().getCourseId();
                    attMap.computeIfAbsent(cid, k -> new long[]{0, 0});
                    attMap.get(cid)[0]++;
                    if (a.getStatus().equalsIgnoreCase("Present")) attMap.get(cid)[1]++;
                }

                for (Course c : student.getCourses()) {
                    Grade g = gradeMap.get(c.getCourseId());
                    long[] a = attMap.get(c.getCourseId());
                    double attPct = (a != null && a[0] > 0) ? (double) a[1] / a[0] * 100 : 0;
                    pw.println(c.getCourseName() + "," + c.getInstructorName() + "," +
                        c.getCreditHours() + "," +
                        (g != null ? String.format("%.1f", g.getScore()) : "N/A") + "," +
                        (g != null ? g.calculateLetter() : "N/A") + "," +
                        (a != null ? String.format("%.0f%%", attPct) : "N/A"));
                }
            }
            exportStatusLabel.setStyle("-fx-text-fill: #7dd887; -fx-font-size: 12px;");
            exportStatusLabel.setText("✓  Exported to: " + filename);
        } catch (Exception e) {
            exportStatusLabel.setStyle("-fx-text-fill: #e66777; -fx-font-size: 12px;");
            exportStatusLabel.setText("Export failed: " + e.getMessage());
        }
    }

    private String getGradeBadgeStyle(String letter) {
        return switch (letter) {
            case "A" -> "-fx-background-color: #1e3a22; -fx-text-fill: #7dd887; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 4; -fx-padding: 3 10;";
            case "B" -> "-fx-background-color: #273646; -fx-text-fill: #8fb2d8; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 4; -fx-padding: 3 10;";
            case "C" -> "-fx-background-color: #463c27; -fx-text-fill: #d8c28f; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 4; -fx-padding: 3 10;";
            case "D" -> "-fx-background-color: #463127; -fx-text-fill: #d8a08f; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 4; -fx-padding: 3 10;";
            default  -> "-fx-background-color: #462727; -fx-text-fill: #d88f8f; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 4; -fx-padding: 3 10;";
        };
    }
}

package studenttracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import studenttracker.model.Student;

public class DatabaseManager {

    // ═══════════════════════════════════════════════════════════════
    //  DATABASE CONFIG
    // ═══════════════════════════════════════════════════════════════

    private static final String DB_NAME = "project";
// abdou samy
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/"
                                         + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";


    private static final String DB_URL   = "jdbc:mysql://localhost:3306/" + DB_NAME
                                         + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String DB_USER  = "root";
    private static final String DB_PASS  = "abdou1234";

    private Connection connection;

    // ═══════════════════════════════════════════════════════════════
    //  CONNECT
    // ═══════════════════════════════════════════════════════════════

    public void connect() {
        try {
            Connection baseConn = DriverManager.getConnection(BASE_URL, DB_USER, DB_PASS);

            Statement st = baseConn.createStatement();
            st.executeUpdate(
                "CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` "
              + "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            );
            st.close();
            baseConn.close();

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✅ Database connected: " + DB_NAME);

        } catch (SQLException e) {
            System.out.println("❌ Connection failed.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database disconnected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    //  CREATE ALL TABLES + SEED
    // ═══════════════════════════════════════════════════════════════

    public void createAllTables() {
        createStudentTable();
        createCourseTable();
        createGradeTable();
        createAttendanceTable();
        createAdminTable();
        createEnrollmentTable();
        seedData();
    }

    // ─────────────────────────────────────────────────────────────

public void createStudentTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS `students` (
                `user_id`         INT           NOT NULL AUTO_INCREMENT,
                `student_id`      VARCHAR(50)   NOT NULL UNIQUE, -- ضفنا كلمة UNIQUE هنا عشان يرضى يربط الجداول التانية
                `name`            VARCHAR(255)  NOT NULL,
                `email`           VARCHAR(255)  NOT NULL UNIQUE,
                `password`        VARCHAR(255)  NOT NULL,
                `phone`           VARCHAR(50),
                `department`      VARCHAR(255),
                `enrollment_year` INT,
                PRIMARY KEY (`user_id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """, "students");
    }
    public void createCourseTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS `courses` (
                `course_id`       VARCHAR(50)   NOT NULL,
                `course_name`     VARCHAR(255)  NOT NULL,
                `instructor_name` VARCHAR(255)  NOT NULL,
                `credit_hours`    INT,
                `semester`        VARCHAR(100),
                PRIMARY KEY (`course_id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """, "courses");
    }

    public void createGradeTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS `grades` (
                `grade_id`     INT         NOT NULL AUTO_INCREMENT,
                `student_id`   VARCHAR(50),
                `course_id`    VARCHAR(50),
                `score`        DOUBLE,
                `letter_grade` VARCHAR(5),
                `semester`     VARCHAR(100),
                PRIMARY KEY (`grade_id`),
                FOREIGN KEY (`student_id`) REFERENCES `students`(`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY (`course_id`)  REFERENCES `courses`(`course_id`)   ON DELETE CASCADE ON UPDATE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """, "grades");
    }

    public void createAttendanceTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS `attendance` (
                `attendance_id`   INT         NOT NULL AUTO_INCREMENT,
                `student_id`      VARCHAR(50),
                `course_id`       VARCHAR(50),
                `attendance_date` DATE,
                `status`          VARCHAR(20),
                PRIMARY KEY (`attendance_id`),
                FOREIGN KEY (`student_id`) REFERENCES `students`(`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY (`course_id`)  REFERENCES `courses`(`course_id`)   ON DELETE CASCADE ON UPDATE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """, "attendance");
    }

    public void createAdminTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS `admins` (
                `admin_id`   INT          NOT NULL AUTO_INCREMENT,
                `name`       VARCHAR(255) NOT NULL,
                `email`      VARCHAR(255) NOT NULL UNIQUE,
                `password`   VARCHAR(255) NOT NULL,
                `phone`      VARCHAR(50),
                `admin_code` VARCHAR(100),
                `department` VARCHAR(255),
                PRIMARY KEY (`admin_id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """, "admins");
    }

    public void createEnrollmentTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS `enrollments` (
                `student_id` VARCHAR(50) NOT NULL,
                `course_id`  VARCHAR(50) NOT NULL,
                PRIMARY KEY (`student_id`, `course_id`),
                FOREIGN KEY (`student_id`) REFERENCES `students`(`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY (`course_id`)  REFERENCES `courses`(`course_id`)   ON DELETE CASCADE ON UPDATE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """, "enrollments");
    }

    // ═══════════════════════════════════════════════════════════════
    //  SEED DATA — بيشتغل مرة واحدة بس لما الجداول تكون فاضية
    // ═══════════════════════════════════════════════════════════════

    private void seedData() {
        try {

            // ── 1. أول طالب ───────────────────────────────────────────
            ResultSet rs1 = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM `students` WHERE `student_id`='S101'");
            if (rs1.next() && rs1.getInt(1) == 0) {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO `students` "
                  + "(`student_id`,`name`,`email`,`password`,`phone`,`department`,`enrollment_year`) "
                  + "VALUES (?,?,?,?,?,?,?)"
                );
                ps.setString(1, "S101");
                ps.setString(2, "Yousef");
                ps.setString(3, "yousef@gmail.com");
                ps.setString(4, "1234");
                ps.setString(5, "01012345678");
                ps.setString(6, "Artificial Intelligence");
                ps.setInt(7,    2025);
                ps.executeUpdate();
                System.out.println("✅ First student S101 (Yousef) seeded.");
            }

            // ── 2. أول أدمن ───────────────────────────────────────────
            PreparedStatement adminPs = connection.prepareStatement(
                "INSERT IGNORE INTO `admins` "
              + "(`name`,`email`,`password`,`phone`,`admin_code`,`department`) "
              + "VALUES (?,?,?,?,?,?)"
            );
            adminPs.setString(1, "Head Warden");
            adminPs.setString(2, "admin@sanctum.edu");
            adminPs.setString(3, "admin123");
            adminPs.setString(4, "00000000000");
            adminPs.setString(5, "ADMIN001");
            adminPs.setString(6, "Administration");
            adminPs.executeUpdate();


            Object[][] courses = {
                {"AI101",  "AI Topics",                     "Dr. Mohamed Abdelmonem", 3, "Spring 2026"},
                {"DS101",  "Discrete Structures",           "Dr. Mohamed Abdelmonem", 3, "Spring 2026"},
                {"OOP101", "Object Oriented Programming",   "Dr. Ahmed Saleh",        3, "Spring 2026"},
                {"NW101",  "Network",                       "Walaa Mohamed",          3, "Spring 2026"},
                {"TW101",  "Technical Writing",             "Dr. Maged Wasfi",        2, "Spring 2026"},
                {"MTH101", "Math 2",                        "Dr. Hamdy Elshamy",      3, "Spring 2026"},
                {"LR101",  "Legal Responsibility",          "Dr. Elshahat Mansour",   2, "Spring 2026"},
                {"SI101",  "Special Issues",                "Dr. Sherif Zakaria",     2, "Spring 2026"},
                {"CS401",  "Algorithms & Complexity",       "Dr. Ibrahim Hassan",     3, "Spring 2026"},
                {"CS402",  "Machine Learning Fundamentals", "Dr. Nour El-Din",        3, "Spring 2026"}
            };
            PreparedStatement coursePs = connection.prepareStatement(
                "INSERT IGNORE INTO `courses` "
              + "(`course_id`,`course_name`,`instructor_name`,`credit_hours`,`semester`) "
              + "VALUES (?,?,?,?,?)"
            );
            for (Object[] c : courses) {
                coursePs.setString(1, (String) c[0]);
                coursePs.setString(2, (String) c[1]);
                coursePs.setString(3, (String) c[2]);
                coursePs.setInt(4,    (int)    c[3]);
                coursePs.setString(5, (String) c[4]);
                coursePs.executeUpdate();
            }
            System.out.println("✅ Courses seeded.");


            // ── 4. التسجيل في المواد ──────────────────────────────────
            ResultSet rsEnr = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM `enrollments`");
            if (rsEnr.next() && rsEnr.getInt(1) == 0) {
                PreparedStatement enrPs = connection.prepareStatement(
                    "INSERT IGNORE INTO `enrollments` (`student_id`,`course_id`) VALUES (?,?)"
                );
                for (String cId : new String[]{"AI101","DS101","OOP101","NW101","TW101","MTH101","LR101","SI101"}) {
                    enrPs.setString(1, "S101");
                    enrPs.setString(2, cId);
                    enrPs.executeUpdate();
                }
                System.out.println("✅ Enrollments seeded.");
            }

            // ── 5. الدرجات ───────────────────────────────────────────
            ResultSet rsGrades = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM `grades`");
            if (rsGrades.next() && rsGrades.getInt(1) == 0) {
                PreparedStatement gradePs = connection.prepareStatement(
                    "INSERT INTO `grades` (`student_id`,`course_id`,`score`,`letter_grade`,`semester`) "
                  + "VALUES (?,?,?,?,?)"
                );
                String[][] gradesData = {
                    {"AI101","82.14","B"}, {"DS101","87.81","B"},
                    {"OOP101","77.39","C"}, {"NW101","82.54","B"},
                    {"TW101","87.76","B"}, {"MTH101","95.64","A"},
                    {"LR101","66.91","D"}, {"SI101","70.47","C"}
                };
                for (String[] g : gradesData) {
                    gradePs.setString(1, "S101");
                    gradePs.setString(2, g[0]);
                    gradePs.setDouble(3, Double.parseDouble(g[1]));
                    gradePs.setString(4, g[2]);
                    gradePs.setString(5, "Spring 2026");
                    gradePs.executeUpdate();
                }
                System.out.println("✅ Grades seeded.");
            }

            // ── 6. الحضور والغياب ─────────────────────────────────────
            ResultSet rsAtt = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM `attendance`");
            if (rsAtt.next() && rsAtt.getInt(1) == 0) {
                PreparedStatement attPs = connection.prepareStatement(
                    "INSERT INTO `attendance` (`student_id`,`course_id`,`attendance_date`,`status`) "
                  + "VALUES (?,?,?,?)"
                );
                String[][] attData = {
                    {"AI101","2026-02-05","Present"}, {"AI101","2026-02-12","Present"},
                    {"AI101","2026-02-19","Present"}, {"AI101","2026-02-26","Present"},
                    {"AI101","2026-03-05","Absent"},  {"AI101","2026-03-12","Present"},
                    {"AI101","2026-03-19","Absent"},  {"AI101","2026-03-26","Present"},
                    {"AI101","2026-04-02","Present"}, {"AI101","2026-04-09","Present"},

                    {"DS101","2026-02-05","Present"}, {"DS101","2026-02-12","Present"},
                    {"DS101","2026-02-19","Present"}, {"DS101","2026-02-26","Present"},
                    {"DS101","2026-03-05","Absent"},  {"DS101","2026-03-12","Absent"},
                    {"DS101","2026-03-19","Present"}, {"DS101","2026-03-26","Present"},
                    {"DS101","2026-04-02","Present"}, {"DS101","2026-04-09","Present"},

                    {"OOP101","2026-02-05","Present"}, {"OOP101","2026-02-12","Present"},
                    {"OOP101","2026-02-19","Present"}, {"OOP101","2026-02-26","Present"},
                    {"OOP101","2026-03-05","Absent"},  {"OOP101","2026-03-12","Present"},
                    {"OOP101","2026-03-19","Present"}, {"OOP101","2026-03-26","Absent"},
                    {"OOP101","2026-04-02","Present"}, {"OOP101","2026-04-09","Present"},

                    {"NW101","2026-02-05","Present"}, {"NW101","2026-02-12","Present"},
                    {"NW101","2026-02-19","Present"}, {"NW101","2026-02-26","Present"},
                    {"NW101","2026-03-05","Present"}, {"NW101","2026-03-12","Present"},
                    {"NW101","2026-03-19","Present"}, {"NW101","2026-03-26","Present"},
                    {"NW101","2026-04-02","Present"}, {"NW101","2026-04-09","Present"}
                };
                for (String[] row : attData) {
                    attPs.setString(1, "S101");
                    attPs.setString(2, row[0]);
                    attPs.setString(3, row[1]);
                    attPs.setString(4, row[2]);
                    attPs.executeUpdate();
                }
                System.out.println("✅ Attendance records seeded.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void execute(String sql, String label) {
        try {
            connection.createStatement().execute(sql);
            System.out.println("✅ Table ready: " + label);
        } catch (SQLException e) {
            System.out.println("❌ Failed to create: " + label);
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  STUDENT CRUD
    // ═══════════════════════════════════════════════════════════════

    public void addStudent(Student student) {
        String sql = """
            INSERT INTO `students`
            (`name`,`email`,`password`,`phone`,`student_id`,`department`,`enrollment_year`)
            VALUES (?,?,?,?,?,?,?)
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPassword());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getStudentId());
            ps.setString(6, student.getDepartment());
            ps.setInt(7,    student.getEnrollmentYear());
            ps.executeUpdate();
            System.out.println("Student added: " + student.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Student getStudentById(String studentId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM `students` WHERE `student_id`=?"
            );
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("student_id"),
                        rs.getString("department"),
                        rs.getInt("enrollment_year")
                );
                loadCoursesForStudent(student);
                loadGradesForStudent(student);
                loadAttendanceForStudent(student);
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement()
                                     .executeQuery("SELECT * FROM `students`");
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("student_id"),
                        rs.getString("department"),
                        rs.getInt("enrollment_year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void updateStudent(Student student) {
        String sql = """
            UPDATE `students`
            SET `name`=?,`email`=?,`password`=?,`phone`=?,`department`=?,`enrollment_year`=?
            WHERE `student_id`=?
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPassword());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getDepartment());
            ps.setInt(6,    student.getEnrollmentYear());
            ps.setString(7, student.getStudentId());
            ps.executeUpdate();
            System.out.println("Student updated: " + student.getStudentId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(String studentId) {
        try {
            String safe = studentId.replace("'", "''");
            connection.createStatement().executeUpdate("DELETE FROM `grades`      WHERE `student_id`='" + safe + "'");
            connection.createStatement().executeUpdate("DELETE FROM `attendance`  WHERE `student_id`='" + safe + "'");
            connection.createStatement().executeUpdate("DELETE FROM `enrollments` WHERE `student_id`='" + safe + "'");
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM `students` WHERE `student_id`=?"
            );
            ps.setString(1, studentId);
            ps.executeUpdate();
            System.out.println("Student deleted: " + studentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  LOAD RELATIONS
    // ═══════════════════════════════════════════════════════════════

    private void loadCoursesForStudent(Student student) {
        String sql = "SELECT DISTINCT c.* FROM `courses` c WHERE c.`course_id` IN ("
                   + "  SELECT `course_id` FROM `enrollments` WHERE `student_id`=?"
                   + "  UNION SELECT `course_id` FROM `grades`     WHERE `student_id`=?"
                   + "  UNION SELECT `course_id` FROM `attendance` WHERE `student_id`=?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getStudentId());
            ps.setString(3, student.getStudentId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                student.addCourse(new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("semester")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadGradesForStudent(Student student) {
        String sql = "SELECT g.`grade_id`,g.`score`,g.`semester`,"
                   + "c.`course_id`,c.`course_name`,c.`instructor_name`,c.`credit_hours` "
                   + "FROM `grades` g JOIN `courses` c ON g.`course_id`=c.`course_id` "
                   + "WHERE g.`student_id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("semester")
                );
                student.addGrade(new studenttracker.model.Grade(
                        rs.getInt("grade_id"), student, course,
                        rs.getDouble("score"), rs.getString("semester")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadAttendanceForStudent(Student student) {
        String sql = "SELECT a.`attendance_id`,a.`attendance_date`,a.`status`,"
                   + "c.`course_id`,c.`course_name`,c.`instructor_name`,c.`credit_hours`,c.`semester` "
                   + "FROM `attendance` a JOIN `courses` c ON a.`course_id`=c.`course_id` "
                   + "WHERE a.`student_id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("semester")
                );
                java.time.LocalDate date;
                try { date = java.time.LocalDate.parse(rs.getString("attendance_date")); }
                catch (Exception ex) { date = java.time.LocalDate.now(); }
                student.addAttendance(new studenttracker.model.Attendance(
                        rs.getInt("attendance_id"), student, course,
                        date, rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN AUTH
    // ═══════════════════════════════════════════════════════════════

    public studenttracker.model.Admin getAdminByCode(String adminCode) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM `admins` WHERE `admin_code`=?"
            );
            ps.setString(1, adminCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new studenttracker.model.Admin(
                        rs.getInt("admin_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("password"),
                        rs.getString("phone"), rs.getString("admin_code"),
                        rs.getString("department")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN — COURSES
    // ═══════════════════════════════════════════════════════════════

    public List<studenttracker.model.Course> getAllCourses() {
        List<studenttracker.model.Course> list = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement()
                .executeQuery("SELECT * FROM `courses` ORDER BY `course_name`");
            while (rs.next()) {
                list.add(new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("semester")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void adminAddCourse(String id, String name, String instructor, int credits, String semester) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT IGNORE INTO `courses` "
              + "(`course_id`,`course_name`,`instructor_name`,`credit_hours`,`semester`) "
              + "VALUES (?,?,?,?,?)"
            );
            ps.setString(1, id); ps.setString(2, name); ps.setString(3, instructor);
            ps.setInt(4, credits); ps.setString(5, semester);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDeleteCourse(String courseId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM `courses` WHERE `course_id`=?"
            );
            ps.setString(1, courseId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN — GRADES
    // ═══════════════════════════════════════════════════════════════

    public List<studenttracker.model.Grade> getGradesForStudent(String studentId) {
        List<studenttracker.model.Grade> list = new ArrayList<>();
        String sql = "SELECT g.`grade_id`,g.`score`,g.`semester`,"
                   + "c.`course_id`,c.`course_name`,c.`instructor_name`,c.`credit_hours`,c.`semester` AS csem "
                   + "FROM `grades` g JOIN `courses` c ON g.`course_id`=c.`course_id` "
                   + "WHERE g.`student_id`=? ORDER BY c.`course_name`";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("csem")
                );
                list.add(new studenttracker.model.Grade(
                        rs.getInt("grade_id"), null, course,
                        rs.getDouble("score"), rs.getString("semester")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void adminAddGrade(String studentId, String courseId, double score, String semester) {
        String letter = score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : score >= 60 ? "D" : "F";
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO `grades` (`student_id`,`course_id`,`score`,`letter_grade`,`semester`) "
              + "VALUES (?,?,?,?,?)"
            );
            ps.setString(1, studentId); ps.setString(2, courseId);
            ps.setDouble(3, score);     ps.setString(4, letter); ps.setString(5, semester);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDeleteGrade(int gradeId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM `grades` WHERE `grade_id`=?"
            );
            ps.setInt(1, gradeId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN — ATTENDANCE
    // ═══════════════════════════════════════════════════════════════

    public List<studenttracker.model.Attendance> getAttendanceForStudent(String studentId) {
        List<studenttracker.model.Attendance> list = new ArrayList<>();
        String sql = "SELECT a.`attendance_id`,a.`attendance_date`,a.`status`,"
                   + "c.`course_id`,c.`course_name`,c.`instructor_name`,c.`credit_hours`,c.`semester` "
                   + "FROM `attendance` a JOIN `courses` c ON a.`course_id`=c.`course_id` "
                   + "WHERE a.`student_id`=? ORDER BY a.`attendance_date` DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("semester")
                );
                java.time.LocalDate date;
                try { date = java.time.LocalDate.parse(rs.getString("attendance_date")); }
                catch (Exception ex) { date = java.time.LocalDate.now(); }
                list.add(new studenttracker.model.Attendance(
                        rs.getInt("attendance_id"), null, course,
                        date, rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void adminAddAttendance(String studentId, String courseId, String date, String status) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO `attendance` (`student_id`,`course_id`,`attendance_date`,`status`) "
              + "VALUES (?,?,?,?)"
            );
            ps.setString(1, studentId); ps.setString(2, courseId);
            ps.setString(3, date);      ps.setString(4, status);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDeleteAttendance(int attendanceId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM `attendance` WHERE `attendance_id`=?"
            );
            ps.setInt(1, attendanceId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN — REPORTS
    // ═══════════════════════════════════════════════════════════════

    public List<String[]> getStudentSummaries() {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT s.`student_id`,s.`name`,s.`department`,"
                   + "COUNT(DISTINCT g.`course_id`) AS courses,"
                   + "AVG(g.`score`) AS avg_score,"
                   + "SUM(CASE WHEN a.`status`='Present' THEN 1 ELSE 0 END) AS present,"
                   + "COUNT(a.`attendance_id`) AS total_att "
                   + "FROM `students` s "
                   + "LEFT JOIN `grades`     g ON s.`student_id`=g.`student_id` "
                   + "LEFT JOIN `attendance` a ON s.`student_id`=a.`student_id` "
                   + "GROUP BY s.`student_id`,s.`name`,s.`department` "
                   + "ORDER BY s.`name`";
        try {
            ResultSet rs = connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                double avg   = rs.getDouble("avg_score");
                double gpa   = avg >= 90 ? 4.0 : avg >= 80 ? 3.0 : avg >= 70 ? 2.0 : avg >= 60 ? 1.0 : 0.0;
                int    total = rs.getInt("total_att");
                int    pres  = rs.getInt("present");
                double rate  = total > 0 ? (double) pres / total * 100 : 0;
                rows.add(new String[]{
                        rs.getString("student_id"), rs.getString("name"),
                        rs.getString("department"),  String.valueOf(rs.getInt("courses")),
                        String.format("%.2f", gpa),  String.format("%.0f%%", rate)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN — ENROLLMENTS
    // ═══════════════════════════════════════════════════════════════

    public List<studenttracker.model.Course> getEnrollmentsForStudent(String studentId) {
        List<studenttracker.model.Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM `courses` c "
                   + "JOIN `enrollments` e ON c.`course_id`=e.`course_id` "
                   + "WHERE e.`student_id`=? ORDER BY c.`course_name`";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new studenttracker.model.Course(
                        rs.getString("course_id"), rs.getString("course_name"),
                        rs.getString("instructor_name"), rs.getInt("credit_hours"),
                        rs.getString("semester")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void enrollStudentInCourse(String studentId, String courseId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT IGNORE INTO `enrollments` (`student_id`,`course_id`) VALUES (?,?)"
            );
            ps.setString(1, studentId); ps.setString(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void unenrollStudentFromCourse(String studentId, String courseId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM `enrollments` WHERE `student_id`=? AND `course_id`=?"
            );
            ps.setString(1, studentId); ps.setString(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
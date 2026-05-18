package studenttracker.database;

import studenttracker.model.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    //----------------------------- DATABASE URL -----------------------------

    private static final String DB_URL =
            "jdbc:sqlite:student_tracker.db";

    //----------------------------- CONNECTION OBJECT -----------------------------

    private Connection connection;

    //----------------------------- CONNECT TO DATABASE -----------------------------

    public void connect() {

        try {

            connection = DriverManager.getConnection(DB_URL);

            System.out.println("Database connected successfully.");

        }

        catch (SQLException e) {

            System.out.println("Connection failed.");

            e.printStackTrace();
        }
    }

    //----------------------------- CLOSE CONNECTION -----------------------------

    public void disconnect() {

        try {

            if(connection != null) {

                connection.close();

                System.out.println("Database disconnected.");
            }

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    //----------------------------- GET CONNECTION -----------------------------

    public Connection getConnection() {

        return connection;
    }
    //----------------------------- CREATE STUDENT TABLE -----------------------------

    public void createStudentTable() {

        String sql = """
            
            CREATE TABLE IF NOT EXISTS students (
            
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT NOT NULL,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                phone TEXT,
                department TEXT,
                enrollment_year INTEGER
            
            );
            
            """;

        try {

            Statement statement = connection.createStatement();

            statement.execute(sql);

            System.out.println("Student table created successfully.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    //----------------------------- INSERT STUDENT -----------------------------

    public void insertStudent(Student student) {

        String sql = """
            
            INSERT INTO students
            (
                student_id,
                name,
                email,
                password,
                phone,
                department,
                gpa,
                enrollment_year
            )
            
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, student.getStudentId());

            preparedStatement.setString(2, student.getName());

            preparedStatement.setString(3, student.getEmail());

            preparedStatement.setString(4, student.getPassword());

            preparedStatement.setString(5, student.getPhone());

            preparedStatement.setString(6, student.getDepartment());

            preparedStatement.setInt(7, student.getEnrollmentYear());

            preparedStatement.executeUpdate();

            System.out.println("Student inserted successfully.");
        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void createCourseTable() {

        String sql = """

            CREATE TABLE IF NOT EXISTS courses (

                course_id TEXT PRIMARY KEY,
                course_name TEXT NOT NULL,
                instructor_name TEXT NOT NULL,
                credit_hours INTEGER,
                semester TEXT

            );

            """;

        try {

            Statement statement = connection.createStatement();

            statement.execute(sql);

            System.out.println("Course table created.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }


    public void createGradeTable() {

        String sql = """

            CREATE TABLE IF NOT EXISTS grades (

                grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT,
                course_id TEXT,
                score REAL,
                letter_grade TEXT,
                semester TEXT,

                FOREIGN KEY(student_id)
                REFERENCES students(student_id),

                FOREIGN KEY(course_id)
                REFERENCES courses(course_id)

            );

            """;

        try {

            Statement statement = connection.createStatement();

            statement.execute(sql);

            System.out.println("Grade table created.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    //----------------------------- ADD STUDENT -----------------------------

    public void addStudent(Student student) {

        String sql = """
            INSERT INTO students(
            name,
            email,
            password,
            phone,
            student_id,
            department,
            enrollment_year
            )
            VALUES(?, ?, ?, ?, ?, ?, ?)
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.setString(3, student.getPassword());
            preparedStatement.setString(4, student.getPhone());
            preparedStatement.setString(5, student.getStudentId());
            preparedStatement.setString(6, student.getDepartment());
            preparedStatement.setInt(7, student.getEnrollmentYear());

            preparedStatement.executeUpdate();

            System.out.println("Student added successfully.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void createAttendanceTable() {

        String sql = """

            CREATE TABLE IF NOT EXISTS attendance (

                attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT,
                course_id TEXT,
                attendance_date TEXT,
                status TEXT,

                FOREIGN KEY(student_id)
                REFERENCES students(student_id),

                FOREIGN KEY(course_id)
                REFERENCES courses(course_id)

            );

            """;

        try {

            Statement statement = connection.createStatement();

            statement.execute(sql);

            System.out.println("Attendance table created.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public Student getStudentById(String studentId) {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, studentId);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if(resultSet.next()) {

                Student student = new Student(

                        resultSet.getInt("user_id"),

                        resultSet.getString("name"),

                        resultSet.getString("email"),

                        resultSet.getString("password"),

                        resultSet.getString("phone"),

                        resultSet.getString("student_id"),

                        resultSet.getString("department"),

                        resultSet.getInt("enrollment_year")
                );

                // Load related data
                loadCoursesForStudent(student);
                loadGradesForStudent(student);
                loadAttendanceForStudent(student);

                return student;
            }

        }

        catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    private void loadCoursesForStudent(Student student) {
        // Collect course IDs the student has grades, attendance, or is enrolled in
        String sql = "SELECT DISTINCT c.course_id, c.course_name, c.instructor_name, c.credit_hours, c.semester " +
                     "FROM courses c WHERE c.course_id IN (" +
                     "  SELECT course_id FROM enrollments WHERE student_id = ? " +
                     "  UNION SELECT course_id FROM grades WHERE student_id = ? " +
                     "  UNION SELECT course_id FROM attendance WHERE student_id = ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getStudentId());
            ps.setString(3, student.getStudentId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getInt("credit_hours"),
                        rs.getString("semester")
                );
                student.addCourse(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGradesForStudent(Student student) {
        String sql = "SELECT g.grade_id, g.score, g.semester, c.course_id, c.course_name, c.instructor_name, c.credit_hours " +
                     "FROM grades g JOIN courses c ON g.course_id = c.course_id WHERE g.student_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getInt("credit_hours"),
                        rs.getString("semester")
                );
                studenttracker.model.Grade grade = new studenttracker.model.Grade(
                        rs.getInt("grade_id"),
                        student,
                        course,
                        rs.getDouble("score"),
                        rs.getString("semester")
                );
                student.addGrade(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAttendanceForStudent(Student student) {
        String sql = "SELECT a.attendance_id, a.attendance_date, a.status, c.course_id, c.course_name, c.instructor_name, c.credit_hours, c.semester " +
                     "FROM attendance a JOIN courses c ON a.course_id = c.course_id WHERE a.student_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getInt("credit_hours"),
                        rs.getString("semester")
                );
                java.time.LocalDate date;
                try {
                    date = java.time.LocalDate.parse(rs.getString("attendance_date"));
                } catch (Exception ex) {
                    date = java.time.LocalDate.now();
                }
                studenttracker.model.Attendance att = new studenttracker.model.Attendance(
                        rs.getInt("attendance_id"),
                        student,
                        course,
                        date,
                        rs.getString("status")
                );
                student.addAttendance(att);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createAdminTable() {

        String sql = """

            CREATE TABLE IF NOT EXISTS admins (

                admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                phone TEXT,
                admin_code TEXT,
                department TEXT

            );

            """;

        try {

            Statement statement = connection.createStatement();

            statement.execute(sql);

            System.out.println("Admin table created.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void createEnrollmentTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS enrollments (
                student_id TEXT,
                course_id TEXT,
                PRIMARY KEY (student_id, course_id),
                FOREIGN KEY(student_id) REFERENCES students(student_id),
                FOREIGN KEY(course_id) REFERENCES courses(course_id)
            );
            """;
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
            System.out.println("Enrollments table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createAllTables() {

        createStudentTable();

        createCourseTable();

        createGradeTable();

        createAttendanceTable();

        createAdminTable();

        createEnrollmentTable();

        ensureDemoStudent();
    }
    private void ensureDemoStudent() {
        try {
            // Remove the default students from the database if they exist with default names
            String[][] defaultToBanish = {
                {"S102", "Yousef yasser abdelkarim Ali"},
                {"S103", "Abdelkawa sami eldali"},
                {"S104", "Bassem Ibrahim"},
                {"S105", "Ziad Wahba Sultan"},
                {"S106", "Mahmoud Saed Ata"}
            };
            for (String[] target : defaultToBanish) {
                String id = target[0];
                String name = target[1];
                String checkSql = "SELECT COUNT(*) FROM students WHERE student_id = ? AND name = ?";
                PreparedStatement ps = connection.prepareStatement(checkSql);
                ps.setString(1, id);
                ps.setString(2, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    connection.createStatement().executeUpdate("DELETE FROM grades WHERE student_id = '" + id + "'");
                    connection.createStatement().executeUpdate("DELETE FROM attendance WHERE student_id = '" + id + "'");
                    connection.createStatement().executeUpdate("DELETE FROM enrollments WHERE student_id = '" + id + "'");
                    connection.createStatement().executeUpdate("DELETE FROM students WHERE student_id = '" + id + "'");
                    System.out.println("Banished default demo student " + id + " (" + name + ")");
                }
            }

            // ── 1. Student S101 Yousef — only seed when S101 is not present ──
            String checkS101 = "SELECT COUNT(*) FROM students WHERE student_id = 'S101'";
            ResultSet rsS101 = connection.createStatement().executeQuery(checkS101);
            if (rsS101.next() && rsS101.getInt(1) == 0) {
                String insertStudentSql = "INSERT INTO students (student_id, name, email, password, phone, department, enrollment_year) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement studentPs = connection.prepareStatement(insertStudentSql);
                studentPs.setString(1, "S101");
                studentPs.setString(2, "Yousef");
                studentPs.setString(3, "yousef@gmail.com");
                studentPs.setString(4, "1234");
                studentPs.setString(5, "01012345678");
                studentPs.setString(6, "Artificial Intelligence");
                studentPs.setInt(7, 2025);
                studentPs.executeUpdate();
                System.out.println("Default student S101 (Yousef) seeded.");
            }

            // ── 2. Courses ──────────────────────────────────────────────────
            Object[][] coursesData = {
                {"AI101", "AI Topics", "Dr. Mohamed Abdelmonem", 3, "Spring 2026"},
                {"DS101", "Discrete Structures", "Dr. Mohamed Abdelmonem", 3, "Spring 2026"},
                {"OOP101", "Object Oriented Programming", "Dr. Ahmed Saleh", 3, "Spring 2026"},
                {"NW101", "Network", "Walaa Mohamed", 3, "Spring 2026"},
                {"TW101", "Technical Writing", "Dr. Maged Wasfi", 2, "Spring 2026"},
                {"MTH101", "Math 2", "Dr. Hamdy Elshamy", 3, "Spring 2026"},
                {"LR101", "Legal Responsibility", "Dr. Elshahat Mansour", 2, "Spring 2026"},
                {"SI101", "Special Issues", "Dr. Sherif Zakaria", 2, "Spring 2026"},
                {"CS401", "Algorithms & Complexity", "Dr. Ibrahim Hassan", 3, "Spring 2026"},
                {"CS402", "Machine Learning Fundamentals", "Dr. Nour El-Din", 3, "Spring 2026"}
            };

            String insertCourseSql = "INSERT OR IGNORE INTO courses (course_id, course_name, instructor_name, credit_hours, semester) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement coursePs = connection.prepareStatement(insertCourseSql);

            for (Object[] c : coursesData) {
                coursePs.setString(1, (String) c[0]);
                coursePs.setString(2, (String) c[1]);
                coursePs.setString(3, (String) c[2]);
                coursePs.setInt(4, (int) c[3]);
                coursePs.setString(5, (String) c[4]);
                coursePs.executeUpdate();
            }
            System.out.println("Courses seeded.");

            // ── 3. Random Grades ────────────────────────────────────────────
            String checkGrades = "SELECT COUNT(*) FROM grades";
            ResultSet rsGrades = connection.createStatement().executeQuery(checkGrades);
            if (rsGrades.next() && rsGrades.getInt(1) == 0) {
                String insertGradeSql = "INSERT INTO grades (student_id, course_id, score, letter_grade, semester) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement gradePs = connection.prepareStatement(insertGradeSql);

                java.util.Random rand = new java.util.Random();
                String[] sIds = {"S101"};
                String[] cIds = {"AI101", "DS101", "OOP101", "NW101", "TW101", "MTH101", "LR101", "SI101"};

                for (String sId : sIds) {
                    for (String cId : cIds) {
                        double score = 65 + rand.nextDouble() * 35; // 65 to 100
                        String letter;
                        if (score >= 90) letter = "A";
                        else if (score >= 80) letter = "B";
                        else if (score >= 70) letter = "C";
                        else if (score >= 60) letter = "D";
                        else letter = "F";

                        gradePs.setString(1, sId);
                        gradePs.setString(2, cId);
                        gradePs.setDouble(3, score);
                        gradePs.setString(4, letter);
                        gradePs.setString(5, "Spring 2026");
                        gradePs.executeUpdate();
                    }
                }
                System.out.println("Random grades seeded for S101.");
            }

            // ── 4. Detailed Attendance ──────────────────────────────────────
            String checkAtt = "SELECT COUNT(*) FROM attendance";
            ResultSet rsAtt = connection.createStatement().executeQuery(checkAtt);
            if (rsAtt.next() && rsAtt.getInt(1) == 0) {
                String insertAttSql = "INSERT INTO attendance (student_id, course_id, attendance_date, status) VALUES (?, ?, ?, ?)";
                PreparedStatement attPs = connection.prepareStatement(insertAttSql);

                java.util.Random rand = new java.util.Random();
                String[] sIds = {"S101"};
                String[] cIds = {"AI101", "DS101", "OOP101", "NW101"};

                String[] dates = {
                    "2026-02-05", "2026-02-12", "2026-02-19", "2026-02-26",
                    "2026-03-05", "2026-03-12", "2026-03-19", "2026-03-26",
                    "2026-04-02", "2026-04-09"
                };

                for (String sId : sIds) {
                    for (String cId : cIds) {
                        for (String date : dates) {
                            String status = (rand.nextDouble() > 0.20) ? "Present" : "Absent";
                            
                            attPs.setString(1, sId);
                            attPs.setString(2, cId);
                            attPs.setString(3, date);
                            attPs.setString(4, status);
                            attPs.executeUpdate();
                        }
                    }
                }
                System.out.println("Detailed attendance records seeded for S101.");
            }

            // ── 4.5 Default Enrollments for S101 ────────────────────────────
            String checkEnr = "SELECT COUNT(*) FROM enrollments";
            ResultSet rsEnr = connection.createStatement().executeQuery(checkEnr);
            if (rsEnr.next() && rsEnr.getInt(1) == 0) {
                String insertEnrSql = "INSERT OR IGNORE INTO enrollments (student_id, course_id) VALUES (?, ?)";
                PreparedStatement enrPs = connection.prepareStatement(insertEnrSql);
                String[] cIds = {"AI101", "DS101", "OOP101", "NW101", "TW101", "MTH101", "LR101", "SI101"};
                for (String cId : cIds) {
                    enrPs.setString(1, "S101");
                    enrPs.setString(2, cId);
                    enrPs.executeUpdate();
                }
                System.out.println("Default enrollments seeded for S101.");
            }

            // ── 5. Default Admin ────────────────────────────────────────────
            String insertAdminSql = "INSERT OR IGNORE INTO admins (name, email, password, phone, admin_code, department) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement adminPs = connection.prepareStatement(insertAdminSql);
            adminPs.setString(1, "Head Warden");
            adminPs.setString(2, "admin@sanctum.edu");
            adminPs.setString(3, "admin123");
            adminPs.setString(4, "00000000000");
            adminPs.setString(5, "ADMIN001");
            adminPs.setString(6, "Administration");
            adminPs.executeUpdate();
            System.out.println("Default admin seeded.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //----------------------------- GET ALL STUDENTS -----------------------------

    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students";

        try {

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next()) {

                Student student = new Student(

                        resultSet.getInt("user_id"),

                        resultSet.getString("name"),

                        resultSet.getString("email"),

                        resultSet.getString("password"),

                        resultSet.getString("phone"),

                        resultSet.getString("student_id"),

                        resultSet.getString("department"),

                        resultSet.getInt("enrollment_year")
                );

                students.add(student);
            }

        }

        catch (SQLException e) {

            e.printStackTrace();
        }

        return students;
    }
    //----------------------------- UPDATE STUDENT -----------------------------

    public void updateStudent(Student student) {

        String sql = """
            UPDATE students
            SET name = ?,
                email = ?,
                password = ?,
                phone = ?,
                department = ?,
                enrollment_year = ?
            WHERE student_id = ?
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.setString(3, student.getPassword());
            preparedStatement.setString(4, student.getPhone());
            preparedStatement.setString(5, student.getDepartment());
            preparedStatement.setInt(6, student.getEnrollmentYear());

            preparedStatement.setString(7, student.getStudentId());

            preparedStatement.executeUpdate();

            System.out.println("Student updated successfully.");

        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

    //----------------------------- DELETE STUDENT -----------------------------

    public void deleteStudent(String studentId) {

        try {
            // Cascade delete related records
            connection.createStatement().executeUpdate("DELETE FROM grades WHERE student_id = '" + studentId.replace("'", "''") + "'");
            connection.createStatement().executeUpdate("DELETE FROM attendance WHERE student_id = '" + studentId.replace("'", "''") + "'");
            connection.createStatement().executeUpdate("DELETE FROM enrollments WHERE student_id = '" + studentId.replace("'", "''") + "'");

            String sql = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, studentId);

            preparedStatement.executeUpdate();

            System.out.println("Student and related records deleted successfully.");
        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }


    //----------------------------- GET ADMIN BY CODE -----------------------------

    public studenttracker.model.Admin getAdminByCode(String adminCode) {

        String sql = "SELECT * FROM admins WHERE admin_code = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, adminCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new studenttracker.model.Admin(
                        rs.getInt("admin_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("admin_code"),
                        rs.getString("department")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ══════════════════════ ADMIN COURSE MANAGEMENT ══════════════════════

    public java.util.List<studenttracker.model.Course> getAllCourses() {
        java.util.List<studenttracker.model.Course> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_name";
        try {
            ResultSet rs = connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new studenttracker.model.Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("instructor_name"),
                    rs.getInt("credit_hours"),
                    rs.getString("semester")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void adminAddCourse(String id, String name, String instructor, int credits, String semester) {
        String sql = "INSERT OR IGNORE INTO courses (course_id, course_name, instructor_name, credit_hours, semester) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id); ps.setString(2, name); ps.setString(3, instructor);
            ps.setInt(4, credits); ps.setString(5, semester);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDeleteCourse(String courseId) {
        try {
            connection.createStatement().executeUpdate(
                "DELETE FROM courses WHERE course_id = '" + courseId.replace("'","''") + "'");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ══════════════════════ ADMIN GRADE MANAGEMENT ═══════════════════════

    public java.util.List<studenttracker.model.Grade> getGradesForStudent(String studentId) {
        java.util.List<studenttracker.model.Grade> list = new java.util.ArrayList<>();
        String sql = "SELECT g.grade_id, g.score, g.semester, c.course_id, c.course_name, c.instructor_name, c.credit_hours, c.semester AS csem " +
                     "FROM grades g JOIN courses c ON g.course_id = c.course_id WHERE g.student_id = ? ORDER BY c.course_name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                    rs.getString("course_id"), rs.getString("course_name"),
                    rs.getString("instructor_name"), rs.getInt("credit_hours"), rs.getString("csem"));
                studenttracker.model.Grade grade = new studenttracker.model.Grade(
                    rs.getInt("grade_id"), null, course, rs.getDouble("score"), rs.getString("semester"));
                list.add(grade);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void adminAddGrade(String studentId, String courseId, double score, String semester) {
        String letter = score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : score >= 60 ? "D" : "F";
        String sql = "INSERT INTO grades (student_id, course_id, score, letter_grade, semester) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId); ps.setString(2, courseId);
            ps.setDouble(3, score); ps.setString(4, letter); ps.setString(5, semester);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDeleteGrade(int gradeId) {
        try {
            connection.createStatement().executeUpdate("DELETE FROM grades WHERE grade_id = " + gradeId);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ══════════════════════ ADMIN ATTENDANCE MANAGEMENT ══════════════════

    public java.util.List<studenttracker.model.Attendance> getAttendanceForStudent(String studentId) {
        java.util.List<studenttracker.model.Attendance> list = new java.util.ArrayList<>();
        String sql = "SELECT a.attendance_id, a.attendance_date, a.status, c.course_id, c.course_name, c.instructor_name, c.credit_hours, c.semester " +
                     "FROM attendance a JOIN courses c ON a.course_id = c.course_id WHERE a.student_id = ? ORDER BY a.attendance_date DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studenttracker.model.Course course = new studenttracker.model.Course(
                    rs.getString("course_id"), rs.getString("course_name"),
                    rs.getString("instructor_name"), rs.getInt("credit_hours"), rs.getString("semester"));
                java.time.LocalDate date;
                try { date = java.time.LocalDate.parse(rs.getString("attendance_date")); }
                catch (Exception ex) { date = java.time.LocalDate.now(); }
                list.add(new studenttracker.model.Attendance(
                    rs.getInt("attendance_id"), null, course, date, rs.getString("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void adminAddAttendance(String studentId, String courseId, String date, String status) {
        String sql = "INSERT INTO attendance (student_id, course_id, attendance_date, status) VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId); ps.setString(2, courseId);
            ps.setString(3, date); ps.setString(4, status);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void adminDeleteAttendance(int attendanceId) {
        try {
            connection.createStatement().executeUpdate("DELETE FROM attendance WHERE attendance_id = " + attendanceId);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ══════════════════════ ADMIN REPORTS SUMMARY ════════════════════════

    public java.util.List<String[]> getStudentSummaries() {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        String sql = "SELECT s.student_id, s.name, s.department, " +
            "COUNT(DISTINCT g.course_id) as courses, " +
            "AVG(g.score) as avg_score, " +
            "SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) as present, " +
            "COUNT(a.attendance_id) as total_att " +
            "FROM students s " +
            "LEFT JOIN grades g ON s.student_id = g.student_id " +
            "LEFT JOIN attendance a ON s.student_id = a.student_id " +
            "GROUP BY s.student_id ORDER BY s.name";
        try {
            ResultSet rs = connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                double avg = rs.getDouble("avg_score");
                double gpa = avg >= 90 ? 4.0 : avg >= 80 ? 3.0 : avg >= 70 ? 2.0 : avg >= 60 ? 1.0 : 0.0;
                int totalAtt = rs.getInt("total_att");
                int present  = rs.getInt("present");
                double attRate = totalAtt > 0 ? (double) present / totalAtt * 100 : 0;
                rows.add(new String[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    String.valueOf(rs.getInt("courses")),
                    String.format("%.2f", gpa),
                    String.format("%.0f%%", attRate)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // ══════════════════════ ADMIN ENROLLMENT MANAGEMENT ══════════════════

    public List<studenttracker.model.Course> getEnrollmentsForStudent(String studentId) {
        List<studenttracker.model.Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c JOIN enrollments e ON c.course_id = e.course_id WHERE e.student_id = ? ORDER BY c.course_name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new studenttracker.model.Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("instructor_name"),
                    rs.getInt("credit_hours"),
                    rs.getString("semester")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void enrollStudentInCourse(String studentId, String courseId) {
        String sql = "INSERT OR IGNORE INTO enrollments (student_id, course_id) VALUES (?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.executeUpdate();
            System.out.println("Enrolled student " + studentId + " in course " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unenrollStudentFromCourse(String studentId, String courseId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.executeUpdate();
            System.out.println("Unenrolled student " + studentId + " from course " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
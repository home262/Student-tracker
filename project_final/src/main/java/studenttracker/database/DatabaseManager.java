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
            user_id,
            name,
            email,
            password,
            phone,
            student_id,
            department,
            enrollment_year
            )
            VALUES(?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setInt(1, student.getUserId());
            preparedStatement.setString(2, student.getName());
            preparedStatement.setString(3, student.getEmail());
            preparedStatement.setString(4, student.getPassword());
            preparedStatement.setString(5, student.getPhone());
            preparedStatement.setString(6, student.getStudentId());
            preparedStatement.setString(7, student.getDepartment());
            preparedStatement.setInt(8, student.getEnrollmentYear());

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
        // Collect course IDs the student has grades or attendance in
        String sql = "SELECT DISTINCT c.course_id, c.course_name, c.instructor_name, c.credit_hours, c.semester " +
                     "FROM courses c WHERE c.course_id IN (" +
                     "  SELECT course_id FROM grades WHERE student_id = ? " +
                     "  UNION SELECT course_id FROM attendance WHERE student_id = ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getStudentId());
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

    public void createAllTables() {

        createStudentTable();

        createCourseTable();

        createGradeTable();

        createAttendanceTable();

        createAdminTable();

        ensureDemoStudent();
    }
    private void ensureDemoStudent() {
        try {
            // ── 1. Students ──────────────────────────────────────────────────
            Object[][] studentsData = {
                {"S101", "Yousef", "yousef@gmail.com", "1234", "01012345678", "Artificial Intelligence", 2025},
                {"S102", "Yousef yasser abdelkarim Ali", "yousef.y@sanctum.edu", "1234", "01022223333", "Information Technology", 2026},
                {"S103", "Abdelkawa sami eldali", "abdelkawa.s@sanctum.edu", "1234", "01033334444", "Computer Science", 2026},
                {"S104", "Bassem Ibrahim", "bassem.i@sanctum.edu", "1234", "01044445555", "Software Engineering", 2026},
                {"S105", "Ziad Wahba Sultan", "ziad.w@sanctum.edu", "1234", "01055556666", "Cyber Security", 2026},
                {"S106", "Mahmoud Saed Ata", "mahmoud.s@sanctum.edu", "1234", "01066667777", "Artificial Intelligence", 2026}
            };

            String insertStudentSql = "INSERT OR IGNORE INTO students (student_id, name, email, password, phone, department, enrollment_year) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement studentPs = connection.prepareStatement(insertStudentSql);

            for (Object[] s : studentsData) {
                studentPs.setString(1, (String) s[0]);
                studentPs.setString(2, (String) s[1]);
                studentPs.setString(3, (String) s[2]);
                studentPs.setString(4, (String) s[3]);
                studentPs.setString(5, (String) s[4]);
                studentPs.setString(6, (String) s[5]);
                studentPs.setInt(7, (int) s[6]);
                studentPs.executeUpdate();
            }
            System.out.println("Students seeded.");

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
            // Only seed grades if the grades table is empty to avoid massive duplication
            String checkGrades = "SELECT COUNT(*) FROM grades";
            ResultSet rsGrades = connection.createStatement().executeQuery(checkGrades);
            if (rsGrades.next() && rsGrades.getInt(1) == 0) {
                String insertGradeSql = "INSERT INTO grades (student_id, course_id, score, letter_grade, semester) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement gradePs = connection.prepareStatement(insertGradeSql);

                java.util.Random rand = new java.util.Random();
                String[] sIds = {"S101", "S102", "S103", "S104", "S105", "S106"};
                String[] cIds = {"AI101", "DS101", "OOP101", "NW101", "TW101", "MTH101", "LR101", "SI101"};

                for (String sId : sIds) {
                    // Give each student grades for all 8 courses to make the UI look full
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
                System.out.println("Random grades seeded.");
            }

            // ── 4. Detailed Attendance ──────────────────────────────────────
            String checkAtt = "SELECT COUNT(*) FROM attendance";
            ResultSet rsAtt = connection.createStatement().executeQuery(checkAtt);
            if (rsAtt.next() && rsAtt.getInt(1) == 0) {
                String insertAttSql = "INSERT INTO attendance (student_id, course_id, attendance_date, status) VALUES (?, ?, ?, ?)";
                PreparedStatement attPs = connection.prepareStatement(insertAttSql);

                java.util.Random rand = new java.util.Random();
                String[] sIds = {"S101", "S102", "S103", "S104", "S105", "S106"};
                String[] cIds = {"AI101", "DS101", "OOP101", "NW101"}; // Pick a few courses for attendance

                String[] dates = {
                    "2026-02-05", "2026-02-12", "2026-02-19", "2026-02-26",
                    "2026-03-05", "2026-03-12", "2026-03-19", "2026-03-26",
                    "2026-04-02", "2026-04-09"
                };

                for (String sId : sIds) {
                    for (String cId : cIds) {
                        for (String date : dates) {
                            // 80% chance of being present
                            String status = (rand.nextDouble() > 0.20) ? "Present" : "Absent";
                            
                            attPs.setString(1, sId);
                            attPs.setString(2, cId);
                            attPs.setString(3, date);
                            attPs.setString(4, status);
                            attPs.executeUpdate();
                        }
                    }
                }
                System.out.println("Detailed attendance records seeded.");
            }

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

        String sql = "DELETE FROM students WHERE student_id = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, studentId);

            preparedStatement.executeUpdate();

            System.out.println("Student deleted successfully.");
        }

        catch (SQLException e) {

            e.printStackTrace();
        }
    }

}
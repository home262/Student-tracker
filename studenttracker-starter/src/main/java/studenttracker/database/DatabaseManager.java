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

                return new Student(

                        resultSet.getInt("user_id"),

                        resultSet.getString("name"),

                        resultSet.getString("email"),

                        resultSet.getString("password"),

                        resultSet.getString("phone"),

                        resultSet.getString("student_id"),

                        resultSet.getString("department"),

                        resultSet.getInt("enrollment_year")
                );
            }

        }

        catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
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
            // Check if any student exists
            String countSql = "SELECT COUNT(*) FROM students";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(countSql);
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert a demo student
                String insertSql = "INSERT INTO students (student_id, name, email, password, phone, department, enrollment_year) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(insertSql);
                ps.setString(1, "demo001");
                ps.setString(2, "Demo Student");
                ps.setString(3, "demo@example.com");
                ps.setString(4, "demo123");
                ps.setString(5, "");
                ps.setString(6, "Demo Department");
                ps.setInt(7, 2026);
                ps.executeUpdate();
                System.out.println("Demo student inserted.");
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
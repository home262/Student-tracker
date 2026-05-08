package studenttracker.database;

public class DatabaseManager {

    //---------------------------------------FIELDS------------------------------------------
    private static DatabaseManager instance;

    private String connection;
    private String DB_URL;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public DatabaseManager() {
    }

    //-----------------------------------METHODS----------------------------------------------
    public static DatabaseManager getInstance() {
        return null;
    }

    public void connect() {

    }

    public void disconnect() {

    }

    public void executeQuery(String sql) {

    }
}
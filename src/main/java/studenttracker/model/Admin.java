package studenttracker.model;

public class Admin extends User{
    //---------------------------------------FIELDS------------------------------------------
    private String adminCode;
    private String department;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public Admin() {super();}

    public Admin(int userId, String name, String email, String password, String phone, String adminCode, String department) {
        super(userId, name, email, password, phone);
        this.adminCode = adminCode;
        this.department = department;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setAdminCode(String adminCode) {this.adminCode = adminCode;}
    public void setDepartment(String department) {this.department = department;}

    //-----------------------------------GETTERS----------------------------------------------
    public String getDepartment() {return department;}
    public String getAdminCode() {return adminCode;}

    //--------------------------------METHODS-------------------------------------------------
    public void addStudent(){

    }

    public void removeStudent(){

    }

    public void generateReport(){

    }

    public void manageAttendance(){

    }

    //----------------------OVERRIDDEN METHOD FROM ABSTRACT CLASS <<USER>>---------------------
    @Override
    public boolean login() {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean validateEmail() {
        return false;
    }
}
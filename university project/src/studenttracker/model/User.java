package studenttracker.model;

public abstract class User {
    //--------FIELDS FOR USER DATA--------
    private int userId ;
    private String name;
    private String email ;
    private String password ;
    private String phone ;
    private String role ;

    //-------ABSTRACT METHOD TO INHERIT-------
    public abstract boolean login();
    public abstract void logout();
    public abstract String getInfo();
    public abstract boolean validateEmail();

    //--------CONSTRUCTOR TO INITIALIZE DATA----------
    public User() {}
    public User(int userId, String name, String email, String password, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    //---------SETTERS--------
    public void setUserId(int userId) {this.userId = userId;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setRole(String role) {this.role = role;}
    public void setName(String name) {this.name = name;}

    //---------GETTERS--------
    public int getUserId() {return userId;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public String getRole() {return role;}

}

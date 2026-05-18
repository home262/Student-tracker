package studenttracker.model;

public abstract class User {
    //---------------------------------------FIELDS------------------------------------------
    private int userId ;
    private String name;
    private String email ;
    private String password ;
    private String phone ;

    //--------------------------------ABSTRACT METHODS---------------------------------------
    public abstract boolean login();
    public abstract void logout();
    public abstract String getInfo();
    public abstract boolean validateEmail();

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public User() {}

    public User(int userId, String name, String email, String password, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setUserId(int userId) {this.userId = userId;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setName(String name) {this.name = name;}

    //-----------------------------------GETTERS----------------------------------------------
    public int getUserId() {return userId;}
    public String getName() {return name;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}

}
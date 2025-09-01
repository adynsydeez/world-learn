package com.example.demo;

public abstract class User {
    /* Class Properties */
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private String school;
    public Role role;
    // public classroom Classroom;     TO BE ADDED

    //User Constructor
    public User(String email, String password, Role role) {
        this.email = email; //VERIFICATION TO BE ADDED
        this.password = password;
        this.role = role;
    }

    //Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password; //VERIFICATION TO BE ADDED
    }

    public void setPassword() {
        this.password = password; //VERIFICATION TO BE ADDED
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter and Setter for age
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Getter and Setter for school
    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    //Getter Setter for role
    public Role getRole(){return role;}
    public void setRole(Role role){
        this.role = role;
    }
    //Methods
    public abstract String getUserRole();
}

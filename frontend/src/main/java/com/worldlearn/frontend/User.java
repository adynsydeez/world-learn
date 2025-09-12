package com.worldlearn.frontend;

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
        this.setEmail(email); //VERIFICATION TO BE ADDED
        this.setPassword(password);
        this.setRole(role);
    }

    //Getters and Setters
    /// ////// USERNAME
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    /// ///// EMAIL
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: please use name@edu.com");
        }
        else {
            this.email = email;
        }
    }

    /// ///// PASSWORD
    public String getPassword(){

        return password;
    }

    public void setPassword(String password) {

        this.password = password; //VERIFICATION TO BE ADDED
    }

    /// ///// FIRST NAME
    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("First name cannot be null");
        }
        // letters only (rejects "", " ", "A1", "J@ne", "John-Doe")
        if (!firstName.matches("^[A-Za-z]+$")) {
            throw new IllegalArgumentException("Invalid first name");
        }
        this.firstName = firstName;
    }

    /// ///// LAST NAME
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        // letters with optional internal hyphens (e.g., "Smith-Jones"),
        // disallows "_", "Sm!th", "-" (single hyphen), leading/trailing hyphen
        if (!lastName.matches("^[A-Za-z]+(?:-[A-Za-z]+)*$")) {
            throw new IllegalArgumentException("Invalid last name");
        }
        this.lastName = lastName;
    }

    /// ///// AGE
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /// ///// SCHOOL
    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    /// ///// ROLE
    public Role getRole(){return role;}

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
    }

    public abstract String getUserRole();
}

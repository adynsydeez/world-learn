package com.worldlearn.backend.database;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class User {
    @JsonProperty("id")
    private int id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private String role;

    /// ////Constructors

    public User(int id, String email, String password, String firstName, String lastName, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //User Constructor
    public User(String email, String password) {
        this.setEmail(email); //VERIFICATION TO BE ADDED
        this.setPassword(password);
        //this.role = role;
    }

    //Getters and Setters
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

    /// ///// ROLE
    public String getRole(){return role;}

    public void setRole(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
    }

    public abstract String getUserRole();

    @Override
    public String toString() {
        return String.format("User{id=%s, firstName='%s', lastName='%s', email='%s', role='%s'}",
                id, firstName, lastName, email, role);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

package com.worldlearn.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base user model
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "userRole"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Student.class, name = "Student"),
        @JsonSubTypes.Type(value = Teacher.class, name = "Teacher"),
})
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

    /**
     * Constructs a user with all fields
     * @param email
     * @param password
     * @param firstName
     * @param lastName
     * @param role
     */
    public User(String email, String password, String firstName, String lastName, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructs a user with email and password
     * @param email
     * @param password
     */
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    /**
     * No-arg constructor
     */
    public User(){}

    /**
     * Gets email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email
     * @param email
     */
    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: please use name@edu.com");
        } else {
            this.email = email;
        }
    }

    /**
     * Gets password
     * @return password
     */
    public String getPassword(){
        return this.password;
    }

    /**
     * Sets password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password; //VERIFICATION TO BE ADDED
    }

    /**
     * Gets first name
     * @return firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets first name
     * @param firstName
     */
    public void setFirstName(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("First name cannot be null");
        }
        if (!firstName.matches("^[A-Za-z]+$")) {
            throw new IllegalArgumentException("Invalid first name");
        }
        this.firstName = firstName;
    }

    /**
     * Gets last name
     * @return lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets last name
     * @param lastName
     */
    public void setLastName(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        if (!lastName.matches("^[A-Za-z]+(?:-[A-Za-z]+)*$")) {
            throw new IllegalArgumentException("Invalid last name");
        }
        this.lastName = lastName;
    }

    /**
     * Gets role
     * @return role
     */
    public String getRole(){ return this.role; }

    /**
     * Sets role
     * @param role
     */
    public void setRole(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
    }

    /**
     * Returns string representation
     * @return string
     */
    @Override
    public String toString() {
        return String.format("User{id=%s, firstName='%s', lastName='%s', email='%s', role='%s'}",
                id, firstName, lastName, email, role);
    }

    /**
     * Sets id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets id
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets user role (polymorphic)
     * @return role name
     */
    public abstract String getUserRole();
}

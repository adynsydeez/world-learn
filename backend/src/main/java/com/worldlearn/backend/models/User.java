package com.worldlearn.backend.models;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("id")
    private String id;

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

    public User() {
    }

    public User(String id, String email, String password, String firstName, String lastName, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getId() { return Integer.parseInt(this.id); }
    public void setId(int id) { this.id = String.valueOf(id); }

    public String getPassword() { return this.password; }

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return this.role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("User{id=%s, firstName='%s', lastName='%s', email='%s', role='%s'}",
                id, firstName, lastName, email, role);
    }
}

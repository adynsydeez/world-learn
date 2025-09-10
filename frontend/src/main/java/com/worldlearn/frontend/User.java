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
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: please use name@edu.com");
        }
        else {
            this.email = email;
        }
    }

    public String getPassword(){
        return password; //VERIFICATION TO BE ADDED
    }

    public void setPassword(String password) {
        this.password = password; //VERIFICATION TO BE ADDED
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private void setLastName(String lastName) {
        if (lastName == null) {
            throw new NullPointerException("Last name cannot be null");
        }
        String s = lastName.trim();
        if (s.isEmpty() || !s.codePoints().allMatch(cp -> Character.isLetter(cp) || cp == '-')) {
            throw new IllegalArgumentException("Last name may contain letters and hyphens only.");
        }
        this.lastName = s;
    }

    private void setFirstName(String firstName) {
        if (firstName == null) {
            throw new NullPointerException("First name cannot be null");
        }
        String s = firstName.trim();
        if (s.isEmpty() || !s.codePoints().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException("First name must contain letters only.");
        }
        this.firstName = s;
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

    private void setRole(Role role) {
        if (role == null) {
            throw new NullPointerException("Role cannot be null");
        }
        switch (role) {
            case STUDENT:
            case TEACHER:
                this.role = role;
                break;
            default:
                throw new IllegalArgumentException("Role must be STUDENT or TEACHER (got " + role + ").");
        }
    }

    public abstract String getUserRole();
}

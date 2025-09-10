# World Learn - Backend Setup Guide

A Java backend application using Javalin web framework and PostgreSQL database.

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- PostgreSQL 12+

## Installation Instructions (Windows)

### 1. Install Java 11+
1. Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Run the installer and follow the setup wizard
3. Open Command Prompt and verify installation: `java -version`

### 2. Install Maven
1. Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract the zip file to `C:\Program Files\Apache\maven`
3. Add `C:\Program Files\Apache\maven\bin` to your PATH environment variable:
   - Press `Win + R`, type `sysdm.cpl`, press Enter
   - Click "Environment Variables"
   - Under "System Variables", find and select "Path", click "Edit"
   - Click "New" and add: `C:\Program Files\Apache\maven\bin`
   - Click OK on all windows
4. Open a new Command Prompt and verify: `mvn -version`

### 3. Install PostgreSQL
1. Download from [PostgreSQL Downloads](https://www.postgresql.org/download/windows/)
2. Run the installer and follow these steps:
   - Choose installation directory (default is fine)
   - Select components (keep all selected)
   - Set data directory (default is fine)
   - **Set password for postgres user** (remember this! We'll use `worldlearnpw`)
   - Set port to 5432 (default)
   - Choose locale (default is fine)
3. Complete the installation

## Database Setup

### 1. Open pgAdmin or SQL Shell (psql)
- Open pgAdmin 4 from Start Menu, or
- Open SQL Shell (psql) from Start Menu

### 2. Create Database (using pgAdmin)
1. Right-click "Databases" → "Create" → "Database"
2. Name: `worldlearn`
3. Click "Save"

### 2. Create Database (using psql)
```sql
-- When prompted, enter the password you set during installation
CREATE DATABASE worldlearn;
\q
```

### 3. Connect to worldlearn Database
- In pgAdmin: Click on "worldlearn" database
- In psql: `psql -U postgres -d worldlearn`

### 4. Create All Tables
Copy and paste this entire script into pgAdmin Query Tool or psql:

```sql
-- ENUM types
CREATE TYPE user_role_type AS ENUM ('teacher', 'student');
CREATE TYPE visibility_type AS ENUM ('private', 'public');
CREATE TYPE teacher_role_type AS ENUM ('creator', 'editor', 'viewer');
CREATE TYPE question_type AS ENUM ('mcq', 'written', 'map');

-- USERS
CREATE TABLE Users (
  user_id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL UNIQUE,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  user_role user_role_type NOT NULL
);

-- CLASSES
CREATE TABLE Classes (
  class_id SERIAL PRIMARY KEY,
  join_code INT NOT NULL UNIQUE
);

-- TEACHER_CLASS
CREATE TABLE Teacher_Class (
  teacher_role teacher_role_type NOT NULL,
  class_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (class_id, user_id),
  FOREIGN KEY (class_id) REFERENCES Classes(class_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- STUDENT_CLASS
CREATE TABLE Student_Class (
  class_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (class_id, user_id),
  FOREIGN KEY (class_id) REFERENCES Classes(class_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- LESSONS
CREATE TABLE Lessons (
  lesson_id SERIAL PRIMARY KEY,
  visibility visibility_type NOT NULL
);

-- TEACHER_LESSON
CREATE TABLE Teacher_Lesson (
  teacher_role teacher_role_type NOT NULL,
  lesson_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (lesson_id, user_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- STUDENT_LESSON
CREATE TABLE Student_Lesson (
  lesson_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (lesson_id, user_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- CLASS_LESSON
CREATE TABLE Class_Lesson (
  class_id INT NOT NULL,
  lesson_id INT NOT NULL,
  PRIMARY KEY (class_id, lesson_id),
  FOREIGN KEY (class_id) REFERENCES Classes(class_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id)
);

-- QUIZZES
CREATE TABLE Quizzes (
  quiz_id SERIAL PRIMARY KEY,
  visibility visibility_type NOT NULL
);

-- TEACHER_QUIZ
CREATE TABLE Teacher_Quiz (
  teacher_role teacher_role_type NOT NULL,
  quiz_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (quiz_id, user_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- STUDENT_QUIZ
CREATE TABLE Student_Quiz (
  attempt_number INT,
  score INT,
  completed_at TIMESTAMP,
  quiz_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (quiz_id, user_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- QUESTIONS
CREATE TABLE Questions (
  question_id SERIAL PRIMARY KEY,
  answer VARCHAR(255),
  options TEXT[],
  prompt VARCHAR(500) NOT NULL,
  type question_type NOT NULL,
  points_worth INT NOT NULL,
  visibility visibility_type NOT NULL
);

-- TEACHER_QUESTION
CREATE TABLE Teacher_Question (
  teacher_role teacher_role_type NOT NULL,
  question_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (question_id, user_id),
  FOREIGN KEY (question_id) REFERENCES Questions(question_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- STUDENT_ANSWER
CREATE TABLE Student_Answer (
  given_answer VARCHAR(500) NOT NULL,
  points_earned INT NOT NULL,
  answered_at TIMESTAMP NOT NULL,
  question_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (question_id, user_id),
  FOREIGN KEY (question_id) REFERENCES Questions(question_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- LESSON_QUIZ
CREATE TABLE Lesson_Quiz (
  lesson_id INT NOT NULL,
  quiz_id INT NOT NULL,
  PRIMARY KEY (lesson_id, quiz_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id)
);

-- QUIZ_QUESTION
CREATE TABLE Quiz_Question (
  quiz_id INT NOT NULL,
  question_id INT NOT NULL,
  PRIMARY KEY (quiz_id, question_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id),
  FOREIGN KEY (question_id) REFERENCES Questions(question_id)
);

-- Insert sample data
INSERT INTO Users (email, first_name, last_name, user_role) VALUES 
('alice@example.com', 'Alice', 'Smith', 'teacher'),
('bob@example.com', 'Bob', 'Johnson', 'student'),
('carol@example.com', 'Carol', 'Williams', 'teacher');

INSERT INTO Classes (join_code) VALUES (123456), (789012);

INSERT INTO Lessons (visibility) VALUES ('public'), ('private');

INSERT INTO Quizzes (visibility) VALUES ('public'), ('private');
```

## Running the Application

### 1. Download/Clone the Project
- Download the project files to a folder like `C:\world-learn`
- Open Command Prompt and navigate to the backend folder:
```cmd
cd C:\world-learn\backend
```

### 2. Install Dependencies
```cmd
mvn clean install
```

### 3. Update Database Connection
Make sure your `Database.java` file has the correct connection details:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/worldlearn";
private static final String USER = "postgres";
private static final String PASSWORD = "worldlearnpw"; // Use the password you set during installation
```

### 4. Run the Backend
```cmd
mvn clean compile exec:java
```

### 5. Verify It's Working
- Open your web browser and go to: http://localhost:7000
- You should see: "Backend is running 🚀"
- Test the API: http://localhost:7000/api/users/1

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Health check |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create new user |

### Example API Usage

**Get User:**
- Open browser: http://localhost:7000/api/users/1
- Or using curl: `curl http://localhost:7000/api/users/1`

**Create User (using curl):**
```cmd
curl -X POST http://localhost:7000/api/users -H "Content-Type: application/json" -d "{\"id\":\"4\",\"firstName\":\"David\",\"lastName\":\"Brown\",\"email\":\"david@example.com\",\"role\":\"student\"}"
```

## Troubleshooting

### "mvn command not found"
- Make sure Maven is installed and added to your PATH
- Restart Command Prompt after adding to PATH

### "Database connection failed"
- Check if PostgreSQL is running:
  - Open Services (Win + R, type `services.msc`)
  - Look for "postgresql" service and make sure it's running
- Verify password in `Database.java` matches what you set during installation
- Make sure database `worldlearn` exists

### "Port 7000 already in use"
- Close any other applications using port 7000
- Or change the port number in `BackendApplication.java`

### "Package io.javalin does not exist"
- Run `mvn clean install` to download dependencies
- Make sure you have internet connection

## Project Structure
```
backend/
├── src/main/java/com/worldlearn/backend/
│   ├── BackendApplication.java    # Main application
│   ├── Database.java             # Database connection
│   └── User.java                 # User model
├── pom.xml                       # Maven dependencies
└── README.md                     # This file
```

## Quick Start Summary

1. **Install**: Java 11+, Maven, PostgreSQL
2. **Database**: Create `worldlearn` database and run the table creation script
3. **Run**: `mvn clean compile exec:java`
4. **Test**: Visit http://localhost:7000

## Need Help?

If you encounter issues:
1. Check that PostgreSQL service is running in Windows Services
2. Verify your database password matches the one in `Database.java`
3. Make sure all software is installed and PATH variables are set
4. Restart Command Prompt after making PATH changes

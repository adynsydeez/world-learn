# World-Learn

A Java application with a PostgreSQL database backend.

## Prerequisites

- [Java 21+](https://adoptium.net/) (Corretto, OpenJDK, etc.)
- [Maven](https://maven.apache.org/install.html)
- [PostgreSQL](https://www.postgresql.org/download/)

## Setup Instructions

### 1. Install Maven
- Download Maven from the link above.
- Extract it and add the `bin` folder to your system `PATH`.
- Verify installation:
  ```bash
  mvn -v
2. Install PostgreSQL
Download PostgreSQL and follow the installer.

Remember your username, password, and the port (default: 5432).

Verify installation:

```bash
  Copy code
  psql --version
3. Create Database
Open psql and run:

```sql
Copy code
CREATE DATABASE world_learn;

Connect to it:

```bash
Copy code
\c world_learn

4. Create Tables
Run the schema script:

```sql
Copy code
CREATE TABLE Users
(
  user_id INT NOT NULL,
  email VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  user_role ENUM('teacher', 'student') NOT NULL,
  PRIMARY KEY (user_id),
  UNIQUE (user_id),
  UNIQUE (email),
  UNIQUE ()
);

CREATE TABLE Classes
(
  class_id INT NOT NULL,
  join_code INT NOT NULL,
  PRIMARY KEY (class_id),
  UNIQUE (class_id),
  UNIQUE (join_code)
);

CREATE TABLE Teacher_Class
(
  teacher_role ENUM('creator', 'editor', 'viewer') NOT NULL,
  class_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (class_id, user_id),
  FOREIGN KEY (class_id) REFERENCES Classes(class_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id),
  UNIQUE ()
);

CREATE TABLE Student_Class
(
  class_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (class_id, user_id),
  FOREIGN KEY (class_id) REFERENCES Classes(class_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Lessons
(
  lesson_id INT NOT NULL,
  visibility ENUM('private', 'public') NOT NULL,
  PRIMARY KEY (lesson_id)
);

CREATE TABLE Teacher_Lesson
(
  teacher_role ENUM('creator', 'editor', 'viewer') NOT NULL,
  lesson_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (lesson_id, user_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Student_Lesson
(
  lesson_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (lesson_id, user_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Class_Lesson
(
  class_id INT NOT NULL,
  lesson_id INT NOT NULL,
  PRIMARY KEY (class_id, lesson_id),
  FOREIGN KEY (class_id) REFERENCES Classes(class_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id)
);

CREATE TABLE Quizzes
(
  quiz_id INT NOT NULL,
  visibility ENUM('private', 'public') NOT NULL,
  PRIMARY KEY (quiz_id)
);

CREATE TABLE Teacher_Quiz
(
  teacher_role ENUM('creator', 'editor', 'viewer') NOT NULL,
  quiz_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (quiz_id, user_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Student_Quiz
(
  attempt_number INT,
  score INT,
  completed_at DATE,
  quiz_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (quiz_id, user_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Questions
(
  question_id INT NOT NULL,
  answer VARCHAR(255),
  options TEXT[],
  prompt VARCHAR(500) NOT NULL,
  type ENUM('mcq', 'written', 'map') NOT NULL,
  points_worth INT NOT NULL,
  visibility ENUM('private', 'public') NOT NULL,
  PRIMARY KEY (question_id)
);

CREATE TABLE Teacher_Question
(
  teacher_role ENUM('creator', 'editor', 'viewer') NOT NULL,
  question_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (question_id, user_id),
  FOREIGN KEY (question_id) REFERENCES Questions(question_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Student_Answer
(
  given_answer VARCHAR(500) NOT NULL,
  points_earned INT NOT NULL,
  answered_at DATE NOT NULL,
  question_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (question_id, user_id),
  FOREIGN KEY (question_id) REFERENCES Questions(question_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Lesson_Quiz
(
  lesson_id INT NOT NULL,
  quiz_id INT NOT NULL,
  PRIMARY KEY (lesson_id, quiz_id),
  FOREIGN KEY (lesson_id) REFERENCES Lessons(lesson_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id)
);

CREATE TABLE Quiz_Question
(
  quiz_id INT NOT NULL,
  question_id INT NOT NULL,
  PRIMARY KEY (quiz_id, question_id),
  FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id),
  FOREIGN KEY (question_id) REFERENCES Questions(question_id)
);



```bash
Copy code
psql -U your_username -d world_learn -f schema.sql

5. Build the Project
From the project root:

bash
Copy code
mvn clean install

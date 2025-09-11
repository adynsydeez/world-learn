package com.worldlearn.backend.services;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.database.QuestionDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QuestionService {

    public void createQuestion(Question q) throws SQLException {
        // Open a DB connection
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            QuestionDAO dao = new QuestionDAO(conn);
            dao.insert(q);
        }
    }

    public Optional<Question> getQuestionById(int id) throws SQLException {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            QuestionDAO dao = new QuestionDAO(conn);
            return dao.findById(id);
        }
    }

    public void deleteQuestion(int id) throws SQLException {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            QuestionDAO dao = new QuestionDAO(conn);
            dao.delete(id);
        }
    }

    public List<Question> getAllQuestions() throws SQLException {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            QuestionDAO dao = new QuestionDAO(conn);
            return dao.getAllQuestions();
        }
    }


}

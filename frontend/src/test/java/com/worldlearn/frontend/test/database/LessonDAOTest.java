package com.worldlearn.frontend.test.database;

import com.worldlearn.backend.database.*;
import com.worldlearn.backend.models.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LessonDAOTest {
    /// //Mocks fake database
    static class FakeDatabase extends com.worldlearn.backend.database.Database {
        private final Connection connection;
        FakeDatabase(Connection c) { this.connection = c; }
        @Override public Connection getConnection() { return connection; }
    }

    private Connection conn;
    private PreparedStatement stmt;
    private PreparedStatement stmtKeys;
    private ResultSet insertRs;
    private ResultSet keys;

    private LessonDAO dao;

    @BeforeEach
    void setup() throws Exception {
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        stmtKeys = mock(PreparedStatement.class);
        insertRs = mock(ResultSet.class);
        keys = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(stmtKeys);

        when(stmt.executeQuery()).thenReturn(insertRs);
        when(stmtKeys.executeUpdate()).thenReturn(1);
        when(stmtKeys.getGeneratedKeys()).thenReturn(keys);

        // dao under test wired with fake Database returning our mocked Connection
        dao = new LessonDAO(new UserDAOTest.FakeDatabase(conn));
    }

    @Nested
    @DisplayName("createLesson")
    class createLesson {
        @Test
        void nullArgumentThrowsNPE() {
            assertThrows(NullPointerException.class, () -> dao.createLesson(null));
        }

        @Test
        void FieldsRecordedOnReturnedObject() throws Exception {
            when(insertRs.next()).thenReturn(true,false);
            when(insertRs.getInt("lesson_name")).thenReturn(1);

        }
    }
}

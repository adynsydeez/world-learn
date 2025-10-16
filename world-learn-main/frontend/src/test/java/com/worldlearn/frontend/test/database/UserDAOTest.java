package com.worldlearn.frontend.test.database;

import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.models.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class UserDAOTest {
    /// //Mocks fake database
    static class FakeDatabase extends com.worldlearn.backend.database.Database {
        private final Connection connection;
        FakeDatabase(Connection c) { this.connection = c; }
        @Override public Connection getConnection() { return connection; }
    }

    private Connection conn;
    private PreparedStatement stmt;
    private PreparedStatement stmtKeys;
    private ResultSet rs;
    private ResultSet keys;

    private UserDAO dao;

    @BeforeEach
    void setup() throws Exception {
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        stmtKeys = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        keys = mock(ResultSet.class);

        // generic stubs many tests rely on
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        // overload with generated keys
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(stmtKeys);

        when(stmt.executeQuery()).thenReturn(rs);
        when(stmtKeys.executeUpdate()).thenReturn(1);
        when(stmtKeys.getGeneratedKeys()).thenReturn(keys);

        // dao under test wired with fake Database returning our mocked Connection
        dao = new UserDAO(new FakeDatabase(conn));
    }

    /// //getUserByID

    @Test
    void getUserByIdStudentMapped() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("user_id")).thenReturn("5");
        when(rs.getString("email")).thenReturn("s@edu.com");
        when(rs.getString("password")).thenReturn("pw");
        when(rs.getString("first_name")).thenReturn("Sam");
        when(rs.getString("last_name")).thenReturn("Jones");
        when(rs.getString("user_role")).thenReturn("student");

        User u = dao.getUserById("5");

        assertNotNull(u);
        assertTrue(u instanceof Student);
        assertEquals(5, u.getId());
        assertEquals("s@edu.com", u.getEmail());
        assertEquals("Sam", u.getFirstName());
        assertEquals("Jones", u.getLastName());
        verify(stmt).setInt(1, 5);
        verify(stmt).executeQuery();
    }

    @Test
    void getUserByIdUnknownRoleThrows() throws Exception {
        when(rs.next()).thenReturn(true);
        when(rs.getString("user_id")).thenReturn("7");
        when(rs.getString("email")).thenReturn("x@edu.com");
        when(rs.getString("password")).thenReturn("pw");
        when(rs.getString("first_name")).thenReturn("X");
        when(rs.getString("last_name")).thenReturn("Y");
        when(rs.getString("user_role")).thenReturn("admin"); // triggers IllegalArgumentException

        assertThrows(IllegalArgumentException.class, () -> dao.getUserById("7"));
    }

    @Test
    void getUserByIdNoRowNull() throws Exception {
        when(rs.next()).thenReturn(false);
        assertNull(dao.getUserById("999"));
    }

    /// //createUSER

    @Test
    void createUserSetsGeneratedIdReturnsUser() throws Exception {
        // Generated keys present
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(42);

        User newStudent = new Student("a@edu.com", "pw", "A", "B", "student");

        User created = dao.createUser(newStudent);

        assertSame(newStudent, created);          // DAO returns same object after setting id
        assertEquals(42, created.getId());        // ID set from generated keys
        verify(stmtKeys, times(1)).executeUpdate();
        verify(stmtKeys, times(1)).getGeneratedKeys();

        // verify parameters bound
        verify(stmtKeys).setString(1, "a@edu.com");
        verify(stmtKeys).setString(2, "pw");
        verify(stmtKeys).setString(3, "A");
        verify(stmtKeys).setString(4, "B");
        verify(stmtKeys).setString(5, "student");
    }

    @Test
    void createUserNoGeneratedIdThrowsSQLException() throws Exception {
        when(stmtKeys.executeUpdate()).thenReturn(1);
        when(stmtKeys.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(false); // no id

        User u = new Student("a@edu.com", "pw", "A", "B", "student");
        SQLException ex = assertThrows(SQLException.class, () -> dao.createUser(u));
        assertTrue(ex.getMessage().toLowerCase().contains("no id"));
    }

    /// //getAllUsers

    @Test
    void getAllUsersMapsMultipleRows() throws Exception {
        when(rs.next()).thenReturn(true, true, false);
        // row 1
        when(rs.getString("user_id")).thenReturn("1", "2");
        when(rs.getString("email")).thenReturn("s@edu.com", "t@edu.com");
        when(rs.getString("password")).thenReturn("pwS", "pwT");
        when(rs.getString("first_name")).thenReturn("Sam", "Tina");
        when(rs.getString("last_name")).thenReturn("Jones", "Smith");
        when(rs.getString("user_role")).thenReturn("student", "teacher");

        List<User> users = dao.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.get(0) instanceof Student);
        assertTrue(users.get(1) instanceof Teacher);
        assertEquals(1, users.get(0).getId());
        assertEquals(2, users.get(1).getId());
    }

    @Test
    void getAllUsersUnknownRoleThrows() throws Exception {
        when(rs.next()).thenReturn(true);
        when(rs.getString("user_id")).thenReturn("3");
        when(rs.getString("email")).thenReturn("x@edu.com");
        when(rs.getString("password")).thenReturn("pw");
        when(rs.getString("first_name")).thenReturn("X");
        when(rs.getString("last_name")).thenReturn("Y");
        when(rs.getString("user_role")).thenReturn("admin");

        assertThrows(IllegalArgumentException.class, () -> dao.getAllUsers());
    }

    /// //getUserByEmailAndPassword

    @Test
    void getByEmailAndPasswordMatchReturnsUser() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("user_role")).thenReturn("teacher");
        when(rs.getInt("user_id")).thenReturn(9);
        when(rs.getString("first_name")).thenReturn("Tina");
        when(rs.getString("last_name")).thenReturn("Smith");
        when(rs.getString("email")).thenReturn("t@edu.com");
        when(rs.getString("password")).thenReturn("pw");

        User u = dao.getUserByEmailAndPassword("t@edu.com", "pw");
        assertNotNull(u);
        assertTrue(u instanceof Teacher);
        assertEquals(9, u.getId());
        verify(stmt).setString(1, "t@edu.com");
        verify(stmt).setString(2, "pw");
    }

    @Test
    void getByEmailAndPasswordNoMatchReturnsNull() throws Exception {
        when(rs.next()).thenReturn(false);
        assertNull(dao.getUserByEmailAndPassword("x@edu.com", "nope"));
    }

    /// //updateUser

    @Test
    void updateUserRowsAffectedReturnsUpdatedUser() throws Exception {
        // use stmt (non-keys)
        when(stmt.executeUpdate()).thenReturn(1);
        User u = new Student("a@edu.com", "pw", "A", "B", "student");

        User updated = dao.updateUser(5, u);

        assertNotNull(updated);
        assertEquals(5, updated.getId());
        verify(stmt).setString(1, "A");
        verify(stmt).setString(2, "B");
        verify(stmt).setString(3, "a@edu.com");
        verify(stmt).setString(4, "pw");
        verify(stmt).setString(5, "student");
        verify(stmt).setInt(6, 5);
    }

    @Test
    void updateUserZeroRowsReturnsNull() throws Exception {
        when(stmt.executeUpdate()).thenReturn(0);
        User u = new Student("a@edu.com", "pw", "A", "B", "student");
        assertNull(dao.updateUser(5, u));
    }

    /// //deleteUser

    @Test
    void deleteUserTrueWhenRowsAffected() throws Exception {
        when(stmt.executeUpdate()).thenReturn(1);
        assertTrue(dao.deleteUser(7));
        verify(stmt).setInt(1, 7);
    }

    @Test
    void deleteUserFalseWhenNoRows() throws Exception {
        when(stmt.executeUpdate()).thenReturn(0);
        assertFalse(dao.deleteUser(7));
    }
}

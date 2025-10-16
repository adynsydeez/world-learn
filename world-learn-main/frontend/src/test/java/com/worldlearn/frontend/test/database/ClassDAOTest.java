package com.worldlearn.frontend.test.database;

import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Student;
import com.worldlearn.backend.models.WlClass;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

class ClassDAOTest {

    // Fake DB that returns our mocked Connection
    static class FakeDatabase extends com.worldlearn.backend.database.Database {
        private final Connection conn;
        FakeDatabase(Connection conn) { this.conn = conn; }
        @Override public Connection getConnection() { return conn; }
    }

    private Connection conn;

    // Separate mocks per SQL path
    private PreparedStatement selectStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;
    private ResultSet selectRs;
    private ResultSet insertRs;

    private ClassDAO dao;

    @BeforeEach
    void setup() throws Exception {
        conn       = mock(Connection.class);
        selectStmt = mock(PreparedStatement.class);
        insertStmt = mock(PreparedStatement.class);
        updateStmt = mock(PreparedStatement.class);
        deleteStmt = mock(PreparedStatement.class);
        selectRs   = mock(ResultSet.class);
        insertRs   = mock(ResultSet.class);

        // Route by SQL prefix (less brittle than call order)
        when(conn.prepareStatement(startsWith("SELECT"))).thenReturn(selectStmt);
        when(conn.prepareStatement(startsWith("INSERT INTO Classes"))).thenReturn(insertStmt);
        when(conn.prepareStatement(startsWith("UPDATE Classes"))).thenReturn(updateStmt);
        when(conn.prepareStatement(startsWith("DELETE FROM"))).thenReturn(deleteStmt);

        when(selectStmt.executeQuery()).thenReturn(selectRs);
        when(insertStmt.executeQuery()).thenReturn(insertRs);
        when(updateStmt.executeUpdate()).thenReturn(1);

        dao = new ClassDAO(new FakeDatabase(conn));
    }

    @Nested
    @DisplayName("GetClassByID")
    class GetClassByID {
        @Test
        void rowMapped() throws Exception {
            when(selectRs.next()).thenReturn(true, false);
            when(selectRs.getInt("class_id")).thenReturn(1);
            when(selectRs.getString("class_name")).thenReturn("Class1");
            when(selectRs.getInt("join_code")).thenReturn(1234);

            var maybe = dao.getClassById(1);

            assertTrue(maybe.isPresent());
            WlClass c = maybe.get();
            assertEquals(1, c.getId());
            assertEquals("Class1", c.getClassName());
            assertEquals(1234, c.getJoinCode());

            verify(selectStmt).setInt(1, 1);
            verify(selectStmt).executeQuery();
        }

        @Test
        void noRow_returnsEmpty() throws Exception {
            when(selectRs.next()).thenReturn(false);

            assertTrue(dao.getClassById(999).isEmpty());
            verify(selectStmt).setInt(1, 999);
            verify(selectStmt).executeQuery();
        }
    }

    @Nested
    @DisplayName("CreateClass")
    class CreateClassTest {
        @Test
        void nullArgumentthrowsNPE() {
            assertThrows(NullPointerException.class, () -> dao.createClass(null));
        }

        @Test
        void fieldsRecorded_onReturnedObject_andUsedInUpdate() throws Exception {
            when(insertRs.next()).thenReturn(true, false);
            when(insertRs.getInt("class_id")).thenReturn(1);

            WlClass out = dao.createClass(new WlClass(0, "Assman", 0));

            assertEquals(1, out.getId());
            assertEquals("Assman", out.getClassName());
            assertTrue(out.getJoinCode() != 0, "joinCode should be generated and non-zero");

            // Also assert SQL params were bound as expected
            verify(insertStmt).setString(1, "Assman");
            verify(insertStmt).setInt(2, 0);                 // temp value per DAO
            verify(insertStmt).executeQuery();

            verify(updateStmt).setInt(1, out.getJoinCode()); // the same code used in UPDATE
            verify(updateStmt).setInt(2, 1);                 // class_id from INSERT
            verify(updateStmt).executeUpdate();

            verify(conn).setAutoCommit(false);
            verify(conn).commit();
            verify(conn, never()).rollback();
        }
    }


    @Nested
    @DisplayName("getAllClassesForUser")
    class getAllClassesForUser {

        @Test
        void studentReturnsAllClasses() throws Exception {
            Student user = new Student("s@edu.com", "pw", "Josh", "Jones", "student");
            user.setId(10);

            // ResultSet: two rows, then end
            when(selectRs.next()).thenReturn(true, true, false);
            when(selectRs.getInt("class_id")).thenReturn(1, 2);
            when(selectRs.getString("class_name")).thenReturn("Class A", "Class B");
            when(selectRs.getInt("join_code")).thenReturn(111111, 222222);

            List<WlClass> out = dao.getAllClassesForUser(user);

            assertEquals(2, out.size());
            assertAll(
                    () -> assertEquals(1, out.get(0).getId()),
                    () -> assertEquals("Class A", out.get(0).getClassName()),
                    () -> assertEquals(111111, out.get(0).getJoinCode()),
                    () -> assertEquals(2, out.get(1).getId()),
                    () -> assertEquals("Class B", out.get(1).getClassName()),
                    () -> assertEquals(222222, out.get(1).getJoinCode())
            );

            // nice to verify the right parameter/SQL path were used:
            verify(selectStmt).setInt(1, 10);
            verify(conn).prepareStatement(contains("student_class sc"));
        }

        @Nested
        @DisplayName("getClassIdByJoinCode")
        class getClassIdByJoinCode {
            @Test
            void returnsFound() throws Exception {
                // one row containing the class_id we expect
                when(selectRs.next()).thenReturn(true, false);
                when(selectRs.getInt("class_id")).thenReturn(1);

                int id = dao.getClassIdByJoinCode(111111);

                assertEquals(1, id);
                verify(selectStmt).setInt(1, 111111);  // correct param bound
                verify(selectStmt).executeQuery();
            }

            @Test
            void returnsNotFound() throws Exception {
                when(selectRs.next()).thenReturn(false);
                int id = dao.getClassIdByJoinCode(9);
                assertEquals(0, id); // DAO returns 0 when nothing found
                verify(selectStmt).setInt(1, 9);
            }
        }

        @Nested
        @DisplayName("assignStudentToClass")
        class assignStudentToClass {
            @Test
            void ExecutesUpdate() throws Exception {
                when(conn.prepareStatement(startsWith("INSERT INTO student_class"))).thenReturn(insertStmt);
                when(insertStmt.executeUpdate()).thenReturn(1);

                dao.assignStudentToClass(7, 99);
                verify(insertStmt).setInt(1, 7);
                verify(insertStmt).setInt(2, 99);  // teacherId
                verify(insertStmt).executeUpdate();
            }

            @Test
            void ZeroRowsAffectedThrowsSQLExceptionWithCustomMessage() throws Exception {
                // route the SQL
                when(conn.prepareStatement(startsWith("INSERT INTO student_class"))).thenReturn(insertStmt);
                // simulate “no rows inserted”
                when(insertStmt.executeUpdate()).thenReturn(0);

                SQLException ex = assertThrows(SQLException.class,
                        () -> dao.assignStudentToClass(7, 99));

                assertTrue(ex.getMessage().contains("Adding user to class failed, no rows affected."));
                // params were bound
                verify(insertStmt).setInt(1, 7);
                verify(insertStmt).setInt(2, 99);
                verify(insertStmt).executeUpdate();
            }

            @Test
            void executeUpdateThrowsSQLException() throws Exception {
                when(conn.prepareStatement(startsWith("INSERT INTO student_class"))).thenReturn(insertStmt);
                // simulate JDBC error
                when(insertStmt.executeUpdate()).thenThrow(new SQLException("boom"));

                SQLException ex = assertThrows(SQLException.class,
                        () -> dao.assignStudentToClass(7, 99));

                // this is the JDBC exception, not your custom message
                assertTrue(ex.getMessage().contains("boom"));

                verify(insertStmt).setInt(1, 7);
                verify(insertStmt).setInt(2, 99);
                verify(insertStmt).executeUpdate();
            }

        }

        @Nested
        @DisplayName("saveTeacherToClass")
        class saveTeacherToClass {
            @Test
            void ExecutesUpdate() throws Exception {
                // Route this specific INSERT to insertStmt
                when(conn.prepareStatement(startsWith("INSERT INTO teacher_class"))).thenReturn(insertStmt);
                when(insertStmt.executeUpdate()).thenReturn(1);

                // Act
                dao.saveTeacherToClass(7, 99);

                // Assert: parameters and execution
                verify(insertStmt).setString(1, "creator");
                verify(insertStmt).setInt(2, 7);   // classId
                verify(insertStmt).setInt(3, 99);  // teacherId
                verify(insertStmt).executeUpdate();
            }

            @Test
            void SQLExceptionRuntime() throws Exception {
                when(conn.prepareStatement(startsWith("INSERT INTO teacher_class"))).thenReturn(insertStmt);
                when(insertStmt.executeUpdate()).thenThrow(new SQLException("boom"));

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> dao.saveTeacherToClass(7, 99));

                assertTrue(ex.getMessage().contains("Failed to insert into teacher_class"));
                assertTrue(ex.getCause() instanceof SQLException);

                // Params were still bound before the failure
                verify(insertStmt).setString(1, "creator");
                verify(insertStmt).setInt(2, 7);
                verify(insertStmt).setInt(3, 99);
            }
        }

        @Nested
        class SaveLessonToClass {
            @Test
            void ExecutesUpdate() throws Exception {
                when(conn.prepareStatement(startsWith("INSERT INTO class_lesson"))).thenReturn(insertStmt);
                when(insertStmt.executeUpdate()).thenReturn(1);

                dao.saveLessonToClass(7, 99);

                verify(insertStmt).setInt(1, 7);   // classId
                verify(insertStmt).setInt(2, 99);  // lessonId
                verify(insertStmt).executeUpdate();
            }

            @Test
            void SQLExceptionRuntime() throws Exception {
                when(conn.prepareStatement(startsWith("INSERT INTO class_lesson"))).thenReturn(insertStmt);
                when(insertStmt.executeUpdate()).thenThrow(new SQLException("boom"));

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> dao.saveLessonToClass(7, 99));

                assertTrue(ex.getMessage().contains("Failed to insert into class_lesson"));
                assertTrue(ex.getCause() instanceof SQLException);

                // params were still bound before the failure
                verify(insertStmt).setInt(1, 7);
                verify(insertStmt).setInt(2, 99);
                verify(insertStmt).executeUpdate();
            }
        }

        @Nested
        @DisplayName("getClassLessons")
        class getClassLessons {
            @Test
            void ReturnsLessonsforWlClass() throws Exception {
                when(selectRs.next()).thenReturn(true, true, false);
                when(selectRs.getInt("lesson_id")).thenReturn(101, 202); // chained for row1, row2
                when(selectRs.getString("lesson_name")).thenReturn("Intro", "Advanced");

                when(selectRs.getString("visibility")).thenReturn("public", "private");

                var out = dao.getClassLessons(7);

                assertEquals(2, out.size());

                assertAll(
                        () -> assertEquals(101, out.get(0).getLessonId()),
                        () -> assertEquals("Intro", out.get(0).getLessonName()),
                        () -> assertEquals(Question.Visibility.PUBLIC, out.get(0).getVisibility()),

                        () -> assertEquals(202, out.get(1).getLessonId()),
                        () -> assertEquals("Advanced", out.get(1).getLessonName()),
                        () -> assertEquals(Question.Visibility.PRIVATE, out.get(1).getVisibility())
                );

                verify(selectStmt).setInt(1, 7);
                verify(selectStmt).executeQuery();

                verify(conn).prepareStatement(contains("FROM lessons l INNER JOIN class_lesson cl"));
            }

            @Test
            void noRowsReturnsEmptyList() throws Exception {
                when(selectRs.next()).thenReturn(false);

                var out = dao.getClassLessons(7);

                assertTrue(out.isEmpty());
                verify(selectStmt).setInt(1, 7);
                verify(selectStmt).executeQuery();
            }
        }

        @Nested
        @DisplayName("removeTeacherFromClass")
        class removeTeacherFromClass {
            @Test
            void UpdateExecutes() throws Exception {
                when(deleteStmt.executeUpdate()).thenReturn(1);  // 1 row deleted

                dao.removeTeacherFromClass(7, 99);

                verify(deleteStmt).setInt(1, 7);   // class_id
                verify(deleteStmt).setInt(2, 99);  // user_id
                verify(deleteStmt).executeUpdate();
            }

            @Test
            void zeroRowSQLException() throws Exception {
                when(deleteStmt.executeUpdate()).thenReturn(0);  // nothing deleted

                assertThrows(SQLException.class, () -> dao.removeTeacherFromClass(7, 99));

                verify(deleteStmt).setInt(1, 7);
                verify(deleteStmt).setInt(2, 99);
                verify(deleteStmt).executeUpdate();
            }
        }
    }
}

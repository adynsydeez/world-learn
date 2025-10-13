package com.worldlearn.frontend.test.services;

import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.models.*;
import com.worldlearn.backend.services.ClassService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClassServiceTest {

    class FakeClassDAO extends ClassDAO {

        private final List<WlClass> classes = new CopyOnWriteArrayList<>();
        private final Map<Integer, Set<Integer>> studentClass = new HashMap<>();
        private final Map<Integer, Set<Integer>> teacherClass = new HashMap<>();
        private final Map<Integer, List<Lesson>> classLessons = new HashMap<>();

        private int seq = 1; // simple in-memory auto-increment

        FakeClassDAO() {
            super(null);
        }

        FakeClassDAO(Collection<WlClass> seed) {
            this();
            if (seed != null) {
                for (WlClass c : seed) {
                    if (c.getId() == 0) c.setId(seq++);
                    classes.add(c);
                }
            }
        }

        @Nested
        @DisplayName("createClass")
        class createClass {
            @Test
            void createClassSetsReturnsClass () throws Exception {
                var dao = new FakeClassDAO();
                var service = new ClassService(dao);

                int teacherId = 42;
                var input = new WlClass(0, "EE101", 0);
                var lessons = List.of(10, 11);

                WlClass c = service.createClass(input,teacherId,lessons);

                assertTrue(c instanceof WlClass);
                assertequals(teacherId,c.)
            }
        }
    }











}

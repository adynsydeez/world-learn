package com.worldlearn.frontend.test.model;

import com.worldlearn.backend.models.WlClass;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class WlClassTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTest {
        @Test
        void wlClassConstructorNoArgSetsDefaults() {
            WlClass c = new WlClass();
            assertAll(
                    () -> assertEquals(0, c.getId()),
                    () -> assertNull(c.getClassName()),
                    () -> assertEquals(0, c.getJoinCode())
            );
        }

        @Test
        void wlClassConstructor3SetsFields() {
            WlClass c = new WlClass(10, "EE101", 123456);
            assertAll(
                    () -> assertEquals(10, c.getId()),
                    () -> assertEquals("EE101", c.getClassName()),
                    () -> assertEquals(123456, c.getJoinCode())
            );
        }
    }

    @Nested
    @DisplayName("SettersGetters")
    class SettersGettersTest {
        @Test
        void idRoundTrip() {
            WlClass c = new WlClass();
            c.setId(7);
            assertEquals(7, c.getId());
        }

        @Test
        void classNameRoundTrip() {
            WlClass c = new WlClass();
            c.setClassName("Maths");
            assertEquals("Maths", c.getClassName());
        }

        @Test
        void joinCodeRoundTrip() {
            WlClass c = new WlClass();
            c.setJoinCode(654321);
            assertEquals(654321, c.getJoinCode());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {
        @Test
        void toStringContainsFields() {
            WlClass c = new WlClass(3, "Sci", 111222);
            String s = c.toString();
            assertAll(
                    () -> assertTrue(s.contains("id=3")),
                    () -> assertTrue(s.contains("Sci")),
                    () -> assertTrue(s.contains("111222"))
            );
        }
    }
}


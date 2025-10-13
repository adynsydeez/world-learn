package com.worldlearn.frontend.test.model;

import com.worldlearn.backend.models.WlClass; // adjust if your package differs
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WlClassTest {

    @Test
    void noArgConstructor_initializesDefaults() {
        WlClass c = new WlClass();
        assertEquals(0, c.getId());
        assertNull(c.getClassName());
        assertEquals(0, c.getJoinCode());
    }

    @Test
    void allArgsConstructor_storesFields() {
        WlClass c = new WlClass(10, "EE101", 123456);
        assertEquals(10, c.getId());
        assertEquals("EE101", c.getClassName());
        assertEquals(123456, c.getJoinCode());
    }
}
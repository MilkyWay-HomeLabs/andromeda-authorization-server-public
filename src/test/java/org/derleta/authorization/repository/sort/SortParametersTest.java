package org.derleta.authorization.repository.sort;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SortParametersTest {

    @Test
    void testSortParametersRecord() {
        String sortBy = "username";
        String sortOrder = "asc";
        
        SortParameters sortParameters = new SortParameters(sortBy, sortOrder);
        
        assertEquals(sortBy, sortParameters.sortBy());
        assertEquals(sortOrder, sortParameters.sortOrder());
    }

    @Test
    void testEqualsAndHashCode() {
        SortParameters params1 = new SortParameters("name", "desc");
        SortParameters params2 = new SortParameters("name", "desc");
        SortParameters params3 = new SortParameters("date", "asc");

        assertEquals(params1, params2);
        assertNotEquals(params1, params3);
        assertEquals(params1.hashCode(), params2.hashCode());
        assertNotEquals(params1.hashCode(), params3.hashCode());
    }

    @Test
    void testToString() {
        SortParameters params = new SortParameters("id", "asc");
        String toString = params.toString();
        
        assertTrue(toString.contains("sortBy=id"));
        assertTrue(toString.contains("sortOrder=asc"));
    }
}

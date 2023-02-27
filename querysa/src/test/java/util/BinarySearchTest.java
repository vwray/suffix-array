package util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link BinarySearch}.
 * 
 * @author Valerie Wray
 *
 */
class BinarySearchTest {

    @Test
    void testComputeLCP() {
        assertEquals(2, BinarySearch.computeLCP("ABCDEF", "ABNORMAL"));
        assertEquals(1, BinarySearch.computeLCP("ABCDEF", "APPLE"));
        assertEquals(0, BinarySearch.computeLCP("APPLE", "BANANA"));
        assertEquals(5, BinarySearch.computeLCP("APPLE", "APPLESAUCE"));
    }

}

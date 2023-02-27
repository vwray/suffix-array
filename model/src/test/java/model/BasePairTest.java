package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link BasePair}.
 * 
 * @author Valerie Wray
 *
 */
class BasePairTest {

    @Test
    void testConvertIntToBasePair() {
        assertEquals(BasePair.A, BasePair.convertIntToBasePair(0));
        assertEquals(BasePair.C, BasePair.convertIntToBasePair(1));
        assertEquals(BasePair.G, BasePair.convertIntToBasePair(2));
        assertEquals(BasePair.T, BasePair.convertIntToBasePair(3));
    }

    @Test
    void testConvertToBase4() {
        assertEquals("0", Integer.toString(0, 4));
        assertEquals("1", Integer.toString(1, 4));
        assertEquals("11", Integer.toString(5, 4));
        assertEquals("12", Integer.toString(6, 4));
    }

    @Test
    void testConvertBasePairStringToInt() {
        assertEquals(4, BasePair.convertDNAStringToInt("ACA"));
        assertEquals(0, BasePair.convertDNAStringToInt("AAAAAAA"));
        assertEquals(16, BasePair.convertDNAStringToInt("ACAA"));
        assertEquals(63, BasePair.convertDNAStringToInt("TTT"));
        assertEquals(11, BasePair.convertDNAStringToInt("AGT"));
        assertEquals(11, BasePair.convertDNAStringToInt("agt"));
    }
}

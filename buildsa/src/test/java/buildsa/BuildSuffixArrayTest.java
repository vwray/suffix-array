package buildsa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import model.IndexInterval;
import model.SerializeableSuffixArray;

/**
 * Test class for {@link BuildSuffixArray}.
 * 
 * @author Valerie Wray
 *
 */
class BuildSuffixArrayTest {

    @Test
    void testBuildSuffixArray() {
        int[] suffixArray = BuildSuffixArray.buildSuffixArray("acaaca$");
        assertEquals(7, suffixArray.length - 3);
    }

    /**
     * Suffix array looks like:
     * <ul>
     * <li>6 $</li>
     * <li>5 a$</li>
     * <li>2 aaca$</li>
     * <li>3 aca$</li>
     * <li>0 acaaca$</li>
     * <li>4 ca$</li>
     * <li>1 caaca$</li>
     * </ul>
     */
    @Test
    void testBuildPrefixTable() {
        SerializeableSuffixArray serializeableSuffixArray = new SerializeableSuffixArray(
                new int[] { 6, 5, 2, 3, 0, 4, 1 }, "ACAACA$");
        BuildSuffixArray.buildPrefixTable(serializeableSuffixArray, 2);
        IndexInterval[] prefixTable = serializeableSuffixArray.getPrefixTable();
        assertNotNull(prefixTable);
        assertEquals(16, prefixTable.length);
        // prefix aa
        assertEquals(2, prefixTable[0].getStart());
        assertEquals(3, prefixTable[0].getEnd());
        // prefix ac
        assertEquals(3, prefixTable[1].getStart());
        assertEquals(5, prefixTable[1].getEnd());
        // prefix ag
        assertNull(prefixTable[2]);
        // prefix at
        assertNull(prefixTable[3]);
        // prefix ca
        assertEquals(5, prefixTable[4].getStart());
        assertEquals(7, prefixTable[4].getEnd());
        // prefix cc
        assertNull(prefixTable[5]);
        // prefix cg
        assertNull(prefixTable[6]);
        // prefix ct
        assertNull(prefixTable[7]);
    }
}

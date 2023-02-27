package querysa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import model.Query;
import model.QueryMode;
import model.SerializeableSuffixArray;

/**
 * Test class for {@link QuerySuffixArray}.
 * 
 * @author Valerie Wray
 *
 */
class QuerySuffixArrayTest {
    @Test
    void testPerformNaiveBinarySearch2Hits() {
        SerializeableSuffixArray serializeableSuffixArray = new SerializeableSuffixArray(
                new int[] { 6, 5, 2, 3, 0, 4, 1 }, "abaaba$");
        Query query = new Query();
        query.appendSequence("aba");
        QuerySuffixArray.performQueries(serializeableSuffixArray, Collections.singletonList(query), QueryMode.NAIVE);
        assertNotNull(query.getHitsRange());
        assertEquals(3, query.getHitsRange().getStart());
        assertEquals(5, query.getHitsRange().getEnd());
    }

    @Test
    void testPerformNaiveBinarySearch0Hits() {
        SerializeableSuffixArray serializeableSuffixArray = new SerializeableSuffixArray(
                new int[] { 6, 5, 2, 3, 0, 4, 1 }, "abaaba$");
        Query query = new Query();
        query.appendSequence("aga");
        QuerySuffixArray.performQueries(serializeableSuffixArray, Collections.singletonList(query), QueryMode.NAIVE);
        assertNotNull(query.getHitsRange());
        assertEquals(5, query.getHitsRange().getStart());
        assertEquals(5, query.getHitsRange().getEnd());
    }

    /**
     * Text is ACCAAGATAGCTAC$. Should find 2 occurrences of "TA" at indices 7 and
     * 11 of original text. Last 2 rows of suffix array: TAC$ TAGCTAC$ (indices 13
     * and 14)
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Test
    void testPerformNaiveBinarySearchWithPrefixTable() throws ClassNotFoundException, IOException {
        SerializeableSuffixArray serializeableSuffixArray = QuerySuffixArray
                .readBinaryFile("src/test/resources/testOutput.bin");
        // "C:\\Users\\valer\\cmsc701git\\suffix-array\\querysa\\src\\test\\resources\\output.bin");
        Query query = new Query();
        query.appendName("Query1");
        query.appendSequence("TA");
        QuerySuffixArray.performQueries(serializeableSuffixArray, Collections.singletonList(query), QueryMode.NAIVE);
        assertEquals("ACCAAGATAGCTAC$", serializeableSuffixArray.getText());
        assertNotNull(query.getHitsRange());
        int start = query.getHitsRange().getStart();
        int end = query.getHitsRange().getEnd();
        assertEquals(13, start);
        assertEquals(15, end);
        assertEquals(11, serializeableSuffixArray.getSuffixArray()[13]);
        assertEquals(7, serializeableSuffixArray.getSuffixArray()[14]);
    }

    @Test
    void testPerformSimpAccelBinarySearch2Hits() {
        SerializeableSuffixArray serializeableSuffixArray = new SerializeableSuffixArray(
                new int[] { 6, 5, 2, 3, 0, 4, 1 }, "abaaba$");
        Query query = new Query();
        query.appendSequence("aba");
        QuerySuffixArray.performQueries(serializeableSuffixArray, Collections.singletonList(query),
                QueryMode.SIMPACCEL);
        assertNotNull(query.getHitsRange());
        assertEquals(3, query.getHitsRange().getStart());
        assertEquals(5, query.getHitsRange().getEnd());
    }

    @Test
    void testPerformSimpAccelBinarySearch0Hits() {
        SerializeableSuffixArray serializeableSuffixArray = new SerializeableSuffixArray(
                new int[] { 6, 5, 2, 3, 0, 4, 1 }, "abaaba$");
        Query query = new Query();
        query.appendSequence("aga");
        QuerySuffixArray.performQueries(serializeableSuffixArray, Collections.singletonList(query),
                QueryMode.SIMPACCEL);
        assertNotNull(query.getHitsRange());
        assertEquals(5, query.getHitsRange().getStart());
        assertEquals(5, query.getHitsRange().getEnd());
    }

    /**
     * Text is ACCAAGATAGCTAC$. Should find 2 occurrences of "TA" at indices 7 and
     * 11 of original text. Last 2 rows of suffix array: TAC$ TAGCTAC$ (indices 13
     * and 14)
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Test
    void testPerformSimpAccelBinarySearchWithPrefixTable() throws ClassNotFoundException, IOException {
        SerializeableSuffixArray serializeableSuffixArray = QuerySuffixArray
                .readBinaryFile("src/test/resources/testOutput.bin");
        Query query = new Query();
        query.appendName("Query1");
        query.appendSequence("TA");
        QuerySuffixArray.performQueries(serializeableSuffixArray, Collections.singletonList(query),
                QueryMode.SIMPACCEL);
        assertEquals("ACCAAGATAGCTAC$", serializeableSuffixArray.getText());
        assertNotNull(query.getHitsRange());
        int start = query.getHitsRange().getStart();
        int end = query.getHitsRange().getEnd();
        assertEquals(13, start);
        assertEquals(15, end);
        assertEquals(11, serializeableSuffixArray.getSuffixArray()[13]);
        assertEquals(7, serializeableSuffixArray.getSuffixArray()[14]);
    }
}

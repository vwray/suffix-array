package querysa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.BasePair;
import model.IndexInterval;
import model.Query;
import model.QueryMode;
import model.SerializeableSuffixArray;
import util.BinarySearch;

/**
 * A main class for querying a suffix array. This program will take as input 4
 * arguments: a serialized suffix array, a FASTA file containing queries, a
 * query mode parameter, and an output file name. It will perform a query in the
 * suffix array and report the results in an output file.
 * 
 * @author Valerie Wray
 *
 */
public class QuerySuffixArray {

    /**
     * A main method for querying a suffix array. This program will take as input 4
     * arguments: a serialized suffix array, a FASTA file containing queries, a
     * query mode parameter, and an output file name. It will perform a query in the
     * suffix array and report the results in an output file.
     * 
     * @param args
     *             <ul>
     *             <li>index - the path to the binary file containing your
     *             serialized suffix array (as written by buildsa)</li>
     *             <li>queries - the path to an input file in FASTA format
     *             containing a set of records</li>
     *             <li>query mode - this argument should be one of two strings;
     *             either naive or simpaccel. If the string is naive, the queries
     *             will be performed using the naive binary search algorithm. If the
     *             string is simpaccel, the queries will be performed using the
     *             “simple accelerant” algorithm. Note: If the serialized input file
     *             contains no prefix lookup table, then these algorithms will be
     *             run on each query on the full suffix array, and if you are
     *             reading in a prefix lookup table as well, then this is the
     *             algorithm that will be used on the relevant interval for each
     *             query.</li>
     *             <li>output - the name to use for the resulting output</li>
     * 
     *             </ul>
     * @throws IOException            if an error occurs during file I/O
     * @throws ClassNotFoundException if a class to be deserialized is not found
     *                                during file I/O
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SerializeableSuffixArray suffixArray = null;

        String indexFile = args[0];
        String queriesFile = args[1];
        QueryMode queryMode = QueryMode.valueOf(args[2].toUpperCase());
        String outputFile = args[3];

        suffixArray = readBinaryFile(indexFile);

        Instant start = Instant.now();

        List<Query> queries = readFastaQueriesFile(queriesFile);
        performQueries(suffixArray, queries, queryMode);

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

        writeToFile(suffixArray, queries, outputFile);
    }

    /**
     * Reads in a binary file containing a suffix array, created by buildsa.
     * 
     * @param filename the file name of the file containing the suffix array
     * @return the {@link SerializeableSuffixArray}
     * @throws IOException            if an I/O error occurs while handling the
     *                                input stream
     * @throws ClassNotFoundException if the {@link SerializeableSuffixArray} class
     *                                cannot be found
     */
    protected static SerializeableSuffixArray readBinaryFile(String filename)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filename));
        SerializeableSuffixArray serializeableSuffixArray = (SerializeableSuffixArray) objectInputStream.readObject();
        objectInputStream.close();
        return serializeableSuffixArray;

    }

    /**
     * Reads in a FASTA file containing a set of records which are names and
     * sequences of queries to perform.
     * 
     * @param filename the file name of the FASTA file
     * @return the list of {@link Query}
     * @throws FileNotFoundException if the file cannot be found
     */
    protected static List<Query> readFastaQueriesFile(String filename) throws FileNotFoundException {
        int currentQuery = -1;
        List<Query> queries = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.charAt(0) == '>') {
                    Query query = new Query(line.substring(1).split(" ")[0]);
                    queries.add(query);
                    currentQuery++;
                } else {
                    queries.get(currentQuery).appendSequence(line);
                }
            }
        }
        return (queries);
    }

    /**
     * Performs the specified queries on the provided suffix array.
     * 
     * @param serializeableSuffixArray the {@link SerializeableSuffixArray}
     * @param queries                  the list of {@link Query} to perform
     * @param queryMode                specifies whether to run the naive binary
     *                                 search algorithm on the suffix array or the
     *                                 simple accelerant using LCP values
     */
    protected static void performQueries(SerializeableSuffixArray serializeableSuffixArray, List<Query> queries,
            QueryMode queryMode) {
        String text = serializeableSuffixArray.getText();
        int lastIndex = text.length() - 1;
        for (Query query : queries) {
            String pattern = query.getSequence();

            IndexInterval[] prefixTable = serializeableSuffixArray.getPrefixTable();
            int startIndex = 0;
            int endIndex = lastIndex;
            if (prefixTable != null) {
                // Take the first k characters of the pattern and lookup in the prefix table
                int prefixTableIndex = BasePair
                        .convertDNAStringToInt(pattern.substring(0, serializeableSuffixArray.getPrefixLength()));
                IndexInterval indexInterval = prefixTable[prefixTableIndex];
                if (indexInterval != null) {
                    startIndex = indexInterval.getStart() > 0 && queryMode == QueryMode.NAIVE
                            ? indexInterval.getStart() - 1
                            : startIndex;
                    endIndex = indexInterval.getEnd();
                    if (queryMode == QueryMode.SIMPACCEL) {
                        endIndex--; // Improve performance on simpaccel if we stay within prefix range
                    }
                } else {
                    // if indexInterval is null, then pattern does not exist in text
                    query.setHitsRange(new IndexInterval(-1, -1));
                    continue;
                }
            }

            query.setHitsRange(BinarySearch.binaryIntervalSearch(serializeableSuffixArray, pattern, startIndex,
                    endIndex == lastIndex ? lastIndex + 1 : endIndex, queryMode));
        }
    }

    /**
     * Writes the query results to file with the query name, number of hits, and hit
     * indices for each query, all space-separated.
     * 
     * @param serializeableSuffixArray the {@link SerializeableSuffixArray}
     * @param queries                  the list of {@link Query}
     * @param outputFile               the output file to write to
     * @throws IOException if an error occurs while trying to write to the file
     */
    protected static void writeToFile(SerializeableSuffixArray serializeableSuffixArray, List<Query> queries,
            String outputFile) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        int[] suffixArray = serializeableSuffixArray.getSuffixArray();
        for (Query query : queries) {
            int startIndex = query.getHitsRange().getStart();
            int endIndex = query.getHitsRange().getEnd();
            fileWriter.write(query.getName() + " " + (endIndex - startIndex));
            for (int i = startIndex; i < endIndex; i++) {
                fileWriter.write(" " + suffixArray[i]);
            }
            fileWriter.write("\n");
        }
        fileWriter.close();

    }

}

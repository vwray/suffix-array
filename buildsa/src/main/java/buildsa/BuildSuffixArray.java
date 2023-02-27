package buildsa;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import org.jsuffixarrays.Skew;

import model.BasePair;
import model.IndexInterval;
import model.SerializeableSuffixArray;

/**
 * A main class to build a suffix array. This program will read in a “genome” in
 * a FASTA file, build the suffix array for the "genome" text string, and output
 * the suffix array and original text string to a binary file. Additionally
 * (when an extra "preftab" option is provided) it will build a secondary index
 * to allow for the improved search heuristic. When invoked with this extra
 * option, it will also write this prefix table to the serialized file.
 * 
 * @author Valerie Wray
 *
 */
public class BuildSuffixArray {

    /**
     * A main method to build a suffix array. This program will read in a “genome”
     * in a FASTA file, build the suffix array for the "genome" text string, and
     * output the suffix array and original text string to a binary file.
     * Additionally (when an extra "preftab" option is provided) it will build a
     * secondary index to allow for the improved search heuristic. When invoked with
     * this extra option, it will also write this prefix table to the serialized
     * file.
     * 
     * @param args
     *             <ul>
     *             <li>--preftab <k> - if the option --preftab is passed in (with
     *             the parameter k), then a prefix table will be built atop the
     *             suffix array, capable of jumping to the suffix array interval
     *             corresponding to any prefix of length k.</li>
     *             <li>reference - the path to a FASTA file containing a "genome" of
     *             which to build the suffix array, which may be split over multiple
     *             input lines</li>
     *             <li>output - the name to use for the binary output file that
     *             contains a serialized version of the input string and the suffix
     *             array</li>
     * 
     *             </ul>
     * @throws IOException if an error occurs during file I/O
     */
    public static void main(String[] args) throws IOException {
        int k = "--preftab".equals(args[0]) ? Integer.parseInt(args[1]) : -1;
        int i = k > 0 ? 2 : 0;

        String reference = args[i++];
        String output = args[i++];

        String text = readFastaFile(reference);
        System.out.println("Text length: " + text.length());

        Instant start = Instant.now();
        text = replaceN(text.toUpperCase());
        int[] suffixArray = buildSuffixArray(text);

        SerializeableSuffixArray serializeableSuffixArray = new SerializeableSuffixArray(suffixArray, text);

        if (k > 0) {
            buildPrefixTable(serializeableSuffixArray, k);
        }

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

        writeToBinaryFile(serializeableSuffixArray, output);
    }

    /**
     * Reads in a FASTA file and returns a string containing the text.
     * 
     * @param filename the file name
     * @return the text string
     * @throws IOException if the file cannot be found
     */
    protected static String readFastaFile(String filename) throws IOException {
        StringBuilder inputString = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            if (line.charAt(0) != '>') {
                inputString = inputString.append(line);
            }
        }
        bufferedReader.close();
        return (inputString.append("$").substring(0));
    }

    /**
     * Reads in the first entry in FASTA file and returns a string containing the
     * text.
     * 
     * @param filename the file name
     * @return the text string
     * @throws IOException if the file cannot be found
     */
    protected static String readFastaFileFirstEntry(String filename) throws IOException {
        StringBuilder inputString = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        boolean isFirst = true;
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            if (line.charAt(0) != '>') {
                inputString = inputString.append(line);
            } else if (!isFirst) {
                break;
            }
            isFirst = false;
        }
        bufferedReader.close();
        return (inputString.append("$").substring(0));
    }

    /**
     * Replaces each occurrence of 'N' in the input string with a randomly chosen
     * character from A, C, T, or G.
     * 
     * @param input the input string
     * @return the string with 'N' replaced
     */
    protected static String replaceN(String input) {
        Random random = new Random();
        char[] charArray = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            if (charArray[i] == 'N') {
                charArray[i] = BasePair.convertIntToBasePair(random.nextInt(4)).getDnaChar();
            }
        }
        return new String(charArray);
    }

    /**
     * Builds the suffix array using a call to jsuffixarray's Skew algorithm.
     * 
     * @param text the text string from which to build the suffix array
     * @return the suffix array as an array of integers
     */
    protected static int[] buildSuffixArray(String text) {
        String paddedText = text.concat("000");
        int[] inputArray = paddedText.chars().toArray();
        Skew skew = new Skew();
        int[] suffixArray = skew.buildSuffixArray(inputArray, 0, paddedText.length() - 3);
        return (suffixArray);
    }

    /**
     * Builds a prefix table with the specified length of prefixes.
     * 
     * @param serializeableSuffixArray {@link SerializeableSuffixArray}
     * @param k                        the length of prefixes
     */
    protected static void buildPrefixTable(SerializeableSuffixArray serializeableSuffixArray, int k) {
        IndexInterval[] prefixTable = new IndexInterval[(int) Math.pow(4, k)];
        String text = serializeableSuffixArray.getText();
        int textLength = text.length();
        int[] suffixArray = serializeableSuffixArray.getSuffixArray();
        boolean isValidPrefix = false;
        String currentPrefix = null;
        int startIndex = 0;
        int endIndex = 0;
        for (int suffixArrayIndex = 1; suffixArrayIndex < textLength; suffixArrayIndex++) {
            int positionInText = suffixArray[suffixArrayIndex];
            int difference = textLength - positionInText;
            int endSubstringIndex = difference <= k ? textLength : positionInText + k + 1;
            String suffix = text.substring(positionInText, endSubstringIndex);
            if (!isValidPrefix) {
                if (suffix.length() >= k + 1) {
                    currentPrefix = suffix.substring(0, k);
                    isValidPrefix = true;
                    startIndex = suffixArrayIndex;
                }
            } else {
                if (suffix.length() <= k) {
                    endIndex = suffixArrayIndex;
                    prefixTable[BasePair.convertDNAStringToInt(currentPrefix)] = new IndexInterval(startIndex,
                            endIndex);
                    startIndex = endIndex;
                    isValidPrefix = false;
                    continue;
                }
                String firstKCharsOfSuffix = suffix.substring(0, k);
                if (!firstKCharsOfSuffix.equals(currentPrefix)) {
                    endIndex = suffixArrayIndex;
                    prefixTable[BasePair.convertDNAStringToInt(currentPrefix)] = new IndexInterval(startIndex,
                            endIndex);
                    startIndex = endIndex;
                    currentPrefix = firstKCharsOfSuffix;
                }
            }
        }
        if (isValidPrefix) {
            prefixTable[BasePair.convertDNAStringToInt(currentPrefix)] = new IndexInterval(startIndex, text.length());
        }
        serializeableSuffixArray.setPrefixTable(prefixTable);
        serializeableSuffixArray.setPrefixLength(k);
    }

    /**
     * Serializes a suffix array to a binary file.
     * 
     * @param serializeableSuffixArray the suffix array to serialize
     * @param outputFile               the output file
     * @throws IOException if there is an issue writing to the file
     */
    protected static void writeToBinaryFile(SerializeableSuffixArray serializeableSuffixArray, String outputFile)
            throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
        objectOutputStream.writeObject(serializeableSuffixArray);
        objectOutputStream.close();
    }
}

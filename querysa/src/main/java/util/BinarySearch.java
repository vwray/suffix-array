package util;

import model.IndexInterval;
import model.LCPPair;
import model.QueryMode;
import model.SerializeableSuffixArray;

/**
 * A class to provide binary search capabilities.
 * 
 * @author Valerie Wray
 *
 */
public class BinarySearch {

    /**
     * Performs binary search on a given suffix array with the given starting left
     * and right indices and the pattern to search for.
     * 
     * @param serializeableSuffixArray the {@link SerializeableSuffixArray}
     * @param prefix                   the prefix to search for
     * @param leftIndex                the starting left index
     * @param rightIndex               the starting right index
     * @return the suffix array index of the first occurrence of the prefix, if it
     *         occurs, othewise the closest index to where it would occur
     */
    public static int binarySearch(SerializeableSuffixArray serializeableSuffixArray, String prefix, int leftIndex,
            int rightIndex) {
        String text = serializeableSuffixArray.getText();
        int[] suffixArray = serializeableSuffixArray.getSuffixArray();
        int left = leftIndex;
        int right = rightIndex;
        while (true) {
            int center = (left + right) / 2;
            if (prefix.compareTo(text.substring(suffixArray[center])) < 0) {
                if (center == left + 1) {
                    return center;
                } else {
                    right = center;
                }
            } else {
                if (center == right - 1) {
                    return right;
                } else {
                    left = center;
                }
            }
        }
    }

    /**
     * Performs binary search on a suffix array to find the interval on which the
     * pattern occurs.
     * 
     * @param serializeableSuffixArray the {@link SerializeableSuffixArray}
     * @param prefix                   the prefix to search for
     * @param leftIndex                the starting left index
     * @param rightIndex               the starting right index
     * @param queryMode                specifies whether to run the naive binary
     *                                 search algorithm on the suffix array or the
     *                                 simple accelerant using LCP values
     * @return the {@link IndexInterval} containing the suffix array indices of the
     *         start (inclusive) and the end (exclusive) of all occurrences of the
     *         prefix. If the prefix does not occur, then returns indices with start
     *         and end equal
     */
    public static IndexInterval binaryIntervalSearch(SerializeableSuffixArray serializeableSuffixArray, String prefix,
            int leftIndex, int rightIndex, QueryMode queryMode) {
        int start = queryMode == QueryMode.NAIVE ? binarySearch(serializeableSuffixArray, prefix, leftIndex, rightIndex)
                : binarySearchWithLCP(serializeableSuffixArray, prefix, leftIndex, rightIndex);
        int lastIndex = prefix.length() - 1;
        char lastChar = prefix.charAt(lastIndex);
        String prefixNext = prefix.substring(0, lastIndex) + (++lastChar);
        int end = queryMode == QueryMode.NAIVE
                ? binarySearch(serializeableSuffixArray, prefixNext, start > 0 ? start - 1 : start, rightIndex)
                : binarySearchWithLCP(serializeableSuffixArray, prefixNext, start, rightIndex);
        return new IndexInterval(start, end);

    }

    /**
     * Performs binary search using the longest common prefix (LCP) values between
     * the left index and the prefix, and between the right index and the prefix, as
     * part of the simple accelerant algorithm.
     * 
     * @param serializeableSuffixArray the {@link SerializeableSuffixArray}
     * @param prefix                   the prefix to search for
     * @param leftIndex                the starting left index
     * @param rightIndex               the starting right index
     * @return the suffix array index of the first occurrence of the prefix, if it
     *         occurs, othewise the closest index to where it would occur
     */
    public static int binarySearchWithLCP(SerializeableSuffixArray serializeableSuffixArray, String prefix,
            int leftIndex, int rightIndex) {
        String text = serializeableSuffixArray.getText();
        int[] suffixArray = serializeableSuffixArray.getSuffixArray();
        LCPPair left = new LCPPair(leftIndex, computeLCP(text.substring(suffixArray[leftIndex]), prefix));
        LCPPair right = new LCPPair(rightIndex,
                rightIndex < text.length() - 1 ? computeLCP(text.substring(suffixArray[rightIndex]), prefix) : 0);

        while (true) {
            int charsToSkip = Math.min(left.getLcp(), right.getLcp());
            int center = (left.getIndex() + right.getIndex()) / 2;
            int centerLCP = charsToSkip;
            int comparison = 0;
            while (comparison == 0) {
                char prefixChar = prefix.charAt(centerLCP);
                char suffixArrayChar = text.charAt(suffixArray[center] + centerLCP);
                if (prefixChar == suffixArrayChar) {
                    centerLCP++;
                } else {
                    comparison = prefixChar - suffixArrayChar;
                }
                if (prefix.length() <= centerLCP) {
                    comparison = -1;
                }
            }

            if (comparison < 0) {
                if (center <= left.getIndex() + 1) {
                    if (left.getIndex() == leftIndex && left.getLcp() < prefix.length()
                            && left.getLcp() < text.length() - suffixArray[leftIndex]
                            && prefix.charAt(left.getLcp()) < text.charAt(suffixArray[leftIndex] + left.getLcp())) {
                        return leftIndex;
                    }
                    return center;
                } else {
                    right = new LCPPair(center, centerLCP);
                }
            } else {
                if (center >= right.getIndex() - 1) {
                    if (right.getIndex() == rightIndex && rightIndex < text.length() && right.getLcp() < prefix.length()
                            && right.getLcp() < text.length() - suffixArray[rightIndex]
                            && prefix.charAt(right.getLcp()) > text.charAt(suffixArray[rightIndex] + right.getLcp())) {
                        right.setIndex(rightIndex + 1);
                    }
                    return right.getIndex();
                } else {
                    left = new LCPPair(center, centerLCP);
                }
            }
        }
    }

    /**
     * Computes the longest common prefix (LCP) values between a pair of strings.
     * 
     * @param thisSuffix the first input string
     * @param nextSuffix the second input string
     * @return the number of characters in the longest common prefix between the two
     *         strings
     */
    protected static int computeLCP(String thisSuffix, String nextSuffix) {
        boolean isMatch = true;
        int lcp = 0;
        int currentCharIndex = 0;
        int thisSuffixLength = thisSuffix.length();
        int nextSuffixLength = nextSuffix.length();
        while (isMatch && currentCharIndex < thisSuffixLength && currentCharIndex < nextSuffixLength) {
            if (thisSuffix.charAt(currentCharIndex) == nextSuffix.charAt(currentCharIndex)) {
                lcp++;
                currentCharIndex++;
            } else {
                isMatch = false;
            }
        }
        return lcp;
    }
}

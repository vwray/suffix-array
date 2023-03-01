package model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A class for serializing a suffix array along with the original text string
 * and an optional prefix table.
 * 
 * @author Valerie Wray
 *
 */
public class SerializeableSuffixArray implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] suffixArray;
    private String text;
    private IndexInterval[] prefixTable;
    private int prefixLength;

    /**
     * Creates a new SerializeableSuffixArray from a suffix array and text string.
     * 
     * @param suffixArray the suffix array
     * @param text        the original text string
     */
    public SerializeableSuffixArray(int[] suffixArray, String text) {
        this.suffixArray = suffixArray;
        this.text = text;
    }

    public int[] getSuffixArray() {
        return suffixArray;
    }

    public String getText() {
        return text;
    }

    public IndexInterval[] getPrefixTable() {
        return prefixTable;
    }

    public void setPrefixTable(IndexInterval[] prefixTable) {
        this.prefixTable = prefixTable;
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    @Override
    public String toString() {
        return "SerializeableSuffixArray [suffixArray=" + Arrays.toString(suffixArray) + ", text=" + text
                + ", prefixTable=" + Arrays.toString(prefixTable) + ", prefixLength=" + prefixLength + "]";
    }
}

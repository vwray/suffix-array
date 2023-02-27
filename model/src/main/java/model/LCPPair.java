package model;

/**
 * A class to represent a pair of numbers consisting of an index and an
 * associated longest common prefix (LCP) integer value.
 * 
 * @author Valerie Wray
 *
 */
public class LCPPair {
    private int index;
    private int lcp;

    public LCPPair(int index, int lcp) {
        this.index = index;
        this.lcp = lcp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLcp() {
        return lcp;
    }

    public void setLcp(int lcp) {
        this.lcp = lcp;
    }

    @Override
    public String toString() {
        return "LCPPair [index=" + index + ", lcp=" + lcp + "]";
    }
}

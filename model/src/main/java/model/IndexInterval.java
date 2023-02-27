package model;

import java.io.Serializable;

/**
 * A class to represent an interval with a start and an end index.
 * 
 * @author Valerie Wray
 *
 */
public class IndexInterval implements Serializable {
    private static final long serialVersionUID = 1L;
    private int start;
    private int end;

    public IndexInterval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "IndexInterval [start=" + start + ", end=" + end + "]";
    }
}

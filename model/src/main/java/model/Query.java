package model;

/**
 * A class representing a query to be made on a suffix array.
 * 
 * @author Valerie Wray
 *
 */
public class Query {

    private String name;
    private String sequence;
    private IndexInterval hitsRange;

    public Query() {
        this.name = "";
        this.sequence = "";
        this.hitsRange = null;
    }

    public Query(String name) {
        this.name = name;
        this.sequence = "";
        this.hitsRange = null;
    }

    public String getName() {
        return name;
    }

    public void appendName(String name) {
        this.name = this.name.concat(name);
    }

    public String getSequence() {
        return sequence;
    }

    public void appendSequence(String sequence) {
        this.sequence = this.sequence.concat(sequence);
    }

    public IndexInterval getHitsRange() {
        return hitsRange;
    }

    public void setHitsRange(IndexInterval hitsRange) {
        this.hitsRange = hitsRange;
    }

    @Override
    public String toString() {
        return "Query [name=" + name + ", sequence=" + sequence + ", hitsRange=" + hitsRange + "]";
    }
}

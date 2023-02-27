package model;

/**
 * An enum to represent a DNA base pair.
 * 
 * @author Valerie Wray
 *
 */
public enum BasePair {
    A('A'), C('C'), G('G'), T('T');

    private char basePair;

    private BasePair(char basePair) {
        this.basePair = basePair;
    }

    public char getDnaChar() {
        return basePair;
    }

    public static BasePair convertCharToBasePair(char basePair) {
        if (basePair == 'A') {
            return A;
        }
        if (basePair == 'C') {
            return C;
        }
        if (basePair == 'G') {
            return G;
        }
        if (basePair == 'T') {
            return T;
        }
        return null;
    }

    public static BasePair convertIntToBasePair(int baseFourDigit) {
        if (baseFourDigit == 0) {
            return A;
        }
        if (baseFourDigit == 1) {
            return C;
        }
        if (baseFourDigit == 2) {
            return G;
        }
        if (baseFourDigit == 3) {
            return T;
        }
        return null;
    }

    /**
     * Interprets a string of base pairs as a number in base 4, with A=0, C=1, G=2,
     * and T=3, and returns the integer value of the base 4 number.
     * 
     * @param prefix the string of base pairs
     * @return the integer value of the base 4 number
     */
    public static int convertDNAStringToInt(String prefix) {
        int sum = 0;
        int j;
        for (int i = prefix.length() - 1; i >= 0; i--) {
            j = prefix.length() - 1 - i;
            sum = sum + ((int) Math.pow(4, j)) * convertCharToBasePair(prefix.charAt(i)).ordinal();
        }
        return sum;
    }
}

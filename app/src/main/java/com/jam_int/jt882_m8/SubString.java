package com.jam_int.jt882_m8;

public class SubString {
    /**
     * Get string value between [first] a and [last] b.
     *
     * @param value
     * @param a
     * @param b
     * @return
     */
    public static String Between(String value, String a, String b) {
        int posA = value.indexOf(a);
        int posB = value.lastIndexOf(b);
        if (posA == -1) {
            return "";
        }
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB - adjustedPosA);
    }

    /**
     * Get string value after [first] a.
     *
     * @param value
     * @param a
     * @return
     */
    public static String Before(String value, String a) {
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }

    /**
     * Get string value before [last] a.
     *
     * @param value
     * @param a
     * @return
     */
    public static String BeforeLast(String value, String a) {
        int posA = value.lastIndexOf(a);
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }

    /**
     * Get string value after [last] a.
     *
     * @param value
     * @param a
     * @return
     */
    public static String After(String value, String a) {
        int posA = value.lastIndexOf(a);
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }
}
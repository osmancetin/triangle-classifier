package com.bms.triangle;

/**
 * The classification of a triangle by how many of its sides are equal.
 */
public enum TriangleType {

    /** All three sides are equal. */
    EQUILATERAL("Equilateral"),

    /** Exactly two sides are equal. */
    ISOSCELES("Isosceles"),

    /** No two sides are equal. */
    SCALENE("Scalene");

    private final String displayName;

    TriangleType(String displayName) {
        this.displayName = displayName;
    }

    /** The exact text required by the specification, e.g. {@code "Equilateral"}. */
    public String displayName() {
        return displayName;
    }
}

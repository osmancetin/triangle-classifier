package com.bms.triangle;

/**
 * Thrown when input cannot form a valid triangle: wrong number of values,
 * values that are not numbers, non-positive sides, or sides that violate
 * the triangle inequality.
 */
public class InvalidTriangleException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public InvalidTriangleException(String message) {
        super(message);
    }
}

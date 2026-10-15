package com.bms.triangle;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An immutable, always-valid triangle described by its three side lengths.
 *
 * <p>Validation happens in the constructor, so any {@code Triangle} instance that
 * exists is guaranteed to be a real (non-degenerate) triangle. Classification can
 * therefore never be asked of invalid input.
 *
 * <p>Sides are {@link BigDecimal} rather than {@code double} so that decimal input is
 * compared exactly: with doubles, {@code 0.1 + 0.2 > 0.3} is {@code true}, which would
 * wrongly accept the degenerate triangle (0.1, 0.2, 0.3).
 */
public record Triangle(BigDecimal sideA, BigDecimal sideB, BigDecimal sideC) {

    public Triangle {
        Objects.requireNonNull(sideA, "sideA must not be null");
        Objects.requireNonNull(sideB, "sideB must not be null");
        Objects.requireNonNull(sideC, "sideC must not be null");
        requirePositive(sideA, "sideA");
        requirePositive(sideB, "sideB");
        requirePositive(sideC, "sideC");
        requireTriangleInequality(sideA, sideB, sideC);
    }

    /** Classifies this triangle by the number of equal sides. */
    public TriangleType type() {
        boolean abEqual = sideA.compareTo(sideB) == 0;
        boolean bcEqual = sideB.compareTo(sideC) == 0;
        boolean acEqual = sideA.compareTo(sideC) == 0;

        if (abEqual && bcEqual) {
            return TriangleType.EQUILATERAL;
        }
        if (abEqual || bcEqual || acEqual) {
            return TriangleType.ISOSCELES;
        }
        return TriangleType.SCALENE;
    }

    private static void requirePositive(BigDecimal side, String name) {
        if (side.signum() <= 0) {
            throw new InvalidTriangleException(name + " must be greater than zero, but was " + side.toPlainString());
        }
    }

    /**
     * Each side must be strictly shorter than the sum of the other two. Equality would
     * describe a degenerate "triangle" (three points on a line) and is rejected.
     */
    private static void requireTriangleInequality(BigDecimal a, BigDecimal b, BigDecimal c) {
        if (a.add(b).compareTo(c) <= 0
                || a.add(c).compareTo(b) <= 0
                || b.add(c).compareTo(a) <= 0) {
            throw new InvalidTriangleException(
                    "Sides %s, %s, %s do not form a triangle: each side must be shorter than the sum of the other two"
                            .formatted(a.toPlainString(), b.toPlainString(), c.toPlainString()));
        }
    }
}

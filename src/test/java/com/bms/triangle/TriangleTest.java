package com.bms.triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TriangleTest {

    private static Triangle triangle(String a, String b, String c) {
        return new Triangle(new BigDecimal(a), new BigDecimal(b), new BigDecimal(c));
    }

    @Nested
    @DisplayName("Classification")
    class Classification {

        @ParameterizedTest(name = "({0}, {1}, {2}) is Equilateral")
        @CsvSource({"1, 1, 1", "5, 5, 5", "2.5, 2.5, 2.5", "1.0, 1.00, 1", "1e3, 1000, 1000.0"})
        void allSidesEqual_isEquilateral(String a, String b, String c) {
            assertEquals(TriangleType.EQUILATERAL, triangle(a, b, c).type());
        }

        @ParameterizedTest(name = "({0}, {1}, {2}) is Isosceles")
        @CsvSource({
            "5, 5, 3",   // equal pair first
            "5, 3, 5",   // equal pair outer
            "3, 5, 5",   // equal pair last
            "2, 2, 3.999",
            "0.5, 0.50, 0.3"
        })
        void exactlyTwoSidesEqual_isIsosceles(String a, String b, String c) {
            assertEquals(TriangleType.ISOSCELES, triangle(a, b, c).type());
        }

        @ParameterizedTest(name = "({0}, {1}, {2}) is Scalene")
        @CsvSource({"3, 4, 5", "5, 4, 3", "4, 5, 3", "2.1, 3.2, 4.3", "0.1, 0.2, 0.25"})
        void noSidesEqual_isScalene(String a, String b, String c) {
            assertEquals(TriangleType.SCALENE, triangle(a, b, c).type());
        }

        @Test
        void displayNamesMatchSpecification() {
            assertEquals("Equilateral", TriangleType.EQUILATERAL.displayName());
            assertEquals("Isosceles", TriangleType.ISOSCELES.displayName());
            assertEquals("Scalene", TriangleType.SCALENE.displayName());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @ParameterizedTest(name = "({0}, {1}, {2}) is rejected: non-positive side")
        @CsvSource({"0, 1, 1", "1, 0, 1", "1, 1, 0", "-1, 2, 2", "0, 0, 0", "-3, -4, -5"})
        void nonPositiveSide_isRejected(String a, String b, String c) {
            assertThrows(InvalidTriangleException.class, () -> triangle(a, b, c));
        }

        @ParameterizedTest(name = "({0}, {1}, {2}) is rejected: violates triangle inequality")
        @CsvSource({"1, 2, 10", "10, 1, 2", "1, 10, 2"})
        void sideLongerThanSumOfOthers_isRejected(String a, String b, String c) {
            assertThrows(InvalidTriangleException.class, () -> triangle(a, b, c));
        }

        @ParameterizedTest(name = "({0}, {1}, {2}) is rejected: degenerate (flat) triangle")
        @CsvSource({"1, 2, 3", "3, 1, 2", "2, 3, 1", "2, 2, 4"})
        void degenerateTriangle_isRejected(String a, String b, String c) {
            assertThrows(InvalidTriangleException.class, () -> triangle(a, b, c));
        }

        @Test
        @DisplayName("0.1 + 0.2 = 0.3 is degenerate — would wrongly pass with double arithmetic")
        void decimalDegenerateTriangle_isRejectedExactly() {
            assertThrows(InvalidTriangleException.class, () -> triangle("0.1", "0.2", "0.3"));
        }

        @Test
        void nullSide_isRejected() {
            assertThrows(NullPointerException.class,
                    () -> new Triangle(null, BigDecimal.ONE, BigDecimal.ONE));
        }
    }
}

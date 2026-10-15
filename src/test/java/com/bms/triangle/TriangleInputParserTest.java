package com.bms.triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TriangleInputParserTest {

    private final TriangleInputParser parser = new TriangleInputParser();

    @Test
    void validInput_isParsedIntoTriangle() {
        Triangle triangle = parser.parse(List.of("3", "4.5", " 5 "));

        assertEquals(0, new BigDecimal("3").compareTo(triangle.sideA()));
        assertEquals(0, new BigDecimal("4.5").compareTo(triangle.sideB()));
        assertEquals(0, new BigDecimal("5").compareTo(triangle.sideC()));
    }

    @Test
    void scientificNotation_isAccepted() {
        assertEquals(TriangleType.EQUILATERAL, parser.parse(List.of("1e2", "100", "100.0")).type());
    }

    @Test
    void tooFewValues_isRejected() {
        InvalidTriangleException e = assertThrows(InvalidTriangleException.class,
                () -> parser.parse(List.of("3", "4")));
        assertEquals("Expected exactly 3 side lengths, but got 2", e.getMessage());
    }

    @Test
    void tooManyValues_isRejected() {
        assertThrows(InvalidTriangleException.class, () -> parser.parse(List.of("3", "4", "5", "6")));
    }

    @Test
    void noValues_isRejected() {
        assertThrows(InvalidTriangleException.class, () -> parser.parse(List.of()));
    }

    @Test
    void nullList_isRejected() {
        assertThrows(InvalidTriangleException.class, () -> parser.parse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "NaN", "Infinity", "-Infinity", "3,5", "1/2", "0x10", ""," "})
    void nonNumericValue_isRejected(String invalid) {
        assertThrows(InvalidTriangleException.class, () -> parser.parse(List.of("3", "4", invalid)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1e101", "1e-101", "1e99999999", "1e-99999999"})
    void valueOutsideSupportedRange_isRejectedQuickly(String extreme) {
        assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> assertThrows(InvalidTriangleException.class, () -> parser.parse(List.of(extreme, "1", "1"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1e99", "1e-100", "1.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"})
    void valueAtEdgeOfSupportedRange_isAccepted(String edge) {
        assertEquals(TriangleType.EQUILATERAL, parser.parse(List.of(edge, edge, edge)).type());
    }

    @Test
    void nullElement_isRejected() {
        assertThrows(InvalidTriangleException.class, () -> parser.parse(Arrays.asList("3", null, "5")));
    }
}

package com.bms.triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** End-to-end tests of the command-line behaviour: output text, error stream and exit code. */
class TriangleClassifierAppTest {

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private TriangleClassifierApp app;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        app = new TriangleClassifierApp(
                new TriangleInputParser(),
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @ParameterizedTest(name = "{0} {1} {2} → {3}")
    @CsvSource({"3, 3, 3, Equilateral", "3, 3, 2, Isosceles", "3, 4, 5, Scalene"})
    void validTriangle_printsTypeAndSucceeds(String a, String b, String c, String expected) {
        int exitCode = app.run(List.of(a, b, c));

        assertEquals(TriangleClassifierApp.EXIT_SUCCESS, exitCode);
        assertEquals(expected, out.toString(StandardCharsets.UTF_8).strip());
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    void invalidTriangle_printsErrorAndUsageToStderr() {
        int exitCode = app.run(List.of("1", "2", "10"));

        assertEquals(TriangleClassifierApp.EXIT_INVALID_INPUT, exitCode);
        assertEquals("", out.toString(StandardCharsets.UTF_8));
        String error = err.toString(StandardCharsets.UTF_8);
        assertTrue(error.startsWith("Error: "), error);
        assertTrue(error.contains("Usage:"), error);
    }

    @Test
    void missingArguments_failsWithInvalidInputExitCode() {
        assertEquals(TriangleClassifierApp.EXIT_INVALID_INPUT, app.run(List.of()));
    }
}

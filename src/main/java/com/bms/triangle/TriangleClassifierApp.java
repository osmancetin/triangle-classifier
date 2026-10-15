package com.bms.triangle;

import java.io.PrintStream;
import java.util.List;

/**
 * Command-line entry point.
 *
 * <pre>
 *   java -jar triangle-classifier.jar 3 4 5   →  Scalene
 * </pre>
 *
 * <p>Writes the classification to stdout and exits with {@code 0}. On invalid input it
 * writes the reason and usage to stderr and exits with {@code 1}, so the tool behaves
 * correctly in scripts and pipelines.
 */
public final class TriangleClassifierApp {

    static final int EXIT_SUCCESS = 0;
    static final int EXIT_INVALID_INPUT = 1;

    private static final String USAGE = "Usage: java -jar triangle-classifier.jar <sideA> <sideB> <sideC>";

    private final TriangleInputParser parser;
    private final PrintStream out;
    private final PrintStream err;

    TriangleClassifierApp(TriangleInputParser parser, PrintStream out, PrintStream err) {
        this.parser = parser;
        this.out = out;
        this.err = err;
    }

    public static void main(String[] args) {
        int exitCode = new TriangleClassifierApp(new TriangleInputParser(), System.out, System.err)
                .run(List.of(args));
        System.exit(exitCode);
    }

    /** Runs the classification and returns the process exit code. Kept free of System.exit for testability. */
    int run(List<String> args) {
        try {
            Triangle triangle = parser.parse(args);
            out.println(triangle.type().displayName());
            return EXIT_SUCCESS;
        } catch (InvalidTriangleException e) {
            err.println("Error: " + e.getMessage());
            err.println(USAGE);
            return EXIT_INVALID_INPUT;
        }
    }
}

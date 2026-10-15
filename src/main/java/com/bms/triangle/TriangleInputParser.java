package com.bms.triangle;

import java.math.BigDecimal;
import java.util.List;

/**
 * Turns raw text input (e.g. command-line arguments) into a {@link Triangle}.
 *
 * <p>Kept separate from {@link Triangle} so the domain model knows nothing about where
 * its data comes from. A REST controller or file reader could reuse the model with
 * its own input handling.
 */
public final class TriangleInputParser {

    private static final int REQUIRED_SIDE_COUNT = 3;
    static final int MAX_INTEGER_DIGITS = 100;
    static final int MAX_FRACTION_DIGITS = 100;

    public Triangle parse(List<String> rawSides) {
        if (rawSides == null || rawSides.size() != REQUIRED_SIDE_COUNT) {
            int actual = rawSides == null ? 0 : rawSides.size();
            throw new InvalidTriangleException(
                    "Expected exactly " + REQUIRED_SIDE_COUNT + " side lengths, but got " + actual);
        }
        return new Triangle(
                parseSide(rawSides.get(0), "sideA"),
                parseSide(rawSides.get(1), "sideB"),
                parseSide(rawSides.get(2), "sideC"));
    }

    /**
     * {@code new BigDecimal(String)} accepts plain and scientific notation ("3", "2.5", "1e3")
     * and rejects "NaN" and "Infinity", so non-finite values never reach the model.
     */
    private static BigDecimal parseSide(String raw, String name) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidTriangleException(name + " is missing");
        }
        BigDecimal value;
        try {
            value = new BigDecimal(raw.strip());
        } catch (NumberFormatException e) {
            throw new InvalidTriangleException(name + " is not a valid number: '" + raw + "'");
        }
        requireSupportedMagnitude(value, name);
        return value;
    }

    /**
     * Exact decimal arithmetic costs time and memory proportional to the digits involved.
     * Without a bound, input such as {@code 1e-99999999 1e99999999 1e99999999} would make
     * the triangle-inequality addition exhaust resources. Real-world lengths never need
     * more than this range, so anything outside it is rejected up front.
     */
    private static void requireSupportedMagnitude(BigDecimal value, String name) {
        BigDecimal normalised = value.stripTrailingZeros();
        int fractionDigits = normalised.scale();
        int integerDigits = normalised.precision() - normalised.scale();
        if (fractionDigits > MAX_FRACTION_DIGITS || integerDigits > MAX_INTEGER_DIGITS) {
            throw new InvalidTriangleException(name + " is outside the supported range (at most "
                    + MAX_INTEGER_DIGITS + " integer digits and " + MAX_FRACTION_DIGITS + " decimal places)");
        }
    }
}

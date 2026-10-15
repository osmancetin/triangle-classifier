# Triangle Classifier

[![CI](https://github.com/osmancetin/triangle-classifier/actions/workflows/ci.yml/badge.svg)](https://github.com/osmancetin/triangle-classifier/actions/workflows/ci.yml)

A small command-line Java application that classifies a triangle as **Equilateral**, **Isosceles** or **Scalene** from its three side lengths.

## Requirements

- Java 21+
- Maven is **not** required: the included Maven Wrapper (`./mvnw`, or `mvnw.cmd` on Windows) downloads the right version.

## Build and test

```bash
./mvnw package
```

This compiles with all lint warnings treated as errors, runs the full test suite, and produces `target/triangle-classifier.jar`.

## Run

```bash
java -jar target/triangle-classifier.jar 3 4 5
# Scalene
```

| Input          | Output (stdout)  | Exit code |
|----------------|------------------|-----------|
| `3 3 3`        | `Equilateral`    | 0         |
| `3 3 2`        | `Isosceles`      | 0         |
| `3 4 5`        | `Scalene`        | 0         |
| `1 2 10`       | error on stderr  | 1         |
| `1 2`          | error on stderr  | 1         |
| `a b c`        | error on stderr  | 1         |

Decimals and scientific notation are accepted (`2.5`, `1e3`). Results go to stdout and errors to stderr with a non-zero exit code, so the tool composes correctly in scripts.

## Design

```
TriangleClassifierApp   CLI entry point: wires dependencies, prints result, maps errors to exit codes
        │
TriangleInputParser     Text → Triangle: argument count, number format, supported range
        │
Triangle (record)       Domain model: always valid, knows how to classify itself
        │
TriangleType (enum)     EQUILATERAL / ISOSCELES / SCALENE with their required display text
```

Key decisions:

- **Always-valid domain object.** `Triangle` validates in its constructor (positive sides, strict triangle inequality). If a `Triangle` exists, it is a real triangle, so classification never has to handle bad data.
- **`BigDecimal`, not `double`.** With `double`, `0.1 + 0.2 > 0.3` is `true`, so the flat "triangle" `(0.1, 0.2, 0.3)` would be accepted. `BigDecimal` compares decimal input exactly, and `compareTo` treats `1.0` and `1.00` as equal.
- **Degenerate triangles are rejected.** Sides like `(1, 2, 3)` describe a line, not a triangle.
- **Bounded input range.** Exact arithmetic costs grow with digit count, so values beyond 100 integer digits or 100 decimal places are rejected up front rather than letting input such as `1e-99999999` exhaust resources.
- **Input handling separate from the domain.** The parser is the only class that knows about text. A REST endpoint or file reader could reuse `Triangle` unchanged.
- **Testable entry point.** `main` only wires real dependencies and calls `System.exit`; the logic lives in `run(...)`, which takes injected output streams and returns an exit code.
- **No frameworks.** The problem doesn't need Spring, logging or a DI container. The only dependency is JUnit, at test scope.

## Tests

- `TriangleTest` – classification for every ordering of the equal sides, decimal and scientific inputs, and all invalid shapes (zero, negative, triangle-inequality violations, degenerate, the `0.1/0.2/0.3` floating-point case).
- `TriangleInputParserTest` – argument count, non-numeric values (`abc`, `NaN`, `Infinity`, `3,5`, blanks), range limits.
- `TriangleClassifierAppTest` – end to end: exact stdout text, stderr content and exit codes.

GitHub Actions runs the build and an executable-jar smoke test on every push (`.github/workflows/ci.yml`).

## Known limitations and assumptions

- **Degenerate triangles are rejected.** `(1, 2, 3)` is treated as invalid. If the business wants it accepted, it is a small change: `<= 0` → `< 0` in `Triangle.requireTriangleInequality`.
- **Decimal separator is `.` only.** `3,5` is rejected rather than guessing the locale.
- **The 100-digit bound is arbitrary.** It is far beyond any real-world length; it exists only to keep exact arithmetic bounded.
- **Any Unicode decimal digit is accepted**, because `BigDecimal` accepts them (e.g. Arabic-Indic `١` is read as `1`).
- **No `--help` flag.** Any wrong argument count prints the usage line.
- **Units are assumed consistent** across the three sides.

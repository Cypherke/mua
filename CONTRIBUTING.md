# Contributing

## Getting started

1. Clone the repository
2. Run `./gradlew build` to verify everything compiles
3. Run `./gradlew test` to run the test suite (Cucumber + JUnit)

## Code style

This project uses [Google Java Style](https://google.github.io/styleguide/javaguide.html)
enforced by Checkstyle. Run `./gradlew checkstyleMain` to check for violations before submitting.

## Submitting a Pull Request

1. Create a branch from `master`
2. Make your changes
3. Make sure `./gradlew build` and `./gradlew test` pass
4. Open a Pull Request
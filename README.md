# Return on investment

Your task if to make `ReportingService` pretty.

`ReportingService.getReport` calculate a little report based on the investment accounts and transactions passed
by someone.

The idea is to take the actual position and rollback all transactions until we get the original position.

From there we can calculate the gain but also give the variation day by day.

## Benchmark

Because speed is important, you can benchmark the performance of your calculator.

```bash
mvn verify -DskipTests
java -jar target/roi.jar
```

## Mutation testing

`mvn package org.pitest:pitest-maven:mutationCoverage`

# Car Rental

A small in-memory car rental model in Java 21.

A customer reserves a car of a given type (Sedan, SUV, Van) starting at a given date and time for a given number of days. Each type has a limited number of cars. A reservation is rejected when no car of that type is free for the whole period.

## Run the tests

```
./gradlew test
```

## Classes

- `CarType`: enum with `SEDAN`, `SUV`, `VAN`.
- `Reservation`: immutable record (car type, start date and time, number of days). It validates its input, calculates its own end (`start + days`) and answers two questions: `overlaps(periodStart, periodEnd)` and `isInUseAt(moment)`.
- `CarNotAvailableException`: runtime exception thrown when no car of the requested type is free.
- `CarRental`: knows how many cars of each type exist and keeps the list of accepted reservations. `reserve(...)` saves a reservation or throws, `isAvailable(...)` only answers the question.

## Key decisions

1. **Time intervals are half-open: [start, end).** A car returned at exactly 10:00 can be rented again at 10:00. A reservation starting one minute earlier is rejected.
2. **The reservation calculates its own end.** The caller gives a start and a number of days, so there is one source of truth and no way to create a reservation whose end does not match its length.
3. **Availability is checked for the whole period, not only at the start.** A request is accepted only if at every moment of the period the number of cars in use plus one does not exceed the number of cars of that type. The moments to check are the request start and the starts of overlapping reservations of the same type, because the number of cars in use only grows when a reservation starts.
4. **Cars are counted per type, not modelled as individual objects.** Cars of one type are treated as interchangeable. Which physical car the customer gets is assigned at pick-up.
5. **Two kinds of errors.** Invalid input (no type, no start, fewer than one day, negative number of cars) throws `IllegalArgumentException`. A valid request with no free car throws `CarNotAvailableException`.
6. **A rejected reservation is never saved.** The reservation is added to the list only after the availability check passes.
7. **No clock inside the logic.** The code never calls `now()`, so tests use fixed dates and always give the same result.

## Assumptions and limitations

- Reservations are kept in memory only. There is no database and no persistence.
- No cancelling or listing of reservations (not required by the task).
- No check that the start date is in the future.
- No time zones (`LocalDateTime`).
- `reserve` and `isAvailable` are `synchronized`, so two callers cannot take the last car at the same time. This is a simple solution for a single process and it locks all reservations at once. The tests run in one thread and do not prove it.
- Because cars are interchangeable, a customer is not bound to one specific car from the moment of booking. If that is required, the model needs individual `Car` objects and an assignment step.
- A car type that was not passed to the constructor has zero cars.

## Tests

JUnit 5, fixed dates. `CarRentalTest` checks booking rules and availability, `ReservationTest` checks the reservation on its own.
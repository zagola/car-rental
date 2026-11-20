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

- **Half-open intervals.** A reservation covers [start, end). A car returned at 10:00 can be rented again at 10:00, but a request starting at 09:59 is rejected.
- **The reservation calculates its own end.** The caller gives a start and a number of days, so the end can never disagree with the length.
- **Availability is checked for the whole period**, not only at the start. A request is accepted only if there is a free car at every moment of the period. It is enough to check the start of the request and the starts of overlapping reservations of the same type, because the number of cars in use only grows when a reservation starts.
- **Cars are counted per type.** Cars of one type are interchangeable, so there are no individual car objects. The physical car would be assigned at pick-up.
- **Two kinds of errors.** Invalid input throws `IllegalArgumentException`. A valid request with no free car throws `CarNotAvailableException`.

## Limitations

- Reservations are stored in memory only, there is no database.
- No cancelling or listing of reservations.
- No check that the start date is in the future.
- `reserve` and `isAvailable` are `synchronized`, so two callers cannot book the last car at the same time. It is a simple solution for one process, and the tests do not cover it.
- A customer is not tied to a specific car at booking time. For that, the model would need individual `Car` objects.
- A type not passed to the constructor has zero cars.

## Tests

JUnit 5 with fixed dates. `CarRentalTest` covers booking rules and availability, `ReservationTest` covers the reservation on its own.
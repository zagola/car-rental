package carrental;

import java.time.LocalDateTime;

public record Reservation(CarType type, LocalDateTime start, int numberOfDays) {

    public Reservation {
        if (type == null) {
            throw new IllegalArgumentException("Car type is required");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start date and time is required");
        }
        if (numberOfDays < 1) {
            throw new IllegalArgumentException("Number of days must be at least 1");
        }
    }

    public LocalDateTime end() {
        return start.plusDays(numberOfDays);
    }

    public boolean overlaps(LocalDateTime periodStart, LocalDateTime periodEnd) {
        return start.isBefore(periodEnd) && periodStart.isBefore(end());
    }

    public boolean isActiveAt(LocalDateTime moment) {
        return !moment.isBefore(start) && moment.isBefore(end());
    }

}
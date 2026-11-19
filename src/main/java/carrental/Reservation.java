package carrental;

import java.time.LocalDateTime;

public record Reservation(CarType type, LocalDateTime start, int days) {

    public Reservation {
        if (type == null) {
            throw new IllegalArgumentException("Car type is required");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start date and time is required");
        }
        if (days < 1) {
            throw new IllegalArgumentException("Number of days must be at least 1");
        }
    }

    public LocalDateTime end() {
        return start.plusDays(days);
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && otherStart.isBefore(end());
    }

    public boolean isActiveAt(LocalDateTime moment) {
        return !moment.isBefore(start) && moment.isBefore(end());
    }

}
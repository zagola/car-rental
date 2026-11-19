package carrental;

import java.time.LocalDateTime;

public record Reservation(CarType carType, LocalDateTime startDateTime, int numberOfDays) {

    public Reservation {
        if (carType == null) {
            throw new IllegalArgumentException("Car carType is required");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date and time is required");
        }
        if (numberOfDays < 1) {
            throw new IllegalArgumentException("Number of days must be at least 1");
        }
    }

    public LocalDateTime endDateTime() {
        return startDateTime.plusDays(numberOfDays);
    }

    public boolean overlaps(LocalDateTime periodStart, LocalDateTime periodEnd) {
        return startDateTime.isBefore(periodEnd) && periodStart.isBefore(endDateTime());
    }

    public boolean isInUseAt(LocalDateTime moment) {
        return !moment.isBefore(startDateTime) && moment.isBefore(endDateTime());
    }
}
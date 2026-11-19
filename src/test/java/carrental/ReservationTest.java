package carrental;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationTest {

    private static final LocalDateTime MONDAY_10_00 = LocalDateTime.of(2026, 11, 2,
            10, 0);

    @Test
    void endIsStartPlusNumberOfDays() {
        Reservation reservation = new Reservation(CarType.SEDAN, MONDAY_10_00, 3);

        assertEquals(MONDAY_10_00.plusDays(3), reservation.endDateTime());
    }
}
package carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {

    private static final LocalDateTime MONDAY_10_00 = LocalDateTime.of(2026, 11, 2,
            10, 0);

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation(CarType.SEDAN, MONDAY_10_00, 3);
    }

    @Test
    void endIsStartPlusNumberOfDays() {
        assertEquals(MONDAY_10_00.plusDays(3), reservation.endDateTime());
    }

    @Test
    void overlapsWhenPeriodIsInsideReservation() {
        assertTrue(reservation.overlaps(MONDAY_10_00.plusDays(1), MONDAY_10_00.plusDays(2)));
    }

    @Test
    void overlapsWhenPeriodCoversReservation() {
        assertTrue(reservation.overlaps(MONDAY_10_00.minusDays(1), MONDAY_10_00.plusDays(5)));
    }

    @Test
    void noOverlapWhenPeriodEndsAtReservationStart() {
        assertFalse(reservation.overlaps(MONDAY_10_00.minusDays(1), MONDAY_10_00));
    }

    @Test
    void noOverlapWhenPeriodStartsAtReservationEnd() {
        assertFalse(reservation.overlaps(MONDAY_10_00.plusDays(3), MONDAY_10_00.plusDays(4)));
    }

    @Test
    void isInUseDuringReservation() {
        assertTrue(reservation.isInUseAt(MONDAY_10_00));
        assertTrue(reservation.isInUseAt(MONDAY_10_00.plusDays(3).minusMinutes(1)));
    }

    @Test
    void isNotInUseOutsideReservation() {
        assertFalse(reservation.isInUseAt(MONDAY_10_00.minusMinutes(1)));
        assertFalse(reservation.isInUseAt(MONDAY_10_00.plusDays(3)));
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(CarType.SEDAN, MONDAY_10_00, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(null, MONDAY_10_00, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(CarType.SEDAN, null, 1));
    }
}
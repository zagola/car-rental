package carrental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarRentalTest {

    private static final LocalDateTime MONDAY_10_00 = LocalDateTime.of(2026, 12, 7,
            10, 0);

    private CarRental carRental;

    @BeforeEach
    void setUp() {
        carRental = new CarRental(Map.of(CarType.SEDAN, 2, CarType.SUV, 1, CarType.VAN, 1));
    }

    @Test
    void reservesCarOfRequestedTypeWhenOneIsFree() {
        Reservation reservation = carRental.reserve(CarType.SEDAN, MONDAY_10_00, 3);

        assertEquals(CarType.SEDAN, reservation.carType());
        assertEquals(MONDAY_10_00, reservation.startDateTime());
        assertEquals(MONDAY_10_00.plusDays(3), reservation.endDateTime());
    }

    @Test
    void rejectsReservationWhenAllCarsOfTheTypeAreTaken() {
        carRental.reserve(CarType.SEDAN, MONDAY_10_00, 2);
        carRental.reserve(CarType.SEDAN, MONDAY_10_00, 2);

        assertThrows(CarNotAvailableException.class,
                () -> carRental.reserve(CarType.SEDAN, MONDAY_10_00, 2));
    }

    @Test
    void typesAreIndependentOfEachOther() {
        carRental.reserve(CarType.SUV, MONDAY_10_00, 2);

        assertDoesNotThrow(() -> carRental.reserve(CarType.VAN, MONDAY_10_00, 2));
        assertDoesNotThrow(() -> carRental.reserve(CarType.SEDAN, MONDAY_10_00, 2));
    }

    @Test
    void carIsFreeAgainWhenEarlierReservationHasEnded() {
        carRental.reserve(CarType.SUV, MONDAY_10_00, 2);

        assertDoesNotThrow(() -> carRental.reserve(CarType.SUV, MONDAY_10_00.plusDays(5), 2));
    }
}
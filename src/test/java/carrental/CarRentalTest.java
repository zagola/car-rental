package carrental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    void carReturnedAtTheExactMomentCanBeRentedAgain() {
        Reservation first = carRental.reserve(CarType.SUV, MONDAY_10_00, 2);

        assertDoesNotThrow(() -> carRental.reserve(CarType.SUV, first.endDateTime(), 1));
    }

    @Test
    void reservationStartingBeforeAnotherEndsIsRejected() {
        Reservation first = carRental.reserve(CarType.SUV, MONDAY_10_00, 2);

        assertThrows(CarNotAvailableException.class,
                () -> carRental.reserve(CarType.SUV, first.endDateTime().minusMinutes(1), 1));
    }

    @Test
    void multiDayReservationBlocksTheCarForEveryDay() {
        carRental.reserve(CarType.VAN, MONDAY_10_00, 5);

        for (int day = 0; day < 5; day++) {
            int dayOffset = day;
            assertThrows(CarNotAvailableException.class,
                    () -> carRental.reserve(CarType.VAN, MONDAY_10_00.plusDays(dayOffset), 1));
        }
    }

    @Test
    void allowsRequestOverlappingTwoConsecutiveReservations() {
        carRental.reserve(CarType.SEDAN, MONDAY_10_00, 2);
        carRental.reserve(CarType.SEDAN, MONDAY_10_00.plusDays(2), 2);

        assertTrue(carRental.isAvailable(CarType.SEDAN, MONDAY_10_00.plusDays(1), 2));
    }

    @Test
    void rejectsRequestWhenAllCarsAreInUseAtSomeMoment() {
        carRental.reserve(CarType.SEDAN, MONDAY_10_00, 2);
        carRental.reserve(CarType.SEDAN, MONDAY_10_00.plusDays(1), 3);

        assertFalse(carRental.isAvailable(CarType.SEDAN, MONDAY_10_00.plusDays(1), 1));
        assertFalse(carRental.isAvailable(CarType.SEDAN, MONDAY_10_00, 4));
    }

    @Test
    void rejectedReservationIsNotSaved() {
        carRental.reserve(CarType.SUV, MONDAY_10_00, 2);
        assertThrows(CarNotAvailableException.class,
                () -> carRental.reserve(CarType.SUV, MONDAY_10_00.plusDays(1), 3));

        assertTrue(carRental.isAvailable(CarType.SUV, MONDAY_10_00.plusDays(3), 1));
    }

    @Test
    void rejectsNumberOfDaysBelowOne() {
        assertThrows(IllegalArgumentException.class,
                () -> carRental.reserve(CarType.SEDAN, MONDAY_10_00, 0));
        assertThrows(IllegalArgumentException.class,
                () -> carRental.reserve(CarType.SEDAN, MONDAY_10_00, -2));
    }

    @Test
    void rejectsMissingTypeOrStart() {
        assertThrows(IllegalArgumentException.class,
                () -> carRental.reserve(null, MONDAY_10_00, 1));
        assertThrows(IllegalArgumentException.class,
                () -> carRental.reserve(CarType.SEDAN, null, 1));
    }

    @Test
    void typeWithoutAnyCarsCannotBeReserved() {
        CarRental onlySedans = new CarRental(Map.of(CarType.SEDAN, 1));

        assertThrows(CarNotAvailableException.class,
                () -> onlySedans.reserve(CarType.VAN, MONDAY_10_00, 1));
    }

    @Test
    void rejectsNegativeNumberOfCars() {
        assertThrows(IllegalArgumentException.class,
                () -> new CarRental(Map.of(CarType.SEDAN, -1)));
    }
}
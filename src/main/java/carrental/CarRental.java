package carrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CarRental {

    private final Map<CarType, Integer> capacityByType = new EnumMap<>(CarType.class);
    private final List<Reservation> reservations = new ArrayList<>();

    public CarRental(Map<CarType, Integer> carsPerType) {
        for (CarType carType : CarType.values()) {
            int numberOfCars = carsPerType.getOrDefault(carType, 0);
            if (numberOfCars < 0) {
                throw new IllegalArgumentException("Number of cars cannot be negative: " + carType);
            }
            capacityByType.put(carType, numberOfCars);
        }
    }

    public synchronized Reservation reserve(CarType carType, LocalDateTime startDateTime, int numberOfDays) {
        Reservation requestedReservation = new Reservation(carType, startDateTime, numberOfDays);
        if (!hasFreeCarForWholePeriod(requestedReservation)) {
            throw new CarNotAvailableException(
                    "No " + carType + " available from " + requestedReservation.startDateTime() + " to " +
                            requestedReservation.endDateTime());
        }
        reservations.add(requestedReservation);
        return requestedReservation;
    }

    public synchronized boolean isAvailable(CarType carType, LocalDateTime startDateTime, int numberOfDays) {
        return hasFreeCarForWholePeriod(new Reservation(carType, startDateTime, numberOfDays));
    }

    private boolean hasFreeCarForWholePeriod(Reservation requestedReservation) {
        int numberOfCarsOfType = capacityByType.get(requestedReservation.carType());
        List<Reservation> overlappingReservationsOfSameType = reservations.stream()
                .filter(r -> r.carType() == requestedReservation.carType())
                .filter(r -> r.overlaps(requestedReservation.startDateTime(),
                        requestedReservation.endDateTime()))
                .toList();

        List<LocalDateTime> momentsToCheck = new ArrayList<>();
        momentsToCheck.add(requestedReservation.startDateTime());
        for (Reservation existingReservation : overlappingReservationsOfSameType) {
            if (existingReservation.startDateTime().isAfter(requestedReservation.startDateTime())) {
                momentsToCheck.add(existingReservation.startDateTime());
            }
        }

        for (LocalDateTime moment : momentsToCheck) {
            long carsInUse = overlappingReservationsOfSameType.stream().filter(r ->
                    r.isInUseAt(moment)).count();
            if (carsInUse + 1 > numberOfCarsOfType) {
                return false;
            }
        }
        return true;
    }
}
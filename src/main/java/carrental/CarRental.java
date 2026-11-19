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
        for (CarType type : CarType.values()) {
            int numberOfCars = carsPerType.getOrDefault(type, 0);
            if (numberOfCars < 0) {
                throw new IllegalArgumentException("Number of cars cannot be negative: " + type);
            }
            capacityByType.put(type, numberOfCars);
        }
    }

    public synchronized Reservation reserve(CarType type, LocalDateTime start, int days) {
        Reservation requestedReservation = new Reservation(type, start, days);
        if (!hasFreeCarForWholePeriod(requestedReservation)) {
            throw new CarNotAvailableException(
                    "No " + type + " available from " + requestedReservation.start() + " to " + requestedReservation.end());
        }
        reservations.add(requestedReservation);
        return requestedReservation;
    }

    public synchronized boolean isAvailable(CarType type, LocalDateTime start, int days) {
        return hasFreeCarForWholePeriod(new Reservation(type, start, days));
    }

    private boolean hasFreeCarForWholePeriod(Reservation requestedReservation) {
        int numberOfCarsOfType = capacityByType.get(requestedReservation.type());
        List<Reservation> overlappingReservationsOfSameType = reservations.stream()
                .filter(r -> r.type() == requestedReservation.type())
                .filter(r -> r.overlaps(requestedReservation.start(), requestedReservation.end()))
                .toList();

        List<LocalDateTime> momentsToCheck = new ArrayList<>();
        momentsToCheck.add(requestedReservation.start());
        for (Reservation existingReservation : overlappingReservationsOfSameType) {
            if (existingReservation.start().isAfter(requestedReservation.start())) {
                momentsToCheck.add(existingReservation.start());
            }
        }

        for (LocalDateTime moment : momentsToCheck) {
            long carsInUse = overlappingReservationsOfSameType.stream().filter(r -> r.isActiveAt(moment)).count();
            if (carsInUse + 1 > numberOfCarsOfType) {
                return false;
            }
        }
        return true;
    }
}
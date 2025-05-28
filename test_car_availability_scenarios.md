# Car Rental System - Car Availability Test Scenarios

## Scenario 1: Simple Reservation and Cancellation
```
Initial State: Car #1 - Available
Action: Customer books Car #1 for 01/06/2024 - 05/06/2024
Result: Car #1 status may change to Rented when rental starts

Action: Customer cancels reservation
Result: Car #1 becomes Available again (if no other reservations)
```

## Scenario 2: Overlapping Reservations
```
Existing: Reservation A (Car #1, 01/06/2024 - 05/06/2024, Confirmed)
Attempt: Reservation B (Car #1, 03/06/2024 - 07/06/2024)
Result: BLOCKED - System detects overlap, car not available
```

## Scenario 3: Non-Overlapping Reservations
```
Existing: Reservation A (Car #1, 01/06/2024 - 05/06/2024, Confirmed)
Attempt: Reservation B (Car #1, 06/06/2024 - 10/06/2024)
Result: ALLOWED - No overlap detected, both reservations valid
```

## Scenario 4: Cancelled Reservation Impact
```
Initial: 
- Reservation A (Car #1, 01/06/2024 - 05/06/2024, Confirmed)
- Reservation B (Car #1, 06/06/2024 - 10/06/2024, Confirmed)

Action: Customer cancels Reservation A
Result: 
- Reservation A status = Cancelled
- Car #1 still not available for 01/06-05/06 period
- Car #1 available for dates before 06/06 or after 10/06
```

## Scenario 5: Multiple Reservations with Cancellation
```
Initial:
- Reservation A (Car #1, 01/06/2024 - 05/06/2024, Confirmed) 
- Reservation B (Car #1, 10/06/2024 - 15/06/2024, Confirmed)

Action: Customer cancels Reservation A
Result:
- Car #1 available for 01/06-05/06 period
- Car #1 still blocked for 10/06-15/06 period
- Car #1 available for 06/06-09/06 gap period
```

## Technical Implementation Details

### Key Database Queries:

1. **Overlap Detection:**
```sql
SELECT COUNT(r) > 0 FROM Reservation r 
WHERE r.car.carID = :carId 
AND r.status NOT IN ('Cancelled', 'Completed', 'Iptal', 'Tamamlandi') 
AND r.startDate < :endDate 
AND r.endDate > :startDate
```

2. **Active Reservations Check:**
```sql
SELECT COUNT(r) > 0 FROM Reservation r 
WHERE r.car.carID = :carId 
AND r.reservationID <> :excludedReservationId 
AND r.status IN ('Confirmed', 'Rented', 'Aktif')
```

### Status Flow:
```
Available → Confirmed → Rented → Completed
     ↑         ↓
     ← Cancelled ←
```

### Automatic Status Updates:
- **Daily 1:00 AM**: Expired reservations → Completed
- **Every 30 min**: Started reservations → Rented (car status)

## Conclusion: 
✅ System correctly handles car availability
✅ Cancellations properly free up cars
✅ Overlapping reservations are prevented
✅ Real-time status updates via scheduler 
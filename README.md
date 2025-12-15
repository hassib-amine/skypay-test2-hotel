## Skypay Technical Test 2 - Hotel Reservation System

### How to run
- Requirements: JDK 21+, Maven 3.9+.
- Build and run:
  - `mvn clean package`
  - `java -cp target/classes com.skypay.hotel.app.Main`

### Design answers
- Q1: All functions in one service?  
  - A single `Service` keeps the exercise simple, but it mixes concerns (room management, users, bookings, printing) which hurts SRP and makes isolated testing harder. In a larger system, splitting into focused services (RoomService, UserService, BookingService) plus a coordinator would improve cohesion and testability while still allowing in-memory collections or alternative storage later.
- Q2: Another way than `setRoom` not impacting bookings?  
  - Current approach snapshots room/user data inside each `Booking`. Alternatives include: versioned room pricing/history and storing the version reference in each booking; immutable room records per change with new identifiers; or event-sourcing bookings based on immutable events. Recommendation here: keep immutable booking snapshots (what we implemented) because it is simple, explicit, and guarantees past bookings remain accurate even as rooms change.


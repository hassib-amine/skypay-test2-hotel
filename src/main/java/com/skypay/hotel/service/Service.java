package com.skypay.hotel.service;

import com.skypay.hotel.exception.InsufficientBalanceException;
import com.skypay.hotel.exception.InvalidInputException;
import com.skypay.hotel.exception.NotFoundException;
import com.skypay.hotel.exception.RoomUnavailableException;
import com.skypay.hotel.model.Booking;
import com.skypay.hotel.model.Room;
import com.skypay.hotel.model.RoomType;
import com.skypay.hotel.model.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Service {
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Booking> bookings = new ArrayList<>();

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        validateRoomInput(roomNumber, roomType, roomPricePerNight);
        Room room = findRoom(roomNumber);
        if (room == null) {
            rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
            return;
        }
        room.setRoomType(roomType);
        room.setPricePerNight(roomPricePerNight);
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        Objects.requireNonNull(checkIn, "checkIn date is required");
        Objects.requireNonNull(checkOut, "checkOut date is required");

        User user = findUserOrThrow(userId);
        Room room = findRoomOrThrow(roomNumber);

        LocalDate checkInDate = toLocalDate(checkIn);
        LocalDate checkOutDate = toLocalDate(checkOut);
        validateDates(checkInDate, checkOutDate);

        ensureRoomAvailability(roomNumber, checkInDate, checkOutDate);

        int nights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        int totalPrice = nights * room.getPricePerNight();
        if (user.getBalance() < totalPrice) {
            throw new InsufficientBalanceException("User balance is insufficient for this booking");
        }

        int userBalanceSnapshot = user.getBalance();
        user.setBalance(userBalanceSnapshot - totalPrice);

        Booking booking =
                new Booking(
                        // capture room and user snapshot so future updates do not affect this booking
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getPricePerNight(),
                        user.getUserId(),
                        userBalanceSnapshot,
                        checkInDate,
                        checkOutDate,
                        totalPrice,
                        LocalDateTime.now());
        bookings.add(booking);
    }

    public void printAll() {
        System.out.println("Rooms (latest -> oldest):");
        for (int i = rooms.size() - 1; i >= 0; i--) {
            Room room = rooms.get(i);
            System.out.println(
                    "Room "
                            + room.getRoomNumber()
                            + " | "
                            + room.getRoomType()
                            + " | price "
                            + room.getPricePerNight());
        }

        System.out.println("Bookings (latest -> oldest):");
        for (int i = bookings.size() - 1; i >= 0; i--) {
            Booking booking = bookings.get(i);
            System.out.println(
                    "Booking room "
                            + booking.getRoomNumberSnapshot()
                            + " ("
                            + booking.getRoomTypeSnapshot()
                            + ", price "
                            + booking.getRoomPriceSnapshot()
                            + ") | user "
                            + booking.getUserIdSnapshot()
                            + " (balance at booking "
                            + booking.getUserBalanceSnapshot()
                            + ") | "
                            + booking.getCheckInDate()
                            + " -> "
                            + booking.getCheckOutDate()
                            + " | total "
                            + booking.getTotalPrice());
        }
    }

    public void setUser(int userId, int balance) {
        if (userId <= 0) {
            throw new InvalidInputException("User id must be positive");
        }
        if (balance < 0) {
            throw new InvalidInputException("User balance cannot be negative");
        }
        User user = findUser(userId);
        if (user == null) {
            users.add(new User(userId, balance));
            return;
        }
        user.setBalance(balance);
    }

    public void printAllUsers() {
        System.out.println("Users (latest -> oldest):");
        for (int i = users.size() - 1; i >= 0; i--) {
            User user = users.get(i);
            System.out.println("User " + user.getUserId() + " | balance " + user.getBalance());
        }
    }

    private void validateRoomInput(int roomNumber, RoomType roomType, int roomPricePerNight) {
        if (roomNumber <= 0) {
            throw new InvalidInputException("Room number must be positive");
        }
        if (roomType == null) {
            throw new InvalidInputException("Room type is required");
        }
        if (roomPricePerNight <= 0) {
            throw new InvalidInputException("Room price must be positive");
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidInputException("Check-in date must be before check-out date");
        }
    }

    private void ensureRoomAvailability(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Booking booking : bookings) {
            if (booking.getRoomNumberSnapshot() != roomNumber) {
                continue;
            }
            if (datesOverlap(checkIn, checkOut, booking.getCheckInDate(), booking.getCheckOutDate())) {
                throw new RoomUnavailableException("Room is not available for the selected dates");
            }
        }
    }

    private boolean datesOverlap(
            LocalDate newCheckIn, LocalDate newCheckOut, LocalDate existingCheckIn, LocalDate existingCheckOut) {
        // Intervals are treated as [checkIn, checkOut); overlap exists if they intersect.
        return newCheckIn.isBefore(existingCheckOut) && newCheckOut.isAfter(existingCheckIn);
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    private Room findRoomOrThrow(int roomNumber) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new NotFoundException("Room not found");
        }
        return room;
    }

    private User findUser(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    private User findUserOrThrow(int userId) {
        User user = findUser(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }
}

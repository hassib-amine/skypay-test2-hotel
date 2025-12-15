package com.skypay.hotel.app;

import com.skypay.hotel.exception.InsufficientBalanceException;
import com.skypay.hotel.exception.InvalidInputException;
import com.skypay.hotel.exception.NotFoundException;
import com.skypay.hotel.exception.RoomUnavailableException;
import com.skypay.hotel.model.RoomType;
import com.skypay.hotel.service.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Service service = new Service();

        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        service.setRoom(3, RoomType.MASTER_SUITE, 3000);

        service.setUser(1, 5000);
        service.setUser(2, 10000);

        attemptBooking(
                service,
                1,
                2,
                toDate(LocalDate.of(2026, 6, 30)),
                toDate(LocalDate.of(2026, 7, 7)));
        attemptBooking(
                service,
                1,
                2,
                toDate(LocalDate.of(2026, 7, 7)),
                toDate(LocalDate.of(2026, 6, 30)));
        attemptBooking(
                service,
                1,
                1,
                toDate(LocalDate.of(2026, 7, 7)),
                toDate(LocalDate.of(2026, 7, 8)));
        attemptBooking(
                service,
                2,
                1,
                toDate(LocalDate.of(2026, 7, 7)),
                toDate(LocalDate.of(2026, 7, 9)));
        attemptBooking(
                service,
                2,
                3,
                toDate(LocalDate.of(2026, 7, 7)),
                toDate(LocalDate.of(2026, 7, 8)));

        service.setRoom(1, RoomType.MASTER_SUITE, 10000);

        service.printAll();
        service.printAllUsers();
    }

    private static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    private static void attemptBooking(
            Service service, int userId, int roomNumber, Date checkIn, Date checkOut) {
        try {
            service.bookRoom(userId, roomNumber, checkIn, checkOut);
            System.out.println("Booking succeeded");
        } catch (InvalidInputException
                | NotFoundException
                | RoomUnavailableException
                | InsufficientBalanceException ex) {
            System.out.println("Booking failed: " + ex.getMessage());
        }
    }
}
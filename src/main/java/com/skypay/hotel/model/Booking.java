package com.skypay.hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/****
 Booking stores a snapshot of room and user data at booking time
 so future updates to Room or User do not affect historical bookings.
 ****/
public class Booking {
    private final int roomNumberSnapshot;
    private final RoomType roomTypeSnapshot;
    private final int roomPriceSnapshot;
    private final int userIdSnapshot;
    private final int userBalanceSnapshot;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final int totalPrice;
    private final LocalDateTime createdAt;

    public Booking(
            int roomNumberSnapshot,
            RoomType roomTypeSnapshot,
            int roomPriceSnapshot,
            int userIdSnapshot,
            int userBalanceSnapshot,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int totalPrice,
            LocalDateTime createdAt) {
        this.roomNumberSnapshot = roomNumberSnapshot;
        this.roomTypeSnapshot = roomTypeSnapshot;
        this.roomPriceSnapshot = roomPriceSnapshot;
        this.userIdSnapshot = userIdSnapshot;
        this.userBalanceSnapshot = userBalanceSnapshot;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public int getRoomNumberSnapshot() {
        return roomNumberSnapshot;
    }

    public RoomType getRoomTypeSnapshot() {
        return roomTypeSnapshot;
    }

    public int getRoomPriceSnapshot() {
        return roomPriceSnapshot;
    }

    public int getUserIdSnapshot() {
        return userIdSnapshot;
    }

    public int getUserBalanceSnapshot() {
        return userBalanceSnapshot;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

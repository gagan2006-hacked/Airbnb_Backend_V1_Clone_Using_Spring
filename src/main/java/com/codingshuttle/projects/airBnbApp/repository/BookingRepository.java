package com.codingshuttle.projects.airBnbApp.repository;

import com.codingshuttle.projects.airBnbApp.entity.Booking;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByHotel(Hotel hotel, Pageable pageable);

    List<Booking> findAllByHotel(Hotel hotel);

    Page<Booking> findAllByBookingStatus(BookingStatus bookingStatus,Pageable pageable);

    @Query("""
SELECT b
FROM Booking b
WHERE b.bookingStatus = :status
AND b.createdAt <= :cutoff
""")
    Page<Booking> findExpiredBookings(
            BookingStatus status,
            LocalDateTime cutoff
            ,Pageable pageable
    );
}
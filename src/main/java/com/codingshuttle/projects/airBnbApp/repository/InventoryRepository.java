package com.codingshuttle.projects.airBnbApp.repository;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void deleteAllByRoom(Room room);

    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city AND
                  i.date BETWEEN :startDate AND :endDate
                  AND
                  (i.totalCount-i.bookedCount-i.reservedCount)>= :roomsCount
                  AND i.closed = false
            GROUP BY i.hotel, i.room
            HAVING COUNT(i.date)=:dateCount
            """)
    Page<Hotel> findHotelByStartAndEnd(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select i from Inventory i
            where i.room.id= :room
                  AND
                  i.date BETWEEN :startDate AND :endDate
                  AND
                  (i.totalCount-i.bookedCount-i.reservedCount)>= :roomsCount
                  AND i.closed = false
            """)
    List<Inventory> findAndLockAvailableInventory(
            @Param("room") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount);

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);


    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select i from Inventory i
            where i.room.id= :room
                  AND
                  i.date BETWEEN :startDate AND :endDate
                  AND
                  (i.totalCount-i.bookedCount)>= :roomsCount
                  AND i.closed = false
            """)
    List<Inventory> findAndLockReservedInventory( @Param("room") Long roomId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("roomsCount") Integer roomsCount);
    @Modifying
    @Query("""
            UPDATE Inventory i 
            SET i.reservedCount= i.reservedCount + :roomsCount
                 where i.room.id= :room
                  AND
                  i.date BETWEEN :startDate AND :endDate
                  AND
                  (i.totalCount-i.bookedCount-i.reservedCount)>= :roomsCount
            """)
    void updateReserved(@Param("room") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("roomsCount") Integer roomsCount);



    @Modifying
    @Query("""
            UPDATE Inventory i 
            SET i.reservedCount= i.reservedCount - :roomsCount,
                i.bookedCount= i.bookedCount + :roomsCount
                 where 
                  i.room.id= :room AND
                  i.date BETWEEN :startDate AND :endDate 
                              AND
                  (i.totalCount-i.bookedCount)>= :roomsCount AND
                  i.reservedCount >=:roomsCount
            """)
    void confirmBooking(@Param("room") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("roomsCount") Integer roomsCount);


    @Modifying
    @Query("""
            UPDATE Inventory i 
            SET 
                i.bookedCount= i.bookedCount - :roomsCount
                 where 
                  i.room.id= :room AND
                  i.date BETWEEN :startDate AND :endDate AND
                  (i.totalCount-i.bookedCount-i.reservedCount)>= :roomsCount 
            """)
    void cancelBooking(@Param("room") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("roomsCount") Integer roomsCount);

    Page<Inventory> findByRoomAndDateBetween(Room room, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findByRoomAndDateBetween(Room room, LocalDate startDate, LocalDate endDate);


    @Modifying
    @Query("""
            UPDATE Inventory i 
            SET i.reservedCount= i.reservedCount - :roomsCount
                 where i.room.id= :room
                  AND
                  i.date BETWEEN :startDate AND :endDate
                  AND
                  (i.totalCount-i.bookedCount-i.reservedCount)>= :roomsCount
                  AND
                  i.reservedCount>= :roomsCount
            """)
    void cancelReserved(@Param("room") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("roomsCount") Integer roomsCount);



    @Modifying
    @Query("""
                UPDATE Inventory i 
                SET i.price= :price ,
                i.surgeFactor = :surge ,
                i.closed= :closedVal
                where i.room.id= :room
                AND
                i.date BETWEEN :startDate AND :endDate
""")
    void updateInventoryByData(@Param("price")BigDecimal price,@Param("surge") BigDecimal surgePrice,@Param("closedVal")Boolean closed,
                               @Param("room")Long roomId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}
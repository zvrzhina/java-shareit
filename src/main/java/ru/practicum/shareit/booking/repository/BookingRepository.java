package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long id);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long id, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.item.id = :itemId AND b.status = 'APPROVED' AND b.start < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findLastBooking(LocalDateTime now, Long userId, Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBooking(LocalDateTime now, Long userId, Long itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatusEqualsAndEndIsBefore(Long itemId, Long bookerId, Status status, LocalDateTime end);

}

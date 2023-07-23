package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long id, Status status, Pageable pageable);

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

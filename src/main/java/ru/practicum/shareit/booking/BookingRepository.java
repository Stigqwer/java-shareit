package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByItemId(long itemId);

    List<Booking> findAllByItemIdOrderByStartDesc(long itemId);
}

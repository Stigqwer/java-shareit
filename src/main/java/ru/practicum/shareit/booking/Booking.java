package ru.practicum.shareit.booking;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    @NotNull
    @Future
    private LocalDateTime start;
    @Column(name = "end_date")
    @NotNull
    @Future
    private LocalDateTime end;
    @Column
    private Status status;
    @Column(name = "booker_id")
    private Long bookerId;
    @Column(name = "item_id")
    @NotNull
    private Long itemId;
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createBooking() {
        UserDto userDto1 = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto1.getId(), itemDto);
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));
        Booking booking = makeBooking(itemDtoFromService.getId(),
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoFromService = bookingService.createBooking(userDto2.getId(), booking);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id", bookingDtoFromService.getId()).getSingleResult();

        assertThat(booking1.getId(), equalTo(bookingDtoFromService.getId()));
        assertThat(booking1.getStart(), equalTo(bookingDtoFromService.getStart()));
        assertThat(booking1.getEnd(), equalTo(bookingDtoFromService.getEnd()));
        assertThat(booking1.getStatus(), equalTo(bookingDtoFromService.getStatus()));
        assertThat(booking1.getBookerId(), equalTo(bookingDtoFromService.getBooker().getId()));
        assertThat(booking1.getItemId(), equalTo(bookingDtoFromService.getItem().getId()));
    }

    @Test
    void findBookingById() {
        UserDto userDto1 = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto1.getId(), itemDto);
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));
        Booking booking = makeBooking(itemDtoFromService.getId(),
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoFromCreate = bookingService.createBooking(userDto2.getId(), booking);
        BookingDto bookingDtoFromService =
                bookingService.findBookingById(userDto2.getId(), bookingDtoFromCreate.getId());

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id", bookingDtoFromService.getId()).getSingleResult();

        assertThat(booking1.getId(), equalTo(bookingDtoFromService.getId()));
        assertThat(booking1.getStart(), equalTo(bookingDtoFromService.getStart()));
        assertThat(booking1.getEnd(), equalTo(bookingDtoFromService.getEnd()));
        assertThat(booking1.getStatus(), equalTo(bookingDtoFromService.getStatus()));
        assertThat(booking1.getBookerId(), equalTo(bookingDtoFromService.getBooker().getId()));
        assertThat(booking1.getItemId(), equalTo(bookingDtoFromService.getItem().getId()));
    }

    @Test
    void patchBooking() {
        UserDto userDto1 = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto1.getId(), itemDto);
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));
        Booking booking = makeBooking(itemDtoFromService.getId(),
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(userDto2.getId(), booking);
        BookingDto bookingDtoFromService =
                bookingService.patchBooking(userDto1.getId(), bookingDtoCreate.getId(), true);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id", bookingDtoFromService.getId()).getSingleResult();

        assertThat(booking1.getId(), equalTo(bookingDtoFromService.getId()));
        assertThat(booking1.getStart(), equalTo(bookingDtoFromService.getStart()));
        assertThat(booking1.getEnd(), equalTo(bookingDtoFromService.getEnd()));
        assertThat(booking1.getStatus(), equalTo(bookingDtoFromService.getStatus()));
        assertThat(booking1.getBookerId(), equalTo(bookingDtoFromService.getBooker().getId()));
        assertThat(booking1.getItemId(), equalTo(bookingDtoFromService.getItem().getId()));
    }

    @Test
    void findAllBookingByUser() {
        UserDto userDto1 = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDto1 = makeItemDto("Отвертка", "Аккумуляторная отвертка", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto1.getId(), itemDto);
        ItemDto itemDtoFromService2 = itemService.createItem(userDto1.getId(), itemDto1);
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));

        List<Booking> sourceBookings =
                List.of(makeBooking(itemDtoFromService.getId(), LocalDateTime.now().plusSeconds(1),
                                LocalDateTime.now().plusSeconds(3)),
                        makeBooking(itemDtoFromService2.getId(), LocalDateTime.now().plusSeconds(1),
                                LocalDateTime.now().plusSeconds(3)));

        for (Booking booking : sourceBookings) {
            booking.setStatus(Status.WAITING);
            booking.setBookerId(userDto2.getId());
            em.persist(booking);
        }
        em.flush();

        List<BookingDto> targetItemRequests =
                bookingService.findAllBookingByUser(userDto2.getId(), "ALL", 0, 10);

        for (Booking sourceBooking : sourceBookings) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(sourceBooking.getStart())),
                    hasProperty("end", equalTo(sourceBooking.getEnd())),
                    hasProperty("status", equalTo(sourceBooking.getStatus())),
                    hasProperty("booker", equalTo(userService.findUserById(sourceBooking.getBookerId()))),
                    hasProperty("item", equalTo(itemService.findItemById(userDto2.getId(),
                            sourceBooking.getItemId()))
                    ))));
        }
    }

    @Test
    void findAllBookingByOwner() {
        UserDto userDto1 = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDto1 = makeItemDto("Отвертка", "Аккумуляторная отвертка", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto1.getId(), itemDto);
        ItemDto itemDtoFromService2 = itemService.createItem(userDto1.getId(), itemDto1);
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));

        List<Booking> sourceBookings =
                List.of(makeBooking(itemDtoFromService.getId(), LocalDateTime.now().plusSeconds(1),
                                LocalDateTime.now().plusSeconds(3)),
                        makeBooking(itemDtoFromService2.getId(), LocalDateTime.now().plusSeconds(1),
                                LocalDateTime.now().plusSeconds(3)));

        for (Booking booking : sourceBookings) {
            booking.setStatus(Status.WAITING);
            booking.setBookerId(userDto2.getId());
            em.persist(booking);
        }
        em.flush();

        List<BookingDto> targetItemRequests =
                bookingService.findAllBookingByOwner(userDto1.getId(), "ALL", 0, 10);

        for (Booking sourceBooking : sourceBookings) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(sourceBooking.getStart())),
                    hasProperty("end", equalTo(sourceBooking.getEnd())),
                    hasProperty("status", equalTo(sourceBooking.getStatus())),
                    hasProperty("booker", equalTo(userService.findUserById(sourceBooking.getBookerId()))),
                    hasProperty("item", equalTo(itemService.findItemById(userDto2.getId(),
                            sourceBooking.getItemId()))
                    ))));
        }
    }

    private ItemDto makeItemDto(String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private Booking makeBooking(long itemId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }
}

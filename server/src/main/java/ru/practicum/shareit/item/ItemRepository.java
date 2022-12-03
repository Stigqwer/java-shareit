package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long id, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    @Query("select i from Item i where (LOWER(i.name) like LOWER(concat('%', ?1, '%'))" +
            " or LOWER(i.description) like lower(concat('%', ?1, '%'))) and i.available = true ")
    List<Item> search(String text, Pageable pageable);
}


package ru.practicum.shareit.validation;

public class PaginationValidation {
    public static void doValidation(Integer from, Integer size) {
        if (size <= 0) {
            throw new ValidationException(String.format("Размер страницы %s", size));
        } else if (from < 0) {
            throw new ValidationException("Индекс первого эллемента меньше нуля");
        }
    }
}

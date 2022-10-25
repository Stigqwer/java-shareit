package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserValidationException;


@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFound(final ItemNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserValidation(final UserValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

}

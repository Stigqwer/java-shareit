package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingException;
import ru.practicum.shareit.booking.BookingNotFoundException;
import ru.practicum.shareit.item.CommentException;
import ru.practicum.shareit.item.ItemException;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.request.ItemRequestException;
import ru.practicum.shareit.request.ItemRequestNotFoundException;
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

    @ExceptionHandler(ItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemException(final  ItemException e) {
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserValidation(final UserValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingException(final BookingException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingException(final BookingNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentException(final CommentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ItemRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemRequestException(final ItemRequestException e){
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ItemRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestNotFound(final ItemRequestNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}

package br.com.retailstore.wishlist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // 503
    @ExceptionHandler(DatabaseException.class)
    @ResponseBody
    public ErrorInformation databaseCannotSaveObject(DatabaseException e) {
        return new ErrorInformation("Content is not supported.", e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorInformation httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        return new ErrorInformation("Bad Request.", e);
    }
}

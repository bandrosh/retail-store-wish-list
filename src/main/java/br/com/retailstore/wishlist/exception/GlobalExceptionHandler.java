package br.com.retailstore.wishlist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorInformation httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        return new ErrorInformation("Bad Request.", e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorInformation notFoundExceptionHandler(NotFoundException e) {
        return new ErrorInformation("Not Found Exception.", e);
    }
}

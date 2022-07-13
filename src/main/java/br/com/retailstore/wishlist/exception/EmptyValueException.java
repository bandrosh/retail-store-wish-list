package br.com.retailstore.wishlist.exception;

public class EmptyValueException extends RuntimeException {
    public EmptyValueException(String message) {
        super(message);
    }
}

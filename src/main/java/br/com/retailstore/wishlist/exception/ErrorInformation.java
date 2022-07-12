package br.com.retailstore.wishlist.exception;

public class ErrorInformation {
    private final String message;
    private String exception;

    public ErrorInformation(String message, Exception ex) {
        this.message = message;
        if (ex != null) {
            this.exception = ex.getMessage();
        }
    }

    public String getMessage() {
        return message;
    }

    public String getException() {
        return exception;
    }
}

package br.com.retailstore.wishlist.domain;

import br.com.retailstore.wishlist.exception.EmptyValueException;

import java.util.Objects;

public record Product(String id) {
    public Product {
        Objects.requireNonNull(id);
        if(id.isEmpty()) {
            throw new EmptyValueException("Product value is empty.");
        }
    }
}

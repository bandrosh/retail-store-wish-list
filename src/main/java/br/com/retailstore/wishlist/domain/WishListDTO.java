package br.com.retailstore.wishlist.domain;

import br.com.retailstore.wishlist.exception.EmptyValueException;

import java.util.Objects;


public record WishListDTO(Product product) {
    public WishListDTO {
        Objects.requireNonNull(product);
        if(product.id().isEmpty()) {
            throw new EmptyValueException("Product value is empty.");
        }
    }
}

package br.com.retailstore.wishlist.domain;

import br.com.retailstore.wishlist.exception.EmptyValueException;

import java.util.Objects;


public record WishListDTO(String client, Product product) {
    public WishListDTO {
        Objects.requireNonNull(client);
        Objects.requireNonNull(product);
        if(client.isEmpty() || product.id().isEmpty()) {
            throw new EmptyValueException("Client or Product value is empty.");
        }
    }
}

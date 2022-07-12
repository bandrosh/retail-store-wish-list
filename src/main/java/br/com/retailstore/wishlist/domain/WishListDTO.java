package br.com.retailstore.wishlist.domain;

import java.util.Objects;


public record WishListDTO(String client, Product product) {
    public WishListDTO {
        Objects.requireNonNull(client);
        Objects.requireNonNull(product);
    }
}

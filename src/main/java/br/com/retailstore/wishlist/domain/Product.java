package br.com.retailstore.wishlist.domain;

import java.util.Objects;

public record Product(String id) {
    public Product {
        Objects.requireNonNull(id);
    }
}

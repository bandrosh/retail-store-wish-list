package br.com.retailstore.wishlist.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Objects;

@Document(collection = "wishlist")
public record WishList(@Id String client, HashSet<Product> products) {
    public WishList {
        Objects.requireNonNull(client);
        Objects.requireNonNull(products);
    }

    public static WishList of(String client, Product product) {
        var products = new HashSet<Product>();
        products.add(product);

        return new WishList(client, products);
    }
}

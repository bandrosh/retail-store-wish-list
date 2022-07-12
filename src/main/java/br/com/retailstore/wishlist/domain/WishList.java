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

    public static WishList of(WishListDTO wishListDTO) {
        var products = new HashSet<Product>();
        products.add(wishListDTO.product());

        return new WishList(wishListDTO.client(), products);
    }
}

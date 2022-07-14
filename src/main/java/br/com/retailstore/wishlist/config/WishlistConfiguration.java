package br.com.retailstore.wishlist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WishlistConfiguration {
    @Value("${wishlist.page-max-size}")
    private Integer wishlistMaxPageSize;

    public Integer getWishlistMaxPageSize() {
        return wishlistMaxPageSize;
    }
}

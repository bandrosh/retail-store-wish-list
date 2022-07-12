package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.exception.DatabaseException;
import br.com.retailstore.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

@Service
public class WishListService {
    final WishListRepository wishListRepository;

    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public void saveClientWishList(WishList wishList) {
        try {
            var clientWishListProducts = getClientWishListProducts(wishList.client());

            if(clientWishListProducts.isEmpty()) {
                wishListRepository.save(wishList);
            } else {
                if(!wishList.products().containsAll(clientWishListProducts)) {
                    wishList.products().addAll(clientWishListProducts);
                    wishListRepository.save(wishList);
                }
            }

        } catch (Exception e) {
            throw new DatabaseException(format("Cannot save client wishlist product. Error %s", e.getMessage()));
        }
    }

    private Set<Product> getClientWishListProducts(String client) {
        var products = wishListRepository.findById(client);

        if(products.isEmpty()) return new HashSet<>();
        return products.get().products();
    }
}

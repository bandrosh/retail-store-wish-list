package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.exception.NotFoundException;
import br.com.retailstore.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class WishListService {
    final WishListRepository wishListRepository;

    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public void saveClientWishList(WishList wishList) {
        if (notExistClient(wishList.client())) {
            wishListRepository.save(wishList);
        } else {
            var clientWishListProducts = getClientWishListProducts(wishList.client());

            if (!wishList.products()
                         .containsAll(clientWishListProducts)) {
                wishList.products()
                        .addAll(clientWishListProducts);
                wishListRepository.save(wishList);
            }
        }
    }

    public void deleteProductFromClientWishList(WishList wishList) {
        if(notExistClient(wishList.client())) {
            throw new NotFoundException("Client not Found.");
        } else {
            var clientWishListProducts = getClientWishListProducts(wishList.client());

            if (clientWishListProducts.isEmpty()) {
                wishListRepository.delete(wishList);
            } else {
                if (!wishList.products()
                             .containsAll(clientWishListProducts)) {
                    wishList.products()
                            .removeAll(clientWishListProducts);
                    wishListRepository.save(wishList);
                } else {
                    throw new NotFoundException("Product not found.");
                }
            }
        }
    }

    private boolean notExistClient(String client) {
        return !wishListRepository.existsById(client);
    }

    private Set<Product> getClientWishListProducts(String client) {
        var products = wishListRepository.findById(client);

        if (products.isEmpty()) return new HashSet<>();
        return products.get().products();
    }
}

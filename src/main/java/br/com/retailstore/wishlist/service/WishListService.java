package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.exception.NotFoundException;
import br.com.retailstore.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class WishListService {
    final WishListRepository wishListRepository;

    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public void saveClientWishList(String clientId, Product product) {
        if (notExistClient(clientId)) {
            wishListRepository.save(WishList.of(clientId, product));
        } else {
            var clientWishListProducts = getClientWishListProducts(clientId);

            if (!clientWishListProducts.contains(product)) {
                clientWishListProducts.add(product);
                wishListRepository.save(new WishList(clientId, clientWishListProducts));
            }
        }
    }

    public void deleteProductFromClientWishList(String clientId, Product product) {
        if(notExistClient(clientId)) {
            throw new NotFoundException("Client not Found.");
        } else {
            var clientWishListProducts = getClientWishListProducts(clientId);

            if (clientWishListProducts.isEmpty() || clientWishListProducts.size() == 1) {
                wishListRepository.delete(WishList.of(clientId, product));
            } else {
                if (clientWishListProducts.contains(product)) {
                    clientWishListProducts.remove(product);
                    wishListRepository.save(new WishList(clientId, clientWishListProducts));
                } else {
                    throw new NotFoundException("Product not found.");
                }
            }
        }
    }

    private boolean notExistClient(String client) {
        return !wishListRepository.existsById(client);
    }

    private HashSet<Product> getClientWishListProducts(String client) {
        var products = wishListRepository.findById(client);

        if (products.isEmpty()) return new HashSet<>();
        return products.get().products();
    }
}

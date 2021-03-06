package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.exception.NotFoundException;
import br.com.retailstore.wishlist.repository.WishListRepository;
import br.com.retailstore.wishlist.repository.ProductsWishListQueryDAORepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class WishListService {
    final WishListRepository wishListRepository;

    final ProductsWishListQueryDAORepository productsWishListQueryRepository;

    public WishListService(WishListRepository wishListRepository,
                           ProductsWishListQueryDAORepository productsWishListQueryDAO) {
        this.wishListRepository = wishListRepository;
        this.productsWishListQueryRepository = productsWishListQueryDAO;
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
        if (notExistClient(clientId)) {
            throw new NotFoundException("Client not Found.");
        } else {
            var clientWishListProducts = getClientWishListProducts(clientId);
            var existProductInClientList = clientWishListProducts.contains(product);

            if (!existProductInClientList) {
                throw new NotFoundException("Product not found.");
            }

            if (clientWishListProducts.size() == 1) {
                wishListRepository.delete(WishList.of(clientId, product));
            } else {
                clientWishListProducts.remove(product);
                wishListRepository.save(new WishList(clientId, clientWishListProducts));
            }
        }
    }

    public List<Product> getProductsWishListByClient(String client, PageRequest pageRequest) {
        return productsWishListQueryRepository.getPagedProductsWishListByClient(client, pageRequest);
    }

    public WishListDTO getClientProductFromWishListByProduct(String client, Product product) {
        var existProductInClientWishlist = productsWishListQueryRepository.
                existProductInClientWishList(client, product.id());

        if (!existProductInClientWishlist) {
            throw new NotFoundException("Product not found.");
        }

        return new WishListDTO(product);
    }

    private boolean notExistClient(String client) {
        return !wishListRepository.existsById(client);
    }

    private HashSet<Product> getClientWishListProducts(String client) {
        var products = wishListRepository.findById(client);

        if (products.isEmpty()) return new HashSet<>();
        return products.get()
                       .products();
    }
}

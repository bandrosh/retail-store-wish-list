package br.com.retailstore.wishlist.repository;

import br.com.retailstore.wishlist.domain.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductsWishListQueryDAORepository {
    List<Product> getPagedProductsWishListByClient(String client, Pageable pageable);
    boolean existProductInClientWishList(String client, String product);
}

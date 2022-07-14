package br.com.retailstore.wishlist.controller.response;

import br.com.retailstore.wishlist.domain.Product;

import java.util.List;

public record ProductsPagedResponse(List<Product> products, Integer currentPage, Integer totalPageItems,
                                    boolean lastPage) {
}

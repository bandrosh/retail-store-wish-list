package br.com.retailstore.wishlist.controller;

import br.com.retailstore.wishlist.config.WishlistConfiguration;
import br.com.retailstore.wishlist.controller.response.ProductsPagedResponse;
import br.com.retailstore.wishlist.domain.ClientDTO;
import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.service.WishListService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishListController {
    final WishListService wishListService;

    final WishlistConfiguration wishlistConfiguration;

    public WishListController(WishListService wishListService, WishlistConfiguration wishlistConfiguration) {
        this.wishListService = wishListService;
        this.wishlistConfiguration = wishlistConfiguration;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveClientWishList(@RequestHeader("client") ClientDTO clientDTO, @RequestBody WishListDTO wishListDTO) {
        wishListService.saveClientWishList(clientDTO.client(), wishListDTO.product());
    }

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public ProductsPagedResponse getProductsWishListByClient(@RequestHeader("client") ClientDTO clientDTO,
                                                             @RequestParam(defaultValue = "0") int page) {
        PageRequest pageRequest = PageRequest.of(page, wishlistConfiguration.getWishlistMaxPageSize());

        var productsPaged = wishListService.getProductsWishListByClient(clientDTO.client(), pageRequest);
        var lastPage = isProductsLastPage(productsPaged.size(),
                wishlistConfiguration.getWishlistMaxPageSize());

        return new ProductsPagedResponse(productsPaged, page, productsPaged.size(), lastPage);
    }

    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WishListDTO getClientProductFromWishListByProductId(@PathVariable("id") String productId,
                                                        @RequestHeader("client") ClientDTO clientDTO) {
        return wishListService.getClientProductFromWishListByProduct(clientDTO.client(), new Product(productId));
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProductFromClientWishList(@PathVariable("id") String productId,
                                                @RequestHeader("client") ClientDTO clientDTO) {
        wishListService.deleteProductFromClientWishList(clientDTO.client(), new Product(productId));
    }

    private boolean isProductsLastPage(int numberPageElements, long numberMaxPageElements) {
        return numberPageElements < numberMaxPageElements;
    }
}

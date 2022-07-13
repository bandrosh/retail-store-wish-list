package br.com.retailstore.wishlist.controller;

import br.com.retailstore.wishlist.domain.ClientDTO;
import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.service.WishListService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class WishListController {
    final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @PostMapping("/wishlist")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveClientWishList(@RequestBody WishListDTO wishListDTO) {
        wishListService.saveClientWishList(wishListDTO.client(), wishListDTO.product());
    }

    @DeleteMapping("/wishlist/product/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProductFromClientWishList(@PathVariable("id") String productId,
                                                @RequestBody ClientDTO clientDTO) {
        wishListService.deleteProductFromClientWishList(clientDTO.client(), new Product(productId));
    }
}

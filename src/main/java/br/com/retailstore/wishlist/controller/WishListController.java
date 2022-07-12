package br.com.retailstore.wishlist.controller;

import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.service.WishListService;
import org.springframework.http.HttpStatus;
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
        wishListService.saveClientWishList(WishList.of(wishListDTO));
    }
}

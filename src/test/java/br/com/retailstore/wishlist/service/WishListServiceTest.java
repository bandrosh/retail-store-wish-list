package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.repository.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class WishListServiceTest {
    WishListService wishListService;

    @MockBean
    WishListRepository wishListRepository;

    @BeforeEach
    public void init() {
        this.wishListService = new WishListService(wishListRepository);
    }

    @Test
    void whenSaveOneExistedClientWishListThenMergeProductSet() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                }});

        var newData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-02"));
                }});

        var result = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(wishListRepository.findById(anyString())).thenReturn(Optional.of(existedData));
        when(wishListRepository.save(any())).thenReturn(result);

        wishListService.saveClientWishList(newData);

        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(1)).save(any());
    }

    @Test
    void whenSaveOneNewClientWishListThenSaveToDatabase() {
        var newData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                }});

        var result = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                }});

        when(wishListRepository.findById(anyString())).thenReturn(Optional.empty());
        when(wishListRepository.save(any())).thenReturn(result);

        wishListService.saveClientWishList(newData);

        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(1)).save(any());
    }
}

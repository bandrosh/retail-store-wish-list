package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.exception.NotFoundException;
import br.com.retailstore.wishlist.repository.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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

        var result = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(wishListRepository.existsById(anyString())).thenReturn(true);
        when(wishListRepository.findById(anyString())).thenReturn(Optional.of(existedData));
        when(wishListRepository.save(any())).thenReturn(result);

        wishListService.saveClientWishList("client-01", new Product("Product-02"));

        verify(wishListRepository, times(1)).existsById(anyString());
        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(1)).save(any());
    }

    @Test
    void whenSaveOneExistedClientWishListWithExistedProductThenDoNotMergeProductSet() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                }});

        var result = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(wishListRepository.existsById(anyString())).thenReturn(true);
        when(wishListRepository.findById(anyString())).thenReturn(Optional.of(existedData));
        when(wishListRepository.save(any())).thenReturn(result);

        wishListService.saveClientWishList("client-01", new Product("Product-01"));

        verify(wishListRepository, times(1)).existsById(anyString());
        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(0)).save(any());
    }

    @Test
    void whenSaveOneNewClientWishListThenReturn() {
        when(wishListRepository.existsById(anyString())).thenReturn(false);

        wishListService.saveClientWishList("client-01", new Product("Product-01"));

        verify(wishListRepository, times(1)).existsById(anyString());
        verify(wishListRepository, times(0)).findById(anyString());
        verify(wishListRepository, times(1)).save(any());
    }

    @Test
    void whenDeleteOneExistedClientProductWishListThenRemoveProduct() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                }});

        when(wishListRepository.existsById(anyString())).thenReturn(true);
        when(wishListRepository.findById(anyString())).thenReturn(Optional.of(existedData));
        doNothing().when(wishListRepository).delete(any(WishList.class));

        wishListService.deleteProductFromClientWishList("client-01", new Product("Product-01"));

        verify(wishListRepository, times(1)).existsById(anyString());
        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(1)).delete(any());
        verify(wishListRepository, times(0)).save(any());
    }

    @Test
    void whenDeleteOneExistedClientProductWishListFromBigListThenRemoveCurrentProduct() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(wishListRepository.existsById(anyString())).thenReturn(true);
        when(wishListRepository.findById(anyString())).thenReturn(Optional.of(existedData));
        doNothing().when(wishListRepository).delete(any(WishList.class));

        wishListService.deleteProductFromClientWishList("client-01", new Product("Product-01"));

        verify(wishListRepository, times(1)).existsById(anyString());
        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(0)).delete(any());
        verify(wishListRepository, times(1)).save(any());
    }

    @Test
    void whenTryDeleteOneNotExistedClientProductFromWishListThenReturnExceptionNotFound() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(wishListRepository.existsById(anyString())).thenReturn(true);
        when(wishListRepository.findById(anyString())).thenReturn(Optional.of(existedData));
        doNothing().when(wishListRepository).delete(any(WishList.class));

        var checkedProduct = new Product("Product-03");

        assertThrows(NotFoundException.class, () -> wishListService.deleteProductFromClientWishList("client-01", checkedProduct));

        verify(wishListRepository, times(1)).existsById(anyString());
        verify(wishListRepository, times(1)).findById(anyString());
        verify(wishListRepository, times(0)).delete(any());
        verify(wishListRepository, times(0)).save(any());
    }

    @Test
    void whenTryDeleteOneNotExistedClientWishListThenReturnExceptionNotFound() {
        when(wishListRepository.existsById(anyString())).thenReturn(false);
        var product = new Product("Product-01");

        assertThrows(NotFoundException.class,
                () -> wishListService.deleteProductFromClientWishList("client-01", product)
        );
    }
}

package br.com.retailstore.wishlist.service;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.exception.NotFoundException;
import br.com.retailstore.wishlist.repository.WishListRepository;
import br.com.retailstore.wishlist.repository.ProductsWishListQueryDAORepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @MockBean
    ProductsWishListQueryDAORepository productsWishListQueryRepository;

    @BeforeEach
    public void init() {
        this.wishListService = new WishListService(wishListRepository,
                productsWishListQueryRepository);
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
        doNothing().when(wishListRepository)
                   .delete(any(WishList.class));

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
        doNothing().when(wishListRepository)
                   .delete(any(WishList.class));

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
        doNothing().when(wishListRepository)
                   .delete(any(WishList.class));

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

    @Test
    void whenGetClientProductWishListThenReturnCompleteUserProductList() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(productsWishListQueryRepository.getPagedProductsWishListByClient(any(), any()))
                .thenReturn(existedData.products()
                                       .stream()
                                       .toList());

        var result = wishListService.getProductsWishListByClient("client-01",
                PageRequest.of(0, 3));

        verify(productsWishListQueryRepository, times(1))
                .getPagedProductsWishListByClient(any(), any());
        assertEquals(2, result.size());
    }

    @Test
    void whenGetClientWithoutProductInWishListThenReturnEmptyList() {
        var existedData = new WishList("client-01",
                new HashSet<>() {{
                    add(new Product("Product-01"));
                    add(new Product("Product-02"));
                }});

        when(productsWishListQueryRepository.getPagedProductsWishListByClient(any(), any()))
                .thenReturn(new ArrayList<>());

        var result = wishListService.getProductsWishListByClient("client-01",
                PageRequest.of(0, 3));

        verify(productsWishListQueryRepository, times(1))
                .getPagedProductsWishListByClient(any(), any());

        assertEquals(0, result.size());
    }

    @Test
    void whenGetProductInExistedClientWishListThenReturnProduct() {
        when(productsWishListQueryRepository.existProductInClientWishList(any(), any()))
                .thenReturn(true);

        var result = wishListService.getClientProductFromWishListByProduct("client-01",
                new Product("Product-01"));

        var check = new WishListDTO(new Product("Product-01"));

        assertEquals(check, result);
        verify(productsWishListQueryRepository, times(1))
                .existProductInClientWishList(any(), any());
    }

    @Test
    void whenGetProductThatNotExistInClientWishListThenThrowNotFoundException() {
        when(productsWishListQueryRepository.existProductInClientWishList(any(), any()))
                .thenReturn(false);

        var product = new Product("Product-01");

        assertThrows(NotFoundException.class,
                () -> wishListService.getClientProductFromWishListByProduct("client-01", product)
        );
    }
}

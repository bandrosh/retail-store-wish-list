package br.com.retailstore.wishlist.controller;

import br.com.retailstore.wishlist.config.WishlistConfiguration;
import br.com.retailstore.wishlist.domain.ClientDTO;
import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.exception.EmptyValueException;
import br.com.retailstore.wishlist.exception.NotFoundException;
import br.com.retailstore.wishlist.service.WishListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WishListController.class)
class WishListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishListService wishListService;

    @MockBean
    private WishlistConfiguration wishlistConfiguration;

    @BeforeEach
    void setUp() {
        when(wishlistConfiguration.getWishlistMaxPageSize()).thenReturn(3);
    }

    @Test
    void whenTrySaveCorrectProductToClientWishListThenReturnStatus201() throws Exception {
        var wishlist = new WishListDTO(new Product(UUID.randomUUID()
                                                       .toString()));

        mockMvc.perform(post("/api/v1/wishlist")
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(wishlist))
               )
               .andExpect(status().isCreated());
    }

    @Test
    void whenTrySaveOneClientWishListWithNullValuesThenReturnStatus400() throws Exception {
        mockMvc.perform(post("/api/v1/wishlist")
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString("{\"client\" : null, \"product\": null}"))
               )
               .andExpect(status().isBadRequest());
    }

    @Test
    void whenTrySaveOneClientWishListAndWeHaveDataBaseErrorThenReturnStatus503() throws Exception {
        var wishlist = new WishListDTO(new Product(UUID.randomUUID()
                                                       .toString()));

        doThrow(new EmptyValueException("Empty Value exception")).when(wishListService)
                                                                 .saveClientWishList(anyString(), any(Product.class));

        mockMvc.perform(post("/api/v1/wishlist")
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(wishlist))
               )
               .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteAllProductFromClientWishListThenReturnStatus200() throws Exception {
        var productId = UUID.randomUUID()
                            .toString();
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        doNothing().when(wishListService)
                   .deleteProductFromClientWishList(anyString(), any(Product.class));

        mockMvc.perform(delete("/api/v1/wishlist/products/{productId}", productId)
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
               )
               .andExpect(status().isOk());
    }

    @Test
    void whenTryDeleteNotExistedClientOrProductWishListAndThenReturnStatus404() throws Exception {
        var productId = UUID.randomUUID()
                            .toString();
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        doThrow(new NotFoundException("Client not Found.")).when(wishListService)
                                                           .deleteProductFromClientWishList(
                                                                   anyString(), any(Product.class));

        mockMvc.perform(delete("/api/v1/wishlist/products/{productId}", productId)
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
               )
               .andExpect(status().isNotFound());
    }

    @Test
    void whenGetClientProductsWishListAndThenReturnProductsAndStatus200() throws Exception {
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        when(wishlistConfiguration.getWishlistMaxPageSize()).thenReturn(20);

        when(wishListService.getProductsWishListByClient(any(), any())).thenReturn(List.of(new Product("P1"),
                new Product("P2"),
                new Product("P3"),
                new Product("P4")));

        mockMvc.perform(get("/api/v1/wishlist/products")
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
                       .param("page", String.valueOf(0))
               )
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.products.size()").value(4))
               .andExpect(jsonPath("$.currentPage").value(0))
               .andExpect(jsonPath("$.totalPageItems").value(4))
               .andExpect(jsonPath("$.lastPage").value(true));
    }

    @Test
    void whenTryGetClientProductWishListWithoutProductsThenReturnEmptyListAndStatus200() throws Exception {
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        when(wishlistConfiguration.getWishlistMaxPageSize()).thenReturn(20);

        when(wishListService.getProductsWishListByClient(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/wishlist/products")
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
                       .param("page", String.valueOf(0))
               )
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.products.size()").value(0))
               .andExpect(jsonPath("$.currentPage").value(0))
               .andExpect(jsonPath("$.totalPageItems").value(0))
               .andExpect(jsonPath("$.lastPage").value(true));
    }

    @Test
    void whenGetClientProductsWishListAndHaveMorePagesThenReturnProductsAndLasPageFalseAndStatus200() throws Exception {
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        when(wishlistConfiguration.getWishlistMaxPageSize()).thenReturn(4);

        when(wishListService.getProductsWishListByClient(any(), any())).thenReturn(List.of(new Product("P1"),
                new Product("P2"),
                new Product("P3"),
                new Product("P4")));

        mockMvc.perform(get("/api/v1/wishlist/products")
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
                       .param("page", String.valueOf(0))
               )
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.products.size()").value(4))
               .andExpect(jsonPath("$.currentPage").value(0))
               .andExpect(jsonPath("$.totalPageItems").value(4))
               .andExpect(jsonPath("$.lastPage").value(false));
    }

    @Test
    void whenTryGetProductExistedInClientWishListThenReturnProductAndStatus200() throws Exception {
        var productId = UUID.randomUUID()
                            .toString();
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        when(wishListService.getClientProductFromWishListByProduct(
                anyString(), any(Product.class))).thenReturn(new WishListDTO(new Product(productId)));

        mockMvc.perform(get("/api/v1/wishlist/products/{productId}", productId)
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.product.id").value(productId));
    }
    @Test
    void whenTryGetProductNotExistedInClientWishListThenReturnStatus404() throws Exception {
        var productId = UUID.randomUUID()
                            .toString();
        var client = new ClientDTO(UUID.randomUUID()
                                       .toString());

        doThrow(new NotFoundException("Product not Found.")).when(wishListService)
                                                           .getClientProductFromWishListByProduct(
                                                                   anyString(), any(Product.class));

        mockMvc.perform(get("/api/v1/wishlist/products/{productId}", productId)
                       .header("client", UUID.randomUUID()
                                             .toString())
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(client))
               )
               .andExpect(status().isNotFound());
    }

}

package br.com.retailstore.wishlist.controller;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.domain.WishListDTO;
import br.com.retailstore.wishlist.exception.DatabaseException;
import br.com.retailstore.wishlist.service.WishListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WishListController.class)
class WishListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishListService wishListService;

    @Test
    void whenTrySaveCorrectProductToClientWishListThenReturnStatus201() throws Exception {
        var wishlist = new WishListDTO(UUID.randomUUID()
                                           .toString(), new Product(UUID.randomUUID()
                                                                        .toString()));

        mockMvc.perform(post("/api/v1/wishlist")
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(wishlist))
               )
               .andExpect(status().isCreated());
    }

    @Test
    void whenTrySaveOneClientWishListWithNullValuesThenReturnStatus400() throws Exception {
        mockMvc.perform(post("/api/v1/wishlist")
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString("{\"client\" : null, \"product\": null}"))
               )
               .andExpect(status().isBadRequest());
    }

    @Test
    void whenTrySaveOneClientWishListAndWeHaveDataBaseErrorThenReturnStatus503() throws Exception {
        var wishlist = new WishListDTO(UUID.randomUUID()
                                           .toString(), new Product(UUID.randomUUID()
                                                                        .toString()));

        doThrow(new DatabaseException("DataBase Error")).when(wishListService)
                                                        .saveClientWishList(any(WishList.class));

        mockMvc.perform(post("/api/v1/wishlist")
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(wishlist))
               )
               .andExpect(status().is5xxServerError());
    }
}

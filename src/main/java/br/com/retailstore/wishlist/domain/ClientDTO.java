package br.com.retailstore.wishlist.domain;

import br.com.retailstore.wishlist.exception.EmptyValueException;

import java.util.Objects;

public record ClientDTO(String client) {
    public ClientDTO {
        Objects.requireNonNull(client);
        if(client.isEmpty()) {
            throw new EmptyValueException("Client value is empty.");
        }
    }
}

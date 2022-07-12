package br.com.retailstore.wishlist.repository;

import br.com.retailstore.wishlist.domain.WishList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends MongoRepository<WishList, String> {
}

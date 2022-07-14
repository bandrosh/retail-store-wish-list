package br.com.retailstore.wishlist.repository.impl;

import br.com.retailstore.wishlist.domain.Product;
import br.com.retailstore.wishlist.domain.WishList;
import br.com.retailstore.wishlist.repository.ProductsWishListQueryDAORepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

@Repository
public class ProductsWishListQueryDAORepositoryImpl implements ProductsWishListQueryDAORepository {
    public static final String PRODUCTS_COLLECTION_FIELD = "products";
    public static final String PRODUCTS_IDS_COLLECTION_FIELD = "products._id";
    public static final String CLIENT_ID_COLLECTION_FIELD = "_id";
    private final MongoTemplate mongoTemplate;

    public ProductsWishListQueryDAORepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Product> getPagedProductsWishListByClient(String client, Pageable pageable) {
        AggregationOperation matchStage = Aggregation.match(new Criteria(CLIENT_ID_COLLECTION_FIELD).is(client));
        AggregationOperation projectStage = Aggregation.project(PRODUCTS_COLLECTION_FIELD)
                                                       .andExclude(CLIENT_ID_COLLECTION_FIELD);
        AggregationOperation unwindStage = Aggregation.unwind(PRODUCTS_COLLECTION_FIELD);
        AggregationOperation replaceRoot = Aggregation.replaceRoot(PRODUCTS_COLLECTION_FIELD);

        var skipChoices = ((long) pageable.getPageNumber() * pageable.getPageSize());

        final Aggregation agg = newAggregation(
                matchStage,
                projectStage,
                unwindStage,
                replaceRoot,
                skip(skipChoices),
                limit(pageable.getPageSize())
        );

        return mongoTemplate.aggregate(agg, WishList.class, Product.class)
                            .getMappedResults();
    }

    public boolean existProductInClientWishList(String client, String product) {
        var query = new Query();
        query.addCriteria(Criteria.where(CLIENT_ID_COLLECTION_FIELD)
                                  .is(client)
                                  .and(PRODUCTS_IDS_COLLECTION_FIELD)
                                  .is(product));

        return mongoTemplate.exists(query, WishList.class);
    }
}

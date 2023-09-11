package comp3350.group9.theauctionhouse.core.application;

import comp3350.group9.theauctionhouse.core.domain.Product;

public interface ProductQueriable extends Queriable<Product> {
    String addProduct(Product product);
}

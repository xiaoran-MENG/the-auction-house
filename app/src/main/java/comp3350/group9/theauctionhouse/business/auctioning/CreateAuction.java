package comp3350.group9.theauctionhouse.business.auctioning;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.exception.auction.DuplicateAuction;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;

public class CreateAuction {
    private UserAccountManager profile;
    private QueryEngine queryEngine;

    public static CreateAuction of() {
        return new CreateAuction(HSQLDBFactory.get(), UserAccountManager.of());
    }

    public CreateAuction(QueryEngine queryEngine, UserAccountManager profile) {
        this.queryEngine = queryEngine;
        this.profile = profile;
    }

    public boolean invoke(AuctionInfo info, Product product) throws CreateAuctionException {
        ensureUnique(info.title());
        String productId = queryEngine.products().addProduct(product);
        if (productId == null) return false;
        Auction auction = Auction.builder()
                .title(info.title())
                .description(info.description())
                .minBid(info.minBid())
                .expiry(info.expiry())
                .seller(info.seller())
                .imagePath(info.imagePath())
                .tags(Flux.of(info.tags()).map(AuctionTagDTO::entitify).get())
                .product(queryEngine.products().findById(productId))
                .build();
        return queryEngine.auctions().add(auction);
    }

    public void ensureUnique(String title){
        Flux.of(queryEngine.auctions().findByTitle(title))
                .by(a -> a.seller().id().equals(profile.getLoggedInUser().id()))
                .one()
                .then(x -> { throw new DuplicateAuction(); });
    }
}

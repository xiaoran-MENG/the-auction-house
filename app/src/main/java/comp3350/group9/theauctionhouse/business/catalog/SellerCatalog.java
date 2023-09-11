package comp3350.group9.theauctionhouse.business.catalog;

import java.util.NoSuchElementException;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.functional.Mono;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.exception.auction.CannotCompleteAuctionException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.User;

public class SellerCatalog {
    private final QueryEngine query;
    private final User seller;
    private final UserAccountManager accountManager;

    public SellerCatalog(QueryEngine query) {
        this.query = query;
        this.accountManager = UserAccountManager.of();
        this.seller = accountManager.getLoggedInUser();
    }

    public SellerCatalog(QueryEngine query, UserAccountManager accManager) {
        this.query = query;
        this.accountManager = accManager;
        this.seller = accManager.getLoggedInUser();
    }

    public static SellerCatalog of() {
        return new SellerCatalog(HSQLDBFactory.get());
    }

    public Flux<AuctionCatalogDTO> getAll() {
        return Flux.of(query.auctions().findAll())
                .by(x -> x.seller().id().equals(seller.id()))
                .map(AuctionCatalogDTO::of);
    }

    public Mono<Auction> getById(String id) {
        return Mono.of(query.auctions().findById(id));
    }

    public void put(String id, Auction o) {
        getById(id).then(x -> query.auctions().update(id, o))
                .or(() -> new NoSuchElementException("Auction does not exist"));
    }

    public void completeAuction(String auctionId, String bidId) throws CannotCompleteAuctionException {
        Auction auction = getById(auctionId).get();
        boolean containsBid = Flux.of(auction.bids()).by(x -> x.id().equals(bidId)).get().size() == 1;
        if (!auction.isCompleted() && containsBid){
            auction.setAsComplete();
            boolean result = query.auctions().update(auctionId,auction) && query.bids().updateCompletedAuctionBidStatus(
                    auctionId,
                    bidId,
                    Bid.Status.PURCHASED.toString(),
                    Bid.Status.NOT_PURCHASED.toString());
            if (!result) throw new CannotCompleteAuctionException("System error while updating data");
        }else if (!containsBid){
            throw new CannotCompleteAuctionException("No such bid for this auction");
        }else if (auction.isCompleted()){
            throw new CannotCompleteAuctionException("Auction is already completed");
        }
    }
}

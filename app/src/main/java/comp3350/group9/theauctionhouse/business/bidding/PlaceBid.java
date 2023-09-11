package comp3350.group9.theauctionhouse.business.bidding;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.persistence.HSQLDBQueryEngine;

public class PlaceBid {
    private final AuctionQueriable auctionQueriable;
    private final BidQueriable bidQueriable;
    private final User bidder;
    private Auction auction;
    private double price;
    private final UserAccountManager profile;

    private PlaceBid(AuctionQueriable auctionQueriable, BidQueriable bidQueriable, UserAccountManager profile) {
        this.profile = profile;
        this.auctionQueriable = auctionQueriable;
        this.bidQueriable = bidQueriable;
        this.bidder = profile.getLoggedInUser();
    }

    private PlaceBid(AuctionQueriable auctionQueriable, BidQueriable bidQueriable, User bidder) {
        this.profile = UserAccountManager.of();
        this.auctionQueriable = auctionQueriable;
        this.bidQueriable = bidQueriable;
        this.bidder = bidder;
    }

    public static PlaceBid of() {
        HSQLDBQueryEngine db = HSQLDBFactory.get();
        return new PlaceBid(db.auctions(), db.bids(), UserAccountManager.of());
    }

    public static PlaceBid of(QueryEngine query, UserAccountManager profile) {
        return new PlaceBid(query.auctions(), query.bids(), profile);
    }

    public static PlaceBid of(QueryEngine query, User user) {
        return new PlaceBid(query.auctions(), query.bids(), user);
    }

    public PlaceBid on(String auctionId) {
        auction = auctionQueriable.findById(auctionId);
        return this;
    }

    public PlaceBid with(double price) {
        this.price = price;
        return this;
    }

    public boolean invoke() throws PlaceBidException {
        ensureAuctionValid();
        ensureNotPlacingBidOnOwnAuction();

        Bid existingBid = bidQueriable.findByUserIdANDAuctionId(bidder.id(),auction.id());
        if (existingBid != null){
            existingBid.setPrice(price);
            auction.addBid(existingBid); //update auction.minBid or throws error if bid is invalid
            return bidQueriable.update(existingBid.id(),existingBid) & auctionQueriable.update(auction.id(),auction);
        } else {
            Bid bid = new Bid(bidder, auction, price, Bid.Status.PENDING);
            auction.addBid(bid); //update auction.minBid or throws error if this bid cant be added
            return auctionQueriable.update(auction.id(),auction) && bidQueriable.add(bid);
        }
    }

    private void ensureAuctionValid() throws PlaceBidException {
        if (auction == null || auction.isCompleted())
            throw new PlaceBidException("Auction not valid");
    }

    private void ensureNotPlacingBidOnOwnAuction() throws PlaceBidException {
        if (auction.seller().id().equalsIgnoreCase(this.bidder.id()))
            throw new PlaceBidException("Can't place bid on your own auction");
    }
}

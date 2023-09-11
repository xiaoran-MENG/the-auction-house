package comp3350.group9.theauctionhouse.business.auctioning;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CannotCompleteAuctionException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;

public class AuctionCompletionService {
    private final QueryEngine query;
    private final User seller;
    private final UserAccountManager accountManager;

    public AuctionCompletionService(QueryEngine query) {
        this.query = query;
        this.accountManager = UserAccountManager.of();
        this.seller = accountManager.getLoggedInUser();
    }

    public AuctionCompletionService(QueryEngine query, UserAccountManager accManager) {
        this.query = query;
        this.accountManager = accManager;
        this.seller = accManager.getLoggedInUser();
    }

    public static AuctionCompletionService of() {
        return new AuctionCompletionService(HSQLDBFactory.get());
    }

    public void completeOldAuctions() throws CannotCompleteAuctionException {
        for (Auction a : this.query.auctions().findIncompleteExpired()) {
            String highestBid = a.bids().isEmpty() ? null : a.bids().get(0).id();
            completeAuction(a.id(),highestBid);
        }
    }

    public void completeAuction(String auctionId, String bidId) throws CannotCompleteAuctionException {
        query.auctions().findAll();
        Auction auction = query.auctions().findById(auctionId);

        if (auction.isCompleted())
            throw new CannotCompleteAuctionException("Auction is already completed");

        if (bidId == null) {
            auction.setAsComplete();
            if (!(query.auctions().update(auctionId, auction) &&
                    query.bids().updateCompletedAuctionBidStatus(auctionId,null,null,Bid.Status.NOT_PURCHASED.toString())))
                throw new CannotCompleteAuctionException("System error while updating data");
        }
        else
        {
            boolean containsBid = Flux.of(auction.bids()).by(x -> x.id().equals(bidId)).get().size() == 1;
            if (containsBid) {
                auction.setAsComplete();
                boolean result = query.auctions().update(auctionId, auction) && query.bids().updateCompletedAuctionBidStatus(
                        auctionId,
                        bidId,
                        Bid.Status.PURCHASED.toString(),
                        Bid.Status.NOT_PURCHASED.toString());
                if (!result)
                    throw new CannotCompleteAuctionException("System error while updating data");
            } else {
                throw new CannotCompleteAuctionException("No such bid for this auction");
            }
        }
    }
}

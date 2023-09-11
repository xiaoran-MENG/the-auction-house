package comp3350.group9.theauctionhouse.business.bidding;

import java.util.List;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.domain.User;

public class BidsHistory {
    private final User user;
    private final QueryEngine query;
    private final UserAccountManager accountManager;

    public BidsHistory(QueryEngine query) {
        this.query = query;
        this.accountManager = UserAccountManager.of();
        this.user = accountManager.getLoggedInUser();
    }

    public BidsHistory(QueryEngine query, UserAccountManager accManager) {
        this.query = query;
        this.accountManager = accManager;
        this.user = accManager.getLoggedInUser();
    }

    public static BidsHistory get(){
        return new BidsHistory(HSQLDBFactory.get());
    }

    public List<BidDTO> getAll() {
        return Flux.of(query.bids().findAll())
            .by(x -> x.user().id().equals(this.user.id()))
            .map(BidDTO::of)
            .get();
    }

    public List<BidDTO> getAllByAuctionId(String auctionId){
        return Flux.of(this.query.bids().findByAuctionId(auctionId)).map(BidDTO::of).get();
    }
}

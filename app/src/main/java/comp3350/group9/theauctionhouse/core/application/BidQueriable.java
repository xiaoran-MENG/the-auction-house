package comp3350.group9.theauctionhouse.core.application;

import java.util.List;

import comp3350.group9.theauctionhouse.core.domain.Bid;

public interface BidQueriable extends Queriable<Bid>{
    boolean updateCompletedAuctionBidStatus(String auctionId, String winningBidId, String winnerStatus, String loserStatus);
    Bid findByUserIdANDAuctionId(String userId, String auctionId);
    List<Bid> findByAuctionId(String auctionId);

}

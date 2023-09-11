package comp3350.group9.theauctionhouse.core.application;

import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.Product;

public interface QueryEngine {

    AuctionQueriable auctions();

    ProductQueriable products();

    BidQueriable bids();

    UserQueriable users();

    ReportQueriable reports();

    TrustFactorQueriable trustFactors();

    AuctionTagQueriable auctionTags();
}

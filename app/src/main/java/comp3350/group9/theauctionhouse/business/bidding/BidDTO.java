package comp3350.group9.theauctionhouse.business.bidding;

import java.math.BigDecimal;

import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.User;

public class BidDTO {
    public final String id;
    public final User user;
    public final BigDecimal price;
    public final Auction auction;
    public final Bid.Status status;
    public final DateTime lastUpdated;

    public BidDTO(String id, User user, Auction auction, BigDecimal price, Bid.Status status, DateTime lastUpdated){
        this.id = id;
        this.user = user;
        this.auction = auction;
        this.price = price;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    public static BidDTO of(Bid o) {
        return new BidDTO(o.id(),o.user(),o.auction(),o.price(),o.status(),o.lastUpdated());
    }
}
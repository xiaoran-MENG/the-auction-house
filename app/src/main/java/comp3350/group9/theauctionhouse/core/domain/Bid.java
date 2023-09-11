package comp3350.group9.theauctionhouse.core.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import comp3350.group9.theauctionhouse.business.common.DateTime;

public class Bid extends Entity {

    public enum Status {
        PURCHASED ("Purchased"),  // Bid is finished, purchased the product
        NOT_PURCHASED ("Sold to others"),       // Bid is finished, product was sold to someone else
        PENDING ("Bid Ongoing");    // Bid is ongoing

        String value;
        private static Map<String,Status> map = new HashMap<>();
        static {
            map.put("Purchased",PURCHASED);
            map.put("PURCHASED",PURCHASED);
            map.put("Sold to others",NOT_PURCHASED);
            map.put("NOT_PURCHASED",NOT_PURCHASED);
            map.put("Bid Ongoing",PENDING);
            map.put("PENDING",PENDING);
        }

        Status(String value) { this.value = value; }
        public static Status Status(String s) { return map.get(s); }
        @Override
        public String toString() { return this.value; }
    }

    private final User user;
    private BigDecimal price;
    private final Auction auction;
    private final Status status;
    private DateTime lastUpdated;

    public Bid(String id, User user, Auction auction, double price, Status status) {
        super(id);
        this.user = user;
        this.auction = auction;
        this.price = BigDecimal.valueOf(price);
        this.status = status;
        this.lastUpdated = DateTime.now();
    }

    public Bid(String id, User user, Auction auction, double price, Status status, DateTime lastUpdated) {
        this(id, user, auction, price, status);
        this.lastUpdated = lastUpdated;
    }

    public Bid(User user, Auction auction, double price, Status status) {
        super();
        this.user = user;
        this.auction = auction;
        this.price = BigDecimal.valueOf(price);
        this.status = status;
    }

    public User user(){
        return user;
    }

    public BigDecimal price() {
        return price;
    }

    public Auction auction() {
        return auction;
    }

    public Status status() {
        return status;
    }

    public DateTime lastUpdated() {
        return lastUpdated;
    }

    public void setPrice(double price){
        this.price = BigDecimal.valueOf(price);
    }
}

package comp3350.group9.theauctionhouse.core.domain;

import java.math.BigDecimal;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;

import java.util.*;

public class Auction extends Entity {
    private String title, description, imagePath = "";
    private User seller;
    private BigDecimal minBid;
    private DateTime expiry;
    private Product product;
    private boolean completed = false;
    private DateTime dateCreated = DateTime.now();
    private DateTime dateUpdated = DateTime.now();
    private final Set<AuctionTag> tags = new HashSet<>();
    private final TreeSet<Bid> bids = new TreeSet<>((x, y) -> y.price().compareTo(x.price()));
    private Auction() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public void addBid(Bid bid) throws PlaceBidException {
        if (!DateTime.now().beforeOrAt(expiry()))
            throw new PlaceBidException("Cannot Bid. This auction is expired");
        if (bid.price().doubleValue() < minBid())
            throw new PlaceBidException("Bidding price cannot be less than current highest bid");

        bids.add(bid);
        minBid = bid.price();
    }

    public void setAsComplete(){
        completed = true;
    }

    public String description(){
        return description;
    }

    public DateTime expiry(){
        return expiry;
    }

    public double minBid() {
        return minBid.doubleValue();
    }

    public String title() {
        return title;
    }

    public String imagePath() { return imagePath; }

    public User seller() {
        return seller;
    }

    public Product product() {
        return product;
    }

    public boolean isCompleted() { return completed; }

    public List<Bid> bids(){
        return new ArrayList<>(bids);
    }

    public List<AuctionTag> tags() {
        return new ArrayList<>(tags);
    }

    public DateTime dateCreated() {
        return dateCreated;
    }

    public static class Builder {
        private final Auction o = new Auction();

        public Builder id(String id) {
            o.id = id;
            return this;
        }

        public Builder seller(User seller) {
            o.seller = seller;
            return this;
        }

        public Builder title(String title) {
            o.title = title;
            return this;
        }

        public Builder description(String description) {
            o.description = description;
            return this;
        }

        public Builder imagePath(String path) {
            o.imagePath = path;
            return this;
        }

        public Builder minBid(double value) {
            o.minBid = BigDecimal.valueOf(value);
            return this;
        }

        public Builder expiry(DateTime expiry) {
            o.expiry = expiry;
            return this;
        }

        public Builder product(Product product) {
            o.product = product;
            return this;
        }

        public Builder completed(boolean completed){
            o.completed = completed;
            return this;
        }

        public Builder tags(List<AuctionTag> tags){
            o.tags.addAll(tags);
            return this;
        }

        public Builder bids(List<Bid> bids) {
            o.bids.addAll(bids);
            return this;
        }

        public Builder dateCreated(DateTime dateCreated){
            o.dateCreated = dateCreated;
            return this;
        }

        public Builder dateUpdated(DateTime dateUpdated){
            o.dateUpdated = dateUpdated;
            return this;
        }

        public Auction build() throws CreateAuctionException {
            ensureValid();
            return o;
        }

        public Auction forceBuild() {
            return o;
        }

        private void ensureValid() throws CreateAuctionException {
            List<String> errors = new ArrayList<>();
            if (o.minBid.doubleValue() <= 0)
                errors.add("Min bid must be greater than $0");
            if (o.title.length() == 0)
                errors.add("Empty auction title");
            if (o.expiry.beforeOrAt(DateTime.now()))
                errors.add("Expiry date must be after the current time");
            if (!errors.isEmpty())
                throw new CreateAuctionException(String.join("\n", errors));
        }
    }
}

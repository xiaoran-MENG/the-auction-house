package comp3350.group9.theauctionhouse.business.auctioning;

import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

public class AuctionInfo {
    public static class Builder {
        private final AuctionInfo o = new AuctionInfo();

        public Builder title(String title) {
            o.title = title.trim();
            return this;
        }

        public Builder description(String description) {
            o.description = description == null ? "" : description.trim();
            return this;
        }

        public Builder minBid(double value) {
            o.minBid = value;
            return this;
        }

        public Builder expiry(DateTime expiry) {
            o.expiry = expiry;
            return this;
        }

        public Builder seller(User seller) {
            o.seller = seller;
            return this;
        }

        public Builder image(String imagePath) {
            o.imagePath = imagePath;
            return this;
        }

        public Builder tags(List<AuctionTagDTO> tags) {
            o.tags.addAll(tags);
            return this;
        }

        public AuctionInfo build() throws CreateAuctionException {
            verify();
            return o;
        }

        private void verify() throws CreateAuctionException {
            List<String> errors = new ArrayList<>();
            if (o.minBid <= 0)
                errors.add("Min bid must be greater than $0");
            if (o.title.length() == 0)
                errors.add("Empty auction title");
            if (o.expiry.beforeOrAt(DateTime.now()))
                errors.add("Expiry date must be after the current time");
            if (!errors.isEmpty())
                throw new CreateAuctionException(String.join("\n", errors));
        }
    }

    private AuctionInfo() {
    }

    private String title, description;

    private String imagePath;
    private User seller;
    private double minBid;
    private DateTime expiry;
    private final List<AuctionTagDTO> tags = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public User seller() {
        return seller;
    }

    public List<AuctionTagDTO> tags(){
        return tags;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public double minBid() {
        return minBid;
    }

    public DateTime expiry() {
        return expiry;
    }

    public String imagePath() { return imagePath; }

    public Auction entitify() throws CreateAuctionException {
        return Auction.builder()
                .seller(seller)
                .title(title)
                .imagePath(imagePath)
                .description(description)
                .minBid(minBid)
                .expiry(expiry)
                .tags(Flux.of(tags).map(AuctionTagDTO::entitify).get())
                .build();
    }
}

package comp3350.group9.theauctionhouse.business.catalog;

import java.util.List;

import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.AuctionTag;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;

public class AuctionCatalogDTO {
    public final String id,  title, description;
    public final User seller;
    public final Product product;
    public final boolean complete;
    public final List<AuctionTag> tags;
    public final double minBid;
    public final DateTime expiry, dateCreated;

    private AuctionCatalogDTO(
            String id, User seller, String title, String description,
            Product product, boolean complete, List<AuctionTag> tags,
            double minBid, DateTime expiry,DateTime dateCreated) {
        this.id = id;
        this.seller = seller;
        this.title = title;
        this.description = description;
        this.product = product;
        this.complete = complete;
        this.minBid = minBid;
        this.expiry = expiry;
        this.tags = tags;
        this.dateCreated = dateCreated;
    }

    public static AuctionCatalogDTO of(Auction o) {
        return new AuctionCatalogDTO(
                o.id(),
                o.seller(),
                o.title(),
                o.description(),
                o.product(),
                o.isCompleted(),
                o.tags(),
                o.minBid(),
                o.expiry(),
                o.dateCreated());
    }

    public String id() {
        return id;
    }

    public List<AuctionTag> tags() {
        return tags;
    }
}

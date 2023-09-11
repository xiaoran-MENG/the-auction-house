package comp3350.group9.theauctionhouse.unittests.business.auctioning;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionTagDTO;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.AuctionTag;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

public class TestAuction {
    private String id, title, description;
    private Product product;
    private List<AuctionTag> tags;
    private User seller;
    private double minBid;
    private DateTime expiry;

    private TestAuction(String id, String title, String description, Product product, List<AuctionTag> tags, User seller, double minBid, DateTime expiry) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.product = product;
        this.tags = tags;
        this.seller = seller;
        this.minBid = minBid;
        this.expiry = expiry;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public Product product() {
        return product;
    }

    public List<AuctionTag> tags() {
        return this.tags;
    }

    public User seller() {
        return seller;
    }

    public double minBid() {
        return minBid;
    }

    public DateTime expiry() {
        return expiry;
    }

    public TestAuction title(String title) {
        this.title = title;
        return this;
    }

    public TestAuction id(String id) {
        this.id = id;
        return this;
    }

    public TestAuction description(String description) {
        this.description = description;
        return this;
    }

    public TestAuction product(Product product) {
        this.product = product;
        return this;
    }

    public TestAuction tags(List<AuctionTag> tags){
        this.tags = tags;
        return this;
    }

    public TestAuction minBid(double price) {
        this.minBid = price;
        return this;
    }

    public TestAuction expiry(DateTime expiry) {
        this.expiry = expiry;
        return this;
    }

    public static TestAuction of(Product p) {
        String id = UUID.randomUUID().toString();
        List<AuctionTag> tags = new ArrayList<>();
        tags.add(AuctionTag.builder().id("1").auctionId(id).tag("Laptops").build());
        tags.add(AuctionTag.builder().id("2").auctionId(id).tag("Pens").build());


        return new TestAuction(
                id,
                "Test Title",
                "Test Description",
                p,
                tags,
                new User("1",null,null,null),
                100.0,
                DateTime.now().addDays(1));
    }

    public AuctionInfo toAuctionInfo() throws CreateAuctionException {
        return AuctionInfo.builder()
                .title(title)
                .description(description)
                .minBid(minBid)
                .expiry(expiry)
                .seller(seller)
                .tags(Flux.of(tags).map(x -> new AuctionTagDTO(x.getAuctionId(), x.tag())).get())
                .build();
    }

    public Auction toEntity() throws CreateAuctionException {
        return Auction.builder()
                .id(id == null ? "1" : id)
                .title(title)
                .description(description)
                .minBid(minBid)
                .product(product)
                .tags(tags)
                .expiry(expiry)
                .seller(seller)
                .build();
    }
}

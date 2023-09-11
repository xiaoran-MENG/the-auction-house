package comp3350.group9.theauctionhouse.business.auctioning;

import comp3350.group9.theauctionhouse.core.domain.AuctionTag;

public class AuctionTagDTO {
    private final String auctionId;
    private final String tag;

    public AuctionTagDTO(String auctionId, String tag) {
        this.auctionId = auctionId;
        this.tag = tag;
    }

    public AuctionTag entitify() {
        return AuctionTag.builder()
                .auctionId(auctionId)
                .tag(tag)
                .build();
    }
}

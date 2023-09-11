package comp3350.group9.theauctionhouse.core.application;

import java.util.List;

import comp3350.group9.theauctionhouse.core.domain.AuctionTag;

public interface AuctionTagQueriable extends Queriable<AuctionTag> {

    List<AuctionTag> findByAuctionId(String auctionId);

    List<AuctionTag> findByTags(List<String> tagsToSearch);

    boolean batchInsert(List<AuctionTag> tags, String auctionId);
}

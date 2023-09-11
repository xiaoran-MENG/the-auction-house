package comp3350.group9.theauctionhouse.core.application;

import java.util.List;

import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.domain.Auction;

public interface AuctionQueriable extends Queriable<Auction> {

    List<Auction> findBySellerIdOrderedByDateCreated(String sellerId, int limit);

    List<Auction> findByTags(List<String> tagsToSearch);

    List<Auction> findByTitle(String title);

    List<Auction> findIncompleteExpired();
}

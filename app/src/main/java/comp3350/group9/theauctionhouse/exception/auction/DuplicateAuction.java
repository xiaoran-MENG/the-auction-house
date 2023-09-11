package comp3350.group9.theauctionhouse.exception.auction;

public class DuplicateAuction extends RuntimeException {
    public DuplicateAuction() {
        super("Auction with this title already exists");
    }
}

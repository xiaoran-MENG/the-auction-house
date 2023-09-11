package comp3350.group9.theauctionhouse.presentation.components.auctions.bidding;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import java.math.BigDecimal;

import comp3350.group9.theauctionhouse.business.bidding.PlaceBid;
import comp3350.group9.theauctionhouse.business.catalog.SellerCatalog;
import comp3350.group9.theauctionhouse.business.common.functional.Mono;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;

public class BiddingViewModel extends AndroidViewModel {
    private final String auctionId;
    private final SellerCatalog catalog;
    private Auction myAuction;
    private BigDecimal myBid;

    public BiddingViewModel(Application application, String id){
        super(application);
        catalog = SellerCatalog.of();
        myBid = BigDecimal.valueOf(0);
        this.auctionId = id;
        Mono<Auction> result = catalog.getById(auctionId);
        this.myAuction = result.get();
    }

    public void postBid() throws PlaceBidException{
        double price = myBid.doubleValue();
        boolean result = PlaceBid.of().on(auctionId).with(price).invoke();
        if (!result) throw new PlaceBidException("Error: Could not add bid to system");
    }

    public void forceRefreshAuction(){
        this.myAuction = catalog.getById(auctionId).get();
    }

    public Auction getAuction(){
        return this.myAuction;
    }

    public BigDecimal getBidAmt(){
        return myBid;
    }

    public void setBid(BigDecimal val){
        this.myBid = val;
    }
}
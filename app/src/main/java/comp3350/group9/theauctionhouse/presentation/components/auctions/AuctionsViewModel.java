package comp3350.group9.theauctionhouse.presentation.components.auctions;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.Catalog;

public class AuctionsViewModel extends AndroidViewModel {
    private final Catalog catalog;
    private List<AuctionCatalogDTO> auctions;
    private boolean refreshData;
    private int refreshPosition;

    public AuctionsViewModel(Application application, String authId) {
        super(application);
        catalog = Catalog.of();
        auctions = getRunningAuctions(catalog);
        refreshData = false;
    }

    public List<AuctionCatalogDTO> getAuctions() {
        if (refreshData) {
            if (refreshPosition >= 0) {
                AuctionCatalogDTO oldAuction = auctions.get(refreshPosition);
                AuctionCatalogDTO newAuction = catalog.getById(oldAuction.id).get();
                auctions.set(refreshPosition, newAuction);
            } else {
                auctions = getRunningAuctions(catalog);
            }
            refreshData = false;
        }
        return auctions;
    }

    public void setRefresh(int position) {
        this.refreshData = true;
        this.refreshPosition = position;
    }

    private List<AuctionCatalogDTO> getRunningAuctions(Catalog c){
        return new ArrayList<>(c.getAll()
                .by(x -> !x.complete).get());
    }
}
package comp3350.group9.theauctionhouse.presentation.components.auctions.bidding;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class BiddingViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String auctionId;

    public BiddingViewModelFactory(Application application, String auctionId) {
        this.mApplication = application;
        this.auctionId = auctionId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BiddingViewModel(mApplication, auctionId);
    }
}


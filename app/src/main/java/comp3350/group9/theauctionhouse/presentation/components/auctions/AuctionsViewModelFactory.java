package comp3350.group9.theauctionhouse.presentation.components.auctions;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AuctionsViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String authUserID;

    public AuctionsViewModelFactory(Application application, String authUserID) {
        this.mApplication = application;
        this.authUserID = authUserID;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AuctionsViewModel(mApplication, authUserID);
    }
}

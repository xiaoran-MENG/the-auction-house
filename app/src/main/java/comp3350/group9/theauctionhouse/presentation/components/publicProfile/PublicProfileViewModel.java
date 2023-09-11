package comp3350.group9.theauctionhouse.presentation.components.publicProfile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;

import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.Catalog;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;

// Fragments and activities are destroyed and recreated constantly (orientation change, switch views, etc) data is lost in such cases
// ViewModels should store the data of the Fragments/application, since they are not destroyed
public class PublicProfileViewModel extends AndroidViewModel {
    private final User authUser;
    private final User publicUser;
    private List<AuctionCatalogDTO> recentAuctions;

    public PublicProfileViewModel(Application application, String authUserId, String publicUserId) {
        super(application);
        authUser = UserAccountManager.of().getLoggedInUser();
        publicUser = UserAccountManager.of().getUserById(publicUserId);
        refreshRecentAuctions();
    }

    public User getAuthUser() {
        return authUser;
    }

    public User getPublicUser() {
        return publicUser;
    }

    public void refreshRecentAuctions() {
        Catalog catalog = Catalog.of();
        recentAuctions = catalog.getRecentAuctionsForSeller(publicUser.id(), 3).get();
    }

    public List<AuctionCatalogDTO> getRecentAuctions() {
        return recentAuctions;
    }

    public String getPublicUserRating(){
        // gets the Users rating
        if (publicUser != null) {
            TrustFactorManager trustManager = TrustFactorManager.get();
            TrustFactor factor = trustManager.getByUserId(publicUser.id());
            return (factor != null) ? factor.displayRating() : "No rating";
        }
        return "No rating";
    }


    // ------------- view model factory --------------//
    public static class PublicProfileViewModelFactory implements ViewModelProvider.Factory {
        private final Application mApplication;
        private final String authUserId;
        private final String publicUserId;


        public PublicProfileViewModelFactory(Application application, String authUserId, String publicUserId) {
            mApplication = application;
            this.authUserId = authUserId;
            this.publicUserId = publicUserId;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PublicProfileViewModel(mApplication, authUserId, publicUserId);
        }
    }
}
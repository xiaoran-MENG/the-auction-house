package comp3350.group9.theauctionhouse.presentation.components.profile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.domain.User;

// Fragments and activities are destroyed and recreated constantly (orientation change, switch views, etc) including data
// ViewModels should store the data of the Fragments/application, since they are not destroyed
public class ProfileViewModel extends AndroidViewModel {

    private final User userInfo;

    public ProfileViewModel(Application application, String userId) {
        super(application);
        userInfo = UserAccountManager.of().getUserById(userId);
    }

    public User getUser() {
        return userInfo;
    }

    public static class ProfileViewModelFactory implements ViewModelProvider.Factory {
        private final Application mApplication;
        private final String userId;

        public ProfileViewModelFactory(Application application, String id) {
            mApplication = application;
            userId = id;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ProfileViewModel(mApplication, userId);
        }
    }

}
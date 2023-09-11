package comp3350.group9.theauctionhouse.presentation.components.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.databinding.FragmentProfileBinding;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.presentation.components.account.LoginActivity;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String userId = getActivity().getIntent().getStringExtra("UserId");
        profileViewModel = new ViewModelProvider(this, new ProfileViewModel.ProfileViewModelFactory(
                getActivity().getApplication(), userId)).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageButton logoutBtn = binding.logoutButton;
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        logoutBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();

        });


        final Button bidHistoryBtn = binding.profileBidHistoryBtn;
        bidHistoryBtn.setOnClickListener(view -> Navigation.findNavController(getView()).navigate(R.id.action_navigation_profile_to_bidding_history));

        final Button auctionHistoryBtn = binding.profileAuctionHistoryBtn;
        auctionHistoryBtn.setOnClickListener(view -> Navigation.findNavController(getView()).navigate(R.id.action_navigation_profile_to_auction_history));

        populateProfilePage();

        return root;
    }

    private void populateProfilePage(){
        final TextView usernameTextView = binding.profileUsername;
        final TextView emailTextView = binding.profileEmail;
        final TextView ratingTextView = binding.profileUserRating;

        User user = profileViewModel.getUser();

        usernameTextView.setText(user.username());
        emailTextView.setText(user.email());

        // Sets the rating
        if (user != null) {
            TrustFactorManager trustManager = TrustFactorManager.get();
            TrustFactor factor = trustManager.getByUserId(user.id());
            if (factor != null) ratingTextView.setText(factor.displayRating());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
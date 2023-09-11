package comp3350.group9.theauctionhouse.presentation.components.publicProfile;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.accounts.ReportingManager;
import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;

import comp3350.group9.theauctionhouse.core.domain.Report;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.databinding.FragmentProfilePublicBinding;
import comp3350.group9.theauctionhouse.presentation.adapters.AuctionListAdapter;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class PublicProfileFragment extends Fragment implements RecycleItemClickListener<AuctionCatalogDTO> {
    private FragmentProfilePublicBinding binding;
    private PublicProfileViewModel viewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //id of current logged in user (auth token)
        String authUserId = getActivity().getIntent().getStringExtra("UserId");
        //id of the user whose information this page populates, passed from previous fragment
        String publicUserId = getArguments().getString("publicUserId");


        viewModel = new ViewModelProvider(this, new PublicProfileViewModel.PublicProfileViewModelFactory(
                getActivity().getApplication(), authUserId, publicUserId)).get(PublicProfileViewModel.class);

        binding = FragmentProfilePublicBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        populateProfilePage();
        binding.publicProfileReportBtn.setOnClickListener(view -> handleReportingBtn());
        binding.publicProfileRateBtn.setOnClickListener(view -> handleRatingBtn());

        AuctionListAdapter auctionAdapter =  new AuctionListAdapter(viewModel.getRecentAuctions(), getActivity() , this, true);

        RecyclerView recentAuctions = binding.publicProfileRecentAuctions;
        recentAuctions.setAdapter(auctionAdapter);
        recentAuctions.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        return root;
    }

    private void handleReportingBtn(){
        ReportWindow reportWindow = new ReportWindow(getContext(), getView(), getActivity());
        reportWindow.setReportTypes(Report.ReportTypes.getAllLabels());

        reportWindow.setReportBtnClickListener((var1, reportType, description) ->
        {
            ReportingManager rManager = ReportingManager.of();
            String reportId = rManager.makeReport(viewModel.getPublicUser().id(),reportType,description);

            if (reportId != null) Toast.makeText(getContext(), "Report Submitted for review.", Toast.LENGTH_LONG).show();
            else Toast.makeText(getContext(), "ERROR: Report failed to be submitted.", Toast.LENGTH_LONG).show();
        },true);

        reportWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
    }

    private void handleRatingBtn(){
        RatingWindow ratingWindow = new RatingWindow(getContext(), getView(), getActivity());

        ratingWindow.setRatingBtnClickListener((value) ->
        {
            if(addProfileRating(value)) Toast.makeText(getContext(), "Rating Submitted.", Toast.LENGTH_LONG).show();
            else Toast.makeText(getContext(), "Rating Not Submitted.", Toast.LENGTH_LONG).show();
            binding.publicProfileUserRating.setText(viewModel.getPublicUserRating());

        },true);

        ratingWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
    }

    // Adds a profile rating
    private boolean addProfileRating(float value) {
        User user = viewModel.getPublicUser();
        if (user != null) {
            TrustFactor newTrust = new TrustFactor(null,user.id());
            newTrust.addRating(value);

            // Stores the change in the DB
            TrustFactorManager trustManager = TrustFactorManager.get();
            return trustManager.addRating(newTrust);
        }
        return false;
    }

    private void populateProfilePage(){
        final TextView usernameTextView = binding.publicProfileUsername;
        final TextView emailTextView = binding.publicProfileEmail;
        final TextView rating = binding.publicProfileUserRating;
        usernameTextView.setText(viewModel.getPublicUser().username());
        emailTextView.setText(viewModel.getPublicUser().email());
        rating.setText(viewModel.getPublicUserRating());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickListener(RecyclerView.ViewHolder holder, AuctionCatalogDTO auction, View v, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("AuctionId", auction.id);
        Navigation.findNavController(holder.itemView).navigate(R.id.action_public_profile_to_navigation_bidding, bundle);
    }
}
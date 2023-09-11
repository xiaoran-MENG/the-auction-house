package comp3350.group9.theauctionhouse.presentation.components.auctions.bidding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.databinding.FragmentBiddingBinding;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;

public class BiddingFragment extends Fragment{
    private FragmentBiddingBinding binding;
    private BiddingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String auctionId = getArguments().getString("AuctionId");

        viewModel = new ViewModelProvider(this, new BiddingViewModelFactory(getActivity().getApplication(), auctionId)).get(BiddingViewModel.class);
        binding = FragmentBiddingBinding.inflate(inflater, container, false);

        populateUI();

        EditText bidAmt = binding.biddingPlaceBidAmt;
        bidAmt.setText(viewModel.getBidAmt().toString());

        Button placeBid = binding.biddingPlaceBidBtn;

        placeBid.setOnClickListener(view -> {
            try {
                // Ensures the bid is valid
                Editable bidAmount = binding.biddingPlaceBidAmt.getText();
                BigDecimal bidInput = BigDecimal.valueOf(Double.valueOf(bidAmount.toString()));
                viewModel.setBid(bidInput);
                viewModel.postBid();
                viewModel.forceRefreshAuction();
                populateUI();
                Toast.makeText(getContext(), "Bid placed", Toast.LENGTH_LONG).show();
            }catch (PlaceBidException e){
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (NumberFormatException n) {
                Toast.makeText(getContext(), "Bidding amount must be whole value, ex: 100", Toast.LENGTH_LONG).show();
            } catch (RuntimeException e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.forceRefreshAuction();

        if (viewModel.getAuction().isCompleted()){
            binding.biddingPlaceMyBidLayout.setVisibility(View.INVISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeKeyboard();
    }

    private void populateUI() {
        Auction auction = viewModel.getAuction();
        if (auction != null) {
            binding.biddingAuctionTitle.setText(auction.title());
            binding.biddingAuctionHighestBid.setText("Current highest bid: $" + auction.minBid());
            binding.biddingAuctionExpiry.setText("Expiry: " + auction.expiry().toString());
            binding.biddingAuctionPosted.setText("Date posted: " + auction.dateCreated().toString());
            binding.biddingAuctionProduct.setText(auction.product().name());

            if (!auction.tags().isEmpty())
                binding.biddingAuctionCategories.setText("Categories: " + String.join(", ", Flux.of(auction.tags()).map(x -> x.tag()).get()));

            File imgFile = new File(auction.imagePath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                binding.biddingAuctionImage.setImageBitmap(bitmap);
            } else {
                try {
                    InputStream stream = getContext().getAssets().open(auction.imagePath());
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    binding.biddingAuctionImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "ERROR: image could not be loaded!", Toast.LENGTH_SHORT).show();
                }
                // Attempts to find the image in our assets folder
            }

            String desc = auction.description();
            binding.biddingAuctionDescription.setText(desc);
            if (desc == null || desc.isEmpty())
                binding.biddingAuctionDescription.setVisibility(View.GONE);

            UserAccountManager manager = UserAccountManager.of();
            User user = manager.getUserById(viewModel.getAuction().seller().id());

            if (user != null) {
                TrustFactorManager trustManager = TrustFactorManager.get();
                TrustFactor factor = trustManager.getByUserId(user.id());
                if (factor != null) binding.biddingUserRating.setText(factor.displayRating());
            }
        } else {
            Toast.makeText(getContext(), "ERROR: NULL auction id!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(getView()).popBackStack();
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = getActivity().getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
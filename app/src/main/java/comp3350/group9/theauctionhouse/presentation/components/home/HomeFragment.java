package comp3350.group9.theauctionhouse.presentation.components.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.business.auctioning.AuctionCompletionService;
import comp3350.group9.theauctionhouse.business.bidding.BidDTO;
import comp3350.group9.theauctionhouse.business.bidding.BidsHistory;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.SellerCatalog;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.databinding.FragmentHomeBinding;
import comp3350.group9.theauctionhouse.exception.auction.CannotCompleteAuctionException;
import comp3350.group9.theauctionhouse.presentation.adapters.BidderListAdapter;
import comp3350.group9.theauctionhouse.presentation.adapters.MyAuctionListAdapter;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class HomeFragment extends Fragment implements RecycleItemClickListener<AuctionCatalogDTO>, BidderListAdapter.Listener {
    private FragmentHomeBinding binding;
    HomeViewModel viewModel;
    String authUserId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        authUserId = getActivity().getIntent().getStringExtra("UserId");
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Update any expired auction as completed
        try {
            AuctionCompletionService.of().completeOldAuctions();
        } catch (CannotCompleteAuctionException e) {
            Toast.makeText(getContext(),"Error updating old auctions",Toast.LENGTH_SHORT);
        }

        SellerCatalog catalog = SellerCatalog.of();
        List<AuctionCatalogDTO> runningAuctions = catalog
                .getAll()
                .by(x -> !x.complete)
                .get();
        MyAuctionListAdapter adapter = new MyAuctionListAdapter(runningAuctions, this);
        RecyclerView recyclerView = binding.homeAuctionsRecyclerView;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickListener(RecyclerView.ViewHolder holder, AuctionCatalogDTO model, View v, int position) {
        inflateRecentBids((MyAuctionListAdapter.MyAuctionViewHolder) holder);

        BidderListAdapter adapter;
        if (viewModel.isExpanded(position)) {
            adapter = new BidderListAdapter(new ArrayList<>(),model,this);

            viewModel.setAsCollapsedItem(position);
            ImageView icon = ((MyAuctionListAdapter.MyAuctionViewHolder) holder).getBinding().myAuctionViewholderRecentBidsIcon;
            icon.setImageResource(R.drawable.ic_arrow_drop_down);
        } else {
            List<BidDTO> bids = viewModel.getExpandedData(position);
            if (bids == null) bids = BidsHistory.get().getAllByAuctionId(model.id());
            adapter = new BidderListAdapter(bids,model,this);

            viewModel.setAsExpandedItem(position);
            viewModel.saveExpandedData(position, bids); //cache it
            ImageView icon = ((MyAuctionListAdapter.MyAuctionViewHolder) holder).getBinding().myAuctionViewholderRecentBidsIcon;
            icon.setImageResource(R.drawable.ic_arrow_drop_up);
        }
        RecyclerView recyclerView = holder.itemView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBidItemClickListener(View v, AuctionCatalogDTO auction, BidDTO bid) {
        TrustFactor factor = TrustFactorManager.get().getByUserId(bid.user.id());
        String rating = (factor != null) ? factor.displayRating() : "No Rating";

        DialogInterface.OnClickListener dialogListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        SellerCatalog.of().completeAuction(auction.id,bid.id);
                        resetRecyclerView();
                        Toast.makeText(getContext(), "Auction sold!", Toast.LENGTH_SHORT).show();
                    } catch (CannotCompleteAuctionException e) {
                        Toast.makeText(getContext(), "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    Bundle bundle = new Bundle();
                    bundle.putString("publicUserId", bid.user.id());
                    Navigation.findNavController(v).navigate(R.id.action_public_profile_from_home, bundle);
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format("Sell to %s (%s) for ($%.2f)?", bid.user.username(),rating,bid.price.doubleValue()))
                .setPositiveButton("Yes", dialogListener)
                .setNegativeButton("No", dialogListener)
                .setNeutralButton("See Profile", dialogListener)
                .show();
    }

    private void inflateRecentBids(MyAuctionListAdapter.MyAuctionViewHolder holder) {
        ViewStub stub = holder.itemView.findViewById(R.id.my_auction_viewholder_viewstub);
        if (stub != null) {
            stub.inflate();
            RecyclerView recyclerView = holder.itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
    }

    private void resetRecyclerView(){
        viewModel.reset();
        SellerCatalog catalog = SellerCatalog.of();
        List<AuctionCatalogDTO> runningAuctions = catalog
                .getAll()
                .by(x -> !x.complete)
                .get();
        MyAuctionListAdapter adapter = new MyAuctionListAdapter(runningAuctions, this);
        RecyclerView recyclerView = binding.homeAuctionsRecyclerView;
        recyclerView.setAdapter(adapter);
    }
}
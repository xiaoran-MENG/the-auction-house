package comp3350.group9.theauctionhouse.presentation.components.profile.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.bidding.BidDTO;
import comp3350.group9.theauctionhouse.business.bidding.BidsHistory;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.Catalog;
import comp3350.group9.theauctionhouse.databinding.GeneralRecycleViewBinding;
import comp3350.group9.theauctionhouse.presentation.adapters.BidListAdapter;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class BiddingHistoryFragment extends Fragment implements RecycleItemClickListener<BidDTO> {
    private String userId;
    private GeneralRecycleViewBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString("UserId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = GeneralRecycleViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BidsHistory hist = BidsHistory.get();

        RecyclerView recyclerView = binding.recyclerView;
        BidListAdapter adapter = new BidListAdapter(hist.getAll(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }


    @Override
    public void onItemClickListener(RecyclerView.ViewHolder holder, BidDTO model, View v, int position) {
        Bundle bundle = new Bundle();
        Catalog c = Catalog.of();
        AuctionCatalogDTO auction = c.getById(model.auction.id()).get();
        bundle.putString("AuctionId", auction.id);
        Navigation.findNavController(holder.itemView).navigate(R.id.action_bidding_history_recycle_view_to_navigation_bidding, bundle);
    }
}
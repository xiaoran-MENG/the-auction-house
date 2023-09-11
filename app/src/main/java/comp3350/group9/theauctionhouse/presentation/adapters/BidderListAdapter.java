package comp3350.group9.theauctionhouse.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import comp3350.group9.theauctionhouse.business.bidding.BidDTO;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.databinding.BidderViewholderBinding;

public class BidderListAdapter extends RecyclerView.Adapter<BidderListAdapter.BidderViewHolder> {
    public interface Listener { void onBidItemClickListener(View v, AuctionCatalogDTO auction, BidDTO bid); }
    private final List<BidDTO> bidderList;
    private AuctionCatalogDTO auctionCatalogDTO;
    private Listener listener;

    public BidderListAdapter(List<BidDTO> bidderList, AuctionCatalogDTO auction, Listener listener) {
        this.bidderList = bidderList;
        this.listener = listener;
        this.auctionCatalogDTO = auction;
    }

    @Override
    public BidderListAdapter.BidderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BidderViewholderBinding binding = BidderViewholderBinding.inflate(inflater,parent,false);
        return new BidderListAdapter.BidderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull BidderListAdapter.BidderViewHolder holder, int position) {
        TextView username = holder.binding.bidderViewholderUsername;
        TextView email = holder.binding.bidderViewholderEmail;
        TextView bidAmt = holder.binding.bidderViewholderBidamt;

        BidDTO bid = bidderList.get(position);

        username.setText(bid.user.username());
        email.setText(bid.user.email());
        bidAmt.setText("$"+bid.price.toString());

        holder.itemView.setOnClickListener(v -> listener.onBidItemClickListener(v,auctionCatalogDTO,bid));
    }

    @Override
    public int getItemCount() {
        return bidderList.size();
    }

    public static class BidderViewHolder extends RecyclerView.ViewHolder {
        private final BidderViewholderBinding binding;
        public BidderViewHolder(@NotNull BidderViewholderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public BidderViewholderBinding getBinding() {
            return binding;
        }
    }
}
package comp3350.group9.theauctionhouse.presentation.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import comp3350.group9.theauctionhouse.business.bidding.BidDTO;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.Catalog;
import comp3350.group9.theauctionhouse.databinding.ViewHolderBidBinding;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class BidListAdapter extends RecyclerView.Adapter<BidListAdapter.BidViewHolder> {

    private final List<BidDTO> bidList;
    private final RecycleItemClickListener<BidDTO> listener;

    public BidListAdapter(List<BidDTO> bidList, RecycleItemClickListener<BidDTO> listener) {
        this.bidList = bidList;
        this.listener = listener;
    }

    @Override
    public BidListAdapter.BidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderBidBinding binding = ViewHolderBidBinding.inflate(inflater,parent,false);
        return new BidListAdapter.BidViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull BidListAdapter.BidViewHolder holder, int position) {
        TextView auctionTitle = holder.getBinding().biditemAuctionTitle;
        TextView bidAmt = holder.binding.biditemBidAmt;
        TextView bidStatus = holder.binding.biditemBidStatus;
        TextView auctionDesc = holder.binding.biditemAuctionDescription;
        TextView expiry = holder.binding.biditemExpiryDate;
        TextView lastUpdated = holder.binding.biditemLastBidDate;
        TextView productDesc = holder.binding.biditemProductDesc;
        ImageButton editBtn =  holder.binding.biditemEditBtn;

        BidDTO bid = bidList.get(position);
        Catalog catalog = Catalog.of();
        AuctionCatalogDTO auction = catalog.getById(bid.auction.id()).get();

        auctionTitle.setText(auction.title);
        bidAmt.setText("BID: " + bid.price.toString());
        bidStatus.setText("Status: " + bid.status.toString());
        auctionDesc.setText("Desc: " + auction.description);
        expiry.setText("Expiry: " + auction.expiry.toString());
        lastUpdated.setText("Updated: " + bid.lastUpdated.toString());
        productDesc.setText("Product: " + auction.product.name());

        if (listener != null) {
            editBtn.setOnClickListener(view -> {
                listener.onItemClickListener(holder,bid,view,position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public static class BidViewHolder extends RecyclerView.ViewHolder {
        private final ViewHolderBidBinding binding;
        public BidViewHolder(@NotNull ViewHolderBidBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolderBidBinding getBinding() {
            return binding;
        }
    }
}
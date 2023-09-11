package comp3350.group9.theauctionhouse.presentation.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.databinding.ViewHolderAuctionBinding;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class AuctionListAdapter extends RecyclerView.Adapter<AuctionListAdapter.AuctionViewHolder>{
    private final List<AuctionCatalogDTO> auctionList;
    private final RecycleItemClickListener<AuctionCatalogDTO> itemClickListener;
    private Activity activity;
    boolean hideProfileBtn;

    public AuctionListAdapter(List<AuctionCatalogDTO> auctionList, Activity activity){
        this.auctionList = auctionList;
        this.itemClickListener = null;
        this.activity = activity;
    }

    public AuctionListAdapter(List<AuctionCatalogDTO> auctionList, Activity activity, RecycleItemClickListener<AuctionCatalogDTO> listener, boolean hideProfileBtn){
        this.auctionList = auctionList;
        this.itemClickListener = listener;
        this.hideProfileBtn = hideProfileBtn;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AuctionListAdapter.AuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderAuctionBinding binding = ViewHolderAuctionBinding.inflate(inflater,parent,false);
        return new AuctionListAdapter.AuctionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AuctionListAdapter.AuctionViewHolder holder, int position) {
        TextView title = holder.getBinding().auctionViewholderTitle;
        TextView description = holder.getBinding().auctionViewholderDescription;
        TextView price = holder.getBinding().auctionViewholderPrice;
        TextView expiry = holder.getBinding().auctionViewholderExpiry;
        TextView product = holder.getBinding().auctionViewholderProduct;
        Button publicProfileBtn = holder.getBinding().auctionViewholderProfileBtn;

        if (hideProfileBtn) publicProfileBtn.setVisibility(ViewGroup.GONE);

        AuctionCatalogDTO auction = auctionList.get(position);

        title.setText(auction.title);
        description.setText("Description: " + auction.description);
        price.setText("$" + auction.minBid);
        expiry.setText("Exp: " + auction.expiry.toString());
        product.setText("Product: "+ auction.product.name());

        if (!auction.complete)
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        else
            holder.itemView.setTag(android.R.drawable.ic_lock_lock); //for system testing

        if (this.itemClickListener != null) {
            //clicking on anywhere in the entire view
            holder.itemView.setOnClickListener(view -> itemClickListener.onItemClickListener(holder, auction, view, position));
            publicProfileBtn.setOnClickListener(view -> itemClickListener.onItemClickListener(holder, auction, publicProfileBtn, position));
        }
    }



    @Override
    public int getItemCount() {
        return auctionList.size();
    }

    public static class AuctionViewHolder extends RecyclerView.ViewHolder {
        private final ViewHolderAuctionBinding binding;

        public AuctionViewHolder(@NonNull ViewHolderAuctionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolderAuctionBinding getBinding() {
            return binding;
        }
    }
}

package comp3350.group9.theauctionhouse.presentation.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.databinding.MyAuctionViewHolderBinding;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class MyAuctionListAdapter extends RecyclerView.Adapter<MyAuctionListAdapter.MyAuctionViewHolder>{
    private final List<AuctionCatalogDTO> auctionList;
    private final RecycleItemClickListener<AuctionCatalogDTO> itemClickListener;
    public MyAuctionListAdapter(List<AuctionCatalogDTO> auctionList, RecycleItemClickListener<AuctionCatalogDTO> listener){
        this.auctionList = auctionList;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public MyAuctionListAdapter.MyAuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MyAuctionViewHolderBinding binding = MyAuctionViewHolderBinding.inflate(inflater,parent,false);
        return new MyAuctionListAdapter.MyAuctionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAuctionListAdapter.MyAuctionViewHolder holder, int position) {
        TextView title = holder.getBinding().myAuctionViewholderTitle;
        TextView description = holder.getBinding().myAuctionViewholderDescription;
        TextView expiry = holder.getBinding().myAuctionViewholderExpiry;
        TextView product = holder.getBinding().myAuctionViewholderProduct;

        AuctionCatalogDTO auction = auctionList.get(position);

        title.setText(auction.title);
        description.setText(auction.description);
        expiry.setText("Exp: " + auction.expiry.toString());
        product.setText(auction.product.name());

        if (itemClickListener != null){
            holder.binding.myAuctionViewholderRecentBidsLayout.setOnClickListener(view -> itemClickListener.onItemClickListener(holder, auction, view, position));
        }
    }

    @Override
    public int getItemCount() {
        return auctionList.size();
    }

    public static class MyAuctionViewHolder extends RecyclerView.ViewHolder {
        private final MyAuctionViewHolderBinding binding;

        public MyAuctionViewHolder(@NonNull MyAuctionViewHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public MyAuctionViewHolderBinding getBinding() {
            return binding;
        }
    }
}


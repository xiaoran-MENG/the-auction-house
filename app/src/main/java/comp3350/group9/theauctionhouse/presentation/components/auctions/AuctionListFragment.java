package comp3350.group9.theauctionhouse.presentation.components.auctions;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.auctioning.AuctionInfo;
import comp3350.group9.theauctionhouse.business.auctioning.AuctionTagDTO;
import comp3350.group9.theauctionhouse.business.auctioning.CreateAuction;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.domain.AuctionTag;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.databinding.FragmentAuctionsListBinding;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;
import comp3350.group9.theauctionhouse.presentation.adapters.AuctionListAdapter;
import comp3350.group9.theauctionhouse.presentation.interfaces.RecycleItemClickListener;

public class AuctionListFragment extends Fragment implements RecycleItemClickListener<AuctionCatalogDTO> {
    private FragmentAuctionsListBinding binding;
    private AuctionsViewModel viewModel;
    private AuctionListAdapter auctionAdapter;
    private String authUserId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        authUserId = getActivity().getIntent().getStringExtra("UserId");

        viewModel = new ViewModelProvider(this, new AuctionsViewModelFactory(getActivity().getApplication(), authUserId)).get(AuctionsViewModel.class);
        binding = FragmentAuctionsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.auctionsListAddBtn.setOnClickListener(this::handleCreateAuctionBtn);

        auctionAdapter = new AuctionListAdapter(viewModel.getAuctions(), this.getActivity(), this, false);

        RecyclerView recyclerView = binding.auctionsRecyclerView;
        recyclerView.setAdapter(auctionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (viewModel.getAuctions().size() == 0)
            binding.auctionsListNoData.setVisibility(View.VISIBLE);

        return root;
    }



    public void handleCreateAuctionBtn(View v) {
        CreateAuctionWindow createAuctionWindow = new CreateAuctionWindow(getContext(), getView(), getActivity());
        createAuctionWindow.setCategoryTags(AuctionTag.validTags);

        createAuctionWindow.setSubmitButtonClickListener((view, instance, popupBindings) ->
        {
            try {
                String auc_title = popupBindings.popupcreateauctionAuctionTitleEdittext.getText().toString();
                String auc_desc = popupBindings.popupcreateauctionAuctionDescEdittext.getText().toString();
                double price = Double.parseDouble(popupBindings.popupcreateauctionAuctionMinbidEdittext.getText().toString());
                DateTime expiry = DateTime.of(popupBindings.popupcreateauctionExpiryText.getText().toString(), "MM/dd/yyyy", null);
                User seller = UserAccountManager.of().getLoggedInUser();
                List<AuctionTag> tags = new ArrayList<>();
                String path = BaseActivity.getImagePath();

                ChipGroup chipGroup = popupBindings.popupcreateauctionChipGroup;
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.isChecked()) {
                        AuctionTag tag = AuctionTag.builder().tag(chip.getText().toString()).build();
                        tags.add(tag);
                    }
                }

                String prod_title = popupBindings.popupcreateauctionProductTitleEdittext.getText().toString();
                String prod_desc = popupBindings.popupcreateauctionProductDescEdittext.getText().toString();

                Product product = new Product(null, prod_title, prod_desc);
                AuctionInfo auction = AuctionInfo.builder()
                        .title(auc_title)
                        .description(auc_desc)
                        .minBid(price)
                        .expiry(expiry)
                        .seller(seller)
                        .image(path)
                        .tags(Flux.of(tags).map(x -> new AuctionTagDTO(x.getAuctionId(), x.tag())).get())
                        .build();

                if (CreateAuction.of().invoke(auction, product)) {
                    if (instance.isShowing()) instance.dismiss();
                    viewModel.setRefresh(-1);
                    refreshList();
                    Toast.makeText(getContext(), "Created New Auction", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Failed to Create Auction", Toast.LENGTH_LONG).show();
                }
            }catch (CreateAuctionException e) {
                if (e.getMessage().contains("Expiry date must be after the current time"))
                    popupBindings.popupcreateauctionExpiryLayout.setError("Expiry date must be after the current time");
                if (e.getMessage().contains("Min bid must be greater than $0"))
                    popupBindings.popupcreateauctionAuctionMinbidEdittext.setError("Min bid must be greater than $0");
            }catch (RuntimeException e){
                popupBindings.popupcreateauctionAuctionTitleLayout.setError(e.getMessage());
            }catch (Exception e){
                Toast.makeText(getContext(), "Failed to Create Auction, Missing fields.", Toast.LENGTH_LONG).show();
            }

        }, false);

        createAuctionWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onItemClickListener(RecyclerView.ViewHolder holder, AuctionCatalogDTO auction, View view, int position) {
        // viewholder was clicked
        if (view.getId() == holder.itemView.getId()) {
            Bundle bundle = new Bundle();
            bundle.putString("AuctionId", auction.id);
            viewModel.setRefresh(position); // refresh 1 auction list item next time we go back to this fragment
            Navigation.findNavController(holder.itemView).navigate(R.id.action_bidding_from_auction_list, bundle);
            // profile button was clicked
        } else if (view.getId() == R.id.auction_viewholder_profile_btn) {
            Bundle bundle = new Bundle();
            bundle.putString("publicUserId", auction.seller.id());
            Navigation.findNavController(view).navigate(R.id.action_public_profile_from_auction_list, bundle);
        }
    }

    private void refreshList(){
        auctionAdapter = new AuctionListAdapter(viewModel.getAuctions(), this.getActivity(), this, false);
        RecyclerView recyclerView = binding.auctionsRecyclerView;
        recyclerView.swapAdapter(auctionAdapter,false);
    }
}
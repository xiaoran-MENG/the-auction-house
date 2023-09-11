package comp3350.group9.theauctionhouse.presentation.components.profile.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.SellerCatalog;
import comp3350.group9.theauctionhouse.databinding.GeneralRecycleViewBinding;
import comp3350.group9.theauctionhouse.presentation.adapters.AuctionListAdapter;

public class AuctionHistoryFragment extends Fragment {
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

        SellerCatalog catalog = SellerCatalog.of();
        List<AuctionCatalogDTO> auctionList = catalog.getAll().get();

        RecyclerView recyclerView = binding.recyclerView;
        AuctionListAdapter adapter =  new AuctionListAdapter(auctionList,this.getActivity(),null,true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        return root;
    }
}
package comp3350.group9.theauctionhouse.presentation.components.home;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp3350.group9.theauctionhouse.business.bidding.BidDTO;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.presentation.components.account.LoginActivity;

public class HomeViewModel extends ViewModel {

    private Map<Integer, List<BidDTO>> expandedData;
    private Map<Integer,Boolean> expandedItems;

    public HomeViewModel() {
        expandedItems = new HashMap<>();
        expandedData = new HashMap<>();
    }

    public void saveExpandedData(int position, List<BidDTO> bids){
        expandedData.put(position,bids);
    }
    public List<BidDTO> getExpandedData(int position){
        return expandedData.get(position);
    }


    public void setAsExpandedItem(int position){
        expandedItems.put(position,true);
    }

    public void setAsCollapsedItem(int position){
        expandedItems.put(position,false);
    }

    public boolean isExpanded(int position){
        Boolean res = expandedItems.get(position);
        return  res != null && res;
    }

    public void reset(){
        this.expandedItems = new HashMap<>();
        this.expandedData = new HashMap<>();
    }

}
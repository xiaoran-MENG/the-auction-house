package comp3350.group9.theauctionhouse.presentation.interfaces;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface RecycleItemClickListener<T> {
    void onItemClickListener(RecyclerView.ViewHolder holder, T model, View v, int position);
}

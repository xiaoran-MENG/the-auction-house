package Comp3350.group9.theauctionhouse.common.espressoHelpers;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

public class RecycleViewAssertion implements ViewAssertion {
    private final AssertCallback callback;

    public RecycleViewAssertion(AssertCallback callback) {
        this.callback = callback;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        if (callback != null) callback.callback(recyclerView);
    }

    public interface AssertCallback{ void callback(RecyclerView recyclerView); }
}

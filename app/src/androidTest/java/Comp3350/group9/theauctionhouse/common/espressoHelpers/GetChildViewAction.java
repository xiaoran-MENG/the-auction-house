package Comp3350.group9.theauctionhouse.common.espressoHelpers;

import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class GetChildViewAction {
    private View v;

    public ViewAction viewAction(int id) {
        return new ViewAction() {
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                v = view.findViewById(id);
            }
        };
    }

    public TextView getTextView() {
        return (TextView) v;
    }

    public View getView() {
        return v;
    }
}

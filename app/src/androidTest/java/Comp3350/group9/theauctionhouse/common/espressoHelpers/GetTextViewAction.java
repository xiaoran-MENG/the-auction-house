package Comp3350.group9.theauctionhouse.common.espressoHelpers;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class GetTextViewAction {
    private TextView textView;

    public ViewAction viewAction() {
        return new ViewAction() {
            @Override
            public String getDescription() {
                return "ViewAction: getTextView";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                textView = (TextView) view;
            }
        };
    }

    public TextView getTextView() {
        return textView;
    }
}

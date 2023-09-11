package Comp3350.group9.theauctionhouse.common.espressoHelpers;

import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TextViewMatcher{
    public static Matcher<View> hasTextViewText(final String expectedText) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView)) {
                    return false;
                }

                return ((TextView) view).getText() != null
                        ? ((TextView) view).getText().toString().equals(expectedText)
                        : expectedText == null;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}

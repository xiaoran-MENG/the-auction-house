package Comp3350.group9.theauctionhouse.common.espressoHelpers;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TextInputMatcher{
    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                return ((TextInputLayout) view).getError() != null ? ((TextInputLayout) view).getError().equals(expectedErrorText)
                        : expectedErrorText == null;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}

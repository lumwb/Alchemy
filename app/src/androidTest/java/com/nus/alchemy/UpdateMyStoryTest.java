package com.nus.alchemy;

import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class UpdateMyStoryTest {
    private String stringToBetyped;

    @Rule
    public ActivityTestRule<ProfileActivity> activityRule
            = new ActivityTestRule<>(ProfileActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        stringToBetyped = "UpdatedStory";
    }


    @Test
    public void changeMyStoryTextTest() {
        // Type text and then press the button.
        onView(withId(R.id.set_profile_status))
                .perform(replaceText(stringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.update_settings_button)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.set_profile_status))
                .check(matches(isEditTextValueEqualTo(stringToBetyped)));
    }




    Matcher<View> isEditTextValueEqualTo(final String content) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Match Edit Text Value with View ID Value: " + content);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView) && !(view instanceof EditText)) {
                    return false;
                }
                if (view != null) {
                    String text;
                    if (view instanceof TextView) {
                        text =((TextView) view).getText().toString();
                    } else {
                        text =((EditText) view).getText().toString();
                    }

                    return (text.equalsIgnoreCase(content));
                }
                return false;
            }
        };
    }
}

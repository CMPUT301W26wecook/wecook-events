package com.example.wecookproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignupFlowTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSignupFlow() {
        // 1. Check if Login screen is displayed
        onView(withId(R.id.tv_title)).check(matches(withText("Login or sign up")));

        // 2. Click on signup prompt to go to Signup Details
        onView(withId(R.id.tv_signup_prompt)).perform(click());

        // 3. Check if Signup Details screen is displayed
        onView(withId(R.id.tv_screen_title)).check(matches(withText("Details")));
        onView(withId(R.id.btn_continue)).perform(click());

        // 4. Check if Signup Address screen is displayed
        onView(withId(R.id.tv_screen_title)).check(matches(withText("Address")));
        onView(withId(R.id.btn_continue)).perform(click());

        // 5. Check if MainActivity (Home) is displayed
        // We look for a view that is in activity_main.xml
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }
}

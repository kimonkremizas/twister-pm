package com.example.twisterpm;


import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.twisterpm", appContext.getPackageName());
    }


    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(AllMessagesActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testIfPostedMessageIsFoundInRecyclerViewAndNavigateToIt(){
        // random int makes sure the message is not duplicate
        Random rand = new Random();
        int randomInt = rand.nextInt(10000);
        onView(withId(R.id.newMessageEditText)).perform(typeText("This is my espresso test number " + randomInt));
        onView(withId(R.id.postNewMessageButton)).perform(click());
        onView(withId(R.id.messagesRecyclerView)).check(matches(hasDescendant(withText("This is my espresso test number " + randomInt))));
        onView(withId(R.id.messagesRecyclerView)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("This is my espresso test number " + randomInt))));
    }
}
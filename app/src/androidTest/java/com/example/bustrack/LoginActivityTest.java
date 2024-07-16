package com.example.bustrack;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private DatabaseReference mDatabase;

    @Before
    public void setUp() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Test
    public void testEmptyUsernameAndPassword() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click());
            Espresso.onView(ViewMatchers.withText("Please enter username and password"))
                    .inRoot(RootMatchers.withDecorView(
                            new TypeSafeMatcher<View>() {
                                @Override
                                protected boolean matchesSafely(View view) {
                                    return view.getWindowToken() != null;
                                }

                                @Override
                                public void describeTo(Description description) {
                                    description.appendText("is toast");
                                }
                            }
                    ))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testAdminLogin() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.editTextUsername))
                    .perform(ViewActions.typeText("Admin"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
                    .perform(ViewActions.typeText("Admin123"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click());

            // Check if AdminActivity is displayed
            Espresso.onView(ViewMatchers.withId(R.id.admin_activity_root))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testDriverLoginSuccess() {
        // Insert a test user into the Firebase database
        mDatabase.child("Driver").child("testDriver").setValue(new User("Test Driver", "Bus1", "Department1", "123456", "testDriver", "password123"));

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.editTextUsername))
                    .perform(ViewActions.typeText("testDriver"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
                    .perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.radioButtonDriver)).perform(ViewActions.click());
            Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click());

            // Check if DriverActivity is displayed
            Espresso.onView(ViewMatchers.withId(R.id.driver_activity_root))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }

        // Clean up the test user from the Firebase database
        mDatabase.child("Driver").child("testDriver").removeValue();
    }

    @Test
    public void testIncorrectPassword() {
        // Insert a test user into the Firebase database
        mDatabase.child("Student").child("testStudent").setValue(new User("Test Student", "Bus2", "Department2", "654321", "testStudent", "password123"));

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.editTextUsername))
                    .perform(ViewActions.typeText("testStudent"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
                    .perform(ViewActions.typeText("wrongPassword"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.radioButtonstudent)).perform(ViewActions.click());
            Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Incorrect password"))
                    .inRoot(RootMatchers.withDecorView(
                            new TypeSafeMatcher<View>() {
                                @Override
                                protected boolean matchesSafely(View view) {
                                    return view.getWindowToken() != null;
                                }

                                @Override
                                public void describeTo(Description description) {
                                    description.appendText("is toast");
                                }
                            }
                    ))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }

        // Clean up the test user from the Firebase database
        mDatabase.child("Student").child("testStudent").removeValue();
    }
}

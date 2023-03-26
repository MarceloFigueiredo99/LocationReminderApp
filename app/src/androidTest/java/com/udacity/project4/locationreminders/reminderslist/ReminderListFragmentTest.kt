package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.base.DataBindingViewHolder
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminder: ReminderDTO

    @Before
    fun setUp() {
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(getApplicationContext(), fakeDataSource)

        stopKoin()

        val myModule = module {
            single {
                viewModel
            }
        }

        startKoin {
            modules(listOf(myModule))
        }

        reminder = ReminderDTO(
            "title",
            "description",
            "location",
            0.001,
            0.002
        )
    }

    @Test
    fun showRemindersList() = runBlockingTest {
        // Given
        fakeDataSource.saveReminder(reminder)

        // When
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // Then
        onView(withId(R.id.noDataTextView)).check(
            ViewAssertions.matches(
                CoreMatchers.not(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.reminderssRecyclerView)).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.reminderssRecyclerView)).perform(
            RecyclerViewActions.scrollTo<DataBindingViewHolder<ReminderDataItem>>(
                hasDescendant(withText(reminder.description))
            )
        )
    }

    @Test
    fun showErrorDueToEmptyList() = runBlockingTest {
        // Given
        fakeDataSource.deleteAllReminders()

        // When
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // Then
        onView(withId(R.id.noDataTextView)).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun goToAddReminder() {
        // Given
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // When
        onView(withId(R.id.addReminderFAB)).perform(
            ViewActions.click()
        )

        // Then
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }
}

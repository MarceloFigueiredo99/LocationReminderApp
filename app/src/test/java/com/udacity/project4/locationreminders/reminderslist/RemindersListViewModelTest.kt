package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var application: Application
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var reminder: ReminderDTO

    @Before
    fun setUp() {
        fakeDataSource = FakeDataSource()
        application = ApplicationProvider.getApplicationContext()
        viewModel = RemindersListViewModel(application, fakeDataSource)

        reminder = ReminderDTO(
            "title",
            "description",
            "location",
            0.001,
            0.002
        )
    }

    @Test
    fun loadRemindersSuccessfully() = mainCoroutineRule.runBlockingTest {
        // Given
        fakeDataSource.saveReminder(reminder)

        // When
        mainCoroutineRule.pauseDispatcher()

        viewModel.loadReminders()

        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        mainCoroutineRule.resumeDispatcher()

        // Then
        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
        val listOfReminders = viewModel.remindersList.getOrAwaitValue()
        MatcherAssert.assertThat(
            listOfReminders.size, CoreMatchers.`is`(1)
        )
        MatcherAssert.assertThat(listOfReminders[0].id, `is`(reminder.id))
    }

    @Test
    fun loadRemindersUnsuccessfully() = mainCoroutineRule.runBlockingTest {
        // Given
        fakeDataSource.shouldReturnError(true)

        // When
        mainCoroutineRule.pauseDispatcher()

        viewModel.loadReminders()

        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        mainCoroutineRule.resumeDispatcher()

        // Then
        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
        MatcherAssert.assertThat(
            viewModel.showSnackBar.getOrAwaitValue(),
            `is`("Error getting reminders")
        )
    }
}

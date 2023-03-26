package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var application: Application
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var reminder: ReminderDataItem

    @Before
    fun setUp() {
        fakeDataSource = FakeDataSource()
        application = ApplicationProvider.getApplicationContext()
        viewModel = SaveReminderViewModel(application, fakeDataSource)

        // Given
        reminder = ReminderDataItem(
            "title",
            "description",
            "location",
            0.001,
            0.002
        )
    }

    @Test
    fun validateEnteredDataWithValidData() {
        // When
        val result = viewModel.validateEnteredData(reminder)

        // Then
        assertThat(result, `is`(true))
    }

    @Test
    fun validateEnteredDataWithInvalidData() {
        // Given
        val invalidReminder = ReminderDataItem(
            null,
            null,
            null,
            null,
            null
        )

        // When
        val result = viewModel.validateEnteredData(invalidReminder)

        // Then
        assertThat(result, `is`(false))
        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun saveReminder() {
        // When
        mainCoroutineRule.pauseDispatcher()

        viewModel.saveReminder(reminder)

        assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            `is`(true)
        )

        mainCoroutineRule.resumeDispatcher()

        // Then
        assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            `is`(false)
        )

        assertThat(
            viewModel.showToast.getOrAwaitValue(),
            `is`(application.getString(R.string.reminder_saved))
        )
    }
}

package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.Error
import com.udacity.project4.locationreminders.data.dto.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var reminder: ReminderDTO

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

        reminder = ReminderDTO(
            "title",
            "description",
            "location",
            0.001,
            0.002
        )
    }

    @Test
    fun saveAndGetReminders() = mainCoroutineRule.runBlockingTest {
        // Given
        remindersLocalRepository.saveReminder(reminder)

        // When
        val result = remindersLocalRepository.getReminders() as Success

        // Then
        assertThat(result.data.size, `is`(1))
        assertEquals(result.data[0].id, reminder.id)
        assertEquals(result.data[0].description, reminder.description)
        assertEquals(result.data[0].location, reminder.location)
        assertEquals(result.data[0].latitude, reminder.latitude)
        assertEquals(result.data[0].longitude, reminder.longitude)
    }

    @Test
    fun saveAndGetReminderById() = mainCoroutineRule.runBlockingTest {
        // Given
        remindersLocalRepository.saveReminder(reminder)

        // When
        val result = remindersLocalRepository.getReminder(reminder.id) as Success

        // Then
        assertEquals(result.data.id, reminder.id)
        assertEquals(result.data.description, reminder.description)
        assertEquals(result.data.location, reminder.location)
        assertEquals(result.data.latitude, reminder.latitude)
        assertEquals(result.data.longitude, reminder.longitude)
    }

    @Test
    fun getReminderByIdReturnsError() = mainCoroutineRule.runBlockingTest {
        // Given
        remindersLocalRepository.deleteAllReminders()

        // When
        val result = remindersLocalRepository.getReminder("fakeId") as Error

        // Then
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun saveAndDeleteReminders() = mainCoroutineRule.runBlockingTest {
        // Given
        remindersLocalRepository.saveReminder(reminder)

        // When
        remindersLocalRepository.deleteAllReminders()

        // Then
        val result = remindersLocalRepository.getReminders() as Success
        assertThat(result.data.size, `is`(0))
    }
}

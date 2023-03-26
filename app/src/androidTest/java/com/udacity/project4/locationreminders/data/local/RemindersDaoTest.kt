package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var reminder: ReminderDTO

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()

        reminder = ReminderDTO(
            "title",
            "description",
            "location",
            0.001,
            0.002
        )
    }

    @Test
    fun saveAndGetReminders() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminder)

        // When
        val remindersFromDb = database.reminderDao().getReminders()

        // Then
        assertThat(remindersFromDb.size, `is`(1))
        assertEquals(remindersFromDb[0].id, reminder.id)
        assertEquals(remindersFromDb[0].description, reminder.description)
        assertEquals(remindersFromDb[0].location, reminder.location)
        assertEquals(remindersFromDb[0].latitude, reminder.latitude)
        assertEquals(remindersFromDb[0].longitude, reminder.longitude)
    }

    @Test
    fun saveAndGetReminderById() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminder)

        // When
        val reminderFromDb = database.reminderDao().getReminderById(reminder.id)

        // Then
        reminderFromDb?.let {
            assertEquals(reminderFromDb.id, reminder.id)
            assertEquals(reminderFromDb.description, reminder.description)
            assertEquals(reminderFromDb.location, reminder.location)
            assertEquals(reminderFromDb.latitude, reminder.latitude)
            assertEquals(reminderFromDb.longitude, reminder.longitude)
        }
    }

    @Test
    fun deleteReminder() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminder)

        // When
        database.reminderDao().deleteAllReminders()

        // Then
        val remindersFromDb = database.reminderDao().getReminders()
        assertThat(remindersFromDb.isEmpty(), `is`(true))
    }

    @After
    fun cleanUp() = database.close()
}

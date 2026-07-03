package com.example.depi_final_project_bloodbank.ui.screens.request


import com.example.depi_final_project_bloodbank.data.repository.NotificationRepository
import com.example.depi_final_project_bloodbank.domain.model.Notification // اتأكد من المسار
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var repository: NotificationRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadNotifications should sort by createdAt descending and set loading false`() = runTest {
        // Arrange
        val notification1 = Notification(id = "1", createdAt = 1000L, isRead = false)
        val notification2 = Notification(id = "2", createdAt = 2000L, isRead = false)

        coEvery { repository.getNotifications() } returns listOf(notification1, notification2)

        // Act
        viewModel = NotificationViewModel(repository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(2, state.notifications.size)
        assertEquals("2", state.notifications[0].id) // الأحدث المفروض يكون هو الأول
        assertFalse(state.isLoading)
    }

    @Test
    fun `markAsRead should call repository and reload notifications`() = runTest {
        // Arrange
        val notification = Notification(id = "1", isRead = false)
        coEvery { repository.getNotifications() } returns listOf(notification)

        viewModel = NotificationViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.markAsRead("1")
        advanceUntilIdle()

        // Assert
        coVerify { repository.markAsRead("1") }
        coVerify(exactly = 2) { repository.getNotifications() } // اتنادت مرة في الـ Init ومرة بعد المارك
    }
}
package com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel

import com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import com.example.depi_final_project_bloodbank.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // 1. تعريف الممثلين (Mocks) والـ ViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var requestRepository: RequestRepositoryImpl
    private lateinit var viewModel: HomeViewModel

    // عشان نتحكم في الوقت بتاع الكوروتنز (المسارات الخلفية)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // بنقول للتست استخدم الـ Dispatcher بتاعنا

        // 2. بنعمل Mocks للـ Repositories (تزييف)
        userRepository = mockk(relaxed = true)
        requestRepository = mockk(relaxed = true)

        // 3. بندي الـ Mocks للـ ViewModel (الـ Dependency Injection اللي عملناه)
        viewModel = HomeViewModel(userRepository, requestRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData successfully loads user name and blood type`() = runTest {
        // Arrange: بنجهز الداتا الوهمية اللي الممثل هيرد بيها
        val mockUser = User(
            uid = "123",
            name = "Mostafa Essam",
            bloodType = "O+",
            lastDonationDate = System.currentTimeMillis() - (100L * 24 * 60 * 60 * 1000) // متبرع من 100 يوم
        )
        // بنقول للممثل: لما حد ينادي عليك بـ getCurrentUser، رد باليوزر الوهمي ده!
        coEvery { userRepository.getCurrentUser() } returns mockUser

        // Act: بنشغل الدالة اللي عايزين نختبرها
        viewModel.loadData(isRefresh = false)
        advanceUntilIdle() // بنقول للبرنامج استنى لحد ما الكوروتين يخلص شغله

        // Assert: بنتأكد إن النتيجة زي ما إحنا متوقعين بالظبط
        val currentState = viewModel.uiState.value
        assertEquals("Mostafa Essam", currentState.userName)
        assertEquals("O+", currentState.bloodType)
        assertEquals(true, currentState.isAvailableForDonation)
    }
}
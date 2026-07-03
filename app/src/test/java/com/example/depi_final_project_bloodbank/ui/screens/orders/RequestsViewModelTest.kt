package com.example.depi_final_project_bloodbank.ui.screens.orders

import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestsViewModelTest {

    private lateinit var requestRepository: RequestRepository
    private lateinit var viewModel: RequestsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 1. تزييف فايربيز عشان نتجنب إيرور (Not Mocked) بدون ما نعدل الكود الأساسي
        mockkStatic(FirebaseAuth::class)
        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns null // ده هيخلي الـ ViewModel يتخطى طلبات السيرفر ويركز في اللوجيك بس

        // 2. تزييف الـ Repository
        requestRepository = mockk(relaxed = true)

        // 3. تشغيل الـ ViewModel
        viewModel = RequestsViewModel(requestRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() // بننضف التزييف بتاع فايربيز بعد ما نخلص
    }

    @Test
    fun `setTab updates selectedTab in uiState correctly`() = runTest {
        // Act: بنجرب نغير التاب لـ COMPLETED
        viewModel.setTab(RequestStatus.COMPLETED)

        // Assert: بنتأكد إن الـ State اتحدثت صح
        val currentState = viewModel.uiState.value
        assertEquals(RequestStatus.COMPLETED, currentState.selectedTab)
    }

    @Test
    fun `toggleShowMyRequests switches boolean value correctly`() = runTest {
        // Arrange: القيمة الافتراضية بتكون false
        val initialState = viewModel.uiState.value.showMyRequests
        assertEquals(false, initialState)

        // Act: بنضغط على الزرار (برمجياً)
        viewModel.toggleShowMyRequests()

        // Assert: بنتأكد إن القيمة اتعكست وبقت true
        val newState = viewModel.uiState.value.showMyRequests
        assertEquals(true, newState)
    }
}
package com.example.depi_final_project_bloodbank.ui.screens.request


import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.data.repository.DonationRepository
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import com.example.depi_final_project_bloodbank.domain.model.User
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileViewModel
// TODO: اضبط الـ imports دي لـ User و Badge و ProfileUiState حسب مسارها عندك
// import com.example.depi_final_project_bloodbank.domain.model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var donationRepository: DonationRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        userRepository = mockk(relaxed = true)
        donationRepository = mockk(relaxed = true)

        // إيقاف وتزييف FirebaseAuth من أجل الـ Live Donations Count
        mockkStatic(FirebaseAuth::class)
        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        val mockUser = mockk<FirebaseUser>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_user_789"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // ==========================================
    // 1. اختبار جلب بيانات المستخدم بنجاح والتنسيق
    // ==========================================
    @Test
    fun `fetchUserData should update UI state successfully when user exists`() = runTest {
        // Arrange
        val currentTimestamp = System.currentTimeMillis()
        val expectedDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(currentTimestamp))

        val mockUser = User(
            name = "مهندس نصر",
            governorate = "Cairo",
            city = "Maadi",
            bloodType = "A+",
            lastDonationDate = currentTimestamp
        )
        coEvery { userRepository.getCurrentUser() } returns mockUser
        every { donationRepository.observeTotalConfirmedDonations(any()) } returns flowOf(0)

        // Act
        viewModel = ProfileViewModel(userRepository, donationRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("مهندس نصر", state.name)
        assertEquals("Cairo , Maadi", state.location)
        assertEquals("A+", state.bloodType)
        assertEquals(expectedDateStr, state.lastDonationDate)
    }

    // ==========================================
    // 2. اختبار التعامل مع مستخدم غير موجود (Null)
    // ==========================================
    @Test
    fun `fetchUserData should set fallback name when user is null`() = runTest {
        // Arrange
        coEvery { userRepository.getCurrentUser() } returns null
        every { donationRepository.observeTotalConfirmedDonations(any()) } returns flowOf(0)

        // Act
        viewModel = ProfileViewModel(userRepository, donationRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("مستخدم غير معروف", state.name)
    }

    // ==========================================
    // 3. اختبار حساب الأيام المتبقية للتبرع القادم
    // ==========================================
    @Test
    fun `calculateRemainingDays should calculate correctly when less than 90 days passed`() = runTest {
        // Arrange
        // هنفترض إن المستخدم متبرع من 10 أيام بالضبط
        val tenDaysInMillis = 10L * 24 * 60 * 60 * 1000
        val lastDonationTimestamp = System.currentTimeMillis() - tenDaysInMillis

        val mockUser = User(
            name = "أحمد",
            governorate = "Giza",
            city = "6th of October",
            bloodType = "O-",
            lastDonationDate = lastDonationTimestamp
        )
        coEvery { userRepository.getCurrentUser() } returns mockUser
        every { donationRepository.observeTotalConfirmedDonations(any()) } returns flowOf(0)

        // Act
        viewModel = ProfileViewModel(userRepository, donationRepository)
        advanceUntilIdle()

        // Assert
        // بما إنه تبرع من 10 أيام، والمدة الإجمالية المبرمجة 91 يوم، المتبقي المفروض يكون حوالي 81 يوم
        val state = viewModel.uiState.value
        assertTrue(state.nextAppointmentDays in 80..81)
    }

    // ==========================================
    // 4. اختبار الـ Live Stream لعدد التبرعات (Flow)
    // ==========================================
    @Test
    fun `observeLiveDonationsCount should stream and update total donations dynamically`() = runTest {
        // Arrange
        coEvery { userRepository.getCurrentUser() } returns null

        // استخدام MutableStateFlow لمحاكاة تحديث البيانات الحي المباشر من الفايربيز
        val liveDonationsFlow = MutableStateFlow(3)
        every { donationRepository.observeTotalConfirmedDonations("test_user_789") } returns liveDonationsFlow

        // Act
        viewModel = ProfileViewModel(userRepository, donationRepository)
        advanceUntilIdle()

        // Assert الأولي: نتأكد إنه قرأ الرقم 3
        assertEquals(3, viewModel.uiState.value.totalDonations)

        // تحديث الرقم بشكل حي إلى 12 لمعرفة هل الـ UI State هتلقط التغيير تلقائياً أم لا
        liveDonationsFlow.value = 12
        advanceUntilIdle()

        // Assert النهائي
        assertEquals(12, viewModel.uiState.value.totalDonations)
    }

    // ==========================================
    // 5. اختبار حساب الشارات (Badges) بناءً على التبرعات
    // ==========================================
    @Test
    fun `calculateBadges should include expert badge when donations are 10 or more`() = runTest {
        // Arrange
        coEvery { userRepository.getCurrentUser() } returns null
        // مستخدم مخلص تبرع 15 مرة
        every { donationRepository.observeTotalConfirmedDonations(any()) } returns flowOf(15)

        // Act
        viewModel = ProfileViewModel(userRepository, donationRepository)
        advanceUntilIdle()

        // Assert: التأكد من لقط الـ Badges المناسبة (life و expert و star الافتراضية)
        val badges = viewModel.uiState.value.badges

        val hasLifeSaver = badges.any { it.type == "life" }
        val hasExpert = badges.any { it.type == "expert" }
        val hasStar = badges.any { it.type == "star" }

        assertTrue(hasLifeSaver)
        assertTrue(hasExpert)
        assertTrue(hasStar)
        assertEquals(3, badges.size)
    }
}
package com.example.depi_final_project_bloodbank.ui.screens.request


import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestViewModelTest {

    private lateinit var repository: RequestRepository
    private lateinit var viewModel: RequestViewModel

    // ده عشان نظبط مسار الـ Coroutines في بيئة الاختبار
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // --- إيقاف فايربيز Auth ---
        mockkStatic(FirebaseAuth::class)
        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        val mockUser = mockk<FirebaseUser>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_user_123"

        // --- إيقاف فايربيز Firestore بالكامل (بدون استخدام Looper) ---
        mockkStatic(FirebaseFirestore::class)
        val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        // 1. بنعمل Task وهمي
        val mockTask = mockk<Task<Void>>(relaxed = true)

        // 2. بنخلي الفايرستور يرجع الـ Task الوهمي ده لما يعمل set
        every { mockFirestore.collection(any()).document(any()).set(any()) } returns mockTask

        // 3. الخدعة القاضية: بنبرمج الـ Task الوهمي إنه ينفذ كود النجاح فوراً
        every { mockTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<Void>>()
            listener.onSuccess(null)
            mockTask // بنرجعه تاني عشان دالة addOnFailureListener متضربش Null
        }
        // -------------------------------------------------------------

        viewModel = RequestViewModel(repository)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() // تنظيف النسخ المزيفة
    }

    // ==========================================
    // الاختبار الأول: التأكد إن دالة updateRequest بتحدث الداتا وتمسح أي خطأ قديم
    // ==========================================
    @Test
    fun `updateRequest should update request state and clear error`() = runTest {
        // 1. Arrange (التجهيز):
        // بنعمل داتا جديدة (مثلاً بنعدل فصيلة الدم لـ AB+)
        val newRequest = BloodRequest(bloodType = "AB+")

        // 2. Act (التنفيذ):
        // بنستدعي الدالة اللي عايزين نختبرها
        viewModel.updateRequest(newRequest)

        // 3. Assert (التحقق):
        // بنتأكد إن فصيلة الدم اتحدثت فعلاً، وإن الإيرور رجع null
        assertEquals("AB+", viewModel.request.value.bloodType)
        assertNull(viewModel.error.value)
    }
    // ==========================================
    // الاختبار التاني: التأكد من فشل النشر لو مفيش لوكيشن (GPS)
    // ==========================================
    @Test
    fun `publish request without location should set LOCATION_REQUIRED error`() = runTest {
        // 1. Arrange: بنجهز داتا (طلب سليم من حيث البيانات بس مفيش لوكيشن)
        val validRequestButNoLocation = BloodRequest(
            hospitalName = "مستشفى السلام",
            governorate = "Cairo",
            city = "Maadi",
            contactPhone = "01012345678"
        )
        viewModel.updateRequest(validRequestButNoLocation)

        // ملحوظة: إحنا هنا مستدعيناش دالة fetchCurrentLocation
        // فبالتالي الـ locationSuccess هيفضل بـ false (الوضع الافتراضي)

        // 2. Act: بنحاول ننشر الطلب
        viewModel.publish()

        // 3. Assert: بنتأكد إن الـ ViewModel طلع إيرور اللوكيشن ومكملش رفع للفايربيز
        assertEquals("LOCATION_REQUIRED", viewModel.error.value)
    }
    // ==========================================
    // الاختبار التالت: ظهور خطأ REQUIRED لو في حقل إجباري فاضي
    // ==========================================
    @Test
    fun `publish request with empty hospital name should set REQUIRED error`() = runTest {
        // 1. Arrange: بنجهز الداتا
        // الخدعة (Reflection): بنوصل للمتغير الـ private اللي اسمه _locationSuccess ونخليه true بالقوة
        val locationField = RequestViewModel::class.java.getDeclaredField("_locationSuccess")
        locationField.isAccessible = true
        (locationField.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>).value = true

        // بنعمل داتا فيها كل حاجة سليمة ما عدا "اسم المستشفى"
        val requestWithEmptyHospital = BloodRequest(
            hospitalName = "", // الحقل ده فاضي
            governorate = "Cairo",
            city = "Maadi",
            contactPhone = "01012345678"
        )
        viewModel.updateRequest(requestWithEmptyHospital)

        // 2. Act: بنحاول ننشر
        viewModel.publish()

        // 3. Assert: بنتأكد إن الإيرور اللي رجع هو REQUIRED مش LOCATION_REQUIRED
        assertEquals("REQUIRED", viewModel.error.value)
    }
    // ==========================================
    // الاختبار الرابع: التأكد من فشل النشر لو رقم التليفون غير صالح (أقل من 11 رقم)
    // ==========================================
    @Test
    fun `publish request with invalid phone should set INVALID_PHONE error`() = runTest {
        // 1. Arrange: بنجهز الداتا
        // الخدعة عشان نعدي فحص اللوكيشن
        val locationField = RequestViewModel::class.java.getDeclaredField("_locationSuccess")
        locationField.isAccessible = true
        (locationField.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>).value = true

        // بنعمل داتا سليمة بالكامل ما عدا رقم التليفون هنخليه قصير (غلط)
        val requestWithInvalidPhone = BloodRequest(
            hospitalName = "مستشفى دار الفؤاد",
            governorate = "Giza",
            city = "6th of October",
            contactPhone = "010123" // المشكلة هنا: أقل من 11 رقم
        )
        viewModel.updateRequest(requestWithInvalidPhone)

        // 2. Act: بنحاول ننشر
        viewModel.publish()

        // 3. Assert: بنتأكد إن الإيرور اللي رجع هو INVALID_PHONE
        assertEquals("INVALID_PHONE", viewModel.error.value)
    }
    // ==========================================
    // الاختبار الخامس والأخير: النجاح التام ونشر الطلب
    // ==========================================
    @Test
    fun `publish valid request should succeed and update state`() = runTest {
        // 1. Arrange: التجهيز
        val locationField = RequestViewModel::class.java.getDeclaredField("_locationSuccess")
        locationField.isAccessible = true
        (locationField.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>).value = true

        val validRequest = BloodRequest(
            hospitalName = "مستشفى السلام",
            governorate = "Cairo",
            city = "Maadi",
            contactPhone = "01012345678",
            bloodType = "O+",
            unitsNeeded = 2
        )
        viewModel.updateRequest(validRequest)

        coEvery { repository.createRequest(any()) } returns Result.success(true)
        // 2. Act: التنفيذ
        viewModel.publish()

        // ✨ السحر هنا: بنجبر التيست يستنى الـ Coroutine لحد ما يخلص ✨
        advanceUntilIdle()

        // 3. Assert: التحقق
        assertNull(viewModel.error.value)
        assertTrue(viewModel.isSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
    }
}

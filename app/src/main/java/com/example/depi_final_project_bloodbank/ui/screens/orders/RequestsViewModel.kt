package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class RequestFeedback {
    object None : RequestFeedback()
    object Success : RequestFeedback()
    object WrongBloodType : RequestFeedback()
    object RequestClosed : RequestFeedback()
    object ActionPending : RequestFeedback()
    data class Error(val message: String) : RequestFeedback()
}

data class RequestUiModel(
    val request: BloodRequest,
    val isOwner: Boolean,
    val buttonText: String,
    val isButtonEnabled: Boolean,
    val isDonating: Boolean,
    val isDonateButtonEnabled: Boolean = false
)

data class RequestsUiState(
    val orders: List<BloodRequest> = emptyList(),
    val selectedTab: RequestStatus = RequestStatus.ACTIVE,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val donatingRequestIds: Set<String> = emptySet(),
    val activeActionIds: Set<String> = emptySet(),
    val managingRequest: BloodRequest? = null,
    val feedback: RequestFeedback = RequestFeedback.None,
    val showMyRequests: Boolean = false,
    val userCity: String = ""
) {
    fun getUiModels(currentUserId: String?): List<RequestUiModel> {
        val THREE_DAYS_MS = 3L * 24 * 60 * 60 * 1000
        val currentTime = System.currentTimeMillis()

        val hasActivePending = orders.any { order ->
            order.donationLog.any { it.donorId == currentUserId && it.status == DonationStatus.PENDING }
        }

        return orders.map { order ->
            // Automatic Expiry Logic
            if (order.status == RequestStatus.ACTIVE && (currentTime - order.createdAt) > THREE_DAYS_MS) {
                order.copy(status = RequestStatus.EXPIRED)
            } else order
        }.filter { order ->
            val matchesSearch = if (searchQuery.isBlank()) true
            else order.bloodType.contains(searchQuery, ignoreCase = true)
            if (!matchesSearch) return@filter false

            if (!showMyRequests) {
                // Requests Feed: Only ACTIVE + Same City + Created by others
                order.createdBy != currentUserId &&
                order.status == RequestStatus.ACTIVE &&
                (userCity.isEmpty() || order.city == userCity)
            } else {
                // My Activity: Created by current user AND matches tab
                order.createdBy == currentUserId && order.status == selectedTab
            }
        }.sortedByDescending { it.createdAt }.map { order ->
            val isOwner = currentUserId == order.createdBy
            val hasDonated = order.donorIds.contains(currentUserId)
            val isDonating = donatingRequestIds.contains(order.id)
            val isFull = order.unitsReserved >= order.unitsNeeded
            val isRequestCancelled = order.status == RequestStatus.CANCELLED
            val isRequestCompleted = order.status == RequestStatus.COMPLETED
            val rawStatus = order.status.name
            val (btnText, btnEnabled) = when {
                // 1. الأولوية للحالات النهائية (لازم تظهر كدة ومقفولة)
                isRequestCancelled -> "Request Cancelled" to false
                isRequestCompleted -> "Request Completed" to false

                // 2. المالك (لو الطلب نشط فقط)
                isOwner -> "Manage Request" to true

                // 3. حالات التبرع
                hasDonated -> "Donated" to false
                hasActivePending -> "Action Locked" to false

                // 4. الحالات الأخرى
                order.status != RequestStatus.ACTIVE -> order.status.name to false
                isFull -> "Full" to false
                isDonating -> "..." to false
                else -> "Donate Now" to true
            }

            RequestUiModel(
                request = order,
                isOwner = isOwner,
                buttonText = btnText,
                isButtonEnabled = btnEnabled,
                isDonating = isDonating
            )
        }
    }
}

class RequestsViewModel(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val userSnapshot = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUserUid)
                    .get()
                    .await()
                val city = userSnapshot.getString("city") ?: ""
                _uiState.update { it.copy(userCity = city) }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            requestRepository.getAllRequests()
                .distinctUntilChanged()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { realOrders ->
                    realOrders.forEach { order ->
                        if (order.status == RequestStatus.CANCELLED) {
                            val myPending = order.donationLog.find { it.donorId == currentUserUid && it.status == DonationStatus.PENDING }
                            if (myPending != null) {
                                launch { requestRepository.cancelDonation(myPending.id, order.id) }
                            }
                        }
                    }
                    _uiState.update { it.copy(
                        orders = realOrders,
                        isLoading = false,
                        isRefreshing = false
                    ) }
                }
        }
    }

    fun donateToRequest(request: BloodRequest) {
        val currentUserUid = auth.currentUser?.uid ?: return

        val isCancelled = request.status == RequestStatus.CANCELLED

        if (isCancelled) {
            _uiState.update { it.copy(feedback = RequestFeedback.Error("This request has been cancelled by the owner.")) }
            return
        }

        val hasPending = _uiState.value.orders.any { order ->
            order.donationLog.any { it.donorId == currentUserUid && it.status == DonationStatus.PENDING }
        }

        if (hasPending) {
            _uiState.update { it.copy(feedback = RequestFeedback.Error("You already have a pending donation!")) }
            return
        }

        // 1. Strict One-Unit Rule Check
        if (request.donorIds.contains(currentUserUid)) {
            _uiState.update { it.copy(feedback = RequestFeedback.Error("You have already donated to this request.")) }
            return
        }

        if (_uiState.value.donatingRequestIds.contains(request.id)) return
        if (request.unitsReserved >= request.unitsNeeded) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(donatingRequestIds = it.donatingRequestIds + request.id) }

                val userSnapshot = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUserUid)
                    .get()
                    .await()

                // 2. 90-Day Eligibility Check
                val lastDonationDate = userSnapshot.getLong("lastDonationDate") ?: 0L
                val ninetyDaysInMillis = 90L * 24 * 60 * 60 * 1000L
                if (System.currentTimeMillis() - lastDonationDate < ninetyDaysInMillis) {
                    _uiState.update { it.copy(feedback = RequestFeedback.Error("You must wait 90 days.")) }
                    return@launch
                }

                // 3. Blood Type Validation Check
                val donorBloodType = userSnapshot.getString("bloodType") ?: ""
                if (donorBloodType.isEmpty() || !isBloodCompatible(donorBloodType, request.bloodType)) {
                    _uiState.update { it.copy(feedback = RequestFeedback.WrongBloodType) }
                    return@launch
                }

                // Optimistic UI Update (Instant units increment)
                val previousOrders = _uiState.value.orders
                val updatedOrders = previousOrders.map { order ->
                    if (order.id == request.id) {
                        val newReserved = order.unitsReserved + 1
                        val newDonorIds = order.donorIds + currentUserUid
                        order.copy(
                            unitsReserved = newReserved,
                            donorIds = newDonorIds,
                            // Fix Flickering: Keep status consistent with server during donation.
                            // Global sync (status change) only happens upon owner confirmation.
                            status = order.status
                        )
                    } else order
                }
                _uiState.update { it.copy(orders = updatedOrders) }

                val donorName = userSnapshot.getString("name") ?: "Anonymous"

                val result = requestRepository.safeIncrementReservedUnits(
                    id = request.id,
                    donorId = currentUserUid,
                    donorName = donorName,
                    donorBloodType = donorBloodType
                )

                if (result.isSuccess) {
                    _uiState.update { it.copy(feedback = RequestFeedback.Success) }
                } else {
                    // Rollback on failure
                    _uiState.update { it.copy(orders = previousOrders) }
                    val errorMsg = result.exceptionOrNull()?.message
                    val feedback = when (errorMsg) {
                        "COMPATIBILITY_ERROR" -> RequestFeedback.WrongBloodType
                        "Limit reached" -> RequestFeedback.RequestClosed
                        else -> RequestFeedback.ActionPending
                    }
                    _uiState.update { it.copy(feedback = feedback) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(feedback = RequestFeedback.Error(e.message ?: "Network error")) }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(donatingRequestIds = _uiState.value.donatingRequestIds - request.id) }
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedback = RequestFeedback.None) }
    }

    fun openManageRequest(request: BloodRequest) {
        _uiState.update { it.copy(managingRequest = request) }
    }

    fun closeManageRequest() {
        _uiState.update { it.copy(managingRequest = null) }
    }

    fun confirmDonationDelivery(donationId: String, requestId: String) {
        // Optimistic UI Update
        val previousOrders = _uiState.value.orders

        val updatedOrders = previousOrders.map { order ->
            if (order.id == requestId) {
                val updatedLog = order.donationLog.map { entry ->
                    if (entry.id == donationId) entry.copy(status = DonationStatus.CONFIRMED)
                    else entry
                }
                val newConfirmed = updatedLog.count { it.status == DonationStatus.CONFIRMED }
                val newReserved = updatedLog.count { it.status == DonationStatus.PENDING || it.status == DonationStatus.CONFIRMED }
                order.copy(
                    donationLog = updatedLog,
                    unitsConfirmed = newConfirmed,
                    unitsReserved = newReserved,
                    status = if (newConfirmed >= order.unitsNeeded) RequestStatus.COMPLETED else order.status
                )
            } else order
        }

        _uiState.update { it.copy(
            orders = updatedOrders,
            managingRequest = updatedOrders.find { o -> o.id == requestId }
        ) }

        viewModelScope.launch {
            val result = requestRepository.confirmDonation(donationId, requestId)
            if (result.isSuccess) {
                _uiState.update { it.copy(feedback = RequestFeedback.Success) }
            } else {
                // Rollback on failure
                _uiState.update { it.copy(
                    orders = previousOrders,
                    managingRequest = previousOrders.find { o -> o.id == requestId },
                    feedback = RequestFeedback.Error("Failed to confirm")
                ) }
            }
        }
    }

    fun updateUnitsNeeded(requestId: String, newUnits: Int) {
        viewModelScope.launch {
            val result = requestRepository.updateUnitsNeeded(requestId, newUnits)
            if (result.isSuccess) {
                _uiState.update { it.copy(
                    managingRequest = it.managingRequest?.copy(unitsNeeded = newUnits),
                    feedback = RequestFeedback.Success
                ) }
            }
        }
    }

    fun cancelRequest(requestId: String) {
        // Atomic Local Update for instant UI response and force switch tab
        _uiState.update { currentState ->
            val updatedOrders = currentState.orders.map { order ->
                if (order.id == requestId) order.copy(status = RequestStatus.CANCELLED) else order
            }
            currentState.copy(
                orders = updatedOrders,
                selectedTab = RequestStatus.CANCELLED
            )
        }
        viewModelScope.launch {
            val result = requestRepository.cancelRequest(requestId)
            if (!result.isSuccess) {
                _uiState.update { it.copy(feedback = RequestFeedback.Error("Failed to cancel request")) }
            }
        }
    }

    fun setTab(status: RequestStatus) {
        _uiState.update { it.copy(selectedTab = status) }
    }

    fun toggleShowMyRequests() {
        _uiState.update { it.copy(showMyRequests = !it.showMyRequests) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refreshOrders() {
        observeOrders()
    }

    private fun isBloodCompatible(donor: String, patient: String): Boolean {
        val d = donor.trim().uppercase()
        val p = patient.trim().uppercase()

        if (d.isEmpty() || p.isEmpty()) return false
        if (d == p) return true
        if (d == "O-") return true
        if (d == "O+") {
            return p == "A+" || p == "B+" || p == "AB+"
        }
        if (d == "A-") {
            return p == "A+" || p == "AB-" || p == "AB+"
        }
        if (d == "A+") {
            return p == "AB+"
        }
        if (d == "B-") {
            return p == "B+" || p == "AB-" || p == "AB+"
        }
        if (d == "B+") {
            return p == "AB+"
        }
        if (d == "AB-") {
            return p == "AB+"
        }
        if (d == "AB+") {
            return false
        }

        return false
    }
}

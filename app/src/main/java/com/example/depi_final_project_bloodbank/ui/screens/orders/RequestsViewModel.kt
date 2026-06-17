package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.model.DonationLogEntry
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
    val isDonating: Boolean
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
    val feedback: RequestFeedback = RequestFeedback.None
) {
    fun getUiModels(currentUserId: String?): List<RequestUiModel> {
        return orders.filter { order ->
            val matchesTab = order.status == selectedTab
            val matchesSearch = if (searchQuery.isBlank()) true 
            else order.bloodType.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesSearch
        }.map { order ->
            val isOwner = currentUserId == order.createdBy
            val isDonating = donatingRequestIds.contains(order.id)
            
            val (btnText, btnEnabled) = when {
                isOwner -> "Manage Request" to true
                order.status != RequestStatus.ACTIVE -> order.status.name to false
                order.unitsConfirmed >= order.unitsNeeded -> "Full" to false
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

            requestRepository.getAllRequests()
                .distinctUntilChanged()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { realOrders ->
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

        if (_uiState.value.donatingRequestIds.contains(request.id)) return
        if (request.unitsReserved >= request.unitsNeeded) return

        // Optimistic UI Update
        val previousOrders = _uiState.value.orders
        val updatedOrders = previousOrders.map { order ->
            if (order.id == request.id) {
                val newReserved = order.unitsReserved + 1
                order.copy(
                    unitsReserved = newReserved,
                    status = if (order.unitsConfirmed + (newReserved - order.unitsReserved) >= order.unitsNeeded) RequestStatus.COMPLETED else order.status
                )
            } else order
        }
        _uiState.update { it.copy(
            orders = updatedOrders,
            donatingRequestIds = it.donatingRequestIds + request.id
        ) }

        viewModelScope.launch {
            try {
                val userSnapshot = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUserUid)
                    .get()
                    .await()

                val donorBloodType = userSnapshot.getString("bloodType") ?: ""
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
                // Rollback on failure
                _uiState.update { it.copy(orders = previousOrders, feedback = RequestFeedback.Error(e.message ?: "Network error")) }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(donatingRequestIds = _uiState.value.donatingRequestIds - request.id) }
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedback = RequestFeedback.None) }
    }

    fun cancelDonation(donationId: String, requestId: String) {
        // 1. Action Locking: Prevent double-clicks
        if (_uiState.value.activeActionIds.contains(donationId)) return
        
        _uiState.update { it.copy(activeActionIds = it.activeActionIds + donationId) }

        // 2. Optimistic UI Update
        val previousOrders = _uiState.value.orders
        val updatedOrders = previousOrders.map { order ->
            if (order.id == requestId) {
                val updatedLog = order.donationLog.map { entry ->
                    if (entry.id == donationId) entry.copy(status = DonationStatus.CANCELLED)
                    else entry
                }
                order.copy(
                    donationLog = updatedLog,
                    unitsReserved = (order.unitsReserved - 1).coerceAtLeast(0)
                )
            } else order
        }
        _uiState.update { it.copy(orders = updatedOrders) }

        viewModelScope.launch {
            try {
                // 3. Batch Update
                val result = requestRepository.cancelDonation(donationId, requestId)
                if (!result.isSuccess) {
                    // Rollback on failure
                    _uiState.update { it.copy(orders = previousOrders) }
                    _uiState.update { it.copy(feedback = RequestFeedback.Error(result.exceptionOrNull()?.message ?: "Failed")) }
                } else {
                    _uiState.update { it.copy(feedback = RequestFeedback.Success) }
                }
            } catch (e: Exception) {
                // Rollback on failure
                _uiState.update { it.copy(orders = previousOrders, feedback = RequestFeedback.Error(e.message ?: "Error")) }
                e.printStackTrace()
            } finally {
                // 4. Release Lock
                _uiState.update { it.copy(activeActionIds = it.activeActionIds - donationId) }
            }
        }
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
                order.copy(
                    donationLog = updatedLog,
                    unitsConfirmed = newConfirmed,
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
        viewModelScope.launch {
            val result = requestRepository.cancelRequest(requestId)
            if (result.isSuccess) {
                _uiState.update { it.copy(feedback = RequestFeedback.Success) }
            } else {
                _uiState.update { it.copy(feedback = RequestFeedback.Error("Failed to cancel request")) }
            }
        }
    }

    fun setTab(status: RequestStatus) {
        _uiState.update { it.copy(selectedTab = status) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refreshOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}

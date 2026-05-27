package com.example.depi_final_project_bloodbank.ui.screens.orders

import com.example.depi_final_project_bloodbank.domain.model.BloodReq
import com.example.depi_final_project_bloodbank.domain.model.RequestStatus

data class RequestsUiState(
    val orders: List<BloodReq> = emptyList(),
    val selectedTab: RequestStatus = RequestStatus.IN_PROGRESS,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
) {
    val filteredOrders: List<BloodReq>
        get() = orders.filter { order ->
            val matchesTab = when (selectedTab) {
                RequestStatus.IN_PROGRESS -> order.status == RequestStatus.IN_PROGRESS || order.status == RequestStatus.URGENT
                else -> order.status == selectedTab
            }
            
            // Search by Blood Type primarily since Hospital names are now localized resource IDs
            val matchesSearch = if (searchQuery.isBlank()) true 
            else order.bloodType.contains(searchQuery, ignoreCase = true)

            matchesTab && matchesSearch
        }
}

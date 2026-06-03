package com.example.depi_final_project_bloodbank.ui.screens.orders

import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus


/*
 * TODO: خلي بالك يا اندرو من هنا
 * RequestPriority (NORMAL / URGENT) will be implemented later.
 * Current filtering is based only on RequestStatus.
 */
data class RequestsUiState(
    val orders: List<BloodRequest> = emptyList(),
    val selectedTab: RequestStatus = RequestStatus.ACTIVE,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
) {
    val filteredOrders: List<BloodRequest>
        get() = orders.filter { order ->
            //مؤقتا
            val matchesTab = order.status == selectedTab
                /*when (selectedTab) {
                RequestStatus.IN_PROGRESS ->
                    order.status == RequestStatus.IN_PROGRESS || order.status == RequestStatus.URGENT
                else -> order.status == selectedTab
            }*/
            
            // Search by Blood Type primarily since Hospital names are now localized resource IDs
            val matchesSearch = if (searchQuery.isBlank()) true 
            else order.bloodType.contains(searchQuery, ignoreCase = true)

            matchesTab && matchesSearch
        }
}

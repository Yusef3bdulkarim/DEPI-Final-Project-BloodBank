package com.example.depi_final_project_bloodbank.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.FirebaseNotificationRepository
import com.example.depi_final_project_bloodbank.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val repository: NotificationRepository = FirebaseNotificationRepository()
    private val _uiState = MutableStateFlow(NotificationUiState())

    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val notifications = repository.getNotifications()
            _uiState.value = _uiState.value.copy(
                notifications = notifications,
                isLoading = false
            )
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
            loadNotifications()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            repository.markAllAsRead()
            loadNotifications()
        }
    }
}
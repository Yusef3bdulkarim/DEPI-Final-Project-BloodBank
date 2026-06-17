package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.EmptyStateLayout
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.FilterTabs
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.OrderCard
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.RequestDetailsBottomSheet
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(navController: NavController, vm: RequestsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val uiModels = remember(state.orders, state.selectedTab, state.searchQuery, state.donatingRequestIds) {
        state.getUiModels(currentUserId)
    }

    LaunchedEffect(state.selectedTab) {
        if (uiModels.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // --- Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    if (isSearchActive) {
                        TextField(
                            value = state.searchQuery,
                            onValueChange = { vm.onSearchQueryChanged(it) },
                            placeholder = { Text(stringResource(R.string.search_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                            )
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.orders_list),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) vm.onSearchQueryChanged("")
                }) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.SearchOff else Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // --- Tabs ---
            FilterTabs(
                selected = state.selectedTab,
                onSelected = { vm.setTab(it) }
            )

            // --- List ---
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { vm.refreshOrders() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiModels.isEmpty()) {
                    EmptyStateLayout(
                        query = state.searchQuery,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiModels, key = { it.request.id }) { uiModel ->
                            OrderCard(
                                uiModel = uiModel,
                                onViewDetailsClicked = { vm.openManageRequest(it) },
                                onDonateClicked = {
                                    if (uiModel.isOwner) {
                                        navController.navigate("manage_request/${uiModel.request.id}")
                                    } else {
                                        vm.donateToRequest(uiModel.request)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- Custom Feedback Overlay (Professional Feel) ---
        if (state.feedback != RequestFeedback.None) {
            val (bgColor, textColor, messageId) = when (state.feedback) {
                is RequestFeedback.Success -> Triple(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary, R.string.donation_success)
                is RequestFeedback.WrongBloodType -> Triple(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError, R.string.incompatible_blood_type)
                is RequestFeedback.RequestClosed -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, R.string.error_request_full)
                is RequestFeedback.ActionPending -> Triple(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, R.string.request_pending_warning)
                else -> Triple(MaterialTheme.colorScheme.surface.copy(alpha = 0f), MaterialTheme.colorScheme.surface.copy(alpha = 0f), null)
            }

            if (messageId != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = bgColor,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = stringResource(messageId),
                        color = textColor,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                LaunchedEffect(state.feedback) {
                    kotlinx.coroutines.delay(3000)
                    vm.clearFeedback()
                }
            }
        }
    }

    // --- Request Details Bottom Sheet (Read-Only) ---
    if (state.managingRequest != null) {
        RequestDetailsBottomSheet(
            request = state.managingRequest!!,
            onDismiss = { vm.closeManageRequest() }
        )
    }
}

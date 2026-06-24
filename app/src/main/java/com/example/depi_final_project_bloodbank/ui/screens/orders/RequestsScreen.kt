package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.EmptyStateLayout
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.FilterTabs
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.OrderCard
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.RequestDetailsBottomSheet
import com.example.depi_final_project_bloodbank.ui.theme.MaroonPrimary
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(navController: NavController, vm: RequestsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid


    val uiModels = remember(state.orders, state.selectedTab, state.searchQuery, state.showMyRequests, state.donatingRequestIds) {
        state.getUiModels(currentUserId)
    }

    LaunchedEffect(state.selectedTab, state.showMyRequests) {
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
                        tint = MaroonPrimary
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
                            placeholder = { 
                                Text(
                                    text = stringResource(R.string.search_placeholder),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    } else {
                        Text(
                            text = if (state.showMyRequests) stringResource(R.string.my_activity) else stringResource(R.string.requests_feed),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaroonPrimary,
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
                        tint = MaroonPrimary
                    )
                }
            }

            // --- Toggle Section (Segmented Button) ---
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = !state.showMyRequests,
                    onClick = { if (state.showMyRequests) vm.toggleShowMyRequests() },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaroonPrimary,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = MaroonPrimary
                    ),
                    label = { Text(stringResource(R.string.requests_feed),
                        style = MaterialTheme.typography.bodyLarge,) }
                )
                SegmentedButton(
                    selected = state.showMyRequests,
                    onClick = { if (!state.showMyRequests) vm.toggleShowMyRequests() },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaroonPrimary,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = MaroonPrimary
                    ),
                    label = { Text(stringResource(R.string.my_activity),
                        style = MaterialTheme.typography.bodyLarge,) }
                )
            }

            // --- Status Tabs (Only visible in My Activity mode) ---
            AnimatedVisibility(
                visible = state.showMyRequests,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FilterTabs(
                    selected = state.selectedTab,
                    onSelected = { vm.setTab(it) }
                )
            }

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
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Strict tab-based filtering
                        val displayList = uiModels.filter { it.request.status == state.selectedTab }

                        items(displayList, key = { it.request.id }) { uiModel ->
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

        // --- Custom Feedback Overlay ---
        if (state.feedback != RequestFeedback.None) {
            val (bgColor, textColor, messageId) = when (state.feedback) {
                is RequestFeedback.Success -> Triple(Color(0xFF4CAF50), Color.White, R.string.donation_success)
                is RequestFeedback.WrongBloodType -> Triple(MaterialTheme.colorScheme.error, Color.White, R.string.incompatible_blood_type)
                is RequestFeedback.RequestClosed -> Triple(Color(0xFFFF9800), Color.White, R.string.error_request_full)
                is RequestFeedback.ActionPending -> Triple(Color(0xFFFBC02D), Color.Black, R.string.request_pending_warning)
                else -> Triple(Color.Transparent, Color.Transparent, null)
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

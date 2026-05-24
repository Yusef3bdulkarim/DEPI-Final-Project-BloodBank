package com.example.depi_final_project_bloodbank.ui.screens.requests
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(vm: RequestsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<BloodRequest?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Top Bar / Header Section
            Surface(
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* Implement Back Navigation */ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
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
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                    )
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.orders_list),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(onClick = {
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) vm.onSearchQueryChanged("")
                        }) {
                            Icon(
                                if (isSearchActive) Icons.Default.SearchOff else Icons.Default.Search,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    FilterTabs(
                        selected = state.selectedTab,
                        onSelected = { vm.setTab(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { vm.refreshOrders() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val filteredOrders = state.filteredOrders

            if (filteredOrders.isEmpty()) {
                EmptyStateLayout(
                    query = state.searchQuery,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Main Content List / LazyColumn
                val listState = rememberLazyListState()
                LaunchedEffect(state.selectedTab) {
                    listState.animateScrollToItem(0)
                }
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onViewDetailsClicked = { selectedRequest = it },
                            onDonateClicked = { /* Implementation Signal */ }
                        )
                    }
                }
            }
        }
    }

    if (selectedRequest != null) {
        RequestDetailsBottomSheet(
            request = selectedRequest!!,
            onDismiss = { selectedRequest = null }
        )
    }
}
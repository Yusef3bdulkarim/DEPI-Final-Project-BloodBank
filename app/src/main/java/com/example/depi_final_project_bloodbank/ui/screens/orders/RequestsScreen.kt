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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.EmptyStateLayout
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.FilterTabs
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.OrderCard
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.RequestDetailsBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(vm: RequestsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<BloodRequest?>(null) }

    // 🎯 تم تحويل الشاشة لـ Column صافي زي النوتيفيكيشن بالظبط لتوحيد الارتفاع والحجم ومسح الـ Scaffold الداخلي
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // الهيدر المتطابق بالسنتيمتر مع النوتيفيكيشن
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Implement Back Navigation */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.primary // لون المارون الموحد
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
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                } else {
                    Text(
                        text = "Orders List",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary // لون المارون الموحد للتايتل
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

        // الـ Tabs تحت الـ Row مباشرة بدون أي فواصل
        FilterTabs(
            selected = state.selectedTab,
            onSelected = { vm.setTab(it) }
        )

        // سيكشن الـ List والـ PullToRefresh
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { vm.refreshOrders() },
            modifier = Modifier.fillMaxSize()
        ) {
            val filteredOrders = state.filteredOrders

            if (filteredOrders.isEmpty()) {
                EmptyStateLayout(
                    query = state.searchQuery,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
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
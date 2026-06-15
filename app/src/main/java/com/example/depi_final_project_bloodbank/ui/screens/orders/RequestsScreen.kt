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
    // 1. جمع الحالة من الـ ViewModel
    val state by vm.uiState.collectAsState()
    
    // 2. الحالات المحلية للشاشة (UI Logic State)
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<BloodRequest?>(null) }
    
    // 3. تعريف الـ Scroll State في المستوى الأعلى لضمان استقراره عبر الـ Re-compositions
    val listState = rememberLazyListState()

    // 4. تصفية الطلبات باستخدام remember لضمان عدم إعادة حساب الفلترة إلا عند تغير الحالة فعلياً
    // هذا يمنع إنشاء Instance جديد للقائمة في كل Re-composition، مما يمنع الحلقات المفرغة (Infinite Loops)
    val filteredOrders = remember(state.orders, state.selectedTab, state.searchQuery) {
        state.filteredOrders
    }

    // 5. التحكم في السكرول عند تغيير التاب (Side Effect محكوم ولا يسبب كتابة في قاعدة البيانات)
    LaunchedEffect(state.selectedTab) {
        if (filteredOrders.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- الهيدر (Header Section) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Back navigation logic */ }) {
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
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                } else {
                    Text(
                        text = "Orders List",
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

        // --- التابات (Filter Tabs Section) ---
        FilterTabs(
            selected = state.selectedTab,
            onSelected = { vm.setTab(it) }
        )

        // --- القائمة والـ PullToRefresh ---
        // يتم العرض فقط ولا توجد أي عمليات كتابة آلية هنا
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { vm.refreshOrders() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredOrders.isEmpty()) {
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
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            isDonating = state.donatingRequestIds.contains(order.id),
                            onViewDetailsClicked = { selectedRequest = it },
                            onDonateClicked = {
                                // دالة التبرع يتم استدعاؤها فقط عند ضغط المستخدم الفعلي
                                vm.donateToRequest(order)
                            }
                        )
                    }
                }
            }
        }
    }

    // --- عرض تفاصيل الطلب (BottomSheet) ---
    if (selectedRequest != null) {
        RequestDetailsBottomSheet(
            request = selectedRequest!!,
            onDismiss = { selectedRequest = null }
        )
    }
}

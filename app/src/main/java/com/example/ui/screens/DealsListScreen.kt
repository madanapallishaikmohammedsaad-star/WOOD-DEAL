package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DealWithDetails
import com.example.ui.WoodDealViewModel
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalBrownDark
import com.example.ui.theme.NaturalBrownMedium
import com.example.ui.theme.NaturalBrownPrimary
import com.example.ui.theme.NaturalClayLight
import com.example.ui.theme.NaturalWoodDeep
import com.example.ui.theme.NeutralSubCard
import com.example.ui.theme.PendingYellowBg
import com.example.ui.theme.PendingYellowText
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenContainer
import com.example.ui.theme.SalesBlue
import com.example.ui.theme.TextSecondary
import com.example.util.CurrencyUtils

enum class DealFilterStatus {
    ALL, IN_PROGRESS, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsListScreen(
    viewModel: WoodDealViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNewDeal: () -> Unit,
    onSelectDeal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val allDeals by viewModel.allDeals.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(DealFilterStatus.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredDeals = remember(allDeals, selectedFilter, searchQuery) {
        allDeals.filter { item ->
            val matchesFilter = when (selectedFilter) {
                DealFilterStatus.ALL -> true
                DealFilterStatus.IN_PROGRESS -> !item.deal.isCompleted
                DealFilterStatus.COMPLETED -> item.deal.isCompleted
            }
            val matchesSearch = searchQuery.isBlank() ||
                    item.deal.name.contains(searchQuery, ignoreCase = true) ||
                    item.deal.location.contains(searchQuery, ignoreCase = true) ||
                    item.partners.any { it.name.contains(searchQuery, ignoreCase = true) }

            matchesFilter && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "📋 MY DEALS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = NaturalBrownDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NaturalBrownDark
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = NaturalBrownDark
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewDeal,
                containerColor = NaturalBrownPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("fab_new_deal")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Deal")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("NEW DEAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NaturalBackground)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search deal name, location, or partner...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NaturalBrownMedium) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaturalBrownPrimary,
                    unfocusedBorderColor = NaturalBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == DealFilterStatus.ALL,
                    onClick = { selectedFilter = DealFilterStatus.ALL },
                    label = { Text("All (${allDeals.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NaturalBrownPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                val inProgressCount = allDeals.count { !it.deal.isCompleted }
                FilterChip(
                    selected = selectedFilter == DealFilterStatus.IN_PROGRESS,
                    onClick = { selectedFilter = DealFilterStatus.IN_PROGRESS },
                    label = { Text("🟡 In Progress ($inProgressCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NaturalBrownDark,
                        selectedLabelColor = Color.White
                    )
                )

                val completedCount = allDeals.count { it.deal.isCompleted }
                FilterChip(
                    selected = selectedFilter == DealFilterStatus.COMPLETED,
                    onClick = { selectedFilter = DealFilterStatus.COMPLETED },
                    label = { Text("🟢 Complete ($completedCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ProfitGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredDeals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪵", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching deals found" else "No deals here yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NaturalWoodDeep
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap '➕ NEW DEAL' to start your first demolition deal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredDeals, key = { it.deal.id }) { dealWithDetails ->
                        DealCardItem(
                            dealWithDetails = dealWithDetails,
                            onClick = {
                                viewModel.selectDeal(dealWithDetails.deal.id)
                                onSelectDeal(dealWithDetails.deal.id)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DealCardItem(
    dealWithDetails: DealWithDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deal = dealWithDetails.deal
    val isCompleted = deal.isCompleted
    val isProfit = dealWithDetails.isProfitable

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            NaturalBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Deal Name + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deal.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = NaturalWoodDeep
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = NaturalBrownMedium,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${deal.location} • ${deal.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isCompleted) ProfitGreenContainer else PendingYellowBg
                ) {
                    Text(
                        text = if (isCompleted) "🟢 COMPLETE" else "🟡 IN PROGRESS",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isCompleted) ProfitGreen else PendingYellowText
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Financial Summary Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = NeutralSubCard
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "TOTAL COST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = TextSecondary
                        )
                        Text(
                            text = CurrencyUtils.formatRupee(dealWithDetails.totalCost),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ProfitGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "SALES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = TextSecondary
                        )
                        Text(
                            text = CurrencyUtils.formatRupee(dealWithDetails.totalSales),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SalesBlue
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isCompleted) "PROFIT" else "PROFIT SO FAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (isProfit) ProfitGreen else NaturalBrownMedium
                        )
                        Text(
                            text = CurrencyUtils.formatRupee(dealWithDetails.profit),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isProfit) ProfitGreen else NaturalBrownDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Partners & Stock Info Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val partnerNames = dealWithDetails.partners.joinToString(", ") { it.name }.ifEmpty { "No partners added" }
                Text(
                    text = "👥 $partnerNames",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = NaturalWoodDeep,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "🪵 ${dealWithDetails.stocks.size} Items • 💸 ${dealWithDetails.expenses.size} Exp",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

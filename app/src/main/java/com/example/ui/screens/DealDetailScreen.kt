package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealSale
import com.example.data.model.DealStock
import com.example.data.model.DealWithDetails
import com.example.ui.WoodDealViewModel
import com.example.ui.components.CameraOrGalleryPicker
import com.example.ui.components.PartnerTabularAnalysisCard
import com.example.ui.components.PhotoViewerDialog
import com.example.util.CurrencyUtils
import com.example.util.PdfExportUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DealDetailScreen(
    viewModel: WoodDealViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dealWithDetails by viewModel.selectedDeal.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sales & Profit, 1: Expenses, 2: Partners, 3: Wood Stock

    // Dialog States
    var showAddSaleDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddPartnerDialog by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showDeleteDealDialog by remember { mutableStateOf(false) }
    var showShareChoiceDialog by remember { mutableStateOf(false) }
    var viewPhotoPath by remember { mutableStateOf<String?>(null) }
    var saleToReceiveCash by remember { mutableStateOf<DealSale?>(null) }
    var isSummaryExpanded by remember { mutableStateOf(false) }

    if (viewPhotoPath != null) {
        PhotoViewerDialog(
            photoPath = viewPhotoPath!!,
            title = "Bill / Slip Photo",
            onDismiss = { viewPhotoPath = null }
        )
    }

    if (dealWithDetails == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Deal not found", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val current = dealWithDetails!!
    val deal = current.deal
    val isCompleted = deal.isCompleted

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = deal.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "${deal.location} • ${deal.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showShareChoiceDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF059669)
                        )
                    }
                    IconButton(onClick = { showDeleteDealDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Deal",
                            tint = Color(0xFFDC2626)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Deal Summary Card (Compact & Expandable)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Status row + Complete Deal Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCompleted) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isCompleted) "🟢 DEAL COMPLETE" else "🟡 DEAL NOT FINISHED",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isCompleted) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.toggleDealCompleted(deal.id, !isCompleted)
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) Color(0xFFF59E0B) else Color(0xFF059669)
                            ),
                            modifier = Modifier.testTag("toggle_complete_deal_btn")
                        ) {
                            Text(
                                text = if (isCompleted) "Mark In-Progress" else "Mark Complete ✓",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Financial Grid: Compact summary
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Total Cost", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(CurrencyUtils.formatRupee(current.totalCost), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF78350F))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (isCompleted) "Total Sales" else "Sales So Far", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(CurrencyUtils.formatRupee(current.totalSales), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0284C7))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(if (isCompleted) "Final Profit" else "Profit So Far", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(
                                        CurrencyUtils.formatRupee(current.profit),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        ),
                                        color = if (current.isProfitable) Color(0xFF065F46) else Color(0xFF991B1B)
                                    )
                                }
                            }

                            if (isSummaryExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Purchase Price:", style = MaterialTheme.typography.bodySmall)
                                    Text(CurrencyUtils.formatRupee(deal.purchasePrice), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Other Expenses (${current.expenses.size}):", style = MaterialTheme.typography.bodySmall)
                                    Text(CurrencyUtils.formatRupee(current.totalExpenses), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFFDC2626))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Customer Pending:", style = MaterialTheme.typography.bodySmall)
                                    Text(CurrencyUtils.formatRupee(current.totalPending), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFFEA580C))
                                }
                            }
                        }
                    }

                    // Toggle expand button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSummaryExpanded = !isSummaryExpanded }
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSummaryExpanded) "▲ Hide Breakdown" else "▼ Show Details",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Tabs for Deal Sections
            val tabs = listOf(
                "💰 SALES & PROFIT",
                "💸 EXPENSES (${current.expenses.size})",
                "👥 PARTNERS (${current.partners.size})",
                "🪵 WOOD STOCK (${current.stocks.size})"
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Tab Content with weight(1f) so LazyColumn gets exact scroll bounds
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> SalesAndProfitTab(
                        dealWithDetails = current,
                        onAddSale = { showAddSaleDialog = true },
                        onDeleteSale = { viewModel.deleteSale(it) },
                        onReceiveCash = { saleToReceiveCash = it },
                        onViewPhoto = { viewPhotoPath = it }
                    )
                    1 -> ExpensesTab(
                        expenses = current.expenses,
                        onAddExpense = { showAddExpenseDialog = true },
                        onDeleteExpense = { viewModel.deleteExpense(it) },
                        onViewPhoto = { viewPhotoPath = it }
                    )
                    2 -> PartnersTab(
                        dealWithDetails = current,
                        onAddPartner = { showAddPartnerDialog = true },
                        onDeletePartner = { viewModel.deletePartner(it) }
                    )
                    3 -> WoodStockTab(
                        stocks = current.stocks,
                        onAddStock = { showAddStockDialog = true },
                        onToggleSold = { viewModel.toggleStockSold(it) },
                        onDeleteStock = { viewModel.deleteStock(it) },
                        onViewPhoto = { viewPhotoPath = it }
                    )
                }
            }
        }
    }

    // 1. ADD SALE DIALOG
    if (showAddSaleDialog) {
        var saleItemName by remember { mutableStateOf("") }
        var saleQuantity by remember { mutableStateOf("") }
        var customerName by remember { mutableStateOf("") }
        var customerPhone by remember { mutableStateOf("") }
        var sellingPriceInput by remember { mutableStateOf("") }
        var amountReceivedInput by remember { mutableStateOf("") }
        var salePhotoPath by remember { mutableStateOf<String?>(null) }

        val sellingPrice = sellingPriceInput.toDoubleOrNull() ?: 0.0
        val amountReceived = amountReceivedInput.toDoubleOrNull() ?: 0.0
        val pendingAmount = (sellingPrice - amountReceived).coerceAtLeast(0.0)

        AlertDialog(
            onDismissRequest = { showAddSaleDialog = false },
            title = {
                Text("💰 Add Sale", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = saleItemName,
                        onValueChange = { saleItemName = it },
                        label = { Text("What was sold? *") },
                        placeholder = { Text("e.g. 5 Pillars & 2 Doors, Beams Lot") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("sale_item_name_input")
                    )

                    OutlinedTextField(
                        value = saleQuantity,
                        onValueChange = { saleQuantity = it },
                        label = { Text("Quantity (Optional)") },
                        placeholder = { Text("e.g. 5 pcs, 2 loads") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            placeholder = { Text("e.g. Ramesh") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = sellingPriceInput,
                        onValueChange = {
                            sellingPriceInput = it
                            if (amountReceivedInput.isEmpty()) {
                                amountReceivedInput = it
                            }
                        },
                        label = { Text("Selling Price (₹) *") },
                        placeholder = { Text("e.g. 50000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("sale_price_input")
                    )

                    OutlinedTextField(
                        value = amountReceivedInput,
                        onValueChange = { amountReceivedInput = it },
                        label = { Text("Amount Received (₹) *") },
                        placeholder = { Text("e.g. 40000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = Color(0xFF059669)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("sale_received_input")
                    )

                    if (pendingAmount > 0) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.dp, Color(0xFFFED7AA))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pending Balance from Customer:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    CurrencyUtils.formatRupee(pendingAmount),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEA580C)
                                )
                            }
                        }
                    }

                    CameraOrGalleryPicker(
                        photoPath = salePhotoPath,
                        onPhotoSelected = { salePhotoPath = it },
                        label = "Bill / Delivery Slip Photo"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (saleItemName.isNotBlank() && sellingPrice > 0) {
                            viewModel.addSale(
                                dealId = deal.id,
                                itemName = saleItemName.trim(),
                                quantity = saleQuantity.trim(),
                                customerName = customerName.trim().ifEmpty { "Cash Customer" },
                                customerPhone = customerPhone.trim(),
                                sellingPrice = sellingPrice,
                                amountReceived = amountReceived,
                                photoUri = salePhotoPath,
                                date = CurrencyUtils.getTodayFormatted()
                            )
                            showAddSaleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    modifier = Modifier.testTag("sale_confirm_btn")
                ) {
                    Text("RECORD SALE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddSaleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. ADD EXPENSE DIALOG
    if (showAddExpenseDialog) {
        val expenseCategories = listOf(
            Pair("Transport 🚚", "🚚"),
            Pair("Labour 👷", "👷"),
            Pair("Loading 🏗️", "🏗️"),
            Pair("Cutting 🪚", "🪚"),
            Pair("Other 📦", "📦")
        )
        var selectedCategory by remember { mutableStateOf(expenseCategories.first()) }
        var expenseName by remember { mutableStateOf(selectedCategory.first.substringBefore(" ")) }
        var expenseAmount by remember { mutableStateOf("") }
        var expensePhotoPath by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddExpenseDialog = false },
            title = {
                Text("💸 Add Expense", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Quick Category:", style = MaterialTheme.typography.labelMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        expenseCategories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = cat
                                    expenseName = cat.first.substringBefore(" ")
                                },
                                label = { Text(cat.first, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFED7AA)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = expenseName,
                        onValueChange = { expenseName = it },
                        label = { Text("Expense Details / Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("expense_dialog_name")
                    )

                    OutlinedTextField(
                        value = expenseAmount,
                        onValueChange = { expenseAmount = it },
                        label = { Text("Amount (₹) *") },
                        placeholder = { Text("e.g. 3000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("expense_dialog_amount")
                    )

                    CameraOrGalleryPicker(
                        photoPath = expensePhotoPath,
                        onPhotoSelected = { expensePhotoPath = it },
                        label = "Slip / Bill Photo"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = expenseAmount.toDoubleOrNull() ?: 0.0
                        if (expenseName.isNotBlank() && amount > 0) {
                            viewModel.addExpense(
                                dealId = deal.id,
                                name = expenseName.trim(),
                                categoryIcon = selectedCategory.second,
                                amount = amount,
                                photoUri = expensePhotoPath,
                                date = CurrencyUtils.getTodayFormatted()
                            )
                            showAddExpenseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    modifier = Modifier.testTag("expense_dialog_confirm")
                ) {
                    Text("ADD EXPENSE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddExpenseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. ADD PARTNER DIALOG
    if (showAddPartnerDialog) {
        var partnerName by remember { mutableStateOf("") }
        var partnerAmount by remember { mutableStateOf("") }
        var partnerPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPartnerDialog = false },
            title = {
                Text("➕ Add Partner to this Deal", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "This partner is specific to '${deal.name}' and will share profits according to their investment %.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF0369A1)
                    )

                    OutlinedTextField(
                        value = partnerName,
                        onValueChange = { partnerName = it },
                        label = { Text("Person Name *") },
                        placeholder = { Text("e.g. Ahmed") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = partnerAmount,
                        onValueChange = { partnerAmount = it },
                        label = { Text("Amount Invested (₹) *") },
                        placeholder = { Text("e.g. 50000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = partnerPhone,
                        onValueChange = { partnerPhone = it },
                        label = { Text("Phone Number (Optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = partnerAmount.toDoubleOrNull() ?: 0.0
                        if (partnerName.isNotBlank() && amount > 0) {
                            viewModel.addPartner(
                                dealId = deal.id,
                                name = partnerName.trim(),
                                amount = amount,
                                phone = partnerPhone.trim()
                            )
                            showAddPartnerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("ADD PARTNER", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddPartnerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. ADD WOOD STOCK DIALOG
    if (showAddStockDialog) {
        val stockPresets = listOf(
            "Pillar 🪵", "Beam 🪵", "Door 🚪", "Window 🪟", "Plank 🪵", "Scrap ⚙️", "Other 📦"
        )
        var selectedStockCategory by remember { mutableStateOf(stockPresets.first()) }
        var stockItemName by remember { mutableStateOf(selectedStockCategory.substringBefore(" ")) }
        var stockQuantity by remember { mutableStateOf("") }
        var stockPhotoPath by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddStockDialog = false },
            title = {
                Text("🪵 Add Wood Stock", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Category:", style = MaterialTheme.typography.labelMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        stockPresets.forEach { preset ->
                            FilterChip(
                                selected = selectedStockCategory == preset,
                                onClick = {
                                    selectedStockCategory = preset
                                    stockItemName = preset.substringBefore(" ")
                                },
                                label = { Text(preset, fontSize = 13.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = stockItemName,
                        onValueChange = { stockItemName = it },
                        label = { Text("Item Name *") },
                        placeholder = { Text("e.g. Teak Pillars, Main Door") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stockQuantity,
                        onValueChange = { stockQuantity = it },
                        label = { Text("Quantity") },
                        placeholder = { Text("e.g. 8 pieces, 1 load") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CameraOrGalleryPicker(
                        photoPath = stockPhotoPath,
                        onPhotoSelected = { stockPhotoPath = it },
                        label = "Wood Photo"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (stockItemName.isNotBlank()) {
                            viewModel.addStock(
                                dealId = deal.id,
                                category = selectedStockCategory,
                                itemName = stockItemName.trim(),
                                quantity = stockQuantity.trim().ifEmpty { "1" },
                                photoUri = stockPhotoPath
                            )
                            showAddStockDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F))
                ) {
                    Text("ADD STOCK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddStockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 5. RECEIVE CASH PAYMENT FOR SALE DIALOG
    if (saleToReceiveCash != null) {
        val sale = saleToReceiveCash!!
        var cashAmountInput by remember { mutableStateOf(sale.amountPending.toString().replace(".0", "")) }

        AlertDialog(
            onDismissRequest = { saleToReceiveCash = null },
            title = {
                Text("💵 Collect Cash Payment", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sale: ${sale.itemName} (${sale.customerName})",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Currently Pending: ${CurrencyUtils.formatRupee(sale.amountPending)}",
                        color = Color(0xFFEA580C),
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = cashAmountInput,
                        onValueChange = { cashAmountInput = it },
                        label = { Text("Cash Received Now (₹)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = Color(0xFF059669)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cash = cashAmountInput.toDoubleOrNull() ?: 0.0
                        if (cash > 0) {
                            viewModel.updateSaleReceived(sale, cash)
                            saleToReceiveCash = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Text("SAVE PAYMENT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { saleToReceiveCash = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. DELETE DEAL CONFIRMATION DIALOG
    if (showDeleteDealDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDealDialog = false },
            title = { Text("Delete this deal?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete '${deal.name}'? All related partners, expenses, stock, and sales will be removed.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeal(deal)
                        showDeleteDealDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("DELETE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDealDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 7. SHARE & EXPORT OPTIONS DIALOG
    if (showShareChoiceDialog) {
        AlertDialog(
            onDismissRequest = { showShareChoiceDialog = false },
            title = {
                Text("📤 Share & Export Deal", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose your preferred export format:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    // Option 1: PDF Document Export
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showShareChoiceDialog = false
                                val pdfFile = PdfExportUtils.exportDealToPdf(context, current)
                                if (pdfFile != null) {
                                    PdfExportUtils.sharePdf(context, pdfFile, "Share Deal Report PDF")
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7)),
                        border = BorderStroke(1.5.dp, Color(0xFFFF5722))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📄", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Export as PDF Document",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD84315)
                                )
                                Text(
                                    text = "Print-ready A4 PDF with partner table, wood stock, sales & receipts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE64A19)
                                )
                            }
                        }
                    }

                    // Option 2: Tabular Analysis via WhatsApp
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showShareChoiceDialog = false
                                val shareText = CurrencyUtils.generatePartnerTabularWhatsApp(current)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Partner Analysis Table")
                                context.startActivity(shareIntent)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = BorderStroke(1.5.dp, Color(0xFF25D366))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📊", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Partner Analysis Table (WhatsApp)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "Tabular columns: Partner | Invested | % | Profit | Received",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    // Option 3: Full Deal Summary
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showShareChoiceDialog = false
                                val shareText = CurrencyUtils.generateWhatsAppDealSummary(current)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Deal Report via WhatsApp")
                                context.startActivity(shareIntent)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📋", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Full Deal Summary (Text)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723)
                                )
                                Text(
                                    text = "Comprehensive costs, sales, stock, and expenses summary",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showShareChoiceDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// ---------------------- TAB 1: SALES & PROFIT ----------------------
@Composable
private fun SalesAndProfitTab(
    dealWithDetails: DealWithDetails,
    onAddSale: () -> Unit,
    onDeleteSale: (DealSale) -> Unit,
    onReceiveCash: (DealSale) -> Unit,
    onViewPhoto: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Big ADD SALE Button
        item {
            Button(
                onClick = onAddSale,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("big_add_sale_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text("💰 ➕ ADD SALE", fontWeight = FontWeight.Black, fontSize = 17.sp)
            }
        }

        // Section: PARTNER INVESTMENT & PROFIT ANALYSIS TABLE
        item {
            PartnerTabularAnalysisCard(dealWithDetails = dealWithDetails)
        }

        // Section: SALES LIST
        item {
            Text(
                text = "SALES RECORDED (${dealWithDetails.sales.size})",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (dealWithDetails.sales.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "No sales recorded yet. Tap '➕ ADD SALE' above whenever timber, doors, or scrap is sold.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(dealWithDetails.sales, key = { it.id }) { sale ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sale.itemName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (sale.customerName.isNotBlank()) {
                                    Text(
                                        text = "Buyer: ${sale.customerName} ${if (sale.customerPhone.isNotBlank()) "(${sale.customerPhone})" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                                Text(
                                    text = "Date: ${sale.date} • Qty: ${sale.quantity.ifEmpty { "1" }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CurrencyUtils.formatRupee(sale.sellingPrice),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    ),
                                    color = Color(0xFF059669)
                                )
                                IconButton(
                                    onClick = { onDeleteSale(sale) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cash Collected / Pending Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Received: ${CurrencyUtils.formatRupee(sale.amountReceived)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF059669)
                            )

                            if (sale.amountPending > 0) {
                                Text(
                                    text = "Pending: ${CurrencyUtils.formatRupee(sale.amountPending)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFDC2626)
                                )
                                Button(
                                    onClick = { onReceiveCash(sale) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Collect Cash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "PAID IN FULL ✓",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF059669)
                                )
                            }
                        }

                        // Photo Slip Preview if attached
                        if (!sale.photoUri.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .clickable { onViewPhoto(sale.photoUri) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📷 View Delivery/Bill Slip Photo", color = Color(0xFF0284C7), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ---------------------- TAB 2: EXPENSES ----------------------
@Composable
private fun ExpensesTab(
    expenses: List<DealExpense>,
    onAddExpense: () -> Unit,
    onDeleteExpense: (DealExpense) -> Unit,
    onViewPhoto: (String) -> Unit
) {
    val totalExpenses = expenses.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Big ADD EXPENSE button
        item {
            Button(
                onClick = onAddExpense,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("big_add_expense_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C))
            ) {
                Text("💸 ➕ ADD EXPENSE", fontWeight = FontWeight.Black, fontSize = 17.sp)
            }
        }

        // Summary bar
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF7ED),
                border = BorderStroke(1.dp, Color(0xFFFED7AA))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL EXPENSES:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF9A3412)
                    )
                    Text(
                        text = CurrencyUtils.formatRupee(totalExpenses),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ),
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }

        if (expenses.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Text(
                        text = "No expenses added yet. Record transport, labour, loading, cutting slips.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(expenses, key = { it.id }) { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFFFFEDD5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(expense.categoryIcon, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = expense.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = expense.date,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                if (!expense.photoUri.isNullOrBlank()) {
                                    Text(
                                        text = "📷 View Bill Slip",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF0284C7),
                                        modifier = Modifier
                                            .clickable { onViewPhoto(expense.photoUri) }
                                            .padding(top = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = CurrencyUtils.formatRupee(expense.amount),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                ),
                                color = Color(0xFFDC2626)
                            )
                            IconButton(
                                onClick = { onDeleteExpense(expense) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ---------------------- TAB 3: PARTNERS ----------------------
@Composable
private fun PartnersTab(
    dealWithDetails: DealWithDetails,
    onAddPartner: () -> Unit,
    onDeletePartner: (DealPartner) -> Unit
) {
    val totalInvested = dealWithDetails.totalInvested

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onAddPartner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("big_add_partner_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("👥 ➕ ADD PERSON TO THIS DEAL", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE0F2FE),
                border = BorderStroke(1.dp, Color(0xFFBAE6FD))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL INVESTED:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF075985)
                    )
                    Text(
                        text = CurrencyUtils.formatRupee(totalInvested),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ),
                        color = Color(0xFF0369A1)
                    )
                }
            }
        }

        if (dealWithDetails.partners.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Text(
                        text = "No partners recorded for this deal yet. Every deal has its own separate partner list.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(dealWithDetails.partners, key = { it.id }) { partner ->
                val pct = if (totalInvested > 0) (partner.amountInvested / totalInvested) * 100.0 else 0.0
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFFE0F2FE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = partner.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (partner.phone.isNotBlank()) {
                                    Text(
                                        text = "📞 ${partner.phone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "Share: ${CurrencyUtils.formatPercent(pct)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = CurrencyUtils.formatRupee(partner.amountInvested),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                ),
                                color = Color(0xFF0F172A)
                            )
                            IconButton(
                                onClick = { onDeletePartner(partner) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                PartnerTabularAnalysisCard(dealWithDetails = dealWithDetails)
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ---------------------- TAB 4: WOOD STOCK ----------------------
@Composable
private fun WoodStockTab(
    stocks: List<DealStock>,
    onAddStock: () -> Unit,
    onToggleSold: (DealStock) -> Unit,
    onDeleteStock: (DealStock) -> Unit,
    onViewPhoto: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onAddStock,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("big_add_stock_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F))
            ) {
                Text("🪵 ➕ ADD WOOD / MATERIAL", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }

        if (stocks.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Text(
                        text = "No stock recorded yet. Add pillars, beams, doors, windows, and scrap obtained from the site.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(stocks, key = { it.id }) { stock ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (stock.isSold) Color(0xFFF1F5F9) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (!stock.photoUri.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onViewPhoto(stock.photoUri) }
                                ) {
                                    val imageModel = if (stock.photoUri.startsWith("content://") || stock.photoUri.startsWith("file://")) {
                                        stock.photoUri
                                    } else {
                                        File(stock.photoUri)
                                    }
                                    AsyncImage(
                                        model = imageModel,
                                        contentDescription = stock.itemName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color(0xFFFEF3C7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(stock.category.takeLast(2), fontSize = 22.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = stock.itemName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Qty: ${stock.quantity} • ${stock.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (stock.isSold) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                                modifier = Modifier.clickable { onToggleSold(stock) }
                            ) {
                                Text(
                                    text = if (stock.isSold) "SOLD ✓" else "IN STOCK",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (stock.isSold) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                            }

                            IconButton(
                                onClick = { onDeleteStock(stock) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

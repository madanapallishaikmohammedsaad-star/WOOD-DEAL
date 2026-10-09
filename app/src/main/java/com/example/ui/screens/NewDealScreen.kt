package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealStock
import com.example.ui.WoodDealViewModel
import com.example.ui.components.CameraOrGalleryPicker
import com.example.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewDealScreen(
    viewModel: WoodDealViewModel,
    onNavigateBack: () -> Unit,
    onDealCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Step 1 - Deal Info
    var dealName by remember { mutableStateOf("") }
    var dealLocation by remember { mutableStateOf("") }
    var dealDate by remember { mutableStateOf(CurrencyUtils.getTodayFormatted()) }
    var purchasePriceInput by remember { mutableStateOf("") }

    // Step 2 - Partners
    val partnersList = remember { mutableStateListOf<DealPartner>() }
    var showAddPartnerDialog by remember { mutableStateOf(false) }

    // Step 3 - Expenses
    val expensesList = remember { mutableStateListOf<DealExpense>() }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    // Step 4 - Wood Stock
    val stocksList = remember { mutableStateListOf<DealStock>() }
    var showAddStockDialog by remember { mutableStateOf(false) }

    val purchasePrice = purchasePriceInput.toDoubleOrNull() ?: 0.0
    val totalExpenses = expensesList.sumOf { it.amount }
    val totalCost = purchasePrice + totalExpenses
    val totalInvested = partnersList.sumOf { it.amountInvested }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "➕ NEW DEAL",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Cost & Invested summary strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "TOTAL COST",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalCost),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                ),
                                color = Color(0xFFD97706)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOTAL INVESTED",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalInvested),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                ),
                                color = Color(0xFF059669)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (dealName.isNotBlank() && purchasePrice > 0) {
                                val newDeal = Deal(
                                    name = dealName.trim(),
                                    location = dealLocation.trim().ifEmpty { "Demolition Site" },
                                    date = dealDate.trim(),
                                    purchasePrice = purchasePrice,
                                    isCompleted = false
                                )
                                viewModel.createDeal(
                                    deal = newDeal,
                                    partners = partnersList.toList(),
                                    expenses = expensesList.toList(),
                                    stocks = stocksList.toList()
                                ) { createdId ->
                                    onDealCreated(createdId)
                                }
                            }
                        },
                        enabled = dealName.isNotBlank() && purchasePrice > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("save_deal_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF059669)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE & START DEAL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // STEP 1 — DEAL INFORMATION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFD97706), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("1", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "DEAL INFORMATION",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = dealName,
                            onValueChange = { dealName = it },
                            label = { Text("Deal Name (Building / Demolition Name) *") },
                            placeholder = { Text("e.g. Old Building, Cinema Hall") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_deal_name"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = dealLocation,
                                onValueChange = { dealLocation = it },
                                label = { Text("Location") },
                                placeholder = { Text("e.g. Madanapalle") },
                                leadingIcon = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD97706))
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = dealDate,
                                onValueChange = { dealDate = it },
                                label = { Text("Date") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = purchasePriceInput,
                            onValueChange = { purchasePriceInput = it },
                            label = { Text("Purchase Price (₹) *") },
                            placeholder = { Text("e.g. 100000") },
                            leadingIcon = {
                                Text("₹", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF059669))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_purchase_price"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF059669)
                            )
                        )
                    }
                }
            }

            // STEP 2 — WHO PUT MONEY IN THIS DEAL? (PARTNERS)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFF0284C7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("2", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "WHO PUT MONEY IN THIS DEAL?",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Partners & their investment amount",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showAddPartnerDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                modifier = Modifier.testTag("add_partner_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ADD", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (partnersList.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF0F9FF),
                                border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                            ) {
                                Text(
                                    text = "👉 Tap 'ADD' to add partners who invested money in this specific deal (e.g. Ahmed ₹60,000, Rahim ₹40,000). Percentages are calculated automatically.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF0369A1),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                partnersList.forEachIndexed { index, partner ->
                                    val pct = if (totalInvested > 0) (partner.amountInvested / totalInvested) * 100.0 else 0.0
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(Color(0xFFE0F2FE), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("👤", fontSize = 18.sp)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = partner.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        text = "Share: ${CurrencyUtils.formatPercent(pct)}",
                                                        color = Color(0xFF0284C7),
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = CurrencyUtils.formatRupee(partner.amountInvested),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF0F172A)
                                                )
                                                IconButton(
                                                    onClick = { partnersList.removeAt(index) },
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
                        }
                    }
                }
            }

            // STEP 3 — EXPENSES (Transport, Labour, Loading, Cutting, Other)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFEA580C), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("3", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ADD EXPENSES",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Transport, Labour, Loading, Cutting",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showAddExpenseDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                modifier = Modifier.testTag("add_expense_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ADD", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (expensesList.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFF7ED),
                                border = BorderStroke(1.dp, Color(0xFFFED7AA))
                            ) {
                                Text(
                                    text = "🚚 Tap 'ADD' to record transport, labour, or cutting costs. You can also attach slip/bill photos.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFC2410C),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                expensesList.forEachIndexed { index, exp ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(exp.categoryIcon, fontSize = 22.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(exp.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    if (!exp.photoUri.isNullOrBlank()) {
                                                        Text("📷 Bill attached", fontSize = 12.sp, color = Color(0xFF059669))
                                                    }
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = CurrencyUtils.formatRupee(exp.amount),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFFDC2626)
                                                )
                                                IconButton(
                                                    onClick = { expensesList.removeAt(index) },
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
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Cost Breakdown Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Purchase Price:", style = MaterialTheme.typography.bodyMedium)
                                    Text(CurrencyUtils.formatRupee(purchasePrice), fontWeight = FontWeight.SemiBold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Other Expenses:", style = MaterialTheme.typography.bodyMedium)
                                    Text(CurrencyUtils.formatRupee(totalExpenses), fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "TOTAL COST:",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = Color(0xFF78350F)
                                    )
                                    Text(
                                        CurrencyUtils.formatRupee(totalCost),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        ),
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 4 — WOOD / STOCK (OPTIONAL AT CREATION)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFF78350F), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("4", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "WOOD & STOCK (Optional)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Pillars, Beams, Doors, Windows",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showAddStockDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ADD", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (stocksList.isEmpty()) {
                            Text(
                                text = "🪵 You can record wood items now or later inside the deal.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                stocksList.forEachIndexed { index, stock ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(stock.category.takeLast(2), fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(stock.itemName, fontWeight = FontWeight.Bold)
                                                    Text("Qty: ${stock.quantity}", fontSize = 13.sp, color = Color.Gray)
                                                }
                                            }
                                            IconButton(
                                                onClick = { stocksList.removeAt(index) },
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
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Add Partner Dialog
    if (showAddPartnerDialog) {
        var partnerName by remember { mutableStateOf("") }
        var partnerAmount by remember { mutableStateOf("") }
        var partnerPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPartnerDialog = false },
            title = {
                Text("➕ Add Partner", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = partnerName,
                        onValueChange = { partnerName = it },
                        label = { Text("Person Name *") },
                        placeholder = { Text("e.g. Ahmed, Rahim") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth().testTag("partner_name_input")
                    )

                    OutlinedTextField(
                        value = partnerAmount,
                        onValueChange = { partnerAmount = it },
                        label = { Text("Amount Invested (₹) *") },
                        placeholder = { Text("e.g. 50000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("partner_amount_input")
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
                            partnersList.add(
                                DealPartner(
                                    dealId = 0,
                                    name = partnerName.trim(),
                                    amountInvested = amount,
                                    phone = partnerPhone.trim()
                                )
                            )
                            showAddPartnerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.testTag("partner_confirm_btn")
                ) {
                    Text("ADD PERSON", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddPartnerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Expense Dialog
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
                    Text("Quick Categories:", style = MaterialTheme.typography.labelMedium)
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
                        modifier = Modifier.fillMaxWidth().testTag("expense_name_input")
                    )

                    OutlinedTextField(
                        value = expenseAmount,
                        onValueChange = { expenseAmount = it },
                        label = { Text("Amount (₹) *") },
                        placeholder = { Text("e.g. 5000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("expense_amount_input")
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
                            expensesList.add(
                                DealExpense(
                                    dealId = 0,
                                    name = expenseName.trim(),
                                    categoryIcon = selectedCategory.second,
                                    amount = amount,
                                    photoUri = expensePhotoPath,
                                    date = CurrencyUtils.getTodayFormatted()
                                )
                            )
                            showAddExpenseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    modifier = Modifier.testTag("expense_confirm_btn")
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

    // Add Stock Dialog
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
                Text("🪵 Add Wood / Material", fontWeight = FontWeight.Bold)
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
                        placeholder = { Text("e.g. 10 pieces, 2 tons") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CameraOrGalleryPicker(
                        photoPath = stockPhotoPath,
                        onPhotoSelected = { stockPhotoPath = it },
                        label = "Wood / Material Photo"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (stockItemName.isNotBlank()) {
                            stocksList.add(
                                DealStock(
                                    dealId = 0,
                                    category = selectedStockCategory,
                                    itemName = stockItemName.trim(),
                                    quantity = stockQuantity.trim().ifEmpty { "1" },
                                    photoUri = stockPhotoPath
                                )
                            )
                            showAddStockDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F))
                ) {
                    Text("ADD ITEM", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddStockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

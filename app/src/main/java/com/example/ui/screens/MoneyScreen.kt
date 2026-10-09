package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DealSale
import com.example.ui.WoodDealViewModel
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalBrownDark
import com.example.ui.theme.NaturalBrownMedium
import com.example.ui.theme.NaturalBrownPrimary
import com.example.ui.theme.NaturalClayExtraLight
import com.example.ui.theme.NaturalClayLight
import com.example.ui.theme.NaturalWoodDeep
import com.example.ui.theme.NeutralSubCard
import com.example.ui.theme.PendingYellowBg
import com.example.ui.theme.PendingYellowText
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenContainer
import com.example.ui.theme.SalesBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyScreen(
    viewModel: WoodDealViewModel,
    onNavigateBack: () -> Unit,
    onSelectDeal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val summary by viewModel.financialSummary.collectAsStateWithLifecycle()
    val allDeals by viewModel.allDeals.collectAsStateWithLifecycle()

    var saleToReceiveCash by remember { mutableStateOf<Pair<DealSale, Long>?>(null) }

    // Collect all pending customer sales across all deals
    val pendingCustomerSales = remember(allDeals) {
        val list = mutableListOf<Triple<DealSale, String, Long>>() // Sale, Deal Name, Deal Id
        allDeals.forEach { dealItem ->
            dealItem.sales.filter { it.amountPending > 0 }.forEach { sale ->
                list.add(Triple(sale, dealItem.deal.name, dealItem.deal.id))
            }
        }
        list
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "💰 MONEY OVERVIEW",
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NaturalBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Capital & Cash Collected Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NaturalWoodDeep),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "TOTAL BUSINESS FINANCIALS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            ),
                            color = NaturalClayLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Sales Made",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NaturalClayExtraLight
                                )
                                Text(
                                    text = CurrencyUtils.formatRupee(summary.totalSales),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Net Profit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NaturalClayExtraLight
                                )
                                Text(
                                    text = CurrencyUtils.formatRupee(summary.totalProfit),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = if (summary.totalProfit >= 0) Color(0xFFA7F3D0) else Color(0xFFFECACA)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = NaturalBrownDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Cash Received",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = NaturalClayLight
                                    )
                                    Text(
                                        CurrencyUtils.formatRupee(summary.totalCashReceived),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA7F3D0),
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = NaturalBrownDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Customer Dues",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = NaturalClayLight
                                    )
                                    Text(
                                        CurrencyUtils.formatRupee(summary.totalCustomerPending),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE68A),
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = NaturalBrownDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Total Deals Cost",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = NaturalClayLight
                                    )
                                    Text(
                                        CurrencyUtils.formatRupee(summary.totalDealsCost),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = NaturalBrownDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Partners Invested",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = NaturalClayLight
                                    )
                                    Text(
                                        CurrencyUtils.formatRupee(summary.totalInvested),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Customer Pending Ledger Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOMER PENDING DUES (${pendingCustomerSales.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = NaturalBrownDark
                    )
                }
            }

            if (pendingCustomerSales.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = ProfitGreenContainer,
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎉", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "All customer dues are clear!",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ProfitGreen
                                )
                                Text(
                                    text = "No pending payments from any buyer across all deals.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProfitGreen.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            } else {
                items(pendingCustomerSales) { (sale, dealName, dealId) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, NaturalBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sale.customerName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        ),
                                        color = NaturalWoodDeep
                                    )
                                    Text(
                                        text = "Deal: $dealName • Item: ${sale.itemName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = CurrencyUtils.formatRupee(sale.amountPending),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = NaturalBrownDark
                                    )
                                    Text(
                                        text = "Pending Due",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { saleToReceiveCash = Pair(sale, dealId) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NaturalBrownPrimary)
                                ) {
                                    Text("💵 Collect Cash", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                if (sale.customerPhone.isNotBlank()) {
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${sale.customerPhone}")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, NaturalBorder)
                                    ) {
                                        Icon(
                                            Icons.Default.Call,
                                            contentDescription = "Call",
                                            modifier = Modifier.size(16.dp),
                                            tint = NaturalBrownDark
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call", color = NaturalBrownDark)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectDeal(dealId)
                                        onSelectDeal(dealId)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, NaturalBorder)
                                ) {
                                    Text("View Deal", color = NaturalBrownDark)
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

    // Collect Cash Dialog
    if (saleToReceiveCash != null) {
        val (sale, _) = saleToReceiveCash!!
        var amountInput by remember { mutableStateOf(sale.amountPending.toString().replace(".0", "")) }

        AlertDialog(
            onDismissRequest = { saleToReceiveCash = null },
            title = {
                Text("💵 Record Cash Collected", fontWeight = FontWeight.Bold, color = NaturalWoodDeep)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Customer: ${sale.customerName}", fontWeight = FontWeight.Bold, color = NaturalWoodDeep)
                    Text(
                        "Pending: ${CurrencyUtils.formatRupee(sale.amountPending)}",
                        color = NaturalBrownDark,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount Collected (₹)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = NaturalBrownPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NaturalBrownPrimary,
                            unfocusedBorderColor = NaturalBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.updateSaleReceived(sale, amount)
                            saleToReceiveCash = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalBrownPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SAVE PAYMENT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { saleToReceiveCash = null },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NaturalBorder)
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }
}

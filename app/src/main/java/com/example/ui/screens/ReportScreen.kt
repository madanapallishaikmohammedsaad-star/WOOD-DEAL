package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.WoodDealViewModel
import com.example.ui.components.OverallPartnerTabularCard
import com.example.util.CurrencyUtils
import com.example.util.PdfExportUtils
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

data class PartnerAggregate(
    val name: String,
    val dealsCount: Int,
    val totalInvested: Double,
    val totalProfitShare: Double,
    val totalReturn: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: WoodDealViewModel,
    onNavigateBack: () -> Unit,
    onSelectDeal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDeals by viewModel.allDeals.collectAsStateWithLifecycle()
    val summary by viewModel.financialSummary.collectAsStateWithLifecycle()
    var showShareReportDialog by remember { mutableStateOf(false) }

    // Aggregate partner totals across deals
    val partnerAggregates = remember(allDeals) {
        val map = mutableMapOf<String, MutableList<Pair<Double, Double>>>() // Name -> list of (invested, profitShare)
        allDeals.forEach { dealWithDetails ->
            val total = dealWithDetails.totalInvested
            dealWithDetails.partners.forEach { partner ->
                val pct = if (total > 0) (partner.amountInvested / total) * 100.0 else 0.0
                val share = dealWithDetails.profit * (pct / 100.0)
                val list = map.getOrPut(partner.name.trim()) { mutableListOf() }
                list.add(Pair(partner.amountInvested, share))
            }
        }
        map.map { (name, list) ->
            val totalInv = list.sumOf { it.first }
            val totalProf = list.sumOf { it.second }
            PartnerAggregate(
                name = name,
                dealsCount = list.size,
                totalInvested = totalInv,
                totalProfitShare = totalProf,
                totalReturn = totalInv + totalProf
            )
        }.sortedByDescending { it.totalInvested }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "📊 BUSINESS REPORT",
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
                actions = {
                    IconButton(
                        onClick = { showShareReportDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report Options",
                            tint = NaturalBrownPrimary
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
            // Profit Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (summary.totalProfit >= 0) NaturalWoodDeep else Color(0xFF7F1D1D)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "OVERALL NET PROFIT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            ),
                            color = NaturalClayLight
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = CurrencyUtils.formatRupee(summary.totalProfit),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Across ${allDeals.size} Deals (${summary.completedDealsCount} Complete)",
                                style = MaterialTheme.typography.bodySmall,
                                color = NaturalClayExtraLight
                            )
                            Text(
                                text = "Sales: ${CurrencyUtils.formatRupee(summary.totalSales)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFDE68A)
                            )
                        }
                    }
                }
            }

            // Partner Tabular Analysis Card (WhatsApp format)
            item {
                OverallPartnerTabularCard(
                    partnerAggregates = partnerAggregates,
                    summary = summary,
                    allDeals = allDeals
                )
            }

            // Partner Summary Across Deals
            item {
                Text(
                    text = "PARTNER TOTALS (ACROSS ALL DEALS)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NaturalBrownDark
                )
            }

            if (partnerAggregates.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, NaturalBorder)
                    ) {
                        Text(
                            text = "No partner investments recorded yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(partnerAggregates) { partner ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(NaturalClayExtraLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👤", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = partner.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp
                                            ),
                                            color = NaturalWoodDeep
                                        )
                                        Text(
                                            text = "Invested in ${partner.dealsCount} deals",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = CurrencyUtils.formatRupee(partner.totalReturn),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = ProfitGreen
                                    )
                                    Text(
                                        text = "Total Return",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NeutralSubCard, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Invested: ${CurrencyUtils.formatRupee(partner.totalInvested)}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "Profit Earned: ${CurrencyUtils.formatRupee(partner.totalProfitShare)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (partner.totalProfitShare >= 0) ProfitGreen else Color(0xFFDC2626)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Deal Breakdown Section
            item {
                Text(
                    text = "ALL DEALS BREAKDOWN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NaturalBrownDark
                )
            }

            items(allDeals) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectDeal(item.deal.id)
                            onSelectDeal(item.deal.id)
                        },
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
                            Text(
                                text = item.deal.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NaturalWoodDeep
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (item.deal.isCompleted) ProfitGreenContainer else PendingYellowBg
                            ) {
                                Text(
                                    text = if (item.deal.isCompleted) "🟢 COMPLETE" else "🟡 IN PROGRESS",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (item.deal.isCompleted) ProfitGreen else PendingYellowText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Cost: ${CurrencyUtils.formatRupee(item.totalCost)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Text(
                                "Sales: ${CurrencyUtils.formatRupee(item.totalSales)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SalesBlue)
                            )
                            Text(
                                "Profit: ${CurrencyUtils.formatRupee(item.profit)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isProfitable) ProfitGreen else NaturalBrownMedium
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showShareReportDialog) {
        AlertDialog(
            onDismissRequest = { showShareReportDialog = false },
            title = {
                Text("📤 Export Business Report", fontWeight = FontWeight.Bold)
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
                                showShareReportDialog = false
                                val pdfFile = PdfExportUtils.exportOverallReportToPdf(
                                    context = context,
                                    summary = summary,
                                    partnerAggregates = partnerAggregates,
                                    allDeals = allDeals
                                )
                                if (pdfFile != null) {
                                    PdfExportUtils.sharePdf(context, pdfFile, "Share Overall Business Report PDF")
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
                                    text = "Print-ready styled A4 PDF with partner analysis table, summary metrics & deals",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE64A19)
                                )
                            }
                        }
                    }

                    // Option 2: WhatsApp Tabular Format
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showShareReportDialog = false
                                val totalInv = partnerAggregates.sumOf { it.totalInvested }
                                val totalProf = partnerAggregates.sumOf { it.totalProfitShare }
                                val totalRec = partnerAggregates.sumOf { it.totalReturn }
                                val whatsappText = CurrencyUtils.generateOverallPartnerTabularWhatsApp(
                                    partners = partnerAggregates,
                                    totalInvested = totalInv,
                                    totalProfit = totalProf,
                                    totalReturn = totalRec
                                )
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, whatsappText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Partner Table via WhatsApp")
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
                                    text = "Clean monospace tabular alignment for WhatsApp chats",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    // Option 3: Full Text Summary
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showShareReportDialog = false
                                val sb = StringBuilder()
                                sb.append("📊 *WOOD DEAL OVERALL REPORT*\n\n")
                                sb.append("• Total Deals: ${allDeals.size} (${summary.activeDealsCount} Active, ${summary.completedDealsCount} Complete)\n")
                                sb.append("• Total Sales: ${CurrencyUtils.formatRupee(summary.totalSales)}\n")
                                sb.append("• Total Deals Cost: ${CurrencyUtils.formatRupee(summary.totalDealsCost)}\n")
                                sb.append("• *Net Profit:* ${CurrencyUtils.formatRupee(summary.totalProfit)}\n")
                                sb.append("• Cash In Hand: ${CurrencyUtils.formatRupee(summary.totalCashReceived)}\n")
                                sb.append("• Customer Pending: ${CurrencyUtils.formatRupee(summary.totalCustomerPending)}\n\n")

                                sb.append("👥 *PARTNERS SUMMARY:*\n")
                                partnerAggregates.forEach { p ->
                                    sb.append("• *${p.name}* (${p.dealsCount} deals)\n")
                                    sb.append("   - Invested: ${CurrencyUtils.formatRupee(p.totalInvested)}\n")
                                    sb.append("   - Profit Earned: ${CurrencyUtils.formatRupee(p.totalProfitShare)}\n")
                                    sb.append("   - Total Payout: ${CurrencyUtils.formatRupee(p.totalReturn)}\n")
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, sb.toString())
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Overall Report")
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
                                    text = "Text Summary Report",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723)
                                )
                                Text(
                                    text = "Formatted text message for quick messaging",
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
                TextButton(onClick = { showShareReportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

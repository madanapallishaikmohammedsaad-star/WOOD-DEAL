package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DealWithDetails
import com.example.ui.BusinessFinancialSummary
import com.example.ui.screens.PartnerAggregate
import com.example.util.CurrencyUtils
import com.example.util.PdfExportUtils

@Composable
fun OverallPartnerTabularCard(
    partnerAggregates: List<PartnerAggregate>,
    summary: BusinessFinancialSummary? = null,
    allDeals: List<DealWithDetails> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalInvested = partnerAggregates.sumOf { it.totalInvested }
    val totalProfit = partnerAggregates.sumOf { it.totalProfitShare }
    val totalReceived = partnerAggregates.sumOf { it.totalReturn }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Color(0xFFD7CCC8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with WhatsApp Ready badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 ALL PARTNERS TABULAR ANALYSIS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF3E2723)
                    )
                    Text(
                        text = "Aggregated across all active & completed deals",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF795548)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Text(
                        text = "📲 WhatsApp Ready",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (partnerAggregates.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFBF8F5)
                ) {
                    Text(
                        text = "No partner investments recorded yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Horizontally scrollable table
                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFDFBF7), RoundedCornerShape(12.dp))
                        .horizontalScroll(scrollState)
                        .padding(12.dp)
                ) {
                    Column {
                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF4E342E), RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Partner Name",
                                modifier = Modifier.width(130.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Invested Amount",
                                modifier = Modifier.width(125.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "Percentage",
                                modifier = Modifier.width(95.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "Profit Amount",
                                modifier = Modifier.width(120.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "Total Received",
                                modifier = Modifier.width(130.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Table Rows
                        partnerAggregates.forEachIndexed { index, partner ->
                            val pct = if (totalInvested > 0) (partner.totalInvested / totalInvested) * 100.0 else 0.0
                            val rowBg = if (index % 2 == 0) Color.White else Color(0xFFF7F4EE)

                            Row(
                                modifier = Modifier
                                    .background(rowBg, RoundedCornerShape(6.dp))
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = partner.name,
                                    modifier = Modifier.width(130.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2D2926)
                                )
                                Text(
                                    text = CurrencyUtils.formatRupee(partner.totalInvested),
                                    modifier = Modifier.width(125.dp),
                                    fontSize = 13.sp,
                                    color = Color(0xFF4A453F),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = CurrencyUtils.formatPercent(pct),
                                    modifier = Modifier.width(95.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6B584C),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = CurrencyUtils.formatRupee(partner.totalProfitShare),
                                    modifier = Modifier.width(120.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (partner.totalProfitShare >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = CurrencyUtils.formatRupee(partner.totalReturn),
                                    modifier = Modifier.width(130.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.End
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            thickness = 2.dp,
                            color = Color(0xFF8D6E63)
                        )

                        // Totals Row
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFEFEBE9), RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL",
                                modifier = Modifier.width(130.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalInvested),
                                modifier = Modifier.width(125.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF3E2723),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "100.0%",
                                modifier = Modifier.width(95.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF3E2723),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalProfit),
                                modifier = Modifier.width(120.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (totalProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalReceived),
                                modifier = Modifier.width(130.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Total Investment & Total Profit Highlights
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFAF7F2),
                    border = BorderStroke(1.dp, Color(0xFFE6DFD5))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL INVESTMENT:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF5D4037)
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalInvested),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFF3E2723)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL PROFIT:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF5D4037)
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalProfit),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = if (totalProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: WhatsApp Share & PDF Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp Sharing Button
                    Button(
                        onClick = {
                            val whatsappText = CurrencyUtils.generateOverallPartnerTabularWhatsApp(
                                partners = partnerAggregates,
                                totalInvested = totalInvested,
                                totalProfit = totalProfit,
                                totalReturn = totalReceived
                            )
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, whatsappText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Overall Partner Table via WhatsApp")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Table",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "WHATSAPP",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    // PDF Export Button
                    Button(
                        onClick = {
                            val curSummary = summary ?: BusinessFinancialSummary(
                                totalInvested = totalInvested,
                                totalDealsCost = allDeals.sumOf { it.totalCost },
                                totalSales = allDeals.sumOf { it.totalSales },
                                totalCashReceived = allDeals.sumOf { it.totalReceived },
                                totalCustomerPending = allDeals.sumOf { it.totalPending },
                                totalProfit = totalProfit,
                                activeDealsCount = allDeals.count { !it.deal.isCompleted },
                                completedDealsCount = allDeals.count { it.deal.isCompleted }
                            )
                            val pdfFile = PdfExportUtils.exportOverallReportToPdf(
                                context = context,
                                summary = curSummary,
                                partnerAggregates = partnerAggregates,
                                allDeals = allDeals
                            )
                            if (pdfFile != null) {
                                PdfExportUtils.sharePdf(context, pdfFile, "Share Overall Business Report PDF")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
                    ) {
                        Text("📄", fontSize = 16.sp, modifier = Modifier.padding(end = 4.dp))
                        Text(
                            text = "EXPORT PDF",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DealWithDetails
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalBrownDark
import com.example.ui.theme.NaturalBrownMedium
import com.example.ui.theme.NaturalBrownPrimary
import com.example.ui.theme.NaturalClayExtraLight
import com.example.ui.theme.NaturalClayLight
import com.example.ui.theme.NaturalWoodDeep
import com.example.ui.theme.NeutralSubCard
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenContainer
import com.example.ui.theme.TextSecondary
import com.example.util.CurrencyUtils
import com.example.util.PdfExportUtils

@Composable
fun PartnerTabularAnalysisCard(
    dealWithDetails: DealWithDetails,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalInvested = dealWithDetails.totalInvested
    val totalProfit = dealWithDetails.profit
    val totalReturn = totalInvested + totalProfit
    val payouts = dealWithDetails.partnerPayouts

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, NaturalBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Title & Quick WhatsApp Share button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📊 PARTNER ANALYSIS TABLE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = NaturalWoodDeep
                    )
                    Text(
                        text = "Tabular investment & profit payout analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF25D366).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.4f)),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WhatsApp Ready",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B8A44)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (payouts.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = NeutralSubCard
                ) {
                    Text(
                        text = "No partners added to this deal yet. Add partners to see the tabular breakdown.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Horizontally scrollable table container to ensure all 5 columns look sharp on any device
                val tableScrollState = rememberScrollState()
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NaturalBorder),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tableScrollState)
                    ) {
                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .background(NaturalWoodDeep)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PARTNER NAME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.width(130.dp)
                            )
                            Text(
                                text = "INVESTED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                text = "PROFIT AMOUNT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(110.dp)
                            )
                            Text(
                                text = "TOTAL RECEIVED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(120.dp)
                            )
                        }

                        HorizontalDivider(color = NaturalClayLight, thickness = 1.dp)

                        // Data Rows
                        payouts.forEachIndexed { index, partner ->
                            val isEven = index % 2 == 0
                            val rowBg = if (isEven) Color.White else NaturalBackground

                            Row(
                                modifier = Modifier
                                    .background(rowBg)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Partner Name
                                Row(
                                    modifier = Modifier.width(130.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(NaturalClayExtraLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👤", fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = partner.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        ),
                                        color = NaturalWoodDeep,
                                        maxLines = 1
                                    )
                                }

                                // Invested Amount
                                Text(
                                    text = CurrencyUtils.formatRupee(partner.amountInvested),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = NaturalBrownDark,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(100.dp)
                                )

                                // Percentage
                                Text(
                                    text = CurrencyUtils.formatPercent(partner.percentage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = NaturalBrownMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(60.dp)
                                )

                                // Profit Amount
                                val isProf = partner.profitShare >= 0
                                Text(
                                    text = "${if (isProf) "+" else ""}${CurrencyUtils.formatRupee(partner.profitShare)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = if (isProf) ProfitGreen else Color(0xFFDC2626),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(110.dp)
                                )

                                // Total Received (Invested + Profit)
                                Text(
                                    text = CurrencyUtils.formatRupee(partner.totalReturn),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = ProfitGreen,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(120.dp)
                                )
                            }

                            HorizontalDivider(color = NaturalBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }

                        // TOTAL Row inside Table
                        Row(
                            modifier = Modifier
                                .background(NaturalClayExtraLight)
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = NaturalWoodDeep,
                                modifier = Modifier.width(130.dp)
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalInvested),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = NaturalBrownDark,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = NaturalBrownMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                text = "${if (totalProfit >= 0) "+" else ""}${CurrencyUtils.formatRupee(totalProfit)}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = if (totalProfit >= 0) ProfitGreen else Color(0xFFDC2626),
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(110.dp)
                            )
                            Text(
                                text = CurrencyUtils.formatRupee(totalReturn),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = ProfitGreen,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Below Table Totals Section (Total Investment & Total Profit highlighted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = NeutralSubCard,
                        border = BorderStroke(1.dp, NaturalBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "TOTAL INVESTMENT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyUtils.formatRupee(totalInvested),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = NaturalWoodDeep
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = if (totalProfit >= 0) ProfitGreenContainer else Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, if (totalProfit >= 0) Color(0xFFA7F3D0) else Color(0xFFFECACA))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (totalProfit >= 0) "TOTAL PROFIT" else "TOTAL LOSS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (totalProfit >= 0) ProfitGreen else Color(0xFFDC2626)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyUtils.formatRupee(totalProfit),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = if (totalProfit >= 0) ProfitGreen else Color(0xFFDC2626)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = NaturalWoodDeep
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL RECEIVED / PAYOUT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontSize = 11.sp
                            ),
                            color = NaturalClayLight
                        )
                        Text(
                            text = CurrencyUtils.formatRupee(totalReturn),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: WhatsApp Share and PDF Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Big Share to WhatsApp Button
                    Button(
                        onClick = {
                            val whatsappText = CurrencyUtils.generatePartnerTabularWhatsApp(dealWithDetails)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, whatsappText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Tabular Analysis via WhatsApp")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // Official WhatsApp green
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share on WhatsApp",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WHATSAPP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    // PDF Export Button
                    Button(
                        onClick = {
                            val pdfFile = PdfExportUtils.exportDealToPdf(context, dealWithDetails)
                            if (pdfFile != null) {
                                PdfExportUtils.sharePdf(context, pdfFile, "Share Deal Report PDF")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalWoodDeep)
                    ) {
                        Text("📄", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXPORT PDF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

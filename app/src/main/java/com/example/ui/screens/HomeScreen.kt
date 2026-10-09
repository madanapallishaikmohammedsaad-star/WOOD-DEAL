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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.WoodDealViewModel
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalBrownDark
import com.example.ui.theme.NaturalBrownMedium
import com.example.ui.theme.NaturalBrownPrimary
import com.example.ui.theme.NaturalClayLight
import com.example.ui.theme.NaturalClayExtraLight
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

@Composable
fun HomeScreen(
    viewModel: WoodDealViewModel,
    onNavigateToNewDeal: () -> Unit,
    onNavigateToMyDeals: () -> Unit,
    onNavigateToMoney: () -> Unit,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val financialSummary by viewModel.financialSummary.collectAsStateWithLifecycle()
    val activeDeals by viewModel.activeDeals.collectAsStateWithLifecycle()
    val currentDeal = activeDeals.firstOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NaturalBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // App Header - Natural Tones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wood Ledger",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            ),
                            color = NaturalBrownDark
                        )
                        Text(
                            text = "Small Business Manager",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NaturalBrownMedium
                        )
                    }

                    // User / Business Initials Badge
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = NaturalClayLight,
                        border = BorderStroke(2.dp, Color.White),
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🪵",
                                fontSize = 22.sp
                            )
                        }
                    }
                }
            }

            // Current Deal Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NaturalBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                // Header row with Label & Status pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT DEAL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = NaturalBrownMedium
                    )

                    val isCompleted = currentDeal?.deal?.isCompleted == true
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isCompleted) ProfitGreenContainer else PendingYellowBg
                    ) {
                        Text(
                            text = if (isCompleted) "COMPLETED" else "IN PROGRESS",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (isCompleted) ProfitGreen else PendingYellowText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentDeal?.deal?.name ?: if (activeDeals.isNotEmpty()) "${activeDeals.size} Deals Active" else "Old Building, Madanapalle",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = NaturalWoodDeep
                )

                if (currentDeal != null && currentDeal.deal.location.isNotBlank()) {
                    Text(
                        text = currentDeal.deal.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Cost box
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = NeutralSubCard
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "TOTAL COST",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val displayCost = currentDeal?.totalCost ?: financialSummary.totalDealsCost
                            Text(
                                text = CurrencyUtils.formatRupee(displayCost),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = ProfitGreen
                            )
                        }
                    }

                    // Sales box
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = NeutralSubCard
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "SALES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val displaySales = currentDeal?.totalSales ?: financialSummary.totalSales
                            Text(
                                text = CurrencyUtils.formatRupee(displaySales),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = SalesBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Indicator
                val progress = if (currentDeal != null && currentDeal.totalCost > 0) {
                    ((currentDeal.totalSales / currentDeal.totalCost).toFloat()).coerceIn(0f, 1f)
                } else if (financialSummary.totalDealsCost > 0) {
                    ((financialSummary.totalSales / financialSummary.totalDealsCost).toFloat()).coerceIn(0f, 1f)
                } else 0.75f

                val progressPercentage = (progress * 100).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = NaturalBrownMedium,
                        trackColor = Color(0xFFE0E0E0),
                    )

                    Text(
                        text = "$progressPercentage% Sold",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = NaturalBrownMedium
                    )
                }
            }
        }
    }

    // 4 Action Buttons - Natural Tones Style
            // 1. ➕ NEW DEAL (Primary Solid Wood Brown)
            item {
                HomeActionCard(
                    title = "➕ NEW DEAL",
                    subtitle = "Start a new building or wood deal",
                    iconEmoji = "➕",
                    badgeText = "CREATE",
                    isPrimary = true,
                    testTag = "home_new_deal_btn",
                    onClick = onNavigateToNewDeal
                )
            }

            // 2. 📋 MY DEALS (Clean Natural Outline Card)
            item {
                HomeActionCard(
                    title = "📋 MY DEALS",
                    subtitle = "View all deals, partners & wood stock",
                    iconEmoji = "📋",
                    badgeText = "${activeDeals.size} Active",
                    isPrimary = false,
                    testTag = "home_my_deals_btn",
                    onClick = onNavigateToMyDeals
                )
            }

            // 3. 💰 MONEY (Clean Natural Outline Card)
            item {
                HomeActionCard(
                    title = "💰 MONEY",
                    subtitle = "Cash collected & customer pending dues",
                    iconEmoji = "💰",
                    badgeText = if (financialSummary.totalCustomerPending > 0) {
                        "Due: ${CurrencyUtils.formatRupee(financialSummary.totalCustomerPending)}"
                    } else "Overview",
                    isPrimary = false,
                    testTag = "home_money_btn",
                    onClick = onNavigateToMoney
                )
            }

            // 4. 📊 REPORT (Clean Natural Outline Card)
            item {
                HomeActionCard(
                    title = "📊 REPORT",
                    subtitle = "Profit breakdown & partner payouts",
                    iconEmoji = "📊",
                    badgeText = "Profits",
                    isPrimary = false,
                    testTag = "home_report_btn",
                    onClick = onNavigateToReport
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    badgeText: String,
    isPrimary: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val containerColor = if (isPrimary) NaturalBrownPrimary else Color.White
    val contentColor = if (isPrimary) Color.White else NaturalBrownDark
    val subtitleColor = if (isPrimary) Color.White.copy(alpha = 0.9f) else TextSecondary
    val iconBgColor = if (isPrimary) Color.White.copy(alpha = 0.20f) else NeutralSubCard
    val border = if (isPrimary) null else BorderStroke(2.dp, NaturalClayLight)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPrimary) 4.dp else 1.dp,
            pressedElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = subtitleColor,
                    maxLines = 1
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isPrimary) Color.Black.copy(alpha = 0.22f) else NaturalClayExtraLight
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = if (isPrimary) Color.White else NaturalBrownDark
                )
            }
        }
    }
}


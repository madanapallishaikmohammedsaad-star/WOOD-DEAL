package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.DealWithDetails
import com.example.ui.BusinessFinancialSummary
import com.example.ui.screens.PartnerAggregate
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtils {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_X = 36f
    private const val MARGIN_BOTTOM = 800f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_X * 2)

    // Palette
    private val COLOR_PRIMARY_DARK = Color.rgb(0x3E, 0x27, 0x23)   // #3E2723
    private val COLOR_PRIMARY_MEDIUM = Color.rgb(0x5D, 0x40, 0x37) // #5D4037
    private val COLOR_PRIMARY_LIGHT = Color.rgb(0x79, 0x55, 0x48)  // #795548
    private val COLOR_ACCENT_BG = Color.rgb(0xEF, 0xEB, 0xE9)      // #EFEBE9
    private val COLOR_ROW_ALT = Color.rgb(0xFA, 0xF9, 0xF6)        // #FAF9F6
    private val COLOR_BORDER = Color.rgb(0xD7, 0xCC, 0xC8)         // #D7CCC8
    private val COLOR_PROFIT = Color.rgb(0x2E, 0x7D, 0x32)         // #2E7D32
    private val COLOR_LOSS = Color.rgb(0xC6, 0x28, 0x28)           // #C62828
    private val COLOR_GRAY = Color.rgb(0x75, 0x75, 0x75)
    private val COLOR_DARK_TEXT = Color.rgb(0x21, 0x21, 0x21)

    /**
     * Exports a single Deal report including complete Partner Tabular Analysis to PDF.
     */
    fun exportDealToPdf(context: Context, dealWithDetails: DealWithDetails): File? {
        val pdfDocument = PdfDocument()
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var currentY = 36f

        fun checkPageBreak(requiredHeight: Float) {
            if (currentY + requiredHeight > MARGIN_BOTTOM) {
                // Draw footer on current page
                drawPageFooter(canvas, paint, pageNumber, dealWithDetails.deal.name)
                pdfDocument.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                currentY = 40f

                // Draw header on subsequent pages
                drawSubsequentHeader(canvas, paint, dealWithDetails.deal.name)
                currentY += 30f
            }
        }

        val deal = dealWithDetails.deal

        // 1. Top Header Banner
        paint.color = COLOR_PRIMARY_DARK
        val bannerRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 70f)
        canvas.drawRoundRect(bannerRect, 10f, 10f, paint)

        // Title text inside banner
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("WOOD DEAL REPORT & PARTNER ANALYSIS", MARGIN_X + 16f, currentY + 28f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        paint.color = Color.rgb(0xDE, 0xD7, 0xD2)
        canvas.drawText("Deal: ${deal.name.uppercase()} • Date: ${deal.date} • Location: ${deal.location}", MARGIN_X + 16f, currentY + 48f, paint)

        // Status Badge inside Banner
        val statusText = if (deal.isCompleted) "STATUS: COMPLETE" else "STATUS: IN PROGRESS"
        paint.color = if (deal.isCompleted) Color.rgb(0x1B, 0x5E, 0x20) else Color.rgb(0xE6, 0x51, 0x00)
        val statusRect = RectF(MARGIN_X + CONTENT_WIDTH - 140f, currentY + 14f, MARGIN_X + CONTENT_WIDTH - 14f, currentY + 36f)
        canvas.drawRoundRect(statusRect, 6f, 6f, paint)

        paint.color = Color.WHITE
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(statusText, MARGIN_X + CONTENT_WIDTH - 132f, currentY + 29f, paint)

        currentY += 85f

        // 2. Financial Overview Cards Grid
        val cardWidth = (CONTENT_WIDTH - 30f) / 4f
        val cardHeight = 52f

        val metrics = listOf(
            Triple("PURCHASE PRICE", CurrencyUtils.formatRupee(deal.purchasePrice), COLOR_PRIMARY_DARK),
            Triple("TOTAL EXPENSES", CurrencyUtils.formatRupee(dealWithDetails.totalExpenses), COLOR_PRIMARY_MEDIUM),
            Triple("TOTAL SALES", CurrencyUtils.formatRupee(dealWithDetails.totalSales), COLOR_PRIMARY_DARK),
            Triple("NET PROFIT", CurrencyUtils.formatRupee(dealWithDetails.profit), if (dealWithDetails.isProfitable) COLOR_PROFIT else COLOR_LOSS)
        )

        for (i in metrics.indices) {
            val left = MARGIN_X + (i * (cardWidth + 10f))
            val right = left + cardWidth

            paint.color = COLOR_ACCENT_BG
            val cRect = RectF(left, currentY, right, currentY + cardHeight)
            canvas.drawRoundRect(cRect, 8f, 8f, paint)

            paint.color = COLOR_BORDER
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(cRect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            // Label
            paint.color = COLOR_GRAY
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(metrics[i].first, left + 8f, currentY + 16f, paint)

            // Value
            paint.color = metrics[i].third
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(metrics[i].second, left + 8f, currentY + 38f, paint)
        }

        currentY += cardHeight + 20f

        // 3. TABULAR COLUMN ANALYSIS: Partner Name | Invested Amount | Percentage | Profit Amount | Total Received
        checkPageBreak(120f)

        paint.color = COLOR_PRIMARY_DARK
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("📊 PARTNER TABULAR ANALYSIS", MARGIN_X, currentY, paint)
        currentY += 12f

        val payouts = dealWithDetails.partnerPayouts
        val colWidths = floatArrayOf(130f, 95f, 65f, 115f, 118f) // Total = 523
        val headers = arrayOf("Partner Name", "Invested Amount", "Percentage", "Profit Amount", "Total Received")

        // Draw Table Header
        paint.color = COLOR_PRIMARY_DARK
        val tblHeaderRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f)
        canvas.drawRoundRect(tblHeaderRect, 4f, 4f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var xOffset = MARGIN_X
        for (i in headers.indices) {
            val alignX = if (i == 0) xOffset + 6f else if (i == 2) xOffset + (colWidths[i] / 2f) - 10f else xOffset + colWidths[i] - 6f
            if (i == 0 || i == 2) {
                paint.textAlign = Paint.Align.LEFT
            } else {
                paint.textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(headers[i], alignX, currentY + 15f, paint)
            xOffset += colWidths[i]
        }
        paint.textAlign = Paint.Align.LEFT
        currentY += 22f

        if (payouts.isEmpty()) {
            paint.color = COLOR_ROW_ALT
            val emptyRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 24f)
            canvas.drawRect(emptyRect, paint)

            paint.color = COLOR_GRAY
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("No partners added for this deal.", MARGIN_X + 12f, currentY + 16f, paint)
            currentY += 24f
        } else {
            payouts.forEachIndexed { index, p ->
                checkPageBreak(25f)
                val rowBg = if (index % 2 == 0) Color.WHITE else COLOR_ROW_ALT
                paint.color = rowBg
                val rowRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f)
                canvas.drawRect(rowRect, paint)

                // Row border bottom
                paint.color = COLOR_BORDER
                canvas.drawLine(MARGIN_X, currentY + 22f, MARGIN_X + CONTENT_WIDTH, currentY + 22f, paint)

                // Col 0: Name
                paint.color = COLOR_DARK_TEXT
                paint.textSize = 9.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText(p.name, MARGIN_X + 6f, currentY + 15f, paint)

                // Col 1: Invested
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(CurrencyUtils.formatRupee(p.amountInvested), MARGIN_X + colWidths[0] + colWidths[1] - 6f, currentY + 15f, paint)

                // Col 2: Percentage
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(CurrencyUtils.formatPercent(p.percentage), MARGIN_X + colWidths[0] + colWidths[1] + (colWidths[2] / 2f), currentY + 15f, paint)

                // Col 3: Profit
                paint.color = if (p.profitShare >= 0) COLOR_PROFIT else COLOR_LOSS
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.RIGHT
                val sign = if (p.profitShare >= 0) "+" else ""
                canvas.drawText("$sign${CurrencyUtils.formatRupee(p.profitShare)}", MARGIN_X + colWidths[0] + colWidths[1] + colWidths[2] + colWidths[3] - 6f, currentY + 15f, paint)

                // Col 4: Total Received
                paint.color = COLOR_PROFIT
                canvas.drawText(CurrencyUtils.formatRupee(p.totalReturn), MARGIN_X + CONTENT_WIDTH - 6f, currentY + 15f, paint)

                paint.textAlign = Paint.Align.LEFT
                currentY += 22f
            }

            // TOTAL Row
            checkPageBreak(25f)
            paint.color = COLOR_ACCENT_BG
            val totRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 24f)
            canvas.drawRect(totRect, paint)

            paint.color = COLOR_PRIMARY_DARK
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TOTAL", MARGIN_X + 6f, currentY + 16f, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(CurrencyUtils.formatRupee(dealWithDetails.totalInvested), MARGIN_X + colWidths[0] + colWidths[1] - 6f, currentY + 16f, paint)

            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("100%", MARGIN_X + colWidths[0] + colWidths[1] + (colWidths[2] / 2f), currentY + 16f, paint)

            paint.color = if (dealWithDetails.profit >= 0) COLOR_PROFIT else COLOR_LOSS
            paint.textAlign = Paint.Align.RIGHT
            val totSign = if (dealWithDetails.profit >= 0) "+" else ""
            canvas.drawText("$totSign${CurrencyUtils.formatRupee(dealWithDetails.profit)}", MARGIN_X + colWidths[0] + colWidths[1] + colWidths[2] + colWidths[3] - 6f, currentY + 16f, paint)

            paint.color = COLOR_PROFIT
            canvas.drawText(CurrencyUtils.formatRupee(dealWithDetails.totalInvested + dealWithDetails.profit), MARGIN_X + CONTENT_WIDTH - 6f, currentY + 16f, paint)

            paint.textAlign = Paint.Align.LEFT
            currentY += 32f

            // Key Totals Banner below table
            checkPageBreak(50f)
            paint.color = COLOR_ROW_ALT
            val keyTotalsRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 44f)
            canvas.drawRoundRect(keyTotalsRect, 6f, 6f, paint)
            paint.color = COLOR_BORDER
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(keyTotalsRect, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            paint.textSize = 9.5f
            paint.color = COLOR_PRIMARY_DARK
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("📌 Total Investment: ${CurrencyUtils.formatRupee(dealWithDetails.totalInvested)}", MARGIN_X + 10f, currentY + 18f, paint)
            canvas.drawText("📌 Total Profit: ${CurrencyUtils.formatRupee(dealWithDetails.profit)}", MARGIN_X + 220f, currentY + 18f, paint)
            canvas.drawText("📌 Total Payout / Received: ${CurrencyUtils.formatRupee(dealWithDetails.totalInvested + dealWithDetails.profit)}", MARGIN_X + 10f, currentY + 36f, paint)

            currentY += 56f
        }

        // 4. Stock & Inventory Table
        if (dealWithDetails.stocks.isNotEmpty()) {
            checkPageBreak(100f)
            paint.color = COLOR_PRIMARY_DARK
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("🪵 WOOD STOCK & INVENTORY (${dealWithDetails.stocks.size} items)", MARGIN_X, currentY, paint)
            currentY += 10f

            val stockCols = floatArrayOf(150f, 150f, 120f, 103f)
            val stockHeaders = arrayOf("Item Name", "Category", "Quantity", "Status")

            // Header
            paint.color = COLOR_PRIMARY_MEDIUM
            canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 20f), 4f, 4f, paint)
            paint.color = Color.WHITE
            paint.textSize = 9f
            var sX = MARGIN_X
            for (i in stockHeaders.indices) {
                canvas.drawText(stockHeaders[i], sX + 6f, currentY + 14f, paint)
                sX += stockCols[i]
            }
            currentY += 20f

            dealWithDetails.stocks.forEachIndexed { index, s ->
                checkPageBreak(22f)
                val rowBg = if (index % 2 == 0) Color.WHITE else COLOR_ROW_ALT
                paint.color = rowBg
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 20f), paint)
                paint.color = COLOR_BORDER
                canvas.drawLine(MARGIN_X, currentY + 20f, MARGIN_X + CONTENT_WIDTH, currentY + 20f, paint)

                paint.color = COLOR_DARK_TEXT
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                canvas.drawText(s.itemName, MARGIN_X + 6f, currentY + 14f, paint)
                canvas.drawText("${s.category}", MARGIN_X + stockCols[0] + 6f, currentY + 14f, paint)
                canvas.drawText(s.quantity, MARGIN_X + stockCols[0] + stockCols[1] + 6f, currentY + 14f, paint)

                paint.color = if (s.isSold) COLOR_PRIMARY_MEDIUM else COLOR_PROFIT
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(if (s.isSold) "SOLD" else "IN STOCK", MARGIN_X + stockCols[0] + stockCols[1] + stockCols[2] + 6f, currentY + 14f, paint)

                currentY += 20f
            }
            currentY += 16f
        }

        // 5. Sales Breakdown Table
        if (dealWithDetails.sales.isNotEmpty()) {
            checkPageBreak(100f)
            paint.color = COLOR_PRIMARY_DARK
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("💰 SALES & CUSTOMER COLLECTIONS (${dealWithDetails.sales.size} sales)", MARGIN_X, currentY, paint)
            currentY += 10f

            val saleCols = floatArrayOf(140f, 110f, 90f, 90f, 93f)
            val saleHeaders = arrayOf("Item Sold", "Customer", "Total Price", "Received", "Pending")

            paint.color = COLOR_PRIMARY_MEDIUM
            canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 20f), 4f, 4f, paint)
            paint.color = Color.WHITE
            paint.textSize = 9f

            var saX = MARGIN_X
            for (i in saleHeaders.indices) {
                if (i >= 2) {
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(saleHeaders[i], saX + saleCols[i] - 6f, currentY + 14f, paint)
                } else {
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText(saleHeaders[i], saX + 6f, currentY + 14f, paint)
                }
                saX += saleCols[i]
            }
            paint.textAlign = Paint.Align.LEFT
            currentY += 20f

            dealWithDetails.sales.forEachIndexed { index, sale ->
                checkPageBreak(22f)
                val rowBg = if (index % 2 == 0) Color.WHITE else COLOR_ROW_ALT
                paint.color = rowBg
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 20f), paint)
                paint.color = COLOR_BORDER
                canvas.drawLine(MARGIN_X, currentY + 20f, MARGIN_X + CONTENT_WIDTH, currentY + 20f, paint)

                paint.color = COLOR_DARK_TEXT
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textAlign = Paint.Align.LEFT

                canvas.drawText(sale.itemName, MARGIN_X + 6f, currentY + 14f, paint)
                canvas.drawText(if (sale.customerName.isNotBlank()) sale.customerName else "-", MARGIN_X + saleCols[0] + 6f, currentY + 14f, paint)

                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(CurrencyUtils.formatRupee(sale.sellingPrice), MARGIN_X + saleCols[0] + saleCols[1] + saleCols[2] - 6f, currentY + 14f, paint)

                paint.color = COLOR_PROFIT
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(CurrencyUtils.formatRupee(sale.amountReceived), MARGIN_X + saleCols[0] + saleCols[1] + saleCols[2] + saleCols[3] - 6f, currentY + 14f, paint)

                paint.color = if (sale.amountPending > 0) COLOR_LOSS else COLOR_GRAY
                canvas.drawText(CurrencyUtils.formatRupee(sale.amountPending), MARGIN_X + CONTENT_WIDTH - 6f, currentY + 14f, paint)

                paint.textAlign = Paint.Align.LEFT
                currentY += 20f
            }
            currentY += 16f
        }

        // 6. Direct Expenses Table
        if (dealWithDetails.expenses.isNotEmpty()) {
            checkPageBreak(100f)
            paint.color = COLOR_PRIMARY_DARK
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("🧾 DIRECT EXPENSES (${dealWithDetails.expenses.size} entries)", MARGIN_X, currentY, paint)
            currentY += 10f

            val expCols = floatArrayOf(200f, 150f, 173f)
            val expHeaders = arrayOf("Expense Description", "Date", "Amount")

            paint.color = COLOR_PRIMARY_MEDIUM
            canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 20f), 4f, 4f, paint)
            paint.color = Color.WHITE
            paint.textSize = 9f

            var eX = MARGIN_X
            for (i in expHeaders.indices) {
                if (i == 2) {
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(expHeaders[i], eX + expCols[i] - 6f, currentY + 14f, paint)
                } else {
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText(expHeaders[i], eX + 6f, currentY + 14f, paint)
                }
                eX += expCols[i]
            }
            paint.textAlign = Paint.Align.LEFT
            currentY += 20f

            dealWithDetails.expenses.forEachIndexed { index, exp ->
                checkPageBreak(22f)
                val rowBg = if (index % 2 == 0) Color.WHITE else COLOR_ROW_ALT
                paint.color = rowBg
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 20f), paint)
                paint.color = COLOR_BORDER
                canvas.drawLine(MARGIN_X, currentY + 20f, MARGIN_X + CONTENT_WIDTH, currentY + 20f, paint)

                paint.color = COLOR_DARK_TEXT
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textAlign = Paint.Align.LEFT

                canvas.drawText("${exp.categoryIcon} ${exp.name}", MARGIN_X + 6f, currentY + 14f, paint)
                canvas.drawText(if (exp.date.isNotBlank()) exp.date else "-", MARGIN_X + expCols[0] + 6f, currentY + 14f, paint)

                paint.textAlign = Paint.Align.RIGHT
                paint.color = COLOR_PRIMARY_DARK
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(CurrencyUtils.formatRupee(exp.amount), MARGIN_X + CONTENT_WIDTH - 6f, currentY + 14f, paint)

                paint.textAlign = Paint.Align.LEFT
                currentY += 20f
            }
        }

        // Draw footer on last page
        drawPageFooter(canvas, paint, pageNumber, dealWithDetails.deal.name)
        pdfDocument.finishPage(page)

        // Save to cache directory
        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val sanitizedName = deal.name.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val fileName = "Deal_Report_${sanitizedName}_${System.currentTimeMillis()}.pdf"
            val file = File(reportsDir, fileName)

            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    /**
     * Exports the Overall Business Financial & Partner Analysis Report to PDF.
     */
    fun exportOverallReportToPdf(
        context: Context,
        summary: BusinessFinancialSummary,
        partnerAggregates: List<PartnerAggregate>,
        allDeals: List<DealWithDetails>
    ): File? {
        val pdfDocument = PdfDocument()
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var currentY = 36f

        fun checkPageBreak(requiredHeight: Float) {
            if (currentY + requiredHeight > MARGIN_BOTTOM) {
                drawPageFooter(canvas, paint, pageNumber, "Overall Business Report")
                pdfDocument.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                currentY = 40f

                drawSubsequentHeader(canvas, paint, "Overall Business Report")
                currentY += 30f
            }
        }

        // 1. Header Banner
        paint.color = COLOR_PRIMARY_DARK
        val bannerRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 68f)
        canvas.drawRoundRect(bannerRect, 10f, 10f, paint)

        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("OVERALL BUSINESS & PARTNERS REPORT", MARGIN_X + 16f, currentY + 28f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10.5f
        paint.color = Color.rgb(0xDE, 0xD7, 0xD2)
        val todayStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Report Date: $todayStr • Total Deals: ${allDeals.size} (${summary.activeDealsCount} Active, ${summary.completedDealsCount} Completed)", MARGIN_X + 16f, currentY + 48f, paint)

        currentY += 82f

        // 2. Metrics Cards Grid (2 rows x 3 columns)
        val mWidth = (CONTENT_WIDTH - 20f) / 3f
        val mHeight = 48f

        val gridMetrics = listOf(
            Triple("TOTAL SALES", CurrencyUtils.formatRupee(summary.totalSales), COLOR_PRIMARY_DARK),
            Triple("TOTAL DEALS COST", CurrencyUtils.formatRupee(summary.totalDealsCost), COLOR_PRIMARY_MEDIUM),
            Triple("NET PROFIT", CurrencyUtils.formatRupee(summary.totalProfit), if (summary.totalProfit >= 0) COLOR_PROFIT else COLOR_LOSS),
            Triple("TOTAL INVESTED", CurrencyUtils.formatRupee(summary.totalInvested), COLOR_PRIMARY_DARK),
            Triple("CASH IN HAND", CurrencyUtils.formatRupee(summary.totalCashReceived), COLOR_PROFIT),
            Triple("CUSTOMER PENDING", CurrencyUtils.formatRupee(summary.totalCustomerPending), if (summary.totalCustomerPending > 0) COLOR_LOSS else COLOR_GRAY)
        )

        for (i in gridMetrics.indices) {
            val row = i / 3
            val col = i % 3
            val left = MARGIN_X + (col * (mWidth + 10f))
            val top = currentY + (row * (mHeight + 8f))

            paint.color = COLOR_ACCENT_BG
            val r = RectF(left, top, left + mWidth, top + mHeight)
            canvas.drawRoundRect(r, 6f, 6f, paint)

            paint.color = COLOR_BORDER
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(r, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            paint.color = COLOR_GRAY
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(gridMetrics[i].first, left + 8f, top + 15f, paint)

            paint.color = gridMetrics[i].third
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(gridMetrics[i].second, left + 8f, top + 36f, paint)
        }

        currentY += (2 * (mHeight + 8f)) + 16f

        // 3. OVERALL PARTNER TABULAR ANALYSIS TABLE
        checkPageBreak(120f)
        paint.color = COLOR_PRIMARY_DARK
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("📊 ALL PARTNERS TABULAR ANALYSIS", MARGIN_X, currentY, paint)
        currentY += 12f

        val totalInvestedAll = partnerAggregates.sumOf { it.totalInvested }
        val totalProfitAll = partnerAggregates.sumOf { it.totalProfitShare }
        val totalReceivedAll = partnerAggregates.sumOf { it.totalReturn }

        val pColWidths = floatArrayOf(130f, 95f, 65f, 115f, 118f)
        val pHeaders = arrayOf("Partner Name", "Invested Amount", "Percentage", "Profit Amount", "Total Received")

        paint.color = COLOR_PRIMARY_DARK
        canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f), 4f, 4f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var pxOffset = MARGIN_X
        for (i in pHeaders.indices) {
            if (i == 0 || i == 2) {
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText(pHeaders[i], if (i == 0) pxOffset + 6f else pxOffset + 6f, currentY + 15f, paint)
            } else {
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(pHeaders[i], pxOffset + pColWidths[i] - 6f, currentY + 15f, paint)
            }
            pxOffset += pColWidths[i]
        }
        paint.textAlign = Paint.Align.LEFT
        currentY += 22f

        if (partnerAggregates.isEmpty()) {
            paint.color = COLOR_ROW_ALT
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f), paint)
            paint.color = COLOR_GRAY
            paint.textSize = 9f
            canvas.drawText("No partner records found.", MARGIN_X + 10f, currentY + 15f, paint)
            currentY += 22f
        } else {
            partnerAggregates.forEachIndexed { index, p ->
                checkPageBreak(22f)
                val rowBg = if (index % 2 == 0) Color.WHITE else COLOR_ROW_ALT
                paint.color = rowBg
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f), paint)
                paint.color = COLOR_BORDER
                canvas.drawLine(MARGIN_X, currentY + 22f, MARGIN_X + CONTENT_WIDTH, currentY + 22f, paint)

                paint.color = COLOR_DARK_TEXT
                paint.textSize = 9.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(p.name, MARGIN_X + 6f, currentY + 15f, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(CurrencyUtils.formatRupee(p.totalInvested), MARGIN_X + pColWidths[0] + pColWidths[1] - 6f, currentY + 15f, paint)

                val pct = if (totalInvestedAll > 0) (p.totalInvested / totalInvestedAll) * 100.0 else 0.0
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(CurrencyUtils.formatPercent(pct), MARGIN_X + pColWidths[0] + pColWidths[1] + (pColWidths[2] / 2f), currentY + 15f, paint)

                paint.color = if (p.totalProfitShare >= 0) COLOR_PROFIT else COLOR_LOSS
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.RIGHT
                val sign = if (p.totalProfitShare >= 0) "+" else ""
                canvas.drawText("$sign${CurrencyUtils.formatRupee(p.totalProfitShare)}", MARGIN_X + pColWidths[0] + pColWidths[1] + pColWidths[2] + pColWidths[3] - 6f, currentY + 15f, paint)

                paint.color = COLOR_PROFIT
                canvas.drawText(CurrencyUtils.formatRupee(p.totalReturn), MARGIN_X + CONTENT_WIDTH - 6f, currentY + 15f, paint)

                paint.textAlign = Paint.Align.LEFT
                currentY += 22f
            }

            // Totals Row
            checkPageBreak(25f)
            paint.color = COLOR_ACCENT_BG
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 24f), paint)

            paint.color = COLOR_PRIMARY_DARK
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TOTAL", MARGIN_X + 6f, currentY + 16f, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(CurrencyUtils.formatRupee(totalInvestedAll), MARGIN_X + pColWidths[0] + pColWidths[1] - 6f, currentY + 16f, paint)

            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("100%", MARGIN_X + pColWidths[0] + pColWidths[1] + (pColWidths[2] / 2f), currentY + 16f, paint)

            paint.color = if (totalProfitAll >= 0) COLOR_PROFIT else COLOR_LOSS
            paint.textAlign = Paint.Align.RIGHT
            val tSign = if (totalProfitAll >= 0) "+" else ""
            canvas.drawText("$tSign${CurrencyUtils.formatRupee(totalProfitAll)}", MARGIN_X + pColWidths[0] + pColWidths[1] + pColWidths[2] + pColWidths[3] - 6f, currentY + 16f, paint)

            paint.color = COLOR_PROFIT
            canvas.drawText(CurrencyUtils.formatRupee(totalReceivedAll), MARGIN_X + CONTENT_WIDTH - 6f, currentY + 16f, paint)

            paint.textAlign = Paint.Align.LEFT
            currentY += 32f

            // Key Totals Banner below table
            checkPageBreak(50f)
            paint.color = COLOR_ROW_ALT
            val keyTotalsRect = RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 44f)
            canvas.drawRoundRect(keyTotalsRect, 6f, 6f, paint)
            paint.color = COLOR_BORDER
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(keyTotalsRect, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            paint.textSize = 9.5f
            paint.color = COLOR_PRIMARY_DARK
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("📌 Total Investment: ${CurrencyUtils.formatRupee(totalInvestedAll)}", MARGIN_X + 10f, currentY + 18f, paint)
            canvas.drawText("📌 Total Profit: ${CurrencyUtils.formatRupee(totalProfitAll)}", MARGIN_X + 220f, currentY + 18f, paint)
            canvas.drawText("📌 Total Payout / Received: ${CurrencyUtils.formatRupee(totalReceivedAll)}", MARGIN_X + 10f, currentY + 36f, paint)

            currentY += 56f
        }

        // 4. DEALS SUMMARY TABLE
        if (allDeals.isNotEmpty()) {
            checkPageBreak(120f)
            paint.color = COLOR_PRIMARY_DARK
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("📋 ALL DEALS BREAKDOWN", MARGIN_X, currentY, paint)
            currentY += 12f

            val dCols = floatArrayOf(150f, 90f, 90f, 95f, 98f)
            val dHeaders = arrayOf("Deal Name", "Status", "Total Cost", "Total Sales", "Net Profit")

            paint.color = COLOR_PRIMARY_MEDIUM
            canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f), 4f, 4f, paint)

            paint.color = Color.WHITE
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            var dxOffset = MARGIN_X
            for (i in dHeaders.indices) {
                if (i >= 2) {
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(dHeaders[i], dxOffset + dCols[i] - 6f, currentY + 15f, paint)
                } else {
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText(dHeaders[i], dxOffset + 6f, currentY + 15f, paint)
                }
                dxOffset += dCols[i]
            }
            paint.textAlign = Paint.Align.LEFT
            currentY += 22f

            allDeals.forEachIndexed { index, dealItem ->
                checkPageBreak(22f)
                val rowBg = if (index % 2 == 0) Color.WHITE else COLOR_ROW_ALT
                paint.color = rowBg
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + CONTENT_WIDTH, currentY + 22f), paint)
                paint.color = COLOR_BORDER
                canvas.drawLine(MARGIN_X, currentY + 22f, MARGIN_X + CONTENT_WIDTH, currentY + 22f, paint)

                paint.color = COLOR_DARK_TEXT
                paint.textSize = 9f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText(dealItem.deal.name, MARGIN_X + 6f, currentY + 15f, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.color = if (dealItem.deal.isCompleted) COLOR_PROFIT else COLOR_PRIMARY_MEDIUM
                canvas.drawText(if (dealItem.deal.isCompleted) "Completed" else "In Progress", MARGIN_X + dCols[0] + 6f, currentY + 15f, paint)

                paint.textAlign = Paint.Align.RIGHT
                paint.color = COLOR_DARK_TEXT
                canvas.drawText(CurrencyUtils.formatRupee(dealItem.totalCost), MARGIN_X + dCols[0] + dCols[1] + dCols[2] - 6f, currentY + 15f, paint)
                canvas.drawText(CurrencyUtils.formatRupee(dealItem.totalSales), MARGIN_X + dCols[0] + dCols[1] + dCols[2] + dCols[3] - 6f, currentY + 15f, paint)

                paint.color = if (dealItem.profit >= 0) COLOR_PROFIT else COLOR_LOSS
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val dSign = if (dealItem.profit >= 0) "+" else ""
                canvas.drawText("$dSign${CurrencyUtils.formatRupee(dealItem.profit)}", MARGIN_X + CONTENT_WIDTH - 6f, currentY + 15f, paint)

                paint.textAlign = Paint.Align.LEFT
                currentY += 22f
            }
        }

        drawPageFooter(canvas, paint, pageNumber, "Overall Business Report")
        pdfDocument.finishPage(page)

        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val fileName = "Overall_Business_Report_${System.currentTimeMillis()}.pdf"
            val file = File(reportsDir, fileName)

            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun drawSubsequentHeader(canvas: Canvas, paint: Paint, title: String) {
        paint.color = COLOR_PRIMARY_DARK
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("WOOD DEAL REPORT • $title", MARGIN_X, 35f, paint)

        paint.color = COLOR_BORDER
        paint.strokeWidth = 1f
        canvas.drawLine(MARGIN_X, 42f, MARGIN_X + CONTENT_WIDTH, 42f, paint)
    }

    private fun drawPageFooter(canvas: Canvas, paint: Paint, pageNumber: Int, docTitle: String) {
        paint.color = COLOR_BORDER
        paint.strokeWidth = 0.8f
        canvas.drawLine(MARGIN_X, MARGIN_BOTTOM + 5f, MARGIN_X + CONTENT_WIDTH, MARGIN_BOTTOM + 5f, paint)

        paint.color = COLOR_GRAY
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.LEFT
        val todayStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generated by Wood Deal Accounting App • $todayStr", MARGIN_X, MARGIN_BOTTOM + 20f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Page $pageNumber", MARGIN_X + CONTENT_WIDTH, MARGIN_BOTTOM + 20f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    /**
     * Launches the Android Share sheet to share the generated PDF file (WhatsApp, Drive, Gmail, etc.)
     */
    fun sharePdf(context: Context, pdfFile: File, chooserTitle: String = "Share PDF Report") {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, pdfFile.nameWithoutExtension)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, chooserTitle)
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the generated PDF in an installed PDF viewer app.
     */
    fun viewPdf(context: Context, pdfFile: File) {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(viewIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            // If no PDF viewer found, fallback to sharing
            sharePdf(context, pdfFile, "Open/Share PDF Report")
        }
    }
}

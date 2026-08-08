package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.*

actual object ReceiptPrinter {

    // 1. Single Order Print (Thermal ya A4)
    actual fun printReceipt(order: OrderDetails, mode: PrintMode) {
        val printerJob = PrinterJob.getPrinterJob()
        val pageFormat = printerJob.defaultPage()
        val paper = pageFormat.paper

        when (mode) {
            PrintMode.THERMAL -> {
                paper.setSize(226.0, 800.0)
                paper.setImageableArea(0.0, 0.0, 226.0, 800.0)
            }
            PrintMode.A4 -> {
                paper.setSize(595.0, 842.0)
                paper.setImageableArea(20.0, 20.0, 555.0, 802.0)
            }
        }
        pageFormat.paper = paper

        val printable = if (mode == PrintMode.THERMAL) ThermalReceiptPrintable(order)
        else DetailedReportPrintable(order)

        printerJob.setPrintable(printable, pageFormat)

        try {
            if (printerJob.printDialog()) {
                printerJob.print()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 2. NAYA: Batch Order Print (Poori List ka Report)
   actual fun printAllOrders(orders: List<OrderDetails>,title: String) {
        val printerJob = PrinterJob.getPrinterJob()
        val pageFormat = printerJob.defaultPage()
        val paper = pageFormat.paper

        // A4 Settings
        paper.setSize(595.0, 842.0)
        paper.setImageableArea(20.0, 20.0, 555.0, 802.0)
        pageFormat.paper = paper

        // List pass kar rahe hain
        printerJob.setPrintable(AllOrdersReportPrintable(orders = orders,title=title), pageFormat)

        try {
            if (printerJob.printDialog()) {
                printerJob.print()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// Thermal Receipt Design
class ThermalReceiptPrintable(private val order: OrderDetails) : Printable {
    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE
        val g2d = graphics as Graphics2D
        g2d.translate(pageFormat?.imageableX ?: 0.0, pageFormat?.imageableY ?: 0.0)

        g2d.font = Font("Monospaced", Font.BOLD, 12)
        g2d.drawString("X CAFE RECEIPT", 20, 20)
        g2d.drawString("Customer: ${order.userName}", 5, 40)
        g2d.drawString("Total: Rs ${order.totalPrice}", 5, 60)

        return Printable.PAGE_EXISTS
    }
}

// Single Detailed A4 Design
class DetailedReportPrintable(private val order: OrderDetails) : Printable {
    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE
        val g2d = graphics as Graphics2D
        g2d.translate(pageFormat?.imageableX ?: 0.0, pageFormat?.imageableY ?: 0.0)

        g2d.font = Font("Serif", Font.BOLD, 24)
        g2d.drawString("ORDER INVOICE", 100, 50)

        g2d.font = Font("Serif", Font.PLAIN, 14)
        g2d.drawString("Customer: ${order.userName}", 50, 90)
        g2d.drawString("Total: Rs ${order.totalPrice}", 50, 110)

        return Printable.PAGE_EXISTS
    }
}

// BATCH REPORT DESIGN (Poori List ek saath)
class AllOrdersReportPrintable(
    private val orders: List<OrderDetails>,
    private val title: String
) : Printable {

    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        val g2d = graphics as? Graphics2D ?: return Printable.NO_SUCH_PAGE

        // Page Margin / Printable Area setup
        val marginX = pageFormat?.imageableX ?: 50.0
        val marginY = pageFormat?.imageableY ?: 50.0
        g2d.translate(marginX, marginY)

        val pageHeight = (pageFormat?.imageableHeight ?: 700.0).toInt()
        val maxY = pageHeight - 50 // Bottom margin cutoff

        // Track Current Page and Y-position across orders
        var currentPage = 0
        var y = 50

        // Function to draw Header on top of every page
        fun drawHeader() {
            g2d.font = Font("Serif", Font.BOLD, 18)
            g2d.drawString(title, 20, y)
            y += 25

            g2d.font = Font("Serif", Font.BOLD, 11)
            g2d.drawLine(20, y, 500, y)
            y += 15

            g2d.drawString("Customer Name / Items", 20, y)
            g2d.drawString("Qty", 320, y)
            g2d.drawString("Price", 380, y)
            g2d.drawString("Total", 450, y)
            y += 10

            g2d.drawLine(20, y, 500, y)
            y += 20
        }

        // Draw header for the starting calculation of current rendering
        drawHeader()

        for (order in orders) {
            // Predict if this whole order header will fit on the page
            if (y + 40 > maxY) {
                currentPage++
                y = 50
                if (currentPage == pageIndex) {
                    drawHeader()
                }
            }

            // Draw Order Header (Only if rendering current requested page)
            if (currentPage == pageIndex) {
                g2d.font = Font("Serif", Font.BOLD, 12)
                g2d.drawString("Order: ${order.userName ?: "Guest"} (${order.phoneNumber ?: "N/A"})", 20, y)
                g2d.drawString("Rs ${order.totalPrice ?: "0"}", 450, y)
                y += 18
            }

            // Draw Sub-items (Foods list)
            val items = order.foodName ?: emptyList()
            val quantities = order.foodQuantity ?: emptyList()
            val prices = order.foodPrice ?: emptyList()

            for (i in items.indices) {
                // Check page limit for each item row
                if (y + 20 > maxY) {
                    currentPage++
                    y = 50
                    if (currentPage == pageIndex) {
                        drawHeader()
                    }
                }

                if (currentPage == pageIndex) {
                    val itemName = items.getOrNull(i) ?: ""
                    val qty = quantities.getOrNull(i) ?: 1
                    val price = prices.getOrNull(i) ?: "0"

                    g2d.font = Font("Serif", Font.PLAIN, 11)
                    g2d.drawString("- $itemName", 40, y)
                    g2d.drawString("x$qty", 320, y)
                    g2d.drawString("Rs $price", 380, y)
                    y += 16
                }
            }

            // Draw separator line between orders
            if (currentPage == pageIndex) {
                g2d.drawLine(20, y, 500, y)
                y += 15
            }
        }

        // Return Page Status based on requested pageIndex
        return if (pageIndex <= currentPage) {
            Printable.PAGE_EXISTS
        } else {
            Printable.NO_SUCH_PAGE
        }
    }
}
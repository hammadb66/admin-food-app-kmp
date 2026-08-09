package org.example.adminfoodapp.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.database.model.PrintOrderDetails
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.*

actual object ReceiptPrinter {

    // Background Scope taake UI bill print hone tak hang na ho
    private val printScope = CoroutineScope(Dispatchers.IO)

    // 1. Single Order Print (Thermal ya A4)
    actual fun printReceipt(order: PrintOrderDetails, mode: PrintMode) {
        printScope.launch {
            try {
                val printerJob = PrinterJob.getPrinterJob()
                val pageFormat = printerJob.defaultPage()
                val paper = pageFormat.paper

                when (mode) {
                    PrintMode.THERMAL -> {
                        // 80mm Thermal paper dimension
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

                // 🚀 SILENT PRINTING: Dialog box (printDialog) skip kar ke direct system default printer par print karega
                printerJob.print()

            } catch (e: Exception) {
                println("Printing Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // 2. Batch Order Print (Poori List ka Report)
    actual fun printAllOrders(orders: List<PrintOrderDetails>, title: String) {
        printScope.launch {
            try {
                val printerJob = PrinterJob.getPrinterJob()
                val pageFormat = printerJob.defaultPage()
                val paper = pageFormat.paper

                // A4 Settings
                paper.setSize(595.0, 842.0)
                paper.setImageableArea(20.0, 20.0, 555.0, 802.0)
                pageFormat.paper = paper

                printerJob.setPrintable(AllOrdersReportPrintable(orders = orders, title = title), pageFormat)

                // 🚀 SILENT PRINTING for Batch Reports
                printerJob.print()

            } catch (e: Exception) {
                println("Batch Printing Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

// -------------------------------------------------------------
// 1. THERMAL RECEIPT DESIGN (Small Paper 80mm)
// -------------------------------------------------------------
class ThermalReceiptPrintable(private val order: PrintOrderDetails) : Printable {
    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE
        val g2d = graphics as? Graphics2D ?: return Printable.NO_SUCH_PAGE

        g2d.translate(pageFormat?.imageableX ?: 0.0, pageFormat?.imageableY ?: 0.0)

        var y = 15

        // Header
        g2d.font = Font("Monospaced", Font.BOLD, 12)
        g2d.drawString("=== X CAFE RECEIPT ===", 10, y)
        y += 18

        g2d.font = Font("Monospaced", Font.PLAIN, 9)
        g2d.drawString("Customer: ${order.userName?.ifEmpty { "Guest" }}", 5, y)
        y += 12
        if (!order.phoneNumber.isNull_or_blank()) {
            g2d.drawString("Phone: ${order.phoneNumber}", 5, y)
            y += 12
        }

        g2d.drawString("--------------------------------", 5, y)
        y += 12

        // Table Header
        g2d.font = Font("Monospaced", Font.BOLD, 9)
        g2d.drawString("Item", 5, y)
        g2d.drawString("Qty", 70, y)
        g2d.drawString("Price", 110, y)
        g2d.drawString("Total", 160, y)
        y += 12

        g2d.font = Font("Monospaced", Font.PLAIN, 9)
        g2d.drawString("--------------------------------", 5, y)
        y += 12

        // Items Loop
        val items = order.foodName ?: emptyList()
        val quantities = order.foodQuantity ?: emptyList()
        val prices = order.foodPrice ?: emptyList()

        for (i in items.indices) {
            val name = items.getOrNull(i) ?: ""
            val qty = quantities.getOrNull(i) ?: 1
            val priceStr = prices.getOrNull(i) ?: "0"

            val priceDouble = priceStr.toDoubleOrNull() ?: 0.0
            val priceInt = priceDouble.toInt()
            val itemTotal = qty * priceInt

            val displayName = if (name.length > 12) name.substring(0, 12) + ".." else name

            g2d.drawString(displayName, 5, y)
            g2d.drawString("x$qty", 70, y)
            g2d.drawString("$priceInt", 110, y)
            g2d.drawString("Rs $itemTotal", 160, y)
            y += 14
        }

        g2d.drawString("--------------------------------", 5, y)
        y += 14

// Subtotal & Discount (agar discount hai to hi dikhega)
        g2d.font = Font("Monospaced", Font.PLAIN, 9)
        if (!order.subtotal.isNullOrBlank()) {
            g2d.drawString("Subtotal: Rs ${order.subtotal}", 5, y)
            y += 12
        }
        if (!order.discountAmount.isNullOrBlank() && order.discountAmount != "0") {
            g2d.drawString("Discount (${order.discountLabel ?: ""}): -Rs ${order.discountAmount}", 5, y)
            y += 12
        }

// Grand Total
        val grandTotal = order.totalPrice?.toDoubleOrNull()?.toInt() ?: order.totalPrice ?: "0"
        g2d.font = Font("Monospaced", Font.BOLD, 11)
        g2d.drawString("TOTAL: Rs $grandTotal", 5, y)
        y += 20

        g2d.font = Font("Monospaced", Font.ITALIC, 8)
        g2d.drawString("Thank you for visiting!", 30, y)

        return Printable.PAGE_EXISTS
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty() || this == "null"
    }
}

// -------------------------------------------------------------
// 2. SINGLE DETAILED A4 INVOICE DESIGN
// -------------------------------------------------------------
class DetailedReportPrintable(private val order: PrintOrderDetails) : Printable {
    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE
        val g2d = graphics as? Graphics2D ?: return Printable.NO_SUCH_PAGE

        g2d.translate(pageFormat?.imageableX ?: 0.0, pageFormat?.imageableY ?: 0.0)

        var y = 40

        g2d.font = Font("Serif", Font.BOLD, 22)
        g2d.drawString("ORDER INVOICE", 40, y)
        y += 30

        g2d.font = Font("Serif", Font.PLAIN, 12)
        g2d.drawString("Customer Name : ${order.userName ?: "Guest"}", 40, y)
        y += 18
        g2d.drawString("Phone Number  : ${order.phoneNumber ?: "N/A"}", 40, y)
        y += 18
        if (!order.address.isNullOrEmpty() && order.address != "N/A") {
            g2d.drawString("Address       : ${order.address}", 40, y)
            y += 18
        }

        y += 10
        g2d.drawLine(40, y, 520, y)
        y += 20

        g2d.font = Font("Serif", Font.BOLD, 12)
        g2d.drawString("Item Description", 40, y)
        g2d.drawString("Quantity", 300, y)
        g2d.drawString("Unit Price", 380, y)
        g2d.drawString("Total Price", 460, y)
        y += 10

        g2d.drawLine(40, y, 520, y)
        y += 20

        val items = order.foodName ?: emptyList()
        val quantities = order.foodQuantity ?: emptyList()
        val prices = order.foodPrice ?: emptyList()

        g2d.font = Font("Serif", Font.PLAIN, 12)
        for (i in items.indices) {
            val itemName = items.getOrNull(i) ?: ""
            val qty = quantities.getOrNull(i) ?: 1
            val unitPrice = prices.getOrNull(i) ?: "0"

            val numericPrice = unitPrice.filter { it.isDigit() }.toIntOrNull() ?: 0
            val itemTotal = numericPrice * qty

            g2d.drawString(itemName, 40, y)
            g2d.drawString("x$qty", 300, y)
            g2d.drawString("Rs $unitPrice", 380, y)
            g2d.drawString("Rs $itemTotal", 460, y)
            y += 20
        }

        y += 10
        g2d.drawLine(40, y, 520, y)
        y += 20

// Subtotal & Discount rows (agar available hain)
        g2d.font = Font("Serif", Font.PLAIN, 12)
        if (!order.subtotal.isNullOrBlank()) {
            g2d.drawString("Subtotal:", 350, y)
            g2d.drawString("Rs ${order.subtotal}", 460, y)
            y += 18
        }
        if (!order.discountAmount.isNullOrBlank() && order.discountAmount != "0") {
            g2d.drawString("Discount (${order.discountLabel ?: ""}):", 350, y)
            g2d.drawString("-Rs ${order.discountAmount}", 460, y)
            y += 18
        }

        y += 5
        g2d.font = Font("Serif", Font.BOLD, 14)
        g2d.drawString("Grand Total:", 350, y)
        g2d.drawString("Rs ${order.totalPrice ?: "0"}", 460, y)

        return Printable.PAGE_EXISTS
    }
}

// -------------------------------------------------------------
// 3. BATCH REPORT DESIGN
// -------------------------------------------------------------
class AllOrdersReportPrintable(
    private val orders: List<PrintOrderDetails>,
    private val title: String
) : Printable {

    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        val g2d = graphics as? Graphics2D ?: return Printable.NO_SUCH_PAGE

        val marginX = pageFormat?.imageableX ?: 50.0
        val marginY = pageFormat?.imageableY ?: 50.0
        g2d.translate(marginX, marginY)

        val pageHeight = (pageFormat?.imageableHeight ?: 700.0).toInt()
        val maxY = pageHeight - 50

        var currentPage = 0
        var y = 50

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

        drawHeader()

        for (order in orders) {
            if (y + 40 > maxY) {
                currentPage++
                y = 50
                if (currentPage == pageIndex) {
                    drawHeader()
                }
            }

            if (currentPage == pageIndex) {
                g2d.font = Font("Serif", Font.BOLD, 12)
                g2d.drawString("Order: ${order.userName ?: "Guest"} (${order.phoneNumber ?: "N/A"})", 20, y)
                g2d.drawString("Rs ${order.totalPrice ?: "0"}", 450, y)
                y += 16

                // Chhoti si discount line (agar hai to hi)
                if (!order.discountAmount.isNullOrBlank() && order.discountAmount != "0") {
                    g2d.font = Font("Serif", Font.ITALIC, 10)
                    g2d.drawString("  (Subtotal: Rs${order.subtotal} - Discount: Rs${order.discountAmount})", 20, y)
                    y += 14
                } else {
                    y += 2
                }
            }

            val items = order.foodName ?: emptyList()
            val quantities = order.foodQuantity ?: emptyList()
            val prices = order.foodPrice ?: emptyList()

            for (i in items.indices) {
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

            if (currentPage == pageIndex) {
                g2d.drawLine(20, y, 500, y)
                y += 15
            }
        }

        return if (pageIndex <= currentPage) {
            Printable.PAGE_EXISTS
        } else {
            Printable.NO_SUCH_PAGE
        }
    }
}
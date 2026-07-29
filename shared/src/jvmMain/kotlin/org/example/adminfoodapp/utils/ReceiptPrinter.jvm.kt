package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.*

actual object ReceiptPrinter {
    actual fun printReceipt(order: OrderDetails) {
        val printerJob = PrinterJob.getPrinterJob()
        val pageFormat = printerJob.defaultPage()
        val paper = pageFormat.paper

        val width = 226.0
        val height = 600.0
        paper.setSize(width, height)
        paper.setImageableArea(0.0, 0.0, width, height)
        pageFormat.paper = paper

        printerJob.setPrintable(DesktopReceiptPrintable(order), pageFormat)

        try {
            if (printerJob.printDialog()) {
                printerJob.print()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class DesktopReceiptPrintable(private val order: OrderDetails) : Printable {
    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE

        val g2d = graphics as Graphics2D
        g2d.translate(pageFormat?.imageableX ?: 0.0, pageFormat?.imageableY ?: 0.0)

        var y = 20
        val fontTitle = Font("Monospaced", Font.BOLD, 14)
        val fontBold = Font("Monospaced", Font.BOLD, 9)
        val fontNormal = Font("Monospaced", Font.PLAIN, 8)

        // Header
        g2d.font = fontTitle
        g2d.drawString("X CAFE", 60, y)
        y += 15

        g2d.font = fontNormal
        g2d.drawString("---------------------------------", 5, y)
        y += 10
        g2d.drawString("Customer: ${order.userName ?: "N/A"}", 5, y)
        y += 10
        g2d.drawString("Phone: ${order.phoneNumber ?: "N/A"}", 5, y)
        y += 10
        g2d.drawString("Address: ${order.address ?: "N/A"}", 5, y)
        y += 10
        g2d.drawString("---------------------------------", 5, y)
        y += 12

        // Table Header
        g2d.font = fontBold
        g2d.drawString("Item", 5, y)
        g2d.drawString("Qty", 125, y)
        g2d.drawString("Price", 165, y)
        y += 10
        g2d.font = fontNormal
        g2d.drawString("---------------------------------", 5, y)
        y += 10

        // Items
        val items = order.foodName ?: emptyList()
        val quantities = order.foodQuantity ?: emptyList()
        val prices = order.foodPrice ?: emptyList()

        for (i in items.indices) {
            val name = items.getOrNull(i) ?: ""
            val qty = quantities.getOrNull(i) ?: 1
            val price = prices.getOrNull(i) ?: "0"

            val displayName = if (name.length > 18) name.substring(0, 16) + ".." else name

            g2d.drawString(displayName, 5, y)
            g2d.drawString("x$qty", 130, y)
            g2d.drawString("Rs $price", 165, y)
            y += 12
        }

        y += 5
        g2d.drawString("---------------------------------", 5, y)
        y += 12
        g2d.font = fontBold
        g2d.drawString("TOTAL AMOUNT:", 5, y)
        g2d.drawString("Rs ${order.totalPrice ?: "0"}", 145, y)
        y += 15

        g2d.font = fontNormal
        g2d.drawString("--- Thank You For Ordering! ---", 15, y)

        return Printable.PAGE_EXISTS
    }
}
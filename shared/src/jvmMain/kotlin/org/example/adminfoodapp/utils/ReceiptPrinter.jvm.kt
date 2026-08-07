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
class AllOrdersReportPrintable(private val orders: List<OrderDetails>,private val title: String) : Printable {
    override fun print(graphics: Graphics?, pageFormat: PageFormat?, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE
        val g2d = graphics as Graphics2D
        g2d.translate(pageFormat?.imageableX ?: 0.0, pageFormat?.imageableY ?: 0.0)

        g2d.font = Font("Serif", Font.BOLD, 20)
        g2d.drawString(title, 50, 50)

        g2d.font = Font("Serif", Font.BOLD, 12)
        g2d.drawLine(50, 70, 500, 70)
        g2d.drawString("Customer Name", 60, 85)
        g2d.drawString("Total Price", 400, 85)
        g2d.drawLine(50, 95, 500, 95)

        var y = 115
        g2d.font = Font("Serif", Font.PLAIN, 12)

        for (order in orders) {
            g2d.drawString(order.userName.toString(), 60, y)
            g2d.drawString("Rs ${order.totalPrice}", 400, y)
            y += 20

            // Agar page khatam hone lage to yahan break lag sakta hai
            if (y > 750) break
        }

        g2d.drawLine(50, y, 500, y)
        return Printable.PAGE_EXISTS
    }
}
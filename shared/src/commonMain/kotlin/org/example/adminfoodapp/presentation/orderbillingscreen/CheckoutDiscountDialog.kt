package org.example.adminfoodapp.presentation.orderbillingscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.adminfoodapp.utils.DISCOUNTTYPE

private val PrimaryAccent = Color(0xFF059669)
private val GoldAccent = Color(0xFFD97706)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)

@Composable
fun CheckoutDiscountDialog(
    subtotal: Double,
    discountType: DISCOUNTTYPE,
    discountInput: String,
    discountAmount: Double,
    finalTotal: Double,
    onDiscountTypeChange: (DISCOUNTTYPE) -> Unit,
    onDiscountInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column {
                Text(
                    "Discount daalo (agar chahiye), phir confirm karo.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = discountType == DISCOUNTTYPE.PERCENTAGE,
                        onClick = { onDiscountTypeChange(DISCOUNTTYPE.PERCENTAGE) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) { Text("% Percent", fontSize = 11.sp) }
                    SegmentedButton(
                        selected = discountType == DISCOUNTTYPE.AMOUNT,
                        onClick = { onDiscountTypeChange(DISCOUNTTYPE.AMOUNT) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) { Text("Rs. Amount", fontSize = 11.sp) }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = discountInput,
                    onValueChange = onDiscountInputChange,
                    label = {
                        Text(
                            if (discountType == DISCOUNTTYPE.PERCENTAGE) "Discount %" else "Discount Rs.",
                            fontSize = 11.sp
                        )
                    },
                    placeholder = { Text("0", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(
                            if (discountType == DISCOUNTTYPE.PERCENTAGE) Icons.Default.Info else Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent, unfocusedBorderColor = Color.White
                    )
                )

                Spacer(Modifier.height(14.dp))

                // 👈 Real-time preview — type karte hi ye update hoga
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 12.sp, color = TextSecondary)
                            Text("Rs. ${subtotal.toInt()}", fontSize = 12.sp, color = TextPrimary)
                        }
                        if (discountAmount > 0) {
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                val label = if (discountType == DISCOUNTTYPE.PERCENTAGE)
                                    "Discount (${discountInput}%)" else "Discount (Flat)"
                                Text(label, fontSize = 12.sp, color = GoldAccent)
                                Text(
                                    "- Rs. ${discountAmount.toInt()}",
                                    fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        HorizontalDivider(color = CardBorderColor)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Final Total", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                "Rs. ${finalTotal.toInt()}",
                                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryAccent
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                Text("Confirm & Checkout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", fontSize = 12.sp) }
        }
    )
}
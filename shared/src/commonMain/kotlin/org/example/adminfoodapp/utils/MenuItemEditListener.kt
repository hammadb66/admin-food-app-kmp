package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.AllMenu

interface MenuItemEditListener {
    fun onMenuItemEdit(item: AllMenu)
}
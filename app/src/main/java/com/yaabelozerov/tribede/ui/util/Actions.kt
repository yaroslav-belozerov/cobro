package com.yaabelozerov.tribede.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.NoDrinks
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Water
import androidx.compose.ui.graphics.vector.ImageVector

enum class Actions(val id: Int, val icon: ImageVector, val title: String) {
    PC_PROBLEM(0, Icons.Outlined.Computer, "Проблема с компьютером"),
    PLACE_PROBLEM(1, Icons.Outlined.Chair, "Проблема с местом"),

    NO_WATER(2, Icons.Outlined.Water, "Вода в кулере закончилась"),
    NO_CUPS(3, Icons.Outlined.NoDrinks, "Вода в кулере закончилась"),
    DISTURB(4, Icons.Outlined.PersonOff, "Мне мешают"),

    ASK_COFFE(5, Icons.Outlined.Coffee, "Принести кофе"),
    ASK_TEA(6, Icons.Outlined.Coffee, "Принести чай"),

    OTHER(7, Icons.Outlined.Bookmark, "Другое");

}
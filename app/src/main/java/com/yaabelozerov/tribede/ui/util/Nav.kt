package com.yaabelozerov.tribede.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class NavIcon(val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

enum class Nav(val route: String, val icon: NavIcon?) {
    BOOK("book", NavIcon(selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)),
    USER("user", NavIcon(selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person)),
    SCAN("scan", null),
    USER_DETAILED("userDetailed", null),
}

package com.yaabelozerov.tribede.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

data class NavIcon(val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

enum class Nav(val route: String, val icon: NavIcon?) {
    AUTH("auth", null)
}

package com.example.stocktracker.tampilan

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")

    // UBAH BAGIAN INI:
    // Kita siapkan slot "itemId" yang sifatnya opsional
    data object AddEdit : Screen("add_edit_barang?itemId={itemId}") {
        fun createRoute(itemId: Long? = null): String {
            return "add_edit_barang?itemId=${itemId ?: -1L}"
        }
    }

    data object Transaction : Screen("transaction")
    data object Report : Screen("report")
}
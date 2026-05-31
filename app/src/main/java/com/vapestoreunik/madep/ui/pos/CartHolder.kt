package com.vapestoreunik.madep.ui.pos

import com.vapestoreunik.madep.domain.model.Cart
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Singleton state untuk share Cart antara PosScreen (input) dan CheckoutScreen (read).
 * Dipakai sebagai bridge — di-clear setelah checkout sukses.
 */
@Singleton
class CartHolder @Inject constructor() {
    val cart = MutableStateFlow(Cart())
    fun set(c: Cart) { cart.value = c }
    fun reset() { cart.value = Cart() }
}

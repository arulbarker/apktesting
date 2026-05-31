package com.vapestoreunik.madep

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object SetupWizard : NavKey
@Serializable data object PinLogin : NavKey
@Serializable data object MainScaffold : NavKey
@Serializable data object Checkout : NavKey
@Serializable data class Receipt(val transactionId: Long) : NavKey
@Serializable data class ProductForm(val productId: Long? = null) : NavKey
@Serializable data object CategoryManage : NavKey
@Serializable data object Reports : NavKey
@Serializable data object Settings : NavKey
@Serializable data object ChangePin : NavKey
@Serializable data object BackupRestore : NavKey
@Serializable data object StoreProfile : NavKey

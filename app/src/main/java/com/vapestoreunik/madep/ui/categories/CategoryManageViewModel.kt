package com.vapestoreunik.madep.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryManageState(val categories: List<CategoryEntity> = emptyList())

@HiltViewModel
class CategoryManageViewModel @Inject constructor(
    private val repo: CategoryRepository,
) : ViewModel() {
    val state: kotlinx.coroutines.flow.StateFlow<CategoryManageState> = repo.observeAll()
        .map { CategoryManageState(categories = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryManageState())

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun add(name: String) = viewModelScope.launch {
        if (name.isBlank()) return@launch
        repo.upsert(CategoryEntity(name = name.trim(), sortOrder = state.value.categories.size))
    }

    fun rename(category: CategoryEntity, newName: String) = viewModelScope.launch {
        if (newName.isBlank()) return@launch
        repo.upsert(category.copy(name = newName.trim()))
    }

    fun delete(category: CategoryEntity) = viewModelScope.launch {
        repo.delete(category).onFailure { _error.value = it.message }
    }

    fun clearError() { _error.value = null }
}

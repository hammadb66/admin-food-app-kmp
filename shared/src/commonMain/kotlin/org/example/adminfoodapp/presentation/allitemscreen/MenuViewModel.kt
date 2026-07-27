package org.example.adminfoodapp.presentation.allitemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.domain.usecase.GetMenuItemsUseCase
import org.example.adminfoodapp.domain.usecase.MenuItemDeleteUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MenuViewModel (
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
    private val menuItemDeleteUseCase: MenuItemDeleteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<MutableList<AllMenu>>>(Resource.Loading)
    val state: StateFlow<Resource<MutableList<AllMenu>>> = _state
    private val _stateDelete = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val stateDelete = _stateDelete

    init {
        getMenu()
    }

     fun getMenu() {
        viewModelScope.launch {
            getMenuItemsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        _state.value = Resource.Success(result.data)
                    }

                    is Resource.Error -> {
                        _state.value = Resource.Error(result.message)
                    }
                }
            }
        }
    }

    fun deleteMenu(id: String) {

        viewModelScope.launch {
            menuItemDeleteUseCase.invoke(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _stateDelete.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        _stateDelete.value = Resource.Success(result.data)
                    }

                    is Resource.Error -> {
                        _stateDelete.value = Resource.Error(result.message)
                    }
                }
            }
        }

    }
}
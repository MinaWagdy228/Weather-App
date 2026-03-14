package com.example.wizzar.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.domain.usecase.ManageFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val manageFavoritesUseCase: ManageFavoritesUseCase,
) : ViewModel() {

    val favoritesList: StateFlow<List<FavoriteLocationEntity>> = manageFavoritesUseCase.observeFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeFavorite(favorite: FavoriteLocationEntity) {
        viewModelScope.launch {
            manageFavoritesUseCase.removeFavorite(favorite.latitude, favorite.longitude)
        }
    }

    fun undoRemoveFavorite(favorite: FavoriteLocationEntity) {
        viewModelScope.launch {
            manageFavoritesUseCase.addFavoriteLocation(
                lat = favorite.latitude,
                lon = favorite.longitude,
                cityName = favorite.cityName
            )
        }
    }
}
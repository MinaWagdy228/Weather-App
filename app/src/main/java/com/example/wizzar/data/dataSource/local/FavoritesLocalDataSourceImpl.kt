package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.dao.FavoriteLocationDao
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesLocalDataSourceImpl @Inject constructor(
    private val favoriteLocationDao: FavoriteLocationDao
) : FavoritesLocalDataSource {

    override fun observeAllFavorites(): Flow<List<FavoriteLocationEntity>> {
        return favoriteLocationDao.observeAllFavorites()
    }

    override suspend fun insertFavorite(entity: FavoriteLocationEntity) {
        favoriteLocationDao.insertFavorite(entity)
    }

    override suspend fun deleteFavorite(lat: Double, lon: Double) {
        favoriteLocationDao.deleteFavorite(lat, lon)
    }
}
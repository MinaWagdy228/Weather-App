package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {

    @Query("SELECT * FROM favorite_locations_table")
    fun observeAllFavorites(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteLocationEntity)

    @Query("DELETE FROM favorite_locations_table WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun deleteFavorite(latitude: Double, longitude: Double)
}
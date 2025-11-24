package dev.jaym21.kept.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.jaym21.kept.data.db.entities.TradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TradesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrades(trades: List<TradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity)

    @Query("SELECT * FROM trades ORDER BY tradeDate ASC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Query("DELETE FROM trades")
    suspend fun deleteAllTrades()
}
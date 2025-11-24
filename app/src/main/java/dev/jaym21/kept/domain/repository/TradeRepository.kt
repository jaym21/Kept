package dev.jaym21.kept.domain.repository

import dev.jaym21.kept.domain.model.Trade
import kotlinx.coroutines.flow.Flow

interface TradeRepository {

    suspend fun insertTrades(trades: List<Trade>)
    suspend fun insertTrade(trade: Trade)
    fun getAllTrades(): Flow<List<Trade>>
    suspend fun clearAllTrades()
}

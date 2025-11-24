package dev.jaym21.kept.domain.repository

import dev.jaym21.kept.data.db.dao.TradesDao
import dev.jaym21.kept.domain.mapper.toDomain
import dev.jaym21.kept.domain.mapper.toEntity
import dev.jaym21.kept.domain.model.Trade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TradeRepositoryImpl @Inject constructor(private val dao: TradesDao): TradeRepository {

    override suspend fun insertTrades(trades: List<Trade>) {
        dao.insertTrades(trades.map { it.toEntity() })
    }

    override suspend fun insertTrade(trade: Trade) {
        dao.insertTrade(trade.toEntity())
    }

    override fun getAllTrades(): Flow<List<Trade>> {
        return dao.getAllTrades().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun clearAllTrades() {
        dao.deleteAllTrades()
    }
}
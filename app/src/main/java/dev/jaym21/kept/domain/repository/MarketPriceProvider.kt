package dev.jaym21.kept.domain.repository

import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

interface MarketPriceProvider {

    suspend fun getCurrentPrice(symbol: String): Double
}

class LastTradePriceProvider @Inject constructor(
    private val tradeRepository: TradeRepository
) : MarketPriceProvider {
    override suspend fun getCurrentPrice(symbol: String): Double {
        val trades = tradeRepository.getAllTrades().firstOrNull() ?: emptyList()
        return trades.filter { it.symbol.trim().uppercase() == symbol.trim().uppercase() }
            .maxByOrNull { it.tradeDate }?.price ?: 0.0
    }
}
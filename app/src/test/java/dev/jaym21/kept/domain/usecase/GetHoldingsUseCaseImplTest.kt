package dev.jaym21.kept.domain.usecase

import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType
import dev.jaym21.kept.domain.repository.MarketPriceProvider
import dev.jaym21.kept.domain.repository.TradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetHoldingsUseCaseImplTest {

    @Test
    fun `correctly computes holdings from trades`() = runTest {
        val repo = FakeTradeRepository2()
        val provider = FakeMarketPriceProvider()

        repo.setTrades(
            listOf(
                Trade(symbol = "AAPL", quantity = 10.0, price = 100.0, tradeType = TradeType.BUY, isin = "INEAAPL", exchange = "NSE", tradeDate = 1000L),
                Trade(symbol = "AAPL", quantity = 4.0, price = 120.0, tradeType = TradeType.SELL, isin = "INEAAPL", exchange = "NSE", tradeDate = 1000L),

                Trade(symbol = "MSFT", quantity = 5.0, price = 200.0, tradeType = TradeType.BUY, isin = "INEAAPL", exchange = "NSE", tradeDate = 1000L),
                Trade(symbol = "MSFT", quantity = 5.0, price = 210.0, tradeType = TradeType.SELL, isin = "INEAAPL", exchange = "NSE", tradeDate = 1000L)
            )
        )

        provider.setPrice("AAPL", 130.0)
        provider.setPrice("MSFT", 300.0)

        val usecase = GetHoldingsUseCaseImpl(repo, provider)

        val holdings = usecase().first()

        // MSFT netQty becomes 0 → excluded
        assertEquals(1, holdings.size)

        val aapl = holdings.first()
        assertEquals("AAPL", aapl.symbol)
        assertEquals(6.0, aapl.quantity, 0.0001)       // 10 - 4
        assertEquals( (10*100 - 4*120).toDouble(), aapl.invested, 0.0001)
        assertEquals(6 * 130.0, aapl.currentValue, 0.0001)
    }
}

class FakeMarketPriceProvider : MarketPriceProvider {
    private val prices = mutableMapOf<String, Double>()

    fun setPrice(symbol: String, price: Double) {
        prices[symbol.uppercase()] = price
    }

    override suspend fun getCurrentPrice(symbol: String): Double {
        return prices[symbol.uppercase()] ?: 0.0
    }
}

class FakeTradeRepository2 : TradeRepository {
    private val state = MutableStateFlow<List<Trade>>(emptyList())
    fun setTrades(t: List<Trade>) { state.value = t }

    override suspend fun insertTrades(trades: List<Trade>) {
        TODO("Not yet implemented")
    }

    override suspend fun insertTrade(trade: Trade) {
        TODO("Not yet implemented")
    }

    override fun getAllTrades(): Flow<List<Trade>> {
        return state
    }

    override suspend fun clearAllTrades() {
        TODO("Not yet implemented")
    }
}
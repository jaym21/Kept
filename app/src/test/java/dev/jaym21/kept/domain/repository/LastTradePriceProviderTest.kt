package dev.jaym21.kept.domain.repository

import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeTradeRepository : TradeRepository {
    private val state = MutableStateFlow<List<Trade>>(emptyList())

    fun setTrades(trades: List<Trade>) {
        state.value = trades
    }

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

class LastTradePriceProviderTest {

    @Test
    fun `returns latest price for symbol`() = runTest {
        val repo = FakeTradeRepository()
        repo.setTrades(listOf(
            Trade(id = 1, symbol = "AAPL", quantity = 10.0, price = 100.0, tradeType = TradeType.BUY, isin = "INEAAPL", exchange = "NSE", tradeDate = 1000L),
            Trade(id = 2, symbol = "AAPL", quantity = 5.0, price = 110.0, tradeType = TradeType.BUY, isin = "INEAAPL", exchange = "NSE", tradeDate = 2000L),
            Trade(id = 3, symbol = "MSFT", quantity = 2.0, price = 50.0, tradeType = TradeType.BUY, isin = "INEMSFT", exchange = "NSE", tradeDate = 1500L)
        ))

        val provider = LastTradePriceProvider(repo)

        val priceAapl = provider.getCurrentPrice("AAPL")
        val priceMsft = provider.getCurrentPrice("MSFT")
        val priceUnknown = provider.getCurrentPrice("UNKNOWN")

        assertEquals(110.0, priceAapl, 0.0001)
        assertEquals(50.0, priceMsft, 0.0001)
        assertEquals(0.0, priceUnknown, 0.0001)
    }

    @Test
    fun `case-insensitive symbol matching`() = runTest {
        val repo = FakeTradeRepository()
        repo.setTrades(
            listOf(Trade(symbol = "gOdReJpRoP", price = 123.45, quantity = 1.0, tradeType = TradeType.BUY, isin = "INEAAPL", exchange = "NSE", tradeDate = 300))
        )

        val provider = LastTradePriceProvider(repo)

        assertEquals(123.45, provider.getCurrentPrice("GODREJPROP"), 0.0001)
    }
}
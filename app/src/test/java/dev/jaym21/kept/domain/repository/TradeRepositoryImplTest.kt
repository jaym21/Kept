package dev.jaym21.kept.domain.repository

import dev.jaym21.kept.data.db.dao.TradesDao
import dev.jaym21.kept.data.db.entities.TradeEntity
import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType
import dev.jaym21.kept.domain.repository.TradeRepositoryImpl
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TradeRepositoryImplTest {

    private val dao = mockk<TradesDao>(relaxed = true)
    private val repo = TradeRepositoryImpl(dao)

    @Test
    fun `insertTrades delegates to dao with mapped entities`() = runTest {
        val trades = listOf(
            Trade(
                symbol = "TCS",
                isin = null,
                exchange = "NSE",
                tradeType = TradeType.BUY,
                quantity = 5.0,
                price = 3000.0,
                tradeDate = 1000L,
                brokerage = 10.0,
                taxes = 2.0
            )
        )

        repo.insertTrades(trades)

        coVerify {
            dao.insertTrades(
                match { it.size == 1 && it[0].symbol == "TCS" }
            )
        }
    }

    @Test
    fun `getAllTrades maps dao entities to domain`() = runTest {
        val entities = listOf(
            TradeEntity(
                symbol = "RELIANCE",
                isin = null,
                exchange = "NSE",
                tradeType = "BUY",
                quantity = 1.0,
                price = 100.0,
                tradeDate = 1L,
                brokerage = 0.0,
                taxes = 0.0
            )
        )
        every { dao.getAllTrades() } returns flowOf(entities)

        val flow = repo.getAllTrades()
        var collected: List<Trade>? = null
        flow.collect { collected = it }

        assertEquals(1, collected?.size)
        assertEquals("RELIANCE", collected!![0].symbol)
    }
}

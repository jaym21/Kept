package dev.jaym21.kept.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.jaym21.kept.data.db.dao.TradesDao
import dev.jaym21.kept.data.db.entities.TradeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TradesDaoTest {

    private lateinit var db: KeptDatabase
    private lateinit var dao: TradesDao

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room
            .inMemoryDatabaseBuilder(context, KeptDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.tradesDao()
    }

    @After
    fun closeDB() {
        db.close()
    }

    @Test
    fun writingTradesToDatabase_readingThemBack () = runBlocking {
        val trade1 = TradeEntity(
            symbol = "A",
            isin = null,
            exchange = "NSE",
            tradeType = "BUY",
            quantity = 1.0,
            price = 100.0,
            tradeDate = 1000L
        )
        val trade2 = TradeEntity(
            symbol = "B",
            isin = null,
            exchange = "NSE",
            tradeType = "BUY",
            quantity = 2.0,
            price = 200.0,
            tradeDate = 2000L
        )
        dao.insertTrades(listOf(trade1, trade2))

        val allTrades = dao.getAllTrades().first()
        Assert.assertEquals(2, allTrades.size)
        Assert.assertEquals("NSE", allTrades[0].exchange)
        Assert.assertEquals(200.0, allTrades[1].price, 0.0)
    }
}
package dev.jaym21.kept.domain.mapper

import dev.jaym21.kept.data.db.entities.TradeEntity
import dev.jaym21.kept.domain.mapper.toDomain
import dev.jaym21.kept.domain.mapper.toEntity
import dev.jaym21.kept.domain.model.TradeType
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TradeMapperTest {

    @Test
    fun `entity to domain and back mapping preserves fields`() {
        val entity = TradeEntity(
            id = 1L,
            symbol = "RELIANCE",
            isin = "INE002A01018",
            exchange = "NSE",
            tradeType = "BUY",
            quantity = 10.0,
            price = 2500.0,
            tradeDate = 1690000000000L
        )

        val domain = entity.toDomain()
        assertEquals(1L, domain.id)
        assertEquals("RELIANCE", domain.symbol)
        assertEquals(TradeType.BUY, domain.tradeType)
        assertEquals(10.0, domain.quantity, 0.0)
        assertEquals(2500.0, domain.price, 0.0)

        val backToEntity = domain.toEntity()
        assertEquals(entity, backToEntity)
    }
}
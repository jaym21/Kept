package dev.jaym21.kept.data.pdf.parsers

import dev.jaym21.kept.domain.model.TradeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class ZerodhaPdfParserTest {

    private val parser = ZerodhaPDFParser()

    @Test
    fun canParse_detectsZerodhaHeader() {
        val headerSample = """
            ZERODHA BROKING LIMITED
            CONTRACT NOTE FOR 03/06/2025
        """.trimIndent()

        val result = parser.canParse(headerSample)

        assertTrue("Parser should detect Zerodha contract note header", result)
    }

    @Test
    fun parse_extractsTradesFromSimpleSample() {
        val sampleText = """
            ZERODHA BROKING LIMITED
            CONTRACT NOTE FOR 03/06/2025

            ANNEXURE A
            AIAENG EQ BUY 10 3590.00
            AIAENG EQ SELL 5 3600.50
        """.trimIndent()

        val trades = parser.parse(sampleText)

        assertEquals("Expected exactly 2 trades from sample", 2, trades.size)

        val t1 = trades[0]
        assertEquals("AIAENG", t1.symbol)
        assertEquals(TradeType.BUY, t1.tradeType)
        assertEquals(10.0, t1.quantity, 0.0)
        assertEquals(3590.00, t1.price, 0.0)

        val t2 = trades[1]
        assertEquals("AIAENG", t2.symbol)
        assertEquals(TradeType.SELL, t2.tradeType)
        assertEquals(5.0, t2.quantity, 0.0)
        assertEquals(3600.50, t2.price, 0.0)
    }


    @Test
    fun parse_setsTradeDateFromHeader() {
        val sampleText = """
            ZERODHA BROKING LIMITED
            CONTRACT NOTE FOR 03/06/2025

            ANNEXURE A
            AIAENG EQ BUY 10 3590.00
        """.trimIndent()

        val trades = parser.parse(sampleText)

        assertTrue("Parser should return at least one trade", trades.isNotEmpty())

        val expectedMillis =
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("03/06/2025")!!.time

        val actualMillis = trades.first().tradeDate

        assertEquals(
            "Trade date in parsed trade should match header date",
            expectedMillis,
            actualMillis
        )
    }

}
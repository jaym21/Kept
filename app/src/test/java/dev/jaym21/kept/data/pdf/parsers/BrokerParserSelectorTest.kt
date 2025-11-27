package dev.jaym21.kept.data.pdf.parsers

import dev.jaym21.kept.domain.model.Trade
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test

class BrokerParserSelectorTest {

    private class FakeZerodhaParser : BrokerPDFParser {
        override fun canParse(extractedText: String): Boolean =
            extractedText.contains("ZERODHA", ignoreCase = true)

        override fun parse(extractedText: String): List<Trade> = emptyList()
    }

    private class FakeUpstoxParser : BrokerPDFParser {
        override fun canParse(extractedText: String): Boolean =
            extractedText.contains("UPSTOX", ignoreCase = true)

        override fun parse(extractedText: String): List<Trade> = emptyList()
    }

    private class FakeNeverMatchesParser : BrokerPDFParser {
        override fun canParse(extractedText: String): Boolean = false
        override fun parse(extractedText: String): List<Trade> = emptyList()
    }


    @Test
    fun `selectParserFor returns Zerodha parser when text contains Zerodha`() {
        val zerodha = FakeZerodhaParser()
        val upstox = FakeUpstoxParser()
        val never = FakeNeverMatchesParser()
        val selector = BrokerParserSelectorImpl(
            setOf(zerodha, upstox, never)
        )

        val text = "This is a CONTRACT NOTE from ZERODHA Broking Limited"

        val parser = selector.selectParserFor(text)

        assertNotNull(parser)
        assertTrue(
            "Expected Zerodha parser to be selected",
            parser is FakeZerodhaParser
        )
    }

    @Test
    fun `selectParserFor returns Upstox parser when text contains Upstox`() {
        val zerodha = FakeZerodhaParser()
        val upstox = FakeUpstoxParser()
        val never = FakeNeverMatchesParser()
        val selector = BrokerParserSelectorImpl(
            setOf(zerodha, upstox, never)
        )

        val text = "UPSTOX CONTRACT NOTE"

        val parser = selector.selectParserFor(text)

        assertNotNull(parser)
        assertTrue(
            "Expected Upstox parser to be selected",
            parser is FakeUpstoxParser
        )
    }

    @Test
    fun `selectParserFor returns null when no parser can handle the text`() {
        val zerodha = FakeZerodhaParser()
        val upstox = FakeUpstoxParser()
        val never = FakeNeverMatchesParser()
        val selector = BrokerParserSelectorImpl(
            setOf(zerodha, upstox, never)
        )

        val text = "Some random PDF text that does not belong to any broker"

        val parser = selector.selectParserFor(text)

        assertNull(
            "Expected null when no parser supports the text",
            parser
        )
    }

    @Test
    fun `selectParserFor prefers first matching parser when multiple canParse`() {
        val parserA = object : BrokerPDFParser {
            override fun canParse(extractedText: String): Boolean = extractedText.contains("NOTE", ignoreCase = true)
            override fun parse(extractedText: String): List<Trade> = emptyList()
        }
        val parserB = object : BrokerPDFParser {
            override fun canParse(extractedText: String): Boolean = extractedText.contains("NOTE", ignoreCase = true)
            override fun parse(extractedText: String): List<Trade> = emptyList()
        }

        val selector = BrokerParserSelectorImpl(setOf(parserA, parserB))
        val parser = selector.selectParserFor("Generic CONTRACT NOTE text")

        assertNotNull(parser)
        assertTrue(parser === parserA || parser === parserB)
    }
}
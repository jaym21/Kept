package dev.jaym21.kept.data.pdf.parsers

import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ZerodhaPDFParser @Inject constructor(): BrokerPDFParser {

    override fun canParse(extractedText: String): Boolean {
        val sample = extractedText.take(1200).uppercase(Locale.getDefault())
        return sample.contains("ZERODHA") || sample.contains("KITE") || sample.contains("CONTRACT NOTE")
    }

    override fun parse(extractedText: String): List<Trade> {
        val lines = extractedText.lines()
        val tradeDateMillis = extractTradeDate(lines)

        val trades = mutableListOf<Trade>()
        for (line in lines) {
            parseTradeLine(line, tradeDateMillis)?.let { trade ->
                trades += trade
            }
        }
        return trades
    }

    private fun extractTradeDate(lines: List<String>): Long {
        val header = lines.take(30).joinToString(" ")
        val dateRegexes = listOf(
            Regex("""\b(\d{1,2}-[A-Za-z]{3}-\d{4})\b"""),
            Regex("""\b(\d{1,2}/\d{1,2}/\d{4})\b"""),
            Regex("""\b(\d{1,2}-\d{1,2}-\d{4})\b"""),
            Regex("""\b(\d{1,2}\s+[A-Za-z]{3}\s+\d{4})\b""")
        )

        dateRegexes.forEach { regex ->
            val match = regex.find(header)
            if (match != null) {
                val dateStr = match.groupValues[1]
                val parsed = tryParseDateVariants(dateStr)
                if (parsed != null) return parsed.time

            }
        }

        //as a fallback, search entire doc for phrases like "TRADE DATE" then number
        val tradeDateLine = lines.firstOrNull {
            it.contains("TRADE DATE", true) || it.contains("TRADE", true) && it.contains("DATE", true)
        }
        if (tradeDateLine != null) {
            val fallbackMatch = Regex("""\b(\d{1,2}[-/][A-Za-z0-9]{3,}[-/]\d{4})\b""").find(tradeDateLine)
            if (fallbackMatch != null) {
                val fallbackParsed = tryParseDateVariants(fallbackMatch.groupValues[1])
                if (fallbackParsed != null) return fallbackParsed.time
            }
        }
        return System.currentTimeMillis()
    }

    private fun tryParseDateVariants(dateStr: String): Date? {
        val formats = listOf("dd-MMM-yyyy", "dd/MM/yyyy", "dd-MM-yyyy", "dd MMM yyyy", "d-MMM-yyyy")
        formats.forEach { format ->
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false
                return sdf.parse(dateStr)
            } catch (e: ParseException) {
                //continue with next format
            }
        }
        return null
    }

    private fun parseTradeLine(line: String, tradeDateMillis: Long): Trade? {
        val trimmed = line.trim()
        if (trimmed.isBlank()) return null

        val parts = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (parts.size < 8) return null

        // 1) Find side index (B/S/BUY/SELL)
        val sideIndex = parts.indexOfFirst {
            val u = it.uppercase(Locale.ENGLISH)
            u == "B" || u == "S" || u == "BUY" || u == "SELL"
        }
        if (sideIndex <= 0 || sideIndex >= parts.size - 2) {
            return null // no side or nothing after side
        }

        val sideToken = parts[sideIndex].uppercase(Locale.ENGLISH)
        val tradeType = when (sideToken) {
            "B", "BUY" -> TradeType.BUY
            "S", "SELL" -> TradeType.SELL
            else -> return null
        }

        // 2) Symbol is token just before side
        val rawSymbolIsin = parts[sideIndex - 1]
        val symbolPart = rawSymbolIsin.substringBefore("/")
        val symbol = symbolPart.substringBefore("-").trim().uppercase(Locale.ENGLISH)
        val isinPart = rawSymbolIsin.substringAfter("/", missingDelimiterValue = "")
            .ifBlank { null }

        // 3) Exchange is just after side, e.g. BSE / NSE
        val exchangeIndex = sideIndex + 1
        if (exchangeIndex >= parts.size) return null
        val exchange = parts[exchangeIndex].uppercase(Locale.ENGLISH)

        // 4) From after exchange onwards, find numeric tokens
        val numericIndices = (exchangeIndex + 1 until parts.size).filter { idx ->
            parts[idx].replace(",", "").toDoubleOrNull() != null
        }

        if (numericIndices.size < 2) return null

        // 5) Quantity: first numeric after exchange that looks like integer
        val qtyIndex = numericIndices.firstOrNull { !parts[it].contains('.') }
            ?: numericIndices.first()

        val quantity = parts[qtyIndex].replace(",", "").toDoubleOrNull()
            ?: return null

        // 6) Price: second last numeric on the line (before amount)
        val priceIndex = numericIndices.getOrNull(numericIndices.size - 2) ?: return null
        val price = parts[priceIndex].replace(",", "").toDoubleOrNull()
            ?: return null

        return Trade(
            symbol = symbol,
            isin = isinPart,
            exchange = exchange,
            quantity = quantity,
            price = price,
            tradeType = tradeType,
            tradeDate = tradeDateMillis
        )
    }
}
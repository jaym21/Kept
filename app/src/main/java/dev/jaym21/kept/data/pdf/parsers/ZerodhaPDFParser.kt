package dev.jaym21.kept.data.pdf.parsers

import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZerodhaPDFParser: BrokerPDFParser {

    override fun canParse(extractedText: String): Boolean {
        val sample = extractedText.take(1200).uppercase(Locale.getDefault())
        return sample.contains("ZERODHA") || sample.contains("KITE") || sample.contains("CONTRACT NOTE")
    }

    override fun parse(extractedText: String): List<Trade> {
        val trades =  mutableListOf<Trade>()
        val lines = extractedText.split("\n")

        val tradeDateMillis = extractTradeDate(lines) ?: System.currentTimeMillis()
        val tradeLineRegex = Regex("""([A-Z0-9\.\- ]+)\s+(EQ|FUT|OPT)\s+(BUY|SELL)\s+(\d+)\s+([\d,]+\.\d+)""", RegexOption.IGNORE_CASE)

        lines.forEach { line ->
            val match = tradeLineRegex.find(line)
            if (match != null) {
                try {
                    val symbolRaw = match.groupValues[1].trim()
                    val segmentRaw = match.groupValues[2].trim()
                    val tradeActionRaw = match.groupValues[3].trim()
                    val qtyRaw = match.groupValues[4].trim()
                    val priceRaw = match.groupValues[5].trim()

                    val qty = parseNumber(qtyRaw)
                    val price = parseNumber(priceRaw)
                    if (qty <= 0.0 || price <= 0.0) return@forEach

                    val tradeType = if (tradeActionRaw.equals("BUY", true)) TradeType.BUY else TradeType.SELL
                    val symbol = symbolRaw.replace(Regex("\\s+"), " ").uppercase(Locale.getDefault())

                    val trade = Trade(
                        symbol = symbol,
                        isin = null,
                        exchange = inferExchangeFromLine(line),
                        tradeType = tradeType,
                        quantity = qty,
                        price = price,
                        tradeDate = tradeDateMillis,
                        brokerage = 0.0,
                        taxes = 0.0
                    )
                    trades.add(trade)
                } catch (e: Exception) {
                    //ignore malformed line — could collect warnings
                }
            } else {
                //fallback: try split-by-spaces heuristic if line contains BUY or SELL
                if (line.contains(" BUY ", ignoreCase = true) || line.contains(" SELL ", ignoreCase = true)) {
                    val parsed = tryParseLineHeuristic(line, tradeDateMillis)
                    if (parsed != null) trades.add(parsed)
                }

            }
        }
        return trades
    }

    private fun extractTradeDate(lines: List<String>): Long? {
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
        return null
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

    // remove commas and other non numeric except dot and minus
    private fun parseNumber(numStr: String): Double {
        val cleaned = numStr.replace(",", "").replace(" ", "")
        return try { cleaned.toDouble() } catch (e: Exception) { 0.0 }
    }

    private fun inferExchangeFromLine(line: String): String {
        return when {
            line.contains("NSE", true) -> "NSE"
            line.contains("BSE", true) -> "BSE"
            else -> "NSE"
        }
    }

    private fun tryParseLineHeuristic(line: String, tradeDateMillis: Long): Trade? {
        val tokens = line.split(Regex("\\s+"))
        val actionIndex = tokens.indexOfFirst { it.equals("BUY", true) || it.equals("SELL", true) }
        if (actionIndex == -1) return null
        val action = tokens[actionIndex].uppercase(Locale.getDefault())
        //find numeric tokens after action
        val numbers = tokens.mapNotNull { t ->
            val num = t.replace(",", "")
            if (num.matches(Regex("""^-?\d+(\.\d+)?$"""))) num.toDouble() else null
        }
        if (numbers.size >= 2) {
            val qty = numbers[0]
            val price = numbers[1]
            val symbol = tokens.take(actionIndex).joinToString(" ").trim().uppercase(Locale.getDefault())
            if (symbol.isEmpty() || qty <= 0.0 || price <= 0.0) return null
            return Trade(
                symbol = symbol,
                isin = null,
                exchange = inferExchangeFromLine(line),
                tradeType = if (action == "BUY") TradeType.BUY else TradeType.SELL,
                quantity = qty,
                price = price,
                tradeDate = tradeDateMillis,
                brokerage = 0.0,
                taxes = 0.0
            )
        }
        return null
    }
}
package dev.jaym21.kept.domain.model

data class Trade(
    val id: Long = 0,
    val symbol: String,
    val isin: String?,
    val exchange: String,
    val tradeType: TradeType,
    val quantity: Double,
    val price: Double,
    val tradeDate: Long
)

enum class TradeType {
    BUY, SELL
}

data class Holding(
    val symbol: String,
    val companyName: String? = null,
    val isin: String? = null,
    val exchange: String? = null,
    val quantity: Double,
    val invested: Double,
    val currentValue: Double,
) {
    val pnlAmount: Double get() = currentValue - invested
    val pnlPct: Double get() = if (invested == 0.0) 0.0 else (pnlAmount / invested) * 100.0
}
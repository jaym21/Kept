package dev.jaym21.kept.domain.model

data class Trade(
    val id: Long = 0,
    val symbol: String,
    val isin: String?,
    val exchange: String,
    val tradeType: TradeType,
    val quantity: Double,
    val price: Double,
    val tradeDate: Long,
    val brokerage: Double,
    val taxes: Double
)

enum class TradeType {
    BUY, SELL
}
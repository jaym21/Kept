package dev.jaym21.kept.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val isin: String?,
    val exchange: String,
    val tradeType: String,
    val quantity: Double,
    val price: Double,
    val tradeDate: Long,
    val brokerage: Double,
    val taxes: Double
)
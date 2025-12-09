package dev.jaym21.kept.domain.mapper

import dev.jaym21.kept.data.db.entities.TradeEntity
import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType

fun TradeEntity.toDomain(): Trade {
    return Trade(
        id = id,
        symbol = symbol,
        isin = isin,
        exchange = exchange,
        tradeType = if (tradeType == "BUY") TradeType.BUY else TradeType.SELL,
        quantity = quantity,
        price = price,
        tradeDate = tradeDate
    )
}

fun Trade.toEntity(): TradeEntity {
    return TradeEntity(
        id = id,
        symbol = symbol,
        isin = isin,
        exchange = exchange,
        tradeType = tradeType.name,
        quantity = quantity,
        price = price,
        tradeDate = tradeDate
    )
}
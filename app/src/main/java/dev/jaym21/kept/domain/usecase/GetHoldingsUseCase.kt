package dev.jaym21.kept.domain.usecase

import dev.jaym21.kept.domain.model.Holding
import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.model.TradeType
import dev.jaym21.kept.domain.repository.MarketPriceProvider
import dev.jaym21.kept.domain.repository.TradeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

interface GetHoldingsUseCase {

    operator fun invoke(): Flow<List<Holding>>
}

class GetHoldingsUseCaseImpl @Inject constructor(
    private val tradeRepository: TradeRepository,
    private val marketPriceProvider: MarketPriceProvider
) : GetHoldingsUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(): Flow<List<Holding>> {
                return tradeRepository.getAllTrades()
                    .mapLatest { trades ->
                        val groupedTrades: Map<String, List<Trade>> = trades.groupBy { it.symbol.trim().uppercase() }

                        coroutineScope {
                            val deferredHolders = groupedTrades.map { (symbol, tradeList) ->
                                async {
                                    val netQty = tradeList.sumOf { if (it.tradeType == TradeType.BUY) it.quantity else -it.quantity }

                                    val invested = tradeList.sumOf { if (it.tradeType == TradeType.BUY) it.quantity * it.price else -it.quantity * it.price }

                                    if (netQty == 0.0) {
                                        null
                                    } else {
                                        val currentPrice = try {
                                            marketPriceProvider.getCurrentPrice(symbol)
                                        } catch (_: Exception) {
                                            tradeList.lastOrNull()?.price ?: 0.0
                                        }

                                        val currentValue = netQty * currentPrice

                                        Holding(
                                            symbol = symbol,
                                            companyName = tradeList.firstOrNull()?.let { null } ?: symbol,
                                            isin = tradeList.firstOrNull()?.isin,
                                            exchange = tradeList.firstOrNull()?.exchange,
                                            quantity = netQty,
                                            invested = invested,
                                            currentValue = currentValue
                                        )
                                    }
                                }
                            }
                            deferredHolders.mapNotNull { it.await() }
                        }
                    }
                    .distinctUntilChangedBy { list -> list.map { Triple(it.symbol, it.quantity, it.currentValue) } }
                    .map { list -> list.sortedByDescending { it.currentValue } }
    }
}
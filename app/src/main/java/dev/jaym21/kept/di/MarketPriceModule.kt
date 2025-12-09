package dev.jaym21.kept.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jaym21.kept.domain.repository.LastTradePriceProvider
import dev.jaym21.kept.domain.repository.MarketPriceProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MarketPriceModule {

    @Binds
    @Singleton
    abstract fun bindMarketPriceProvider(
        impl: LastTradePriceProvider
    ): MarketPriceProvider
}
package dev.jaym21.kept.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jaym21.kept.data.db.dao.TradesDao
import dev.jaym21.kept.domain.repository.TradeRepository
import dev.jaym21.kept.domain.repository.TradeRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTradeRepository(tradesDao: TradesDao): TradeRepository {
        return TradeRepositoryImpl(tradesDao)
    }
}
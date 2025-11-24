package dev.jaym21.kept.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jaym21.kept.data.db.KeptDatabase
import dev.jaym21.kept.data.db.dao.TradesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKeptDatabase(context: Context): KeptDatabase {
        return Room
            .databaseBuilder(context = context, klass = KeptDatabase::class.java, name = "kept_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideTradesDao(db: KeptDatabase): TradesDao = db.tradesDao()
}
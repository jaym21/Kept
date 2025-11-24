package dev.jaym21.kept.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.jaym21.kept.data.db.dao.TradesDao
import dev.jaym21.kept.data.db.entities.TradeEntity

@Database(entities = [TradeEntity::class], version = 1, exportSchema = true)
abstract class KeptDatabase: RoomDatabase() {
    abstract fun tradesDao(): TradesDao
}
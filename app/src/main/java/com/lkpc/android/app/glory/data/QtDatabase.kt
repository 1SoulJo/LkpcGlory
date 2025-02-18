package com.lkpc.android.app.glory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lkpc.android.app.glory.entity.Qt

@Database(entities = [Qt::class], version = 1)
abstract class QtDatabase : RoomDatabase() {
    abstract fun qtDao(): QtDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: QtDatabase? = null

        fun getDatabase(context: Context): QtDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QtDatabase::class.java,
                    "qt_database"
                ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
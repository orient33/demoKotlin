package com.example.room

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.log

@Database(entities = [User::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var ins: AppDb? = null

        fun getIns(c: Context): AppDb {
            return ins ?: synchronized(this) {
                ins ?: buildDb(c).also { ins = it }
            }
        }

        private fun buildDb(c: Context): AppDb {
            return Room.databaseBuilder(c, AppDb::class.java, "user")
//                    .allowMainThreadQueries()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            log("onDb create!")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            log("onDb open!")
                        }
                    }).build()
        }
    }
}
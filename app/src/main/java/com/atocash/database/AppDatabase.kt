package com.atocash.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atocash.R
import com.atocash.database.converter.DocumentsConverter
import com.atocash.database.converter.PendingExpenseConverter
import com.atocash.database.converter.SubClaimsConverter
import com.atocash.network.localDb.PendingExpenseReimbursement
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse


@Database(
    entities = [ExpenseRaisedForEmployeeResponse::class, PendingExpenseReimbursement::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    DocumentsConverter::class,
    SubClaimsConverter::class,
    PendingExpenseConverter::class
)
abstract class AtoCashDB : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AtoCashDB? = null

        fun getDatabaseClient(context: Context): AtoCashDB {
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(
                        context,
                        AtoCashDB::class.java,
                        context.getString(R.string.app_local_db)
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                return INSTANCE!!
            }
        }

    }
}
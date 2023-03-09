package com.atocash.database.repo

import android.content.Context
import com.atocash.database.AtoCashDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PendingExpenseRepo {

    companion object {

        var pendingExpenseDb: AtoCashDB? = null

        fun initializePendingExpense(context: Context): AtoCashDB {
            return AtoCashDB.getDatabaseClient(context)
        }

        fun insertData(context: Context) {
            pendingExpenseDb = initializePendingExpense(context)

            CoroutineScope(Dispatchers.IO).launch {
//                val loginDetails = LoginTableModel(username, password)
//                pendingExpenseDb!!.loginDao().InsertData(loginDetails)
            }
        }
    }

}
package com.atocash.database.converter

import androidx.room.TypeConverter
import com.atocash.network.response.Documents
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.google.gson.Gson

class PendingExpenseConverter {

    @TypeConverter
    fun listToJson(value: List<ExpenseRaisedForEmployeeResponse>?): String {
        value?.let {
            return Gson().toJson(value)
        }
        return ""
    }

    @TypeConverter
    fun jsonToList(value: String?): List<ExpenseRaisedForEmployeeResponse>? {
        value?.let {
            if (value.isNotEmpty()) {
                val objects =
                    Gson().fromJson(
                        value,
                        Array<ExpenseRaisedForEmployeeResponse>::class.java
                    ) as Array<ExpenseRaisedForEmployeeResponse>
                return if (objects.isNotEmpty())
                    objects.toList() as MutableList<ExpenseRaisedForEmployeeResponse>
                else arrayListOf()
            }
        }
        return arrayListOf()
    }
}
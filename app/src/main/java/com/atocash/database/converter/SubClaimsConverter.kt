package com.atocash.database.converter

import androidx.room.TypeConverter
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SubClaimsConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String): List<ExpenseReimburseListingResponse> {
        val listType = object : TypeToken<List<ExpenseReimburseListingResponse?>?>() {}.type
        return gson.fromJson<List<ExpenseReimburseListingResponse>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<ExpenseReimburseListingResponse>): String {
        return gson.toJson(someObjects)
    }
}
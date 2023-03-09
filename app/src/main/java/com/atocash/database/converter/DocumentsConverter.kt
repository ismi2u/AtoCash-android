package com.atocash.database.converter

import androidx.room.TypeConverter
import com.atocash.network.response.Documents
import com.google.gson.Gson

class DocumentsConverter {

    @TypeConverter
    fun listToJson(value: List<Documents>?): String {
        value?.let {
            return Gson().toJson(value)
        }
        return ""
    }

    @TypeConverter
    fun jsonToList(value: String?): List<Documents>? {
        value?.let {
            if (value.isNotEmpty()) {
                val objects =
                    Gson().fromJson(
                        value,
                        Array<Documents>::class.java
                    ) as Array<Documents>
                return if (objects.isNotEmpty())
                    objects.toList() as MutableList<Documents>
                else arrayListOf()
            }
        }
        return arrayListOf()
    }
}
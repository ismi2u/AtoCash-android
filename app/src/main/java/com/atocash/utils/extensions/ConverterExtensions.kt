package com.atocash.utils.extensions

import com.atocash.base.common.AtoCash
import com.atocash.base.view.SuperCompatActivity
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.utils.DataStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.text.DecimalFormat
import javax.sql.DataSource

fun <T> Gson.convertToJsonString(t: T): String {
    return toJson(t).toString()
}

fun String.getExtension(): String {
    return substring(lastIndexOf("."))
}

fun String.getFileName() : String {
    return substring(lastIndexOf("/"))
}

fun <T> Gson.convertToModel(jsonString: String, cls: Class<T>): T? {
    return try {
        fromJson(jsonString, cls)
    } catch (e: Exception) {
        null
    }
}

fun String.formatCard(): String {
    val delimiter = ' '
    return this.replace(".{4}(?!$)".toRegex(), "$0$delimiter")
}

inline fun <reified T> Gson.fromJson(json: String) =
    this.fromJson<T>(json, object : TypeToken<T>() {}.type)

fun ArrayList<JSONObject>.toJson(): String {
    return Gson().toJson(this)
}

fun getDecimalFormatValue(amount: Float): String {
    val decimalFormat = DecimalFormat("00.00")
    return decimalFormat.format(amount)
}

fun Float.roundOffToTwoDecimal(): Float {
    val s = String.format("%.2f", this)
    return s.toFloat()
}

fun ExpenseRaisedForEmployeeResponse.clone(): ExpenseRaisedForEmployeeResponse {
    val tempClonedItem = Gson().toJson(this, ExpenseRaisedForEmployeeResponse::class.java)
    return Gson().fromJson<ExpenseRaisedForEmployeeResponse>(
        tempClonedItem,
        ExpenseRaisedForEmployeeResponse::class.java
    )
}

fun ExpenseReimburseListingResponse.clone(): ExpenseReimburseListingResponse {
    val tempClonedItem = Gson().toJson(this, ExpenseReimburseListingResponse::class.java)
    return Gson().fromJson<ExpenseReimburseListingResponse>(
        tempClonedItem,
        ExpenseReimburseListingResponse::class.java
    )
}

fun String.isLiveServer() : Boolean {
    return false
}
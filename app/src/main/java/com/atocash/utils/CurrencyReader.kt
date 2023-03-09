package com.atocash.utils

import android.text.TextUtils
import com.atocash.utils.extensions.printLog
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import kotlin.Comparator
import kotlin.collections.ArrayList


object CurrencyReader {

    fun readCurrencyJson(openRawResource: InputStream): ArrayList<String> {
        val inputStream: InputStream = openRawResource
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        inputStream.use { inputStream_ ->
            val reader: Reader = BufferedReader(InputStreamReader(inputStream_, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        val countriesStr = writer.toString()
//        printLog("countriesStr: $countriesStr")

        val currencyListStr = ArrayList<String>()
        if (!TextUtils.isEmpty(countriesStr)) {
            val jsonArray = JSONArray(countriesStr)

            for (item in 0 until jsonArray.length()) {
                val singleCountry = jsonArray.get(item) as JSONObject
                val tempCountryName = singleCountry.getString("name")


                val index: Int = tempCountryName.lastIndexOf(" ")

                val countryName: String = if (index != -1) {
                    tempCountryName.substring(0, index)
                } else {
                    tempCountryName
                }

                printLog("tempCountryName: $tempCountryName")
                printLog("Country name: $countryName")
                printLog("-------------------------------")

                currencyListStr.add(countryName)

                printLog("country Name added as: $countryName")
            }
        }

        //sorting country list alphabetically
        if(currencyListStr.isNullOrEmpty()) {
            currencyListStr.sortWith(Comparator { o1, o2 -> o1.toString().compareTo(o2.toString()) })
        }

        return currencyListStr
    }
}

data class CountryData(
    var id: Int = 0,
    var decimal_digits: Int = 0,
    var rounding: Int = 0,
    var code: String = "",
    var name: String = "",
    var name_plural: String = "",
    var symbol: String = "",
    var symbol_native: String = ""
)
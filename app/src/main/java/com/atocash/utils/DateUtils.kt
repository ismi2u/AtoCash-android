package com.atocash.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    //    private const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    private const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    private const val SLASHED_DATE_FORMAT = "dd/MM/yyyy"
    const val HIPHEN_SLASHED_DATE_FORMAT = "dd-MM-yyyy"
    const val EXPENSE_REIMBURSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" //2023-03-23T12:00:00Z
    const val EXPENSE_REIMBURSE_FORMAT_SS = "yyyy-MM-dd'T'HH:mm:ss'Z'" //2023-03-23T12:00:00Z
    const val EXPENSE_REIMBURSE_FORMAT_NEW =
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" //2023-01-27T17:29:09.145609Z

    fun getIsoDateFromMillis(timeStamp: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault())
        calendar.timeInMillis = timeStamp * 1000
        val dateFormat = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar)
    }

    fun parseIsoDate(timeStampStr: String, format: String? = EXPENSE_REIMBURSE_FORMAT): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault())
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return try {
            calendar.time = dateFormat.parse(timeStampStr)!!
            val date1: Date = calendar.time
            SimpleDateFormat(SLASHED_DATE_FORMAT, Locale.getDefault()).format(date1)
        } catch (e: ParseException) {
            val dateF = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.getDefault())
            return try {
                calendar.time = dateF.parse(timeStampStr)!!
                val date1: Date = calendar.time
                SimpleDateFormat(SLASHED_DATE_FORMAT, Locale.getDefault()).format(date1)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    fun getDate(timeStampStr: String): String {
        val calendar = getCalendarFromISO(timeStampStr)
        val date1: Date = calendar.time
        return SimpleDateFormat(SLASHED_DATE_FORMAT, Locale.getDefault()).format(date1)
    }

    fun getTimeInMillis(timeStr: String): Long {
        val calendar = getCalendarFromISO(timeStr)
        return calendar.timeInMillis
    }

    fun getDateNew(timeStr: String): String {
        val customFormat = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        return SimpleDateFormat(
            SLASHED_DATE_FORMAT,
            Locale.getDefault()
        ).format(customFormat.parse(timeStr)!!).toString()
    }

    fun getCalendarFromISO(dateString: String): Calendar {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault())
        val dateFormat = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        try {
            calendar.time = dateFormat.parse(dateString)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar
    }

    fun fromCalendar(calendar: Calendar, format: String? = DATE_TIME_FORMAT): String {
        val date = calendar.time
        return SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

    fun getLongTime(it1: String): String {
        val calendarMillis = getTimeInMillis(it1)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault())
        calendar.timeInMillis = calendarMillis
        return fromCalendar(calendar)
    }

    fun getLongTimeWithIso(it1: String): String {
        val calendarMillis = getTimeInMillis(it1)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault())
        calendar.timeInMillis = calendarMillis
        return fromCalendar(calendar, EXPENSE_REIMBURSE_FORMAT)
    }

    fun getDaysBetween(firstDate: Calendar?, secondDate: Calendar?): Long {
        val timeInMillisDiff = (secondDate?.timeInMillis ?: 0) - (firstDate?.timeInMillis ?: 0)
        return ((timeInMillisDiff / (1000 * 60 * 60 * 24)))
    }

}
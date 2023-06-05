package com.example.tasksapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.example.tasksapp.model.TaskModel
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


object CommonUtils {

    fun formatDateWithSuperscript(strDate: String?, withYear: Boolean = false): String? {
        val yyyyMmDd = "dd-MM-yyyy"
        var fmtOut: SimpleDateFormat? = null
        var date: Date? = null
        try {
            val formatter: DateFormat = SimpleDateFormat(yyyyMmDd, Locale.ENGLISH)
            date = formatter.parse(strDate ?: "") as Date
            val dayNumberSuffix: String =
                getDayNumberSuffix(Calendar.getInstance()[Calendar.DAY_OF_MONTH])
            fmtOut =
                if (withYear) SimpleDateFormat(" d'$dayNumberSuffix' MMM yyyy", Locale.ENGLISH)
                else SimpleDateFormat(" d'$dayNumberSuffix' MMM", Locale.ENGLISH)
        } catch (_: java.lang.Exception) {
        }
        return fmtOut?.format(date ?: Date())
    }

    private fun getDayNumberSuffix(day: Int): String {
        return if (day in 11..13) {
            "<sup>th</sup>"
        } else when (day % 10) {
            1 -> "<sup>st</sup>"
            2 -> "<sup>nd</sup>"
            3 -> "<sup>rd</sup>"
            else -> "<sup>th</sup>"
        }
    }

    inline fun <reified T> genericCastOrNull(anything: Any?): T? {
        return anything as? T
    }

    fun hideKeyboard(activity: Activity, view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return df.format(c)
    }

    fun getNextDate(): String {
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 1)

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return df.format(c.time)
    }

    fun getCurrentTimeInMillis(): Long {
        val calendar = Calendar.getInstance()
        return calendar.time.time
    }

    fun getCurrentDateAndTime(): String {
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return df.format(c)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeGreater(time1: String, time2: String): Boolean {
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH)
        val localDate: LocalDateTime = LocalDateTime.parse(time1, formatter)
        val timeInMilliseconds: Long = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val localDate2: LocalDateTime = LocalDateTime.parse(time2, formatter)
        val timeInMilliseconds2: Long =
            localDate2.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        return timeInMilliseconds > timeInMilliseconds2
    }

    fun sortAsc(list: List<TaskModel>): List<TaskModel> {
        return list.sortedWith(
            comparator = object : Comparator<TaskModel> {
                @SuppressLint("SimpleDateFormat")
                var f: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
                override fun compare(p0: TaskModel?, p1: TaskModel?): Int {
                    return try {
                        f.parse("${p0?.date} ${p0?.time}")
                            .compareTo("${p1?.date} ${p1?.time}".let { f.parse(it) })
                    } catch (e: ParseException) {
                        throw IllegalArgumentException(e)
                    }
                }
            })
    }

    fun sortDsc(list: List<TaskModel>): List<TaskModel> {
        return list.sortedWith(
            comparator = object : Comparator<TaskModel> {
                @SuppressLint("SimpleDateFormat")
                var f: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
                override fun compare(p0: TaskModel?, p1: TaskModel?): Int {
                    return try {
                        f.parse("${p1?.date} ${p1?.time}")
                            .compareTo("${p0?.date} ${p0?.time}".let { f.parse(it) })
                    } catch (e: ParseException) {
                        throw IllegalArgumentException(e)
                    }
                }
            })
    }

    fun isTimeCheck(time: String): Boolean {
        val formatter =
            SimpleDateFormat("hh:mm a")
        val currCal = Calendar.getInstance()
        val currTimeInMillis = getCurrentTimeInMillis()
        val localDate = formatter.parse(time)
        val cal = Calendar.getInstance()
        cal.time = localDate
        cal.set(currCal.get(Calendar.YEAR), currCal.get(Calendar.MONTH), currCal.get(Calendar.DATE))
        val timeInMillis = cal.time.time
        return timeInMillis < currTimeInMillis
    }

}
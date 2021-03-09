package kr.shlim.roomcornermovie.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {


    fun getYesterDay(format : String) : String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1);
        val simpleDateFormat = SimpleDateFormat(format)
        val result = simpleDateFormat.format(calendar.time)
        return result
    }



}
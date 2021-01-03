package kr.shlim.roomcornermovie

import android.app.Application
import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy


class RoomCornerMovieApplication : Application() {
    private val TAG = RoomCornerMovieApplication::class.java.simpleName
    
    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "[timecheck] RoomCornerMovieApplication onCrete start")

        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(2) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            .tag("My custom tag") // (Optional) Global tag for every log. Default PRETTY_LOGGER

            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Logger.d("[timecheck] RoomCornerMovieApplication Log Setting end")

    }
}
package kr.shlim.roomcornermovie

import android.view.View
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger

class MainViewModel : ViewModel() {

    fun onButtonClick(v : View) {
        Logger.d("ViewModel 버튼 클릭!!!!")

    }
}
package kr.shlim.roomcornermovie.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO

class MainViewModel : ViewModel() {

    val movieList = arrayListOf<NaverMovieListDTO>()

    val movieListData : MutableLiveData<ArrayList<NaverMovieListDTO>> = MutableLiveData<ArrayList<NaverMovieListDTO>>()

    init {

    }

    fun onButtonClick(v : View) {
        Logger.d("ViewModel 버튼 클릭!!!!")

    }


    fun getMovieList() {
        movieList.clear()

//        val boxOffice = getBoxOffice()
//
//        var index = 0
//
//        boxOffice.toObservable()
//            .map{ it -> it.boxOfficeResult.dailyBoxOfficeList }
//            .repeat(maxCount.toLong())
//            .map{ it ->
//                it.get(index++)}
//            .flatMap {
//                Logger.d("it.movieNm : ${it.movieNm}")
//                getNaverAPI(it.movieNm).toObservable().base().take(maxCount.toLong())
//            }.base().subscribe(
//                {
//                    Logger.d("naverAPI : ${it.items.get(0).title}")
//                    movieList.add(it)
//                },
//                {
//                    Logger.e("naverAPI exception : ${it.message}")
//                    updateList()
//                }, {
//                    updateList()
//                }
//            )
    }
}
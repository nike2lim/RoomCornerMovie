package kr.shlim.roomcornermovie.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_main.*
import kr.shlim.roomcornermovie.BuildConfig
import kr.shlim.roomcornermovie.ext.base
import kr.shlim.roomcornermovie.model.KobisDailyBoxOfficeListDTO
import kr.shlim.roomcornermovie.model.naver.NaverMovieItem
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.network.APIClient
import org.jsoup.Jsoup

class MainViewModel : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private val _movieList = MutableLiveData<List<NaverMovieListDTO>>()
    val movieList : LiveData<List<NaverMovieListDTO>> by lazy { _movieList }

    private val _movieItemList = MutableLiveData<List<NaverMovieItem>>()
    val movieItemList : LiveData<List<NaverMovieItem>> by lazy { _movieItemList }

    val maxCount : Int = 20

    fun onButtonClick(v : View) {
        Logger.d(TAG, "onButtonClick() called with: v = [${v.id}]")
    }

    /**
     * 날짜에 해당하는 영화 리스트를 가져온다.
     * @param targetDt ex) 20201129
     */
    fun getMovieList(targetDt : String) {
//        _movieList.value?.forEachIndexed { index, item ->
//
//            val temp = _movieList?.value?.get(index) as List<NaverMovieListDTO>
//
//
//        }
//        _movieItemList

        val boxOffice = getBoxOffice(targetDt)

        var index = 0

        boxOffice.toObservable()
            .map{ it -> it.boxOfficeResult.dailyBoxOfficeList }
            .repeat(maxCount.toLong())
            .map{ it ->
                it.get(index++)}
            .flatMap {
                Logger.d("it.movieNm : ${it.movieNm}")
                getNaverAPI(it.movieNm).toObservable().base().take(maxCount.toLong())
            }.base().subscribe(
                {
                    Logger.d("naverAPI : ${it.items.get(0).title}")

                    val list = mutableListOf<NaverMovieListDTO>()
                    list.add(it)

                    updateData(list.get(0))

                    Logger.d("updateData : ${list.get(0).items.get(0).image}")
                    _movieList.value = list
                },
                {
                    Logger.e("naverAPI exception : ${it.message}")
                }, {
                    Logger.e("naverAPI updateList")
                }
            )
    }

    /**
     * 영화진흥위원회에서 날짜에 해당하는 영화 리스트를 가져온다.
     * @param targetDt ex) 20201129
     * @return
     */
    fun getBoxOffice(targetDt : String) : Single<KobisDailyBoxOfficeListDTO> {
        val apiKey = BuildConfig.kobisApiKey
        return APIClient.kobisApi.getDailyBoxOffice(
            apiKey,
            targetDt,
            maxCount
        )
    }

    /**
     * 영화 제목으로 검색하여 리스트를 가져온다.
     */
    fun getNaverAPI(title: String) : Single<NaverMovieListDTO> {
        val clientId: String = BuildConfig.clientId
        val clientSecret: String = BuildConfig.clientSecret
        return APIClient.naverApi.getMovieList(clientId, clientSecret, title, 100)
    }

    /**
     *
     */
    fun updateData( data : NaverMovieListDTO) {
        Observable.just(data)
            .base()
            .map { data.items.get(0) }
            .flatMap { getImageUrl(it) }
            .flatMap { getTitle(it) }
            .subscribe({

            },{

            },{

                val list = mutableListOf<NaverMovieItem>()
                list.add(data.items.get(0))
                _movieItemList.value = list
            },{
            }
            )
    }

    /**
     * image url Observable
     * @param data
     * @return
     */
    fun getImageUrl(data : NaverMovieItem) : Observable<NaverMovieItem> {
        val defaultImageUrl = "https://movie.naver.com/movie/bi/mi/photoViewPopup.nhn?movieCode="

        val imageObservable = Observable.fromCallable{
            val code = data.link.substringAfterLast("=")
            val codeUrl = defaultImageUrl + code

            var doc = Jsoup.connect(codeUrl).get()
            val element = doc.select("img")

            Logger.d("element : ${element.toString()}")

            if(false == element.isEmpty())  {
                val imageUrl = element.get(0).absUrl("src")
                Logger.d("element imageUrl : ${imageUrl}")
                data.image = imageUrl
            }

            return@fromCallable data
        }.base()

        return imageObservable
    }

    /**
     * get title Observable
     * @param data
     * @return
     */
    fun getTitle(data : NaverMovieItem) : Observable<NaverMovieItem> {
        val titleObservable = Observable.fromCallable{
            data.title = Jsoup.parse(data.title).text()

            return@fromCallable data
        }.base()
        return titleObservable
    }

}
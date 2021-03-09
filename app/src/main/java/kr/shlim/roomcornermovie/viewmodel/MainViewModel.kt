package kr.shlim.roomcornermovie.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kr.shlim.roomcornermovie.BuildConfig
import kr.shlim.roomcornermovie.common.CONSTANT_MAX_MOVIE_COUNT
import kr.shlim.roomcornermovie.ext.base
import kr.shlim.roomcornermovie.ext.plusAssign
import kr.shlim.roomcornermovie.model.KobisDailyBoxOfficeListDTO
import kr.shlim.roomcornermovie.model.naver.NaverMovieItem
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.network.APIClient
import org.jsoup.Jsoup

class MainViewModel : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val _movieList = MutableLiveData<List<NaverMovieListDTO>>()
    val movieList : LiveData<List<NaverMovieListDTO>> by lazy { _movieList }

    enum class LoadingStatus {
        LOADING,
        ERROR,
        DONE
    }

    val _loadingState = MutableLiveData<LoadingStatus>()
    val loadingStatus = _loadingState

    var recvItemCount = 0

    private val _movieItemList = MutableLiveData<List<NaverMovieItem>>()
    val movieItemList : LiveData<List<NaverMovieItem>> by lazy { _movieItemList }

    val maxCount : Int = CONSTANT_MAX_MOVIE_COUNT

    /**
     * viewmodel click test
     * @param v
     */
    fun onButtonClick(v : View) {
        Logger.d(TAG, "onButtonClick() called with: v = [${v.id}]")
    }

    /**
     * 날짜에 해당하는 영화 리스트를 가져온다.
     * @param targetDt ex) 20201129
     */
    fun getMovieList(targetDt : String) {
        _loadingState.value = LoadingStatus.LOADING

        val boxOffice = getBoxOffice(targetDt)
        var index = 0
        var totalCount = 0

        compositeDisposable += boxOffice.toObservable()
            .map{ it ->
                recvItemCount = it.boxOfficeResult.dailyBoxOfficeList.size
                Logger.d("boxOffice recvItemCount = {$recvItemCount}")
                totalCount = it.boxOfficeResult.dailyBoxOfficeList.size
                it.boxOfficeResult.dailyBoxOfficeList
                 }
            .map{ it ->
                totalCount = it.size
                it.get(index)
            }.repeat()
            .flatMap {
                Logger.d("getMovieList index : ${index}")
                Logger.d("it.movieNm : ${it.movieNm}")
                Logger.d("take num : ${totalCount.toLong()-1}")

                index++
//                getNaverAPI(it.movieNm).toObservable().base().take(totalCount.toLong()-1)
                getNaverAPI(it.movieNm).toObservable()
            }.base().subscribe(
                {
                    Logger.d("naverAPI : ${it.items.get(0).title}")

                    val list = mutableListOf<NaverMovieListDTO>()
                    list.add(it)

                    updateData(list.get(0))

                    Logger.d("updateData : ${list.get(0).items.get(0).image}")
                    Logger.d("updateData list : ${_movieItemList.value?.get(0)?.image}")

                    _movieList.value = list
                },
                {
                    Logger.e("naverAPI exception : ${it.message}")
                    _loadingState.value = LoadingStatus.ERROR
                    _loadingState.value = LoadingStatus.DONE
                }, {
                    _loadingState.value = LoadingStatus.DONE
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

        Logger.d("getBoxOffice call targetDt = {$targetDt}")
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
        Logger.d("getNaverAPI title : {$title}")

        val clientId: String = BuildConfig.clientId
        val clientSecret: String = BuildConfig.clientSecret
        return APIClient.naverApi.getMovieList(clientId, clientSecret, title, 100)
    }

    /**
     *
     */
    fun updateData( data : NaverMovieListDTO) {
        compositeDisposable += Observable.just(data)
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

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
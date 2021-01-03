package kr.shlim.roomcornermovie.network.api

import io.reactivex.Observable
import io.reactivex.Single
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverApi {



    // X-Naver-Client-Id
    // X-Naver-Client-Secret
    //https://openapi.naver.com/v1/search/movie.json?query=%EC%A3%BC%EC%8B%9D&display=100

    @GET("/v1/search/movie.json")
    fun getMovieList(
        @Header("X-Naver-Client-Id") id : String,
        @Header("X-Naver-Client-Secret") secret : String,
        @Query("query")query : String,
        @Query("display") displayCount : Int) : Single<NaverMovieListDTO>


    @GET("/v1/search/movie.json")
    fun getObserverMovieList(
        @Header("X-Naver-Client-Id") id : String,
        @Header("X-Naver-Client-Secret") secret : String,
        @Query("query")query : String,
        @Query("display") displayCount : Int) : Observable<NaverMovieListDTO>
}
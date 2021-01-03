package kr.shlim.roomcornermovie.network.api

import io.reactivex.Single
import kr.shlim.roomcornermovie.model.KmdbMovieListDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface KMDBApi {



    // http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?
    // collection=kmdb_new2&detail=N&ServiceKey=EL0V4K1532U77G39NPKY&title=%EC%89%AC%EB%A6%AC

    @GET("/openapi-data2/wisenut/search_api/search_json2.jsp")
    fun getMovieList(
        @Query("collection")collection : String,
        @Query("detail")detail : String,
        @Query("ServiceKey") key : String,
        @Query("title")title : String)  : Single<KmdbMovieListDTO>
}
package kr.shlim.roomcornermovie.network

import io.reactivex.Observable
import io.reactivex.Single
import kr.shlim.roomcornermovie.data.dto.kobis.KobisDailyBoxOfficeListDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface KobisApi {


    // http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=0aaff6d6268952be706a0332880380af&targetDt=20200307&itemPerPage=10

    @GET("/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json")
    fun getDailyBoxOffice(@Query("key") key : String, @Query("targetDt") targetDt : String, @Query("itemPerPage") pageCount : Int) : Single<KobisDailyBoxOfficeListDTO>
}
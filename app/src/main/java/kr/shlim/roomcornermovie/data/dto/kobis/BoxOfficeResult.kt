package kr.shlim.roomcornermovie.data.dto.kobis

data class BoxOfficeResult(
    val boxofficeType: String,                                      // 일별 박스오피스, 월별 박스오피스
    val dailyBoxOfficeList: List<DailyBoxOffice>,                   // DailyBoxOffice 리스트
    val showRange: String                                           // 박스오피스 조회 일자
)
package kr.shlim.roomcornermovie.model

data class DailyBoxOffice(
    val audiAcc: String,
    val audiChange: String,                 // 전일 대비 관객수 증감 비율
    val audiCnt: String,                    // 해당일의 관객수
    val audiInten: String,                  // 전일 대비 관객수 증감분
    val movieCd: String,                    // 영화의 대표코드
    val movieNm: String,                    // 영화명(국문)
    val openDt: String,                     // 영화의 개봉일
    val rank: String,                       // 해당일자의 박스오피스 순위
    val rankInten: String,                  // 전일대비 순위의 증감분
    val rankOldAndNew: String,              // 랭킹에 신규진입여부를 출력합니다. “OLD” : 기존 , “NEW” : 신규
    val rnum: String,                       // 순번
    val salesAcc: String,                   // 누적매출액
    val salesAmt: String,                   // 해당일의 매출액
    val salesChange: String,                // 전일 대비 매출액 증감 비율
    val salesInten: String,                 // 전일 대비 매출액 증감분
    val salesShare: String,                 // 해당일자 상영작의 매출총액 대비 해당 영화의 매출비율
    val scrnCnt: String,                    // 해당일자에 상영한 스크린수
    val showCnt: String                     // 해당일자에 상영된 횟수
)
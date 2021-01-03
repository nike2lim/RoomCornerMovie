package kr.shlim.roomcornermovie.model.naver

data class NaverMovieListDTO(
    val display: Int,
    val items: List<NaverMovieItem>,
    val lastBuildDate: String,
    val start: Int,
    val total: Int
)
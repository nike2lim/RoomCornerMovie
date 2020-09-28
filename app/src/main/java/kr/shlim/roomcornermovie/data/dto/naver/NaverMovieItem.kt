package kr.shlim.roomcornermovie.data.dto.naver

data class NaverMovieItem(
    val actor: String,
    val director: String,
    var image: String,
    val link: String,
    val pubDate: String,
    val subtitle: String,
    var title: String,
    val userRating: String
)
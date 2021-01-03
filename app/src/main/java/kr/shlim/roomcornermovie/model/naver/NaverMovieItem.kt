package kr.shlim.roomcornermovie.model.naver

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
package kr.shlim.roomcornermovie.data.dto.naver

import kr.shlim.roomcornermovie.data.dto.naver.NaverMovieItem

data class NaverMovieListDTO(
    val display: Int,
    val items: List<NaverMovieItem>,
    val lastBuildDate: String,
    val start: Int,
    val total: Int
)